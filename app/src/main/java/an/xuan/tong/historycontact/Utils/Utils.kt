package an.xuan.tong.historycontact.Utils

import android.media.MediaMetadataRetriever
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.IOException
import java.util.*


public class Utils {
    companion object {
        fun getLocalTime(): Long {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), Locale.getDefault())
            val currentLocalTime = calendar.time
            return currentLocalTime.time / 1000 + 7 * 60 * 60
        }

        fun timeOffset(): Long {
            return 7 * 60 * 60
        }

        fun sizeFolder(): Int {
            if (getDefaultPath().isNullOrBlank()) return -1
            Log.e("antx", "getDefaultPath() " + getDefaultPath())
            val dirStorage = getDefaultPath()
            val file = File(dirStorage)
            try {
                val list = file.listFiles()
                list?.let {
                    return list.size
                }
            } catch (e: IOException) {
                return -1
            }
            return -1
        }

        fun getFilePathNew(): String {
            val dirStorage = getDefaultPath()
            val file = File(dirStorage)
            try {
                val list = file.listFiles()
                list?.let {
                    return list[list.size - 1].toString()
                }

            } catch (e: IOException) {
                Log.v("List error:", "can't list$dirStorage")
                return ""
            }
            return ""
        }

        fun getDefaultPath(): String {
            return String.format("%s%s",
                    normalDir(Environment.getExternalStorageDirectory().getAbsolutePath()),
                    "ACRCalls/")
        }

        fun normalDir(dir: String): String? {
            var dir = dir
            if (TextUtils.isEmpty(dir)) {
                return dir
            }

            dir = dir.replace('\\', '/')
            if (dir.substring(dir.length - 1, dir.length) != "/") {
                dir += "/"
            }
            return dir
        }

        fun getDuration(file: File): String {
            try {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(file.absolutePath)
                val durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                return (durationStr.toLong() / 1000).toString()
            } catch (e: Exception) {
                Log.e("antx","getDuration: "+e.message)
                return "0"
            }
        }

        fun formateMilliSeccond(milliseconds: Long): String {
            var finalTimerString = ""
            var secondsString = ""

            // Convert total duration into time
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours.toString() + ":"
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0$seconds"
            } else {
                secondsString = "" + seconds
            }

            finalTimerString = "$finalTimerString$minutes:$secondsString"

            //      return  String.format("%02d Min, %02d Sec",
            //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
            //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

            // return timer string
            return finalTimerString
        }

    }
}