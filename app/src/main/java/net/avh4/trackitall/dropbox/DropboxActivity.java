package net.avh4.trackitall.dropbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.crashlytics.android.Crashlytics;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.segment.android.Analytics;
import com.segment.android.models.Traits;
import net.avh4.trackitall.ActivityBase;
import net.avh4.trackitall.Credentials;
import net.avh4.trackitall.R;
import net.avh4.trackitall.app.MainActivity;
import org.json.JSONException;

public class DropboxActivity extends ActivityBase {
    private static final int REQUEST_LINK_TO_DBX = 0;
    private DbxAccountManager mAccountManager;
    private static final String APP_KEY = "qbgqkez2zubgho2";
    public static DbxAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_dropbox);

        mAccountManager = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, Credentials.Dropbox.APP_SECRET);
        mAccountManager.addListener(new DbxAccountManager.AccountListener() {
            @Override
            public void onLinkedAccountChange(DbxAccountManager dbxAccountManager, DbxAccount dbxAccount) {
                mAccountManager.removeListener(this);
                System.out.println(dbxAccount.getAccountInfo());
                dbxAccount.addListener(new DbxAccount.Listener() {
                    @Override
                    public void onAccountChange(DbxAccount dbxAccount) {
                        System.out.println(dbxAccount.getAccountInfo());
                        dbxAccount.removeListener(this);
                        setAccount(dbxAccount);
                        startMain();
                    }
                });
            }
        });

        final Button mLinkButton = (Button) findViewById(R.id.link_button);
        mLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinkButton.setVisibility(View.GONE);
                mAccountManager.startLink(DropboxActivity.this, REQUEST_LINK_TO_DBX);
            }
        });

        if (mAccountManager.hasLinkedAccount()) {
            setAccount(mAccountManager.getLinkedAccount());
            mLinkButton.setVisibility(View.GONE);

            startMain();
        } else {
            mLinkButton.setVisibility(View.VISIBLE);
        }
    }

    private void setAccount(DbxAccount linkedAccount) {
        account = linkedAccount;
        Traits traits = new Traits();
        try {
            traits = new Traits(traits
                    .put("name", account.getAccountInfo().displayName)
                    .put("username", account.getAccountInfo().userName)
                    .put("orgName", account.getAccountInfo().orgName));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Analytics.alias(Analytics.getAnonymousId(), account.getUserId());
        Analytics.identify(account.getUserId(), traits);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
//                setAccount(mAccountManager.getLinkedAccount());
//                startMain();
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
        finish();
    }
}
