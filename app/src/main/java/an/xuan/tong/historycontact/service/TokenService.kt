package an.xuan.tong.historycontact.service

import an.xuan.tong.historycontact.Constant
import an.xuan.tong.historycontact.api.ApiService
import an.xuan.tong.historycontact.api.Repository
import an.xuan.tong.historycontact.realm.RealmUtils
import an.xuan.tong.historycontact.realm.RealmUtils.Companion.isRunReTocket
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.facebook.accountkit.Account
import com.facebook.accountkit.AccountKit
import com.facebook.accountkit.AccountKitCallback
import com.facebook.accountkit.AccountKitError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class TokenService : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        AccountKit.getCurrentAccount(object : AccountKitCallback<Account> {
            override fun onSuccess(account: Account) {
                if (isRunReTocket())
                    account.phoneNumber?.let {
                        val result: HashMap<String, String> = HashMap()
                        result["Authorization"] = RealmUtils.getAuthorization()
                        Repository.createService(ApiService::class.java, result).getRetoken(Constant.KEY_API, RealmUtils.getAccountId())
                                .subscribeOn(Schedulers.io())
                                .retry(5)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { result ->
                                            RealmUtils.saveCacheInformation(result)
                                            jobFinished(params, true)

                                        },
                                        { e ->
                                            jobFinished(params, true)
                                                                                    })

                    }
                account.email?.let {
                }
            }

            override fun onError(error: AccountKitError) {}
        })

        // false when it is synchronous.
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }


    companion object {
        private val JOB_ID = 19
        val ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L // 1 Day
        val ONE_WEEK_INTERVAL = 7 * 24 * 60 * 60 * 1000L // 1 Week

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun schedule(context: Context, intervalMillis: Long) {
                      val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val componentName = ComponentName(context, TokenService::class.java)
            val builder = JobInfo.Builder(JOB_ID, componentName).apply {
                setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                setPeriodic(intervalMillis)
            }
            jobScheduler.schedule(builder.build())
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun cancel(context: Context) {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(JOB_ID)
        }


    }
}
