package vscanner.android.ui.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

import vscanner.android.App;
import vscanner.android.R;
import vscanner.android.network.http.HttpRequestResult;
import vscanner.android.ui.CardboardActivityBase;
import vscanner.android.ui.addition.ProductAdditionActivity;

public class ScanActivity extends CardboardActivityBase {
    public static final int MAX_STATES_RESTORERS_COUNT = 4;

    public static interface FirstStateCreator {
        ScanActivityState createFor(final ScanActivity activity);
    }

    private static FirstStateCreator firstStateCreator = new FirstStateCreator() {
        @Override
        public ScanActivityState createFor(final ScanActivity activity) {
            return new ActivityFirstState(activity);
        }
    };
    private final Deque<ScanActivityState.Restorer> stateRestorers =
            new ArrayDeque<ScanActivityState.Restorer>();
    private ScanActivityState state = firstStateCreator.createFor(this);


    public static void setFirstStateCreator(final FirstStateCreator firstStateCreator) {
        ScanActivity.firstStateCreator = firstStateCreator;
    }

    public static void startBy(final Context context) {
        final Intent intent = new Intent(context, ScanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public ScanActivityState getState() {
        return state;
    }

    public void onStateRequestsChangeTo(final ScanActivityState otherState) {
        App.assertCondition(otherState != null, "requested to change state to null");

        final ScanActivityState.Restorer newRestorer = this.state.save();

        if (newRestorer != null) {
            stateRestorers.offerFirst(newRestorer);
            validateRestorersCount();
        }
        this.state = otherState;
    }

    private void validateRestorersCount() {
        if (stateRestorers.size() > MAX_STATES_RESTORERS_COUNT) {
            final ScanActivityState.Restorer lastRestorer = stateRestorers.pollLast();
            if (lastRestorer.doesStayLast()) {
                stateRestorers.pollLast();
                stateRestorers.addLast(lastRestorer);
            }
        }
    }

    public Deque<ScanActivityState.Restorer> getRestorersCopy() {
        return new ArrayDeque<ScanActivityState.Restorer>(stateRestorers);
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

    @Override
    public void onBackPressed() {
        if (!stateRestorers.isEmpty()) {
            tryCollapsingLastRestorers();

            final ScanActivityState.Restorer restorer = stateRestorers.pollFirst();
            state = restorer.restoreFor(this);

            App.assertCondition(
                    state != null,
                    "current "
                            + ScanActivityState.class.getCanonicalName()
                            + " is null! Something horrible will happen soon!");
        } else {
            super.onBackPressed();
        }
    }

    private void tryCollapsingLastRestorers() {
        if (stateRestorers.size() == 2) {
            final ScanActivityState.Restorer beforeLastRestorer = stateRestorers.pollFirst();
            final ScanActivityState.Restorer lastRestorer = stateRestorers.pollFirst();

            if (lastRestorer.doesStayLast()
                    && beforeLastRestorer.doesStayLast()
                    && lastRestorer.getClass() == beforeLastRestorer.getClass()) {
                stateRestorers.addFirst(beforeLastRestorer);
            } else {
                stateRestorers.addFirst(lastRestorer);
                stateRestorers.addFirst(beforeLastRestorer);
            }
        }
    }
}
