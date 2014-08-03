package org.buildsomethingawesome.myapplication.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.dropbox.sync.android.DbxDatastore;

public class MyService extends Service {
    private static final int VEG_CODE = 1000;
    private static final int NID = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.bar);
        views.setOnClickPendingIntent(R.id.btn_veg, PendingIntent.getBroadcast(this, VEG_CODE, new Intent("VegEvent"), 0));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Store.inc(MyService.this, "vegetables");
            }
        }, new IntentFilter("VegEvent"));

        final NotificationManager mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContent(views)
                .build();


        DropboxStore.getStore().addSyncStatusListener(new DbxDatastore.SyncStatusListener() {
            @Override
            public void onDatastoreStatusChange(DbxDatastore dbxDatastore) {
                views.setTextViewText(R.id.btn_veg, "Veg " + Store.getCount(MyService.this, "vegetables"));
                mManager.notify(NID, notification);
            }
        });

        startForeground(NID, notification);
        return START_STICKY;
    }
}
