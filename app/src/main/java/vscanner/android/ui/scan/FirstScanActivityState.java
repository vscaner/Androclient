package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;

import vscanner.android.App;
import vscanner.android.ui.CardboardUI;

final class FirstScanActivityState extends ScanActivityState {
    public static final String STATE_NAME_EXTRA = "FirstScanActivityState.STATE_NAME_EXTRA";

    protected FirstScanActivityState(final ScanActivity scanActivity) {
        super(scanActivity);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        final ScanActivityState nextState;
        if (savedInstanceState == null) {
            nextState = new BeforeStartScanActivityState(this);
        } else {
            final String nextStateClassName = savedInstanceState.getString(STATE_NAME_EXTRA);
            if (nextStateClassName == null) {
                App.assertCondition(false);
                nextState = new BeforeStartScanActivityState(this);
            } else if (nextStateClassName.equals(BeforeStartScanActivityState.class.toString())) {
                nextState = new BeforeStartScanActivityState(this);
            } else if (nextStateClassName.equals(ProductDescriptionScanActivityState.class.toString())) {
                nextState = new ProductDescriptionScanActivityState(
                        this,
                        ProductDescriptionScanActivityState.parseProductFrom(savedInstanceState));
            } else {
                App.assertCondition(false);
                nextState = new BeforeStartScanActivityState(this);
            }
        }
        requestStateChangeTo(nextState);
        nextState.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        // This state is supposed only to switch to another state.
        App.assertCondition(false);
    }

    @Override
    public void onSaveStateData(final Bundle outState) {
        // This state is supposed only to switch to another state.
        App.assertCondition(false);
    }
}
