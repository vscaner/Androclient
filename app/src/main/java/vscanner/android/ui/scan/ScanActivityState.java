package vscanner.android.ui.scan;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import vscanner.android.App;
import vscanner.android.ui.CardboardUI;
import vscanner.android.ui.MyActivityBase;

/**
 * The hierarchy is so complicated (i.e. many protected methods and no access to ScanActivity <br>
 * from classes-inheritors) only for requestStateChangeTo(), so it would be able to assert some <br>
 * conditions. <br>
 * Maybe it will lose any sense in the future.
 */
abstract class ScanActivityState {
    public static interface Listener {
        void onStateRequestsChangeTo(ScanActivityState otherState);
    }

    private final ScanActivity scanActivity;

    /**
     * @return not null
     */
    protected final CardboardUI getCardboardUI() {
        return scanActivity;
    }

    protected final Resources getResources() {
        return scanActivity.getResources();
    }

    protected final MyActivityBase getActivity() {
        return scanActivity;
    }

    /**
     * @param scanActivity must be not null
     * @throws java.lang.IllegalArgumentException if any argument is null
     */
    protected ScanActivityState(final ScanActivity scanActivity) {
        if (scanActivity == null) {
            throw new IllegalArgumentException("state can't work without a ScanActivity");
        }
        this.scanActivity = scanActivity;
    }

    /**
     * @param parent must not be null
     */
    protected ScanActivityState(final ScanActivityState parent) {
        this(parent.scanActivity);
    }

    /**
     * @throws java.lang.IllegalArgumentException if any argument is null
     */
    protected final void requestStateChangeTo(final ScanActivityState otherState) {
        if (otherState == null) {
            throw new IllegalArgumentException("otherState must not be null");
        }
        App.assertCondition(scanActivity.getState() == this);
        scanActivity.onStateRequestsChangeTo(otherState);
        App.assertCondition(scanActivity.getState() == otherState);
    }

    public abstract void onCreate(final Bundle savedInstanceState);

    public abstract void onActivityResult(final int requestCode, final int resultCode, final Intent intent);

    public final void onSaveInstanceState(final Bundle outState) {
        outState.putString(FirstScanActivityState.STATE_NAME_EXTRA, this.getClass().toString());
        onSaveStateData(outState);
    }

    public abstract void onSaveStateData(final Bundle outState);
}
