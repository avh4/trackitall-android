package net.avh4.trackitall.model;

import android.content.Context;
import android.widget.Toast;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import net.avh4.trackitall.dropbox.DropboxStore;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Store {
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM");
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd");

    public static Integer getCount(Context context, String type) {
        Date now = new Date();
        try {
            DbxFields query = new DbxFields()
                    .set("type", type)
                    .set("year", YEAR_FORMAT.format(now))
                    .set("month", MONTH_FORMAT.format(now))
                    .set("day", DAY_FORMAT.format(now));
            return DropboxStore.getTable("records").query(query).count();
        } catch (DbxException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error querying to Dropbox", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static void inc(Context context, String type) {
        Date now = new Date();
        DropboxStore.getTable("records").insert()
                .set("type", type)
                .set("timestamp", now)
                .set("year", YEAR_FORMAT.format(now))
                .set("month", MONTH_FORMAT.format(now))
                .set("day", DAY_FORMAT.format(now));
        try {
            DropboxStore.getStore().sync();
        } catch (DbxException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error syncing to Dropbox", Toast.LENGTH_LONG).show();
        }
    }

    public static void dec(Context context, String type) {
        Date now = new Date();
        DbxFields query = new DbxFields()
                .set("type", type)
                .set("year", YEAR_FORMAT.format(now))
                .set("month", MONTH_FORMAT.format(now))
                .set("day", DAY_FORMAT.format(now));


        try {
            List<DbxRecord> list = DropboxStore.getTable("records").query(query).asList();
            if (list.size() == 0) {
                Toast.makeText(context, "None left", Toast.LENGTH_LONG).show();
                return;
            }

            Collections.sort(list, new Comparator<DbxRecord>() {
                @Override
                public int compare(DbxRecord lhs, DbxRecord rhs) {
                    return rhs.getDate("timestamp").compareTo(lhs.getDate("timestamp"));
                }
            });
            list.get(0).deleteRecord();
        } catch (DbxException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error deleting from Dropbox", Toast.LENGTH_LONG).show();
        }

        try {
            DropboxStore.getStore().sync();
        } catch (DbxException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error syncing to Dropbox", Toast.LENGTH_LONG).show();
        }
    }
}
