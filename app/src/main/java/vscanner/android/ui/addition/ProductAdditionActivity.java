package vscanner.android.ui.addition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.network.ParcelableNameValuePair;
import vscanner.android.network.http.HttpRequestResult;
import vscanner.android.ui.CardboardActivityBase;
import vscanner.android.ui.UIConstants;

public class ProductAdditionActivity extends CardboardActivityBase {
    private static final String ADDITION_URL = "http://lumeria.ru/vscaner/tobase.php";

    private static final String KEYS_START = ProductAdditionActivity.class.getCanonicalName() + ".";
    private static final String BARCODE_EXTRA = KEYS_START + "BARCODE_EXTRA";
    private static final String ADDITION_HTTP_REQUEST_ID = KEYS_START + "ADDITION_HTTP_REQUEST_ID";

    private final View.OnClickListener onSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (App.isOnline()) {
                final Fragment middleFragment = getMiddleFragment();

                App.assertCondition(middleFragment != null);
                App.assertCondition(middleFragment instanceof ProductAdditionFragment);
                if (middleFragment != null
                        && middleFragment instanceof ProductAdditionFragment) {
                    final ProductAdditionFragment additionFragment =
                            (ProductAdditionFragment) middleFragment;
                    final String errorMessage = additionFragment.getAdditionErrorMessage();

                    if (errorMessage != null) {
                        showToastWith(errorMessage);
                    } else {
                        final Product product = additionFragment.getProduct();
                        App.assertCondition(product != null);

                        sendHttpPostRequest(
                                ADDITION_HTTP_REQUEST_ID,
                                ADDITION_URL,
                                createPostParametersFor(product));
                        showToastWith(R.string.product_addition_activity_submit_request_sent_toast);
                    }
                }
            } else {
                showToastWith(R.string.raw_internet_connection_is_not_available);
            }
        }
    };

    private List<ParcelableNameValuePair> createPostParametersFor(final Product product) {
        final List<ParcelableNameValuePair> postParameters = new ArrayList<ParcelableNameValuePair>();

        postParameters.add(new ParcelableNameValuePair("bcod", product.getBarcode()));
        postParameters.add(new ParcelableNameValuePair("name", product.getName()));
        postParameters.add(new ParcelableNameValuePair("companyname", product.getCompany()));

        postParameters.addAll(getLogicalInversedParametersFor(product));

        postParameters.add(new ParcelableNameValuePair("gmo", "0"));
        postParameters.add(
                new ParcelableNameValuePair(
                        "animals",
                        String.valueOf(product.wasTestedOnAnimals())));
        postParameters.add(new ParcelableNameValuePair("comment", "")); // TODO: empty comment? Maybe not pass it at all?

        return postParameters;
    }

    private List<ParcelableNameValuePair> getLogicalInversedParametersFor(final Product product) {
        final List<ParcelableNameValuePair> inversedPostParameters = new ArrayList<ParcelableNameValuePair>();

        // NOTE: Logical inversion with the 2 below parameters is intentional,
        // because server mistakenly uses negative naming convention for these 2.
        // (Like that: isNotVegan instead of isVegan.)
        inversedPostParameters.add(new ParcelableNameValuePair(
                "veganstatus",
                String.valueOf(!product.isVegan())));
        inversedPostParameters.add(new ParcelableNameValuePair(
                "vegetstatus",
                String.valueOf(!product.isVegetarian())));

        return inversedPostParameters;
    }

    @Override
    protected final void onHttpPostResult(final HttpRequestResult resultHolder) {
        if (resultHolder.getResultType() == HttpRequestResult.ResultType.SUCCESS) {
            showToastWith(R.string.product_addition_activity_on_request_successfully_delivered);
        } else if (resultHolder.getResultType() == HttpRequestResult.ResultType.NETWORK_ERROR) {
            showToastWith(R.string.product_addition_activity_on_request_failed);
        } else {
            App.error(HttpRequestResult.ResultType.class.getCanonicalName()
                    + ": some element is not handled");
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final String barcode;
        if (intent != null) {
            barcode = intent.getStringExtra(BARCODE_EXTRA);
            if (!BarcodeToolkit.isValid(barcode)) {
                App.error("can't add a product without a valid barcode");
                finish();
                return;
            }
        } else {
            App.error("can't add a product without a barcode");
            finish();
            return;
        }

        setWeights(0.75f, 2.00f, 0.75f);
        setNewScanButtonVisibility(View.INVISIBLE);

        if (savedInstanceState == null) {
            putToMiddleSlot(ProductAdditionFragment.createFor(barcode));
        }

        putToTopSlot(createTitle());
        addBottomButtonWith(
                R.string.raw_submit,
                onSubmitClickListener);

    }

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
        titleTextView.setText(getString(R.string.product_addition_activity_title));
        titleTextView.setTextSize(UIConstants.MEDIUM_TEXT_SIZE);
        return titleTextView;
    }

    /**
     * @param barcode must be valid (i.e. BarcodeToolkit.isValid(barcode)==true)
     * @param context must not be null
     * @throws java.lang.IllegalArgumentException if any parameter is invalid
     */
    public static void startFor(final String barcode, final Context context) {
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode must be valid");
        } else if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        final Intent intent = new Intent(context, ProductAdditionActivity.class);
        intent.putExtra(BARCODE_EXTRA, barcode);
        context.startActivity(intent);
    }
}
