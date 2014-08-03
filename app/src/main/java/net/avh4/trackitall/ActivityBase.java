package net.avh4.trackitall;

import com.segment.android.Analytics;
import com.segment.android.TrackedActivity;

public class ActivityBase extends TrackedActivity {

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.screen(getLocalClassName());
    }
}
