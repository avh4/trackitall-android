package net.avh4.trackitall.app;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import net.avh4.trackitall.model.Counter;
import net.avh4.trackitall.model.Store;

public class CounterButtonController {
    private final Counter counter;
    private final Button button;
    private final String label;

    public CounterButtonController(Counter counter, Button button, String label) {
        this.counter = counter;
        this.button = button;
        this.label = label;
    }

    public static CounterButtonController attach(final Counter counter, Activity activity) {
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
        return new CounterButtonController(counter, button, label);
    }

    public void update(Context context) {
        button.setText(label + " " + Store.getCount(context, counter.getType()));
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
