package an.xuan.tong.historycontact.api.model

import com.squareup.moshi.Json

data class CallSMSReponse(
        @Json(name = "status")
        val status: String? = null,
        @Json(name = "message")
        val message: String? = null,
        @Json(name = "id")
        val id: String
)

data class UpfileResponse(
        @Json(name = "Version")
        val Version: String? = null,
        @Json(name = "Content")
        val Content: String? = null,
        @Json(name = "StatusCode")
        val StatusCode: String,
        @Json(name = "Content")
        val ReasonPhrase: String? = null,
        @Json(name = "ReasonPhrase")
        val Headers: String? = null,
        @Json(name = "Headers")
        val RequestMessage: String? = null,
        @Json(name = "RequestMessage")
        val IsSuccessStatusCode: Boolean? = false
)