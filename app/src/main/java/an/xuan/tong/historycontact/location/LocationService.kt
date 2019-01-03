package an.xuan.tong.historycontact.location

import an.xuan.tong.historycontact.Constant
import an.xuan.tong.historycontact.R
import an.xuan.tong.historycontact.Utils.Utils
import an.xuan.tong.historycontact.api.ApiService
import an.xuan.tong.historycontact.api.Repository
import an.xuan.tong.historycontact.api.model.InformationResponse
import an.xuan.tong.historycontact.api.model.LocationServer
import an.xuan.tong.historycontact.realm.ApiCaching
import an.xuan.tong.historycontact.realm.HistoryContactConfiguration
import an.xuan.tong.historycontact.realm.RealmUtils
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_MIN
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class LocationService : Service() {
    var mLocationManager: LocationManager? = null
    private var alarmManager: AlarmManager? = null

    private var mLocationListeners = arrayOf(LocationListener(LocationManager.GPS_PROVIDER), LocationListener(LocationManager.NETWORK_PROVIDER))

    inner class LocationListener(provider: String) : android.location.LocationListener {
        internal var mLastLocation: Location

        init {
            Log.e(TAG, "LocationListener $provider")
            mLastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {
            Log.e(TAG, "onLocationChanged: $location")
            mLastLocation.set(location)
            Log.e("LastLocation", mLastLocation.latitude.toString() + "  " + mLastLocation.longitude.toString())

            saveLocation(mLastLocation.latitude, mLastLocation.longitude)
            val id = convertJsonToObject(getCacheInformation()?.data).data?.id
            val timeCreate = Utils.getLocalTime()
            val token = convertJsonToObject(getCacheInformation()?.data).token
            val result: HashMap<String, String> = HashMap()
            result.apply {
                result["Authorization"] = "Bearer $token"
            }

            val sendLocation = LocationServer(id, timeCreate.toString(), mLastLocation.latitude.toString(), mLastLocation.longitude.toString())
            Log.e("sendLocation", " " + sendLocation.toString())
            if (RealmUtils.isActive())
            Repository.createService(ApiService::class.java, result).insertLocation(Constant.KEY_API, sendLocation.toMap())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                Log.e("antx", "insertLocation  " + result.toString())

                            },
                            { e ->
                                Log.e("test", "insertLocation error " + e.message)
                            })
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.e(TAG, "onStatusChanged: $provider")
        }
    }


    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        initializeLocationManager()
        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[1])
        } catch (ex: java.lang.SecurityException) {
            Log.e(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "network provider does not exist, " + ex.message)
        }

        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL.toLong(), LOCATION_DISTANCE,
                    mLocationListeners[0])
        } catch (ex: java.lang.SecurityException) {
            Log.e(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "gps provider does not exist " + ex.message)
        }
        return Service.START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(notificationManager) else ""
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
        startForeground(99, notification)
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        for (i in mLocationListeners.indices) {
            try {
                mLocationManager!!.removeUpdates(mLocationListeners[i])
            } catch (ex: Exception) {
                Log.e(TAG, "fail to remove location listners, ignore", ex)
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val intent = Intent(this, LocationService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, 0)
        val now = Utils.getLocalTime()
        getAlarmManager().set(AlarmManager.RTC_WAKEUP, now + 1000, pendingIntent)
        super.onTaskRemoved(rootIntent)

        Log.e("antx", "onTaskRemoved")
    }

    private fun getAlarmManager(): AlarmManager {
        return if (alarmManager != null) alarmManager!! else getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager")
        mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    companion object {
        private val TAG = "BOOMBOOMTESTGPS"
        private val LOCATION_INTERVAL = 15 * 60 * 1000 //15 min
        private val LOCATION_DISTANCE = 100f
    }

    private fun getCacheInformation(): ApiCaching? {
        val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
        val mangaSearchObj: ApiCaching? = mRealm.where(ApiCaching::class.java).equalTo("id", mKeyAPI).findFirst()
        val result = ApiCaching(mangaSearchObj?.id, mangaSearchObj?.data, mangaSearchObj?.updateAt)
        mRealm.close()
        return result
    }

    private fun saveLocation(lat: Double, lng: Double) {
        val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
        mRealm.executeTransaction {
            var locationCurrent = LocationCurrent(Constant.KEY_LOCATION_CURRENT, lat.toString(), lng.toString())
            mRealm.insertOrUpdate(locationCurrent)
        }
    }

    private val mKeyAPI: Int by lazy {
      1
    }

    private fun convertJsonToObject(json: String?): InformationResponse {
        return Gson().fromJson(json, object : TypeToken<InformationResponse?>() {}.type)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        val channelId = "my_service_channelid"
        val channelName = "My Foreground Service"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        // omitted the LED color
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

}