package vscanner.android.ui.scan;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import vscanner.android.App;
import vscanner.android.network.http.HttpRequestResult;
import vscanner.android.ui.CardboardActivityBase;

/**
 * The hierarchy is so complicated (i.e. many protected methods and no access to ScanActivity <br>
 * from classes-inheritors) only for requestStateChangeTo(), so it would be able to assert some <br>
 * conditions. <br>
 * Maybe it will lose any sense in the future.
 */
public abstract class ScanActivityState {
    private final ScanActivity scanActivity;

    protected final Resources getResources() {
        return scanActivity.getResources();
    }

    /**
     * @return not null
     */
    protected final CardboardActivityBase getActivity() {
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
     * NOTE: Never call it from a state constructor. If you need to change a state in its
     * constructor, use method requestAsyncStateChangeTo().
     * @throws java.lang.IllegalArgumentException if any argument is null
     */
    public final void requestStateChangeTo(final ScanActivityState otherState) {
        if (otherState == null) {
            throw new IllegalArgumentException("otherState must not be null");
        }
        App.assertCondition(scanActivity.getState() == this);
        scanActivity.onStateRequestsChangeTo(otherState);
        App.assertCondition(scanActivity.getState() == otherState);
    }

    /**
     * @throws java.lang.IllegalArgumentException if any argument is null
     */
    public final void requestAsyncStateChangeTo(final ScanActivityState otherState) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                requestStateChangeTo(otherState);
            }
        });
    }

    protected final boolean isCurrent() {
        return scanActivity.getState() == this;
    }

    public abstract void onCreate(final Bundle savedInstanceState);

    public abstract void onResumeFragments();

    public abstract void onActivityResult(final int requestCode, final int resultCode, final Intent intent);

    public final void onSaveInstanceState(final Bundle outState) {
        outState.putString(ActivityFirstState.STATE_NAME_EXTRA, this.getClass().toString());
        onSaveStateData(outState);
    }

    public abstract void onSaveStateData(final Bundle outState);

    public void onHttpPostResult(final HttpRequestResult resultHolder) {
    }

    public interface Restorer {
        /**
         * @return not null
         * @param activity must never be null
         * @throws java.lang.IllegalArgumentException if activity is null
         * @throws java.lang.IllegalStateException if called when the activity is NOT in front
         */
        ScanActivityState restoreFor(final ScanActivity activity);

        /**
         * @return Whether the Restorer should be kept as the last one when new Restorers are
         * being added to the stack and the stack reaches its size limit.<br>
         * I.e. if this method would return <b>false</b> when the Restorer is last in the stack
         * and a new Restorer is being added,
         * then it (the last one) is going to be removed. But if <b>true</b> would be returned, then the Restorer
         * is going to stay in the end of the stack <b>AND</b> the Restorer before the last one
         * is going to be removed.<br>
         * <b>NOTE</b> that this means that the first added to stack
         * Restorer with (.doesStayLast() == true) will stay in the stack until the activity is paused.<br>
         * <b>ALSO NOTE</b> that if there are just 2 Restorers left in a stack, if they both
         * have the same class and both want to stay last,
         * then <b>they both are going to be removed by one click of the
         * back button</b>.<br>
         * <i>wow, so many words about such a tiny method</i>
         */
        boolean doesStayLast();
    }

    /**
     * @return a Restorer, which would recreate the ScanActivityState with
     * condition it (the state) had on a moment of the call.<br>
     * If a ScanActivityState does not support saving, the method should return null.
     */
    public abstract Restorer save();
}
