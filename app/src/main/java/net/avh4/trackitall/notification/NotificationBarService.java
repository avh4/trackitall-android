package net.avh4.trackitall.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.dropbox.sync.android.DbxDatastore;
import com.segment.android.Analytics;
import net.avh4.trackitall.model.Counter;
import net.avh4.trackitall.model.Counters;
import net.avh4.trackitall.dropbox.DropboxStore;
import net.avh4.trackitall.R;

import java.util.HashSet;
import java.util.Set;

public class NotificationBarService extends Service implements DbxDatastore.SyncStatusListener {
    private static final int INC_CODE = 1000;
    private static final int NID = 1;
    private Set<CounterRemoteButtonController> remoteButtonControllers = new HashSet<CounterRemoteButtonController>();
    private Notification notification;
    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.remote_notification_bar);

        for (Counter counter : Counters.ALL) {
            remoteButtonControllers.add(CounterRemoteButtonController.attach(counter, this, views, INC_CODE));
        }

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContent(views)
                .setPriority(Notification.PRIORITY_MIN)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();

        DropboxStore.getStore().addSyncStatusListener(this);
        this.onDatastoreStatusChange(null);

        Analytics.track("service:created");
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {
        for (CounterRemoteButtonController counterRemoteButtonController : remoteButtonControllers) {
            counterRemoteButtonController.update(NotificationBarService.this);
        }
        notificationManager.notify(NID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NID, notification);
        Analytics.track("service:start");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Analytics.track("service:destroy");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Analytics.track("service:low_memory");
        super.onLowMemory();
    }
}
