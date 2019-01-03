package an.xuan.tong.historycontact.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.util.Log
import java.util.ArrayList

class UtilsInfomationPhone() {
    companion object {
        private fun getAllSMSDetails(context: Context): List<String> {
            var sms = ArrayList<String>()
            var uriSMSURI = Uri.parse("content://sms/inbox")
            var cur = context.contentResolver.query(uriSMSURI, null, null, null, null)
            while (cur != null && cur.moveToNext()) {
                var address = cur.getString(cur.getColumnIndex("address"))
                var body = cur.getString(cur.getColumnIndexOrThrow("body"))
                sms.add("Number: $address .Message: $body")
                Log.e("sms: ", "" + sms.toString())
            }
            cur?.close()
            return sms
        }

        @SuppressLint("MissingPermission")
        private fun getAllCallDetails(context: Context): String {
            var stringBuffer = StringBuffer()
            var cursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI,
                    null, null, null, CallLog.Calls.DATE + " DESC")
            var number = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            var type = cursor.getColumnIndex(CallLog.Calls.TYPE)
            var date = cursor.getColumnIndex(CallLog.Calls.DATE)
            var duration = cursor.getColumnIndex(CallLog.Calls.DURATION)
            while (cursor.moveToNext()) {
                var phNumber = cursor.getString(number)
                var callType = cursor.getString(type)
                var callDate = cursor.getString(date)
                var callDayTime = callDate
                var callDuration = cursor.getString(duration)
                var dir: String? = null
                var dircode = Integer.parseInt(callType)
                when (dircode) {
                    CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> dir = "MISSED"

                }
                stringBuffer.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                        + dir + " \nCall Date:--- " + callDayTime
                        + " \nCall duration in sec :--- " + callDuration)
                stringBuffer.append("\n----------------------------------")
            }
            cursor.close()
            Log.e("allCall: ", stringBuffer.toString())
            return stringBuffer.toString()
        }
    }
}