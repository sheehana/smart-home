package ie.sheehan.smarthome.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ie.sheehan.smarthome.IntrusionViewActivity;
import ie.sheehan.smarthome.MainActivity;
import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

public class IntrusionService extends Service {

    Timer timer;
    TimerTask task;
    String lastIntrusionId = "";

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();

        task = new TimerTask() {
            @Override
            public void run() {
                new CheckForIntrusions().execute();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.schedule(task, 0, 5000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public void sendNotification(IntrusionReading intrusionReading) {
        Notification.Builder notification = new Notification.Builder(IntrusionService.this);
        notification.setSmallIcon(R.drawable.ic_tab_security);
        notification.setContentTitle("BREAK IN");
        notification.setContentText("Break in has been detected");
        notification.setVibrate(new long[]{1000, 1000, 1000});
        notification.setAutoCancel(true);

        Intent intent = new Intent(IntrusionService.this, IntrusionViewActivity.class);
        Bundle arguments = new Bundle();
        arguments.putSerializable("intrusion", intrusionReading);
        intent.putExtras(arguments);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(IntrusionService.this);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification.build());
    }


    private class CheckForIntrusions extends AsyncTask<Void, Void, IntrusionReading> {
        @Override
        protected IntrusionReading doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getLatestIntrusionReading();
        }

        @Override
        protected void onPostExecute(IntrusionReading intrusionReading) {
            super.onPostExecute(intrusionReading);

            if (intrusionReading == null) { return; }

            if (! intrusionReading.viewed) {
                if (! intrusionReading.id.equals(lastIntrusionId)) {
                    lastIntrusionId = intrusionReading.id;
                    sendNotification(intrusionReading);
                }
            }
        }
    }

}
