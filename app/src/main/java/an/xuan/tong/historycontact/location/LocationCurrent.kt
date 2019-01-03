package an.xuan.tong.historycontact.location

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LocationCurrent(
        @PrimaryKey
        var idCurrent: String? = "current",
        var lat: String? = "",
        var log: String? = "") : RealmObject()