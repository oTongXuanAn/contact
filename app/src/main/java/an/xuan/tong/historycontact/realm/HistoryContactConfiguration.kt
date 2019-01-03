package an.xuan.tong.historycontact.realm

import io.realm.RealmConfiguration

/**
 *
 */
object HistoryContactConfiguration {
    private const val REALM_NAME = "historycache.realm"
    private const val SCHEMA_VERSION = 1L
    fun createBuilder(
    ): RealmConfiguration.Builder {
        return RealmConfiguration.Builder()
                .name(REALM_NAME)
                //.encryptionKey(encryptionKey)
                .schemaVersion(SCHEMA_VERSION)
                .modules(MixiCacheModule())
                .migration(CacheRealmMigration())
    }
}