package net.avh4.trackitall.notification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;
import net.avh4.trackitall.model.Counter;
import net.avh4.trackitall.model.Store;

class CounterRemoteButtonController {
    private final String type;
    private final String label;
    private final int buttonId;
    private final RemoteViews views;

    CounterRemoteButtonController(RemoteViews views, final String type, int buttonId, String label) {
        this.type = type;
        this.label = label;
        this.buttonId = buttonId;
        this.views = views;
    }

    public void update(Context context) {
        views.setTextViewText(buttonId, label + " " + Store.getCount(context, type));
    }

    public static CounterRemoteButtonController attach(Counter counter, Context context, RemoteViews views, int INC_CODE) {
        final String type = counter.getType();
        int buttonId = counter.getButtonId();
        String label = context.getResources().getString(counter.getLabelId());
        views.setOnClickPendingIntent(buttonId, PendingIntent.getBroadcast(context, INC_CODE, new Intent(type), 0));
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Store.inc(context, type);
            }
        }, new IntentFilter(type));
        return new CounterRemoteButtonController(views, type, buttonId, label);
    }
}
