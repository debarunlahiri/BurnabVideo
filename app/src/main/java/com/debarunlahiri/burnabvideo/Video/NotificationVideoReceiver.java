package com.debarunlahiri.burnabvideo.Video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationVideoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("notificationVideoPause");
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
