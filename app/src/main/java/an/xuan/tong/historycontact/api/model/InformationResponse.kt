package an.xuan.tong.historycontact.api.model

import com.squareup.moshi.Json

data class Calllog(
        @Json(name = "id")
        val id: Int? = null,
        @Json(name = "idAccount")
        val idaccount: Int? = null,
        @Json(name = "phone")
        val phone: String? = null,
        @Json(name = "datecreate")
        val datecreate: Int? = null,
        @Json(name = "duration")
        val duration: String? = null,
        @Json(name = "location")
        val location: String? = null,
        @Json(name = "fileaudio")
        val fileaudio: String? = null,
        @Json(name = "status")
        val status: Boolean? = null,
        val accounts: String? = null)

data class City(
        @Json(name = "id")
        val id: Int? = null,
        @Json(name = "code")
        val code: String? = null,
        @Json(name = "title")
        val title: String? = null,
        @Json(name = "status")
        val status: Boolean? = null,
        @Json(name = "accounts")
        val accounts: List<Any>? = null)

data class Account(
        @Json(name = "id")
        val id: Int? = null,
        @Json(name = "phone")
        val phone: String? = null,
        @Json(name = "password")
        val password: String? = null,
        @Json(name = "idcity")
        val idcity: Int? = null,
        @Json(name = "fullname")
        val fullname: String? = null,
        @Json(name = "address")
        val address: String? = null,
        @Json(name = "email")
        val email: String? = null,
        @Json(name = "gender")
        val gender: Boolean? = null,
        @Json(name = "birthday")
        val birthday: String? = null,
        @Json(name = "lasttime")
        val lasttime: Int? = null,
        @Json(name = "datecreate")
        val datecreate: Int? = null,
        @Json(name = "status")
        val status: Boolean? = null,
        @Json(name = "powers")
        val powers: List<Power>? = null,
        @Json(name = "calllogs")
        val calllogs: List<Calllog>? = null,
        @Json(name = "internets")
        val internets: List<Internet>? = null,
        @Json(name = "locations")
        val locations: List<Location>? = null,
        @Json(name = "messages")
        val messages: List<Message>? = null,
        @Json(name = "city")
        val city: City? = null)


data class Internet(
        @Json(name = "id")
        val id: Int? = null,
        @Json(name = "idAccount")
        val idaccount: Int? = null,
        @Json(name = "datecreate")
        val datecreate: Int? = null,
        @Json(name = "status")
        val status: Boolean? = null)


data class Location(
        @Json(name = "id")
        val id: Int? = null,
        @Json(name = "idAccount")
        val idaccount: Int? = null,
        @Json(name = "datecreate")
        val datecreate: Int? = null,
        @Json(name = "lat")
        val lat: Double? = null,
        @Json(name = "lng")
        val lng: Double? = null
)


data class Message(
        @Json(name = "id")
        val id: Int? = null,
        @Json(name = "idAccount")
        val idaccount: Int? = null,
        @Json(name = "phone")
        val phone: String? = null,
        @Json(name = "datecreate")
        val datecreate: String? = null,
        @Json(name = "location")
        val location: String? = null,
        @Json(name = "contentmessage")
        val contentmessage: String? = null,
        @Json(name = "status")
        val status: Boolean? = null,
        val accounts: String? = null
)


data class Power(
        @Json(name = "id")
        val id: Int? = null,
        @Json(name = "idAccount")
        val idaccount: Int? = null,
        @Json(name = "datecreate")
        val datecreate: Int? = null,
        @Json(name = "status")
        val status: Boolean? = null)

data class InformationResponse(
        @Json(name = "token")
        val token: String? = null,
        @Json(name = "data")
        val data: Account? = null,
        @Json(name = "status")
        val status: String? = null,
        @Json(name = "message")
        val message: String? = null
)