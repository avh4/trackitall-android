package net.avh4.trackitall.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.dropbox.sync.android.DbxDatastore;
import net.avh4.trackitall.ActionBarActivityBase;
import net.avh4.trackitall.R;
import net.avh4.trackitall.dropbox.DropboxStore;
import net.avh4.trackitall.model.Counter;
import net.avh4.trackitall.model.Counters;
import net.avh4.trackitall.notification.NotificationBarService;

import java.util.HashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivityBase implements DbxDatastore.SyncStatusListener {
    private Set<CounterButtonController> counterButtonControllers = new HashSet<CounterButtonController>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, NotificationBarService.class));

        for (Counter counter : Counters.ALL) {
            counterButtonControllers.add(CounterButtonController.attach(counter, this));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DropboxStore.getStore().addSyncStatusListener(this);
        onDatastoreStatusChange(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        DropboxStore.getStore().removeSyncStatusListener(this);
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore store) {
        for (CounterButtonController counterButtonController : counterButtonControllers) {
            counterButtonController.update(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
