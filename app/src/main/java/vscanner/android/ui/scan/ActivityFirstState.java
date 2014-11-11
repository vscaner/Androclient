package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;

import vscanner.android.App;

final class ActivityFirstState extends ScanActivityState {
    public static final String STATE_NAME_EXTRA = "FirstState.STATE_NAME_EXTRA";

    protected ActivityFirstState(final ScanActivity scanActivity) {
        super(scanActivity);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        final ScanActivityState nextState;
        if (savedInstanceState == null) {
            nextState = new ActivityBeforeScanState(this);
        } else {
            final String nextStateClassName = savedInstanceState.getString(STATE_NAME_EXTRA);
            if (nextStateClassName == null) {
                App.assertCondition(false);
                nextState = new ActivityBeforeScanState(this);
            } else if (nextStateClassName.equals(ActivityBeforeScanState.class.toString())) {
                nextState = new ActivityBeforeScanState(this);
            } else if (nextStateClassName.equals(ActivityProductDescriptionState.class.toString())) {
                nextState = new ActivityProductDescriptionState(
                        this,
                        ActivityProductDescriptionState.parseProductFrom(savedInstanceState));
            } else if (nextStateClassName.equals(ActivityLoadingState.class.toString())) {
                nextState = new ActivityLoadingState(this);
            } else if (nextStateClassName.equals(ActivityNewScanState.class.toString())) {
                nextState = new ActivityNewScanState(this, true);
            } else if (nextStateClassName.equals(ActivityProductNotFoundState.class.toString())) {
                nextState = new ActivityProductNotFoundState(this);
            } else {
                App.assertCondition(false);
                nextState = new ActivityBeforeScanState(this);
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

    @Override
    public void onResumeFragments() {
        // This state is supposed only to switch to another state.
        App.assertCondition(false);
    }
}
