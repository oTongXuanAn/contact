package an.xuan.tong.historycontact.realm

import an.xuan.tong.historycontact.Constant
import an.xuan.tong.historycontact.Utils.Utils
import an.xuan.tong.historycontact.api.model.InformationResponse
import an.xuan.tong.historycontact.location.LocationCurrent
import an.xuan.tong.historycontact.service.TokenService.Companion.ONE_WEEK_INTERVAL
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.realm.Realm
import io.realm.RealmModel
import io.realm.exceptions.RealmException


class RealmUtils {
    companion object {
        fun clearCaching() {
            try {
                Realm.getInstance(HistoryContactConfiguration.createBuilder().build()).use { realm ->
                    realm.executeTransaction { realm ->
                        realm.deleteAll()
                    }
                }
            } catch (e: RealmException) {
            }
        }

        fun getCacheInformation(): ApiCaching? {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            var result: ApiCaching? = null
            mRealm.executeTransaction {
                val mangaSearchObj: ApiCaching? = mRealm.where(ApiCaching::class.java).equalTo("id", mKeyAPI).findFirst()
                result = ApiCaching(mangaSearchObj?.id, mangaSearchObj?.data, mangaSearchObj?.updateAt)
            }
            return result
        }

        fun isRunReTocket(): Boolean {
            getCacheInformation()?.updateAt?.let {
                return ((System.currentTimeMillis() - it.toLong()) >= ONE_WEEK_INTERVAL)
            }
            return false
        }

        fun getAccountId(): Int? {
            return convertJsonToObject(getCacheInformation()?.data)?.data?.id
        }

        fun getAuthorization(): String {
            return "Bearer ${getToken()}"
        }

        fun getLocationCurrent(): LocationCurrent? {
            var locationCurrent: LocationCurrent? = null
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                locationCurrent = mRealm.where(LocationCurrent::class.java).findFirst()
            }
            return locationCurrent
        }


        fun saveCacheInformation(listData: InformationResponse) {
            val objCache = ApiCaching(mKeyAPI, Gson().toJson(listData), System.currentTimeMillis().toString())
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                mRealm.insertOrUpdate(objCache)
            }
        }

        //Power

        fun savePowerOnOff(isPowerOn: Boolean) {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                var dateCreate = Utils.getLocalTime()
                val objCache = PowerHistoryCaching(idAutoIncrement(PowerHistoryCaching::class.java), dateCreate.toString(), isPowerOn)
                mRealm.insertOrUpdate(objCache)
            }

        }

        fun getAllPowerCaching(): List<PowerHistoryCaching>? {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            var listPower: List<PowerHistoryCaching>? = null
            mRealm.executeTransaction {
                listPower = mRealm.where(PowerHistoryCaching::class.java).findAll()
            }
            return listPower

        }

        fun deleteItemPower(id: Int? = 0) {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                mRealm.where(PowerHistoryCaching::class.java).equalTo("id", id).findAll().deleteAllFromRealm()
            }
        }


        //Internet
        fun saveInternetOnOff(isOn: Boolean) {
            var dateCreate = Utils.getLocalTime()
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                val objCache = InternetHistoryCaching(idAutoIncrement(InternetHistoryCaching::class.java), dateCreate.toString(), isOn)
                mRealm.insertOrUpdate(objCache)
            }

        }

        fun getAllInternetCaching(): List<InternetHistoryCaching>? {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            var listPower: List<InternetHistoryCaching>? = null
            mRealm.executeTransaction {
                listPower = mRealm.where(InternetHistoryCaching::class.java).findAll()
            }
            return listPower
        }

        fun deleteItemInternet(id: Int? = 0) {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                mRealm.where(InternetHistoryCaching::class.java).equalTo("id", id).findAll().deleteAllFromRealm()
            }

        }


        fun getCurrentLocation() {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.beginTransaction()
            var size = mRealm.where(LocationCurrent::class.java).findAll().size
            val locationCurrentRealm = mRealm.where(LocationCurrent::class.java).contains("idCurrent", Constant.KEY_LOCATION_CURRENT).findFirst()
            var locationCurrent: LocationCurrent? = locationCurrentRealm
            mRealm.commitTransaction()
        }

        private val mKeyAPI: Int by lazy {
            1
        }

        private fun convertJsonToObject(json: String?): InformationResponse? {
            json?.let {
                return Gson().fromJson(json, object : TypeToken<InformationResponse?>() {}.type)
            }
            return null

        }

        fun getToken(): String? {
            return convertJsonToObject(getCacheInformation()?.data)?.token
        }

        fun isActive(): Boolean {
            getCacheInformation()?.let {
                return convertJsonToObject(getCacheInformation()?.data)?.status.equals("success")
            }
            return false

        }

        //=============handler call====================

        fun saveCallLogFail(callLog: CachingCallLog) {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                mRealm.insert(callLog)
            }

        }


        fun getAllCallLog(): List<CachingCallLog>? {
            var getAllCall: List<CachingCallLog>? = null
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                getAllCall = mRealm.where(CachingCallLog::class.java).findAll()
            }
            return getAllCall

        }

        fun deleteItemCachingCallLog(id: Int?) {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                mRealm.where(CachingCallLog::class.java).equalTo("id", id).findAll().deleteAllFromRealm()
            }
        }

        //=============handler sms====================

        fun saveSmsFail(smsFail: CachingMessage) {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                mRealm.insert(smsFail)
            }
        }

        fun deleteItemCachingMess(id: Int?) {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                mRealm.where(CachingMessage::class.java).equalTo("id", id).findAll().deleteAllFromRealm()
            }
        }

        fun getAllMessCaching(): List<CachingMessage>? {
            var listMess: List<CachingMessage>? = null
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            mRealm.executeTransaction {
                listMess = mRealm.where(CachingMessage::class.java).findAll()
            }
            return listMess
        }


        fun <E : RealmModel> idAutoIncrement(clazz: Class<E>): Int {
            val mRealm = Realm.getInstance(HistoryContactConfiguration.createBuilder().build())
            val currentIdNum = mRealm.where(clazz).max("id")
            var nextId: Int
            nextId = if (currentIdNum == null) {
                1
            } else {
                currentIdNum.toInt() + 1
            }
            return nextId
        }

    }


}