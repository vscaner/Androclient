package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;

import vscanner.android.App;

final class FirstState extends ScanActivityState {
    public static final String STATE_NAME_EXTRA = "FirstState.STATE_NAME_EXTRA";

    protected FirstState(final ScanActivity scanActivity) {
        super(scanActivity);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        final ScanActivityState nextState;
        if (savedInstanceState == null) {
            nextState = new BeforeScanState(this);
        } else {
            final String nextStateClassName = savedInstanceState.getString(STATE_NAME_EXTRA);
            if (nextStateClassName == null) {
                App.assertCondition(false);
                nextState = new BeforeScanState(this);
            } else if (nextStateClassName.equals(BeforeScanState.class.toString())) {
                nextState = new BeforeScanState(this);
            } else if (nextStateClassName.equals(ProductDescriptionState.class.toString())) {
                nextState = new ProductDescriptionState(
                        this,
                        ProductDescriptionState.parseProductFrom(savedInstanceState));
            } else {
                App.assertCondition(false);
                nextState = new BeforeScanState(this);
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
