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

        things.add(new Thing(this, "vegetables", R.id.btn_veg, R.string.type_vegetables_label));
        things.add(new Thing(this, "fruit", R.id.btn_fruit, R.string.type_fruit_label));
        things.add(new Thing(this, "water", R.id.btn_water, R.string.type_water_label));
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

    private static class Thing {
        private final String type;
        private Button button;
        private String label;

        public Thing(Activity activity, final String type, int buttonId, int labelId) {
            this.type = type;
            button = (Button) activity.findViewById(buttonId);
            this.label = activity.getResources().getString(labelId);
            button.setOnClickListener(new IncListener(type));
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Store.dec(v.getContext(), type);
                    return true;
                }
            });

        }

        public void update(Context context) {
            button.setText(label + " " + Store.getCount(context, type));
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
