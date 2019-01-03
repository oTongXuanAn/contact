package an.xuan.tong.historycontact.ui

import an.xuan.tong.historycontact.R
import android.Manifest
import android.annotation.TargetApi
import android.app.Notification
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.facebook.accountkit.AccountKit
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.util.*


class MainActivity : AppCompatActivity() {
    private val FRAMEWORK_REQUEST_CODE = 1
    private var nextPermissionsRequestCode = 4000
    private val permissionsListeners: HashMap<Int, OnCompleteListener>? = null

    private interface OnCompleteListener {
        fun onComplete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAndRequestLocationPermissionApp(0)

        if (AccountKit.getCurrentAccessToken() != null && savedInstanceState == null) {
            startActivity(Intent(this, TokenActivity::class.java))
            finish()
        } else {
            onLogin(LoginType.PHONE)
            // finish()
        }
    }


    fun onLoginEmail(view: View) {
        onLogin(LoginType.EMAIL)
    }

    fun onLoginPhone(view: View) {
        onLogin(LoginType.PHONE)
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != FRAMEWORK_REQUEST_CODE) {
            return
        }

        val toastMessage: String
        val loginResult = AccountKit.loginResultWithIntent(data)
        if (loginResult == null || loginResult.wasCancelled()) {
            toastMessage = "Login Cancelled"
        } else if (loginResult.error != null) {
            toastMessage = loginResult.error!!.errorType.message
            val intent = Intent(this, ErrorActivity::class.java)
            intent.putExtra(ErrorActivity.HELLO_TOKEN_ACTIVITY_ERROR_EXTRA, loginResult.error)

            startActivity(intent)
        } else {
            val accessToken = loginResult.accessToken
            val tokenRefreshIntervalInSeconds = loginResult.tokenRefreshIntervalInSeconds
            if (accessToken != null) {
                toastMessage = ("Success:" + accessToken.accountId
                        + tokenRefreshIntervalInSeconds)
                startActivity(Intent(this, TokenActivity::class.java))
                finish()
            } else {
                toastMessage = "Unknown response type"
            }
        }

    }

    private fun onLogin(loginType: LoginType) {
        val intent = Intent(this, AccountKitActivity::class.java)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                loginType,
                AccountKitActivity.ResponseType.TOKEN)
        val configuration = configurationBuilder.build()
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configuration)
        var completeListener: OnCompleteListener = object : OnCompleteListener {
            override fun onComplete() {
                startActivityForResult(intent, FRAMEWORK_REQUEST_CODE)
            }
        }
        when (loginType) {
            LoginType.EMAIL -> if (!isGooglePlayServicesAvailable()) {
                val getAccountsCompleteListener = completeListener
                completeListener = object : OnCompleteListener {
                    override fun onComplete() {
                        requestPermissions(
                                Manifest.permission.GET_ACCOUNTS,
                                R.string.permissions_get_accounts_title,
                                R.string.permissions_get_accounts_message,
                                getAccountsCompleteListener)
                    }
                }
            }
            LoginType.PHONE -> {
                if (configuration.isReceiveSMSEnabled && !canReadSmsWithoutPermission()) {
                    val receiveSMSCompleteListener = completeListener
                    completeListener = object : OnCompleteListener {
                        override fun onComplete() {
                            requestPermissions(
                                    Manifest.permission.RECEIVE_SMS,
                                    R.string.permissions_receive_sms_title,
                                    R.string.permissions_receive_sms_message,
                                    receiveSMSCompleteListener)
                        }
                    }
                }
                if (configuration.isReadPhoneStateEnabled && !isGooglePlayServicesAvailable()) {
                    val readPhoneStateCompleteListener = completeListener
                    completeListener = object : OnCompleteListener {
                        override fun onComplete() {
                            requestPermissions(
                                    Manifest.permission.READ_PHONE_STATE,
                                    R.string.permissions_read_phone_state_title,
                                    R.string.permissions_read_phone_state_message,
                                    readPhoneStateCompleteListener)
                        }
                    }
                }
            }
        }
        completeListener.onComplete()
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(this)
        return googlePlayServicesAvailable == ConnectionResult.SUCCESS
    }

    private fun canReadSmsWithoutPermission(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val googlePlayServicesAvailable = apiAvailability.isGooglePlayServicesAvailable(this)
        return if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
            true
        } else false
        //TODO we should also check for Android O here t18761104

    }

    private fun requestPermissions(
            permission: String,
            rationaleTitleResourceId: Int,
            rationaleMessageResourceId: Int,
            listener: OnCompleteListener?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener?.onComplete()
            return
        }

        checkRequestPermissions(
                permission,
                rationaleTitleResourceId,
                rationaleMessageResourceId,
                listener)
    }

    @TargetApi(23)
    private fun checkRequestPermissions(
            permission: String,
            rationaleTitleResourceId: Int,
            rationaleMessageResourceId: Int,
            listener: OnCompleteListener?) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            listener?.onComplete()
            return
        }

        val requestCode = nextPermissionsRequestCode++
        listener?.let {
            permissionsListeners?.set(requestCode, listener)
        }
        if (shouldShowRequestPermissionRationale(permission)) {
            AlertDialog.Builder(this)
                    .setTitle(rationaleTitleResourceId)
                    .setMessage(rationaleMessageResourceId)
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which -> requestPermissions(arrayOf(permission), requestCode) })
                    .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                        // ignore and clean up the listener
                        permissionsListeners?.remove(requestCode)
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        } else {
            requestPermissions(arrayOf(permission), requestCode)
        }
    }

    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int,
                                            @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        val permissionsListener = permissionsListeners?.remove(requestCode)
        if (permissionsListener != null
                && grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionsListener.onComplete()
        }
        when (requestCode) {
            0 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                }

                return
            }
        }
    }

    fun checkAndRequestLocationPermissionApp(requestCode: Int): Boolean {
        val ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        val ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        val WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val RECEIVE_SMS = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECEIVE_SMS)
        val GET_ACCOUNTS = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.GET_ACCOUNTS)
        val READ_PHONE_STATE = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_PHONE_STATE)
        val RECORD_AUDIO = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        val READ_SMS = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_SMS)
        val SEND_SMS = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.SEND_SMS)
        val READ_CONTACTS = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CONTACTS)
        val READ_CALL_LOG = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CALL_LOG)
        val WRITE_CALL_LOG = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_CALL_LOG)

        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED
                || ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED
                || WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED
                || RECEIVE_SMS != PackageManager.PERMISSION_GRANTED
                || GET_ACCOUNTS != PackageManager.PERMISSION_GRANTED
                || READ_PHONE_STATE != PackageManager.PERMISSION_GRANTED
                || RECORD_AUDIO != PackageManager.PERMISSION_GRANTED
                || READ_SMS != PackageManager.PERMISSION_GRANTED
                || SEND_SMS != PackageManager.PERMISSION_GRANTED
                || READ_CONTACTS != PackageManager.PERMISSION_GRANTED
                || READ_CALL_LOG != PackageManager.PERMISSION_GRANTED
                || WRITE_CALL_LOG != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_CONTACTS), requestCode)
            return false
        }
        return true
    }
}
