package an.xuan.tong.historycontact

import android.app.Application
import android.os.Build
import android.os.StrictMode
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmConfiguration
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric


class HistoryContactAplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Fabric.with(this, Crashlytics())
    }

    fun enableStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build())
    }


}