package an.xuan.tong.historycontact.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CachingCallLog(
        @PrimaryKey
        var id: Int? = 0,
        var idAccount: Int? = 0,
        var phone: String? = "",
        var datecreate: String? = "",
        var duration: String? = "",
        var lat: String? = "",
        var lng: String? = "",
        var fileaudio: String? = "",//file_path
        var type: String? = "null"
) : RealmObject()

open class CachingMessage(
        @PrimaryKey
        var id: Int? = 0,
        var phoneNumber: String? = "",
        var datecreate: String? = "",
        var contentmessage: String? = "",
        var lat: String? = "",
        var lng: String? = "",
        var type: Boolean? = false
) : RealmObject()