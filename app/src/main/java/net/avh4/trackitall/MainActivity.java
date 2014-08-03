package net.avh4.trackitall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.dropbox.sync.android.DbxDatastore;

import java.util.HashSet;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements DbxDatastore.SyncStatusListener {
    private Set<Thing> things = new HashSet<Thing>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, MyService.class));

        for (Counter counter : Counters.ALL) {
            things.add(Thing.attach(counter, this));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DropboxStore.getStore().addSyncStatusListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        DropboxStore.getStore().removeSyncStatusListener(this);
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore store) {
        for (Thing thing : things) {
            thing.update(this);
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

    public static class Thing {
        private final Counter counter;
        private final Button button;
        private final String label;

        public Thing(Counter counter, Button button, String label) {
            this.counter = counter;
            this.button = button;
            this.label = label;
        }

        public static Thing attach(final Counter counter, Activity activity) {
            String label = activity.getResources().getString(counter.getLabelId());
            Button button = (Button) activity.findViewById(counter.getButtonId());
            button.setOnClickListener(new IncListener(counter.getType()));
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Store.dec(v.getContext(), counter.getType());
                    return true;
                }
            });
            return new Thing(counter, button, label);
        }

        public void update(Context context) {
            button.setText(label + " " + Store.getCount(context, counter.getType()));
        }
    }

    private static class IncListener implements View.OnClickListener {
        private final String type;

        private IncListener(String type) {
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            Store.inc(v.getContext(), type);
        }
    }
}
