package vscanner.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.R;
import vscanner.android.network.ParcelableNameValuePair;
import vscanner.android.network.http.HttpRequestResult;
import vscanner.android.ui.scan.ScanActivity;

/**
 * Need a String with BARCODE_EXTRA to be provided in a starting intent.
 */
public abstract class BarcodeHttpActionActivity extends CardboardActivityBase {
    protected static final String BARCODE_EXTRA =
            BarcodeHttpActionActivity.class.getCanonicalName() + "barcodeExtra";
    private final String httpRequestId = getClass().getCanonicalName() + ".httpRequestId";

    private final String actionUrl;
    private final int requestSentToastStringId;
    private final int requestSuccessfullyDeliveredStringId;
    private final int titleStringId;
    private Runnable finalAction;

    private String barcode;

    private final View.OnClickListener onSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (App.isOnline()) {
                final Fragment middleFragment = getMiddleFragment();

                App.assertCondition(middleFragment != null);
                App.assertCondition(middleFragment instanceof BarcodeHttpActionFragment);

                if (middleFragment != null
                        && middleFragment instanceof BarcodeHttpActionFragment) {
                    final BarcodeHttpActionFragment reportFragment =
                            (BarcodeHttpActionFragment) middleFragment;
                    final String errorMessage = reportFragment.getErrorMessage();

                    if (errorMessage != null) {
                        showToastWith(errorMessage);
                    } else {
                        final Object actionResult = reportFragment.getResult();

                        sendHttpPostRequest(
                                httpRequestId,
                                actionUrl,
                                createPostParametersFor(actionResult));
                        showToastWith(requestSentToastStringId);
                        setProgressForegroundVisibility(View.VISIBLE);
                    }
                }
            } else {
                showToastWith(R.string.raw_internet_connection_is_not_available);
            }
        }
    };

    protected abstract List<ParcelableNameValuePair> createPostParametersFor(final Object actionResult);

    @Override
    protected final void onHttpPostResult(final HttpRequestResult resultHolder) {
        setProgressForegroundVisibility(View.GONE);

        if (resultHolder.getResultType() == HttpRequestResult.ResultType.SUCCESS) {
            showToastWith(requestSuccessfullyDeliveredStringId);
            if (finalAction != null) {
                finalAction.run();
            }
            finish();
        } else if (resultHolder.getResultType() == HttpRequestResult.ResultType.NETWORK_ERROR) {
            showToastWith(R.string.raw_error_occurred_during_sending_request_to_the_server);
        } else {
            App.error(
                    HttpRequestResult.ResultType.class.getCanonicalName()
                    + ": some element is not handled");
        }
    }

    /**
     * @param actionUrl must be a valid url, i.e. (URLUtil.isValidUrl(url) == true)
     * @throws java.lang.IllegalArgumentException if actionUrl is invalid
     */
    protected BarcodeHttpActionActivity(
            final String actionUrl,
            final int requestSentToastStringId,
            final int requestSuccessfullyDeliveredStringId,
            final int titleStringId) {
        if (!URLUtil.isValidUrl(actionUrl)) {
            throw new IllegalArgumentException("url must be valid");
        }
        this.actionUrl = actionUrl;
        this.requestSentToastStringId = requestSentToastStringId;
        this.requestSuccessfullyDeliveredStringId = requestSuccessfullyDeliveredStringId;
        this.titleStringId = titleStringId;
    }

    /**
     * @param finalAction will be called exactly before finish() call (on UI thread), may be null
     */
    protected final void setFinalAction(final Runnable finalAction) {
        this.finalAction = finalAction;
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        if (intent != null) {
            barcode = intent.getStringExtra(BARCODE_EXTRA);
            if (!BarcodeToolkit.isValid(barcode)) {
                App.error("need a valid barcode");
                finish();
                return;
            }
        } else {
            App.error("need a barcode");
            finish();
            return;
        }

        setWeights(0.75f, 2.00f, 0.75f);
        setNewScanButtonVisibility(View.INVISIBLE);

        if (savedInstanceState == null) {
            putToMiddleSlot(createFragment());
        }

        putToTopSlot(createTitle());
        addBottomButtonWith(
                R.string.raw_submit,
                onSubmitClickListener);
    }

    protected abstract BarcodeHttpActionFragment createFragment();

    private TextView createTitle() {
        final TextView titleTextView = new TextView(this);
        final RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(0, 0, 0,
                (int) getResources().getDimension(R.dimen.cardboard_activity_title_bottom_margin));
        titleTextView.setLayoutParams(params);
        titleTextView.setText(getString(titleStringId));
        titleTextView.setTextSize(UIConstants.MEDIUM_TEXT_SIZE);
        return titleTextView;
    }

    /**
     * @return valid barcode (i.e. BarcodeToolkit.isValid(..) == true), for which the Activity
     * was created
     */
    protected final String getBarcode() {
        return barcode;
    }
}
