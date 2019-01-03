package an.xuan.tong.historycontact.api.model


import com.google.firebase.database.Exclude


data class User(val id: String, val email: String? = "", val sdt: String? = "") {

    @Exclude
    fun toMap(): Map<String, String?> {
        val result: HashMap<String, String?> = HashMap()
        result.apply {
            put("Id", id)
            put("email", email)
            put("sdt", sdt)
        }
        return result
    }
}