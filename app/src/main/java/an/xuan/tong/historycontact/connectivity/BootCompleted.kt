package an.xuan.tong.historycontact.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompleted internal constructor(private val mBootCompletedListener: BootCompletedListener) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        mBootCompletedListener.onBootCompleted()
    }

    interface BootCompletedListener {
        fun onBootCompleted()
    }

}
