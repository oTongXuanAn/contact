package an.xuan.tong.historycontact.realm

import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmMigration


class CacheRealmMigration : RealmMigration {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is CacheRealmMigration
    }

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {

    }
}