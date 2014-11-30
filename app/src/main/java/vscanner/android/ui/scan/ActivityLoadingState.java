package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.network.ParcelableNameValuePair;
import vscanner.android.network.ServerConstants;
import vscanner.android.network.ServersProductsParser;
import vscanner.android.network.http.HttpRequestResult;
import vscanner.android.ui.CardboardActivityBase;

// TODO: this state should has some timeout - this would protect us from infinite progress bar
class ActivityLoadingState extends ScanActivityState {
    private static final String REQUEST_URL = "http://lumeria.ru/vscaner/index.php";
    private static final String KEYS_START = ActivityLoadingState.class.getCanonicalName() + ".";
    private static final String REQUEST_ID = KEYS_START + "PRODUCT_REQUEST";
    private static final String BARCODE_EXTRA = KEYS_START + "BARCODE_EXTRA";
    private String barcode;
    private boolean isViewInitialized;

    /**
     * must be called only before onCreate() call
     */
    ActivityLoadingState(final ScanActivityState parent) {
        super(parent);
        App.assertCondition(App.getFrontActivity() != getActivity());
    }

    /**
     * @param barcode must be a valid barcode (ie BarcodeToolkit.isValid(barcode) == true)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    ActivityLoadingState(final ScanActivityState parent, final String barcode) {
        super(parent);
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode is not valid");
        }
        this.barcode = barcode;

        requestProductDataFromServer();
        if (App.getFrontActivity() == getActivity()) {
            initView();
        }
    }

    private void requestProductDataFromServer() {
        getActivity().sendHttpPostRequest(REQUEST_ID, REQUEST_URL, createPostParameters());
    }

    protected List<ParcelableNameValuePair> createPostParameters() {
        final List<ParcelableNameValuePair> postParameters = new ArrayList<ParcelableNameValuePair>();
        postParameters.add(new ParcelableNameValuePair("bcod", barcode));
        return postParameters;
    }

    private void initView() {
        final CardboardActivityBase activity = getActivity();

        activity.setNewScanButtonVisibility(View.GONE);
        activity.putToMiddleSlot(createProgressBar());
        activity.removeBottomButtons();

        final CowSaysFragment cowSaysFragment = new CowSaysFragment();
        cowSaysFragment.setCowMood(CowState.Mood.NEUTRAL);
        cowSaysFragment.setCowsText(activity.getString(R.string.scan_activity_product_data_loading));
        activity.putToTopSlot(cowSaysFragment);

        isViewInitialized = true;
    }

    private View createProgressBar() {
        final ProgressBar progressBar = new ProgressBar(getActivity());

        final RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        progressBar.setLayoutParams(params);

        return progressBar;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        barcode = savedInstanceState.getString(BARCODE_EXTRA);
        if (!BarcodeToolkit.isValid(barcode)) {
            App.assertCondition(false);
            requestStateChangeTo(new ActivityBeforeScanState(this));
            return;
        }

        if (!getActivity().getSupportLoaderManager().hasRunningLoaders()) {
            requestProductDataFromServer();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        App.assertCondition(false);
    }

    @Override
    public void onSaveStateData(final Bundle outState) {
        outState.putString(BARCODE_EXTRA, barcode);
        isViewInitialized = false;
    }

    @Override
    public void onResumeFragments() {
        if (!isViewInitialized) {
            initView();
        }
    }

    @Override
    public void onHttpPostResult(final HttpRequestResult resultHolder) {
        final HttpRequestResult.ResultType resultType = resultHolder.getResultType();

        if (resultType != HttpRequestResult.ResultType.SUCCESS) {
            onHttpPostFailed();
            if (resultType != HttpRequestResult.ResultType.NETWORK_ERROR) {
                App.error(
                        "an element of "
                                + HttpRequestResult.ResultType.class.getCanonicalName()
                                + " is not handled");
            }
            return;
        }

        final String serverResponse = resultHolder.getResponse();
        if (serverResponse.equals(ServerConstants.RESPONSE_NO_SUCH_PRODUCT)) {
            requestStateChangeTo(new ActivityProductNotFoundState(this, barcode));
        } else {
            final Product product;
            try {
                product = ServersProductsParser.parse(serverResponse, barcode);
            } catch (final ParseException e) {
                App.logInfo(this, "parsing of a product has failed", e);
                onHttpPostFailed();
                return;
            }

            requestStateChangeTo(new ActivityProductDescriptionState(this, product));
        }
    }

    private void onHttpPostFailed() {
        getActivity().showToastWith(
                R.string.scan_activity_product_downloading_error_message);
        App.logError(this, "a task failed at downloading a product");
        requestStateChangeTo(new ActivityBeforeScanState(this));
    }

    @Override
    public Restorer save() {
        return null;
    }
}
