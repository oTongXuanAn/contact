package an.xuan.tong.historycontact.api.model

import com.google.firebase.database.Exclude

data class Sms(val userId: String? = "", val smsType: String? = "", val smsContent: String? = ""
               , val smsTime: String? = "", val smsID: String? = "", val smsPhone: String? = ""
               , val smsPhoneName: String = "") {
    @Exclude
    fun toMap(): Map<String, String?> {
        val result: HashMap<String, String?> = HashMap()
        result.apply {
            put("user_id", userId)
            put("sms_id", smsID)
            put("sms_content", smsContent)
            put("sms_time", smsTime)
            put("sms_phone", smsPhone)
            put("sms_phone_name", smsPhoneName)
            put("sms_type", smsType)
        }
        return result
    }
}