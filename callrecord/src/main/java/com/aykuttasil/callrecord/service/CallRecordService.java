package com.aykuttasil.callrecord.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.aykuttasil.callrecord.CallRecord;
import com.aykuttasil.callrecord.R;
import com.aykuttasil.callrecord.helper.PrefsHelper;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

/**
 * Created by aykutasil on 19.10.2016.
 */

public class CallRecordService extends Service {
    private static final String TAG = CallRecordService.class.getSimpleName();
    protected CallRecord mCallRecord;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG + "antx", "onCreate()");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(99, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flas, int startId) {
        Log.e(TAG + "antx", "onStartCommand()");

        String file_name = PrefsHelper.readPrefString(this, CallRecord.PREF_FILE_NAME);
        String dir_path = PrefsHelper.readPrefString(this, CallRecord.PREF_DIR_PATH);
        String dir_name = PrefsHelper.readPrefString(this, CallRecord.PREF_DIR_NAME);
        boolean show_seed = PrefsHelper.readPrefBool(this, CallRecord.PREF_SHOW_SEED);
        boolean show_phone_number = PrefsHelper.readPrefBool(this, CallRecord.PREF_SHOW_PHONE_NUMBER);
        int output_format = PrefsHelper.readPrefInt(this, CallRecord.PREF_OUTPUT_FORMAT);
        int audio_source = PrefsHelper.readPrefInt(this, CallRecord.PREF_AUDIO_SOURCE);
        int audio_encoder = PrefsHelper.readPrefInt(this, CallRecord.PREF_AUDIO_ENCODER);

        mCallRecord = new CallRecord.Builder(this)
                .setRecordFileName(file_name)
                .setRecordDirName(dir_name)
                .setRecordDirPath(dir_path)
                .setAudioEncoder(audio_encoder)
                .setAudioSource(audio_source)
                .setOutputFormat(output_format)
                .setShowSeed(show_seed)
                .setShowPhoneNumber(show_phone_number)
                .build();
        mCallRecord.startCallReceiver();
        return START_REDELIVER_INTENT;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallRecord.stopCallReceiver();
        stopForeground(true);
        Log.e(TAG + "antx", "onDestroy()");
    }
}
