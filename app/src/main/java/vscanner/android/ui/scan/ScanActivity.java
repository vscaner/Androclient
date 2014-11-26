package vscanner.android.ui.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import vscanner.android.App;
import vscanner.android.R;
import vscanner.android.network.http.HttpRequestResult;
import vscanner.android.ui.CardboardActivityBase;
import vscanner.android.ui.addition.ProductAdditionActivity;

// TODO: handle back button?
public class ScanActivity extends CardboardActivityBase {
    private ScanActivityState state = new ActivityFirstState(this);

    public ScanActivityState getState() {
        return state;
    }

    public void onStateRequestsChangeTo(final ScanActivityState otherState) {
        this.state = otherState;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        state.onCreate(savedInstanceState);

        findViewById(R.id.button_new_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (App.isOnline()) {
                    state.requestStateChangeTo(new ActivityNewScanState(state, false));
                } else {
                    showToastWith(R.string.raw_internet_connection_is_not_available);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        state.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        state.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        state.onResumeFragments();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        state.requestStateChangeTo(new ActivityBeforeScanState(state));
    }

    @Override
    protected void onHttpPostResult(final HttpRequestResult resultHolder) {
        super.onHttpPostResult(resultHolder);
        state.onHttpPostResult(resultHolder);
    }


    public static void startBy(final Context context) {
        final Intent intent = new Intent(context, ScanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
