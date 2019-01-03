package an.xuan.tong.historycontact.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PowerHistoryCaching(
        @PrimaryKey
        var id: Int? = 0,
        var datecreate: String? = "",
        var isPowerOn: Boolean? = true//true:Start -false:

) : RealmObject()

open class InternetHistoryCaching(
        @PrimaryKey
        var id: Int? = 0,
        var datecreate: String? = "",
        var isInternet: Boolean? = true//true:on -off
) : RealmObject()