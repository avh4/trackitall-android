package net.avh4.trackitall;

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

import java.util.HashSet;
import java.util.Set;

public class MyService extends Service {
    private static final int INC_CODE = 1000;
    private static final int NID = 1;
    private Set<Thing> things = new HashSet<Thing>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.bar);

        things.add(new Thing(this, views, "vegetables", R.id.btn_veg, R.string.type_vegetables_label));
        things.add(new Thing(this, views, "fruit", R.id.btn_fruit, R.string.type_fruit_label));
        things.add(new Thing(this, views, "water", R.id.btn_water, R.string.type_water_label));

        final NotificationManager mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContent(views)
                .build();

        DropboxStore.getStore().addSyncStatusListener(new DbxDatastore.SyncStatusListener() {
            @Override
            public void onDatastoreStatusChange(DbxDatastore dbxDatastore) {
                for (Thing thing : things) {
                    thing.update(MyService.this);
                }
                mManager.notify(NID, notification);
            }
        });

        startForeground(NID, notification);
        return START_STICKY;
    }

    private static class Thing {
        private final String type;
        private final String label;
        private final int buttonId;
        private final RemoteViews views;

        public Thing(Context context, RemoteViews views, final String type, int buttonId, int labelId) {
            this.type = type;
            this.label = context.getResources().getString(labelId);
            this.buttonId = buttonId;
            this.views = views;
            views.setOnClickPendingIntent(buttonId, PendingIntent.getBroadcast(context, INC_CODE, new Intent(type), 0));
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Store.inc(context, type);
                }
            }, new IntentFilter(type));
        }

        public void update(Context context) {
            views.setTextViewText(buttonId, label + " " + Store.getCount(context, type));
        }
    }
}
