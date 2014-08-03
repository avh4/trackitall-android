package org.buildsomethingawesome.myapplication.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;

public class DropboxActivity extends Activity {
    private static final int REQUEST_LINK_TO_DBX = 0;
    private DbxAccountManager mAccountManager;
    private static final String APP_KEY = "e43bpvhjnjtgvtg";
    private static final String APP_SECRET = "ip2m1xr0k2ch1if";
    public static DbxAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dropbox);

        mAccountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);

        Button mLinkButton = (Button) findViewById(R.id.link_button);
        mLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountManager.startLink(DropboxActivity.this, REQUEST_LINK_TO_DBX);
            }
        });

        if (mAccountManager.hasLinkedAccount()) {
            account = mAccountManager.getLinkedAccount();
            mLinkButton.setVisibility(View.GONE);

            startMain();
        } else {
            mLinkButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                account = mAccountManager.getLinkedAccount();
//                mLinkButton.setVisibility(View.GONE);
                startMain();
            } else {
                // ... Link failed or was cancelled by the user.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
