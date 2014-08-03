package net.avh4.trackitall.model;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CsvWriter {
    private static final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static String write(DbxTable records) {
        StringBuilder sb = new StringBuilder();
        try {
            List<DbxRecord> list = records.query().asList();
            Collections.sort(list, new Comparator<DbxRecord>() {
                @Override
                public int compare(DbxRecord lhs, DbxRecord rhs) {
                    return lhs.getDate("timestamp").compareTo(rhs.getDate("timestamp"));
                }
            });

            sb.append("timestamp");
            sb.append(",");
            sb.append("type");
            sb.append("\n");

            for (DbxRecord record : list) {
                sb.append(isoFormat.format(record.getDate("timestamp")));
                sb.append(",");
                sb.append(record.getString("type"));
                sb.append("\n");
            }
        } catch (DbxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
