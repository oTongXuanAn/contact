package an.xuan.tong.historycontact.api.model

import com.google.firebase.database.Exclude
import com.squareup.moshi.Json
import io.realm.RealmObject

data class SmsSendServer(
        @Json(name = "idAccount")
        val idaccount: Int? = -1,
        @Json(name = "phone")
        val phone: String? = null,
        @Json(name = "datecreate")
        val datecreate: String? = null,
        @Json(name = "lat")
        val lat: String? = null,
        @Json(name = "lng")
        val lng: String? = null,
        @Json(name = "contentmessage")
        val contentmessage: String? = null,
        @Json(name = "status")
        val status: Boolean? = null) {
    @Exclude
    fun toMap(): Map<String, String?> {
        val result: HashMap<String, String?> = HashMap()
        result.apply {
            put("idAccount", idaccount.toString())
            put("phone", phone)
            put("datecreate", datecreate)
            put("lng", lng)
            put("lat", lat)
            put("contentmessage", contentmessage)
            put("status", status.toString())
        }
        return result
    }
}

data class CallLogServer(
        @Json(name = "idAccount")
        val idaccount: Int? = null,
        @Json(name = "phone")
        val phone: String? = null,
        @Json(name = "datecreate")
        val datecreate: String? = null,
        @Json(name = "duration")
        val duration: String? = null,
        @Json(name = "lat")
        val lat: String? = null,
        @Json(name = "lng")
        val lng: String? = null,
        @Json(name = "fileaudio")
        val fileaudio: String? = null,
        @Json(name = "status")
        val status: String? = null) {
    fun toMap(): Map<String, String?> {
        val result: HashMap<String, String?> = HashMap()
        result.apply {
            put("idAccount", idaccount.toString())
            put("phone", phone)
            put("datecreate", datecreate)
            put("lat", lat)
            put("lng", lng)
            put("duration", duration)
            put("fileaudio", fileaudio)
            put("status", status.toString())
        }
        return result
    }
}

data class LocationServer(
        @Json(name = "idAccount")
        val idaccount: Int? = null,
        @Json(name = "phone")
        val datecreate: String? = null,
        @Json(name = "lat")
        val lat: String? = null,
        @Json(name = "lng")
        val lng: String? = null
) {
    fun toMap(): Map<String, String?> {
        val result: HashMap<String, String?> = HashMap()
        result.apply {
            put("idAccount", idaccount.toString())
            put("datecreate", datecreate)
            put("lat", lat)
            put("lng", lng)
        }
        return result
    }
}

data class PowerAndInternet(
        @Json(name = "idAccount")
        val idaccount: Int? = null,
        @Json(name = "phone")
        val datecreate: String? = null,
        @Json(name = "status")
        val status: Boolean? = null) {
    fun toMap(): Map<String, String?> {
        val result: HashMap<String, String?> = HashMap()
        result.apply {
            put("idAccount", idaccount.toString())
            put("datecreate", datecreate)
            put("status", status.toString())
        }
        return result
    }
}



