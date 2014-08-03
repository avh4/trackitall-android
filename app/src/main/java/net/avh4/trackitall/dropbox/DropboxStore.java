package net.avh4.trackitall.dropbox;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxTable;

public class DropboxStore {
    private static DbxDatastore store;

    public static DbxTable getTable(String tableName) {
        return getStore().getTable(tableName);
    }

    public static DbxDatastore getStore() {
        if (store == null) {
            try {
                store = DbxDatastore.openDefault(DropboxActivity.account);
            } catch (DbxException e) {
                throw new RuntimeException(e);
            }
        }
        return store;
    }
}
