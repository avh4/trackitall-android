package net.avh4.trackitall.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.dropbox.sync.android.DbxDatastore;
import net.avh4.trackitall.model.Counter;
import net.avh4.trackitall.model.Counters;
import net.avh4.trackitall.dropbox.DropboxStore;
import net.avh4.trackitall.R;

import java.util.HashSet;
import java.util.Set;

public class NotificationBarService extends Service {
    private static final int INC_CODE = 1000;
    private static final int NID = 1;
    private Set<CounterRemoteButtonController> remoteButtonControllers = new HashSet<CounterRemoteButtonController>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.remote_notification_bar);

        for (Counter counter : Counters.ALL) {
            remoteButtonControllers.add(CounterRemoteButtonController.attach(counter, this, views, INC_CODE));
        }

        final NotificationManager mManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContent(views)
                .build();

        DropboxStore.getStore().addSyncStatusListener(new DbxDatastore.SyncStatusListener() {
            @Override
            public void onDatastoreStatusChange(DbxDatastore dbxDatastore) {
                for (CounterRemoteButtonController counterRemoteButtonController : remoteButtonControllers) {
                    counterRemoteButtonController.update(NotificationBarService.this);
                }
                mManager.notify(NID, notification);
            }
        });

        startForeground(NID, notification);
        return START_STICKY;
    }

}
