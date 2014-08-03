package org.buildsomethingawesome.myapplication.app;

import android.content.Context;
import android.widget.Toast;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;

import java.util.Date;

public class Store {
    public static Integer getCount(Context context, String type) {
        try {
            DbxFields query = new DbxFields().set("type", type);
            return DropboxStore.getTable("records").query(query).count();
        } catch (DbxException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error querying to Dropbox", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static void inc(Context context, String type) {
        DropboxStore.getTable("records").insert().set("type", type).set("timestamp", new Date());
        try {
            DropboxStore.getStore().sync();
        } catch (DbxException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error syncing to Dropbox", Toast.LENGTH_LONG).show();
        }
    }
}
