package vscanner.android.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.HumanizingToolkit;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.network.ErrorReportingAsyncTask;
import vscanner.android.network.ProductLoaderResultHolder;
import vscanner.android.network.ProductLoadingAsyncTask;

public class ProductDescriptionActivity extends MyActivityBase {
    private static final String GENERAL_DIALOG_TAG = "dialog";
    private Product currentProduct;
    private boolean hasStarted;

    // TODO: ZOMFG! 3 listeners and 2 runnables! That's too much!
    // TODO: either create a error reporting Activity already or.. or clean it up somehow else!
    private String lastErrorReportText;
    private Runnable errorReportResender = new Runnable() {
        @Override
        public void run() {
            errorReportingDialogListener.onSendClicked(lastErrorReportText);
        }
    };
    private Runnable progressDialogHider = new Runnable() {
        @Override
        public void run() {
            hideProgressDialog();
        }
    };

    private final ErrorReportingAsyncTask.Listener errorReportingTaskListener =
            new ErrorReportingAsyncTask.Listener() {
                @Override
                public void onResult(final boolean success) {
                    if (success) {
                        hideProgressDialog();
                    } else if (errorReportResender != null) {
                        final InternetUnavailableDialogFragment dialog =
                                InternetUnavailableDialogFragment.create(
                                        errorReportResender,
                                        progressDialogHider);
                        dialog.show(getSupportFragmentManager(), GENERAL_DIALOG_TAG);
                    }
                }
            };

    private final ErrorReportDialogFragment.Listener errorReportingDialogListener =
            new ErrorReportDialogFragment.Listener() {
                @Override
                public void onSendClicked(final String errorReportText) {
                    App.assertCondition(errorReportText != null);
                    lastErrorReportText = errorReportText;

                    final ErrorReportingAsyncTask task =
                            new ErrorReportingAsyncTask(
                                    currentProduct.getBarcode(),
                                    errorReportText,
                                    errorReportingTaskListener);
                    showProgressDialog(R.string.raw_connecting_to_database);
                    task.execute();
                }
            };

    private final ProductLoadingAsyncTask.Listener productLoaderListener =
            new ProductLoadingAsyncTask.Listener() {
                @Override
                public void onResult(final ProductLoaderResultHolder resultHolder) {
                    final Product resultProduct = resultHolder.getProduct();
                    currentProduct = resultProduct.isFullyInitialized() ? resultProduct : null;

                    if (hasStarted) {
                        displayCurrentProduct();

                        final ProductLoaderResultHolder.ResultType resultType =
                                resultHolder.getResultType();

                        if (resultType == ProductLoaderResultHolder.ResultType.NO_SUCH_PRODUCT) {
                            showProductNotFoundDialog(resultProduct.getBarcode());
                        } else if (resultType != ProductLoaderResultHolder.ResultType.SUCCESS) {
                            showToastWith(R.string.product_description_activity_product_downloading_error_message);
                            App.logError(this, "a task failed at downloading a product");
                        }
                        hideProgressDialog();
                    }
                }
            };

    /**
     * Note: no null checking. Call it only if the activity has started.
     */
    private void displayCurrentProduct() {
        if (currentProduct != null) {
            ((TextView) findViewById(R.id.text_barcode)).
                    setText(currentProduct.getBarcode());
            ((TextView) findViewById(R.id.text_product_name)).
                    setText(currentProduct.getName());
            ((TextView) findViewById(R.id.text_company_name)).
                    setText(currentProduct.getCompany());
            ((TextView) findViewById(R.id.text_is_vegan)).
                    setText(HumanizingToolkit.booleanToAnswer(currentProduct.isVegan()));
            ((TextView) findViewById(R.id.text_is_vegetarian)).
                    setText(HumanizingToolkit.booleanToAnswer(currentProduct.isVegetarian()));
            ((TextView) findViewById(R.id.text_was_tested_on_animals)).
                    setText(HumanizingToolkit.booleanToAnswer(currentProduct.wasTestedOnAnimals()));
        } else {
            ((TextView) findViewById(R.id.text_barcode)).setText("");
            ((TextView) findViewById(R.id.text_product_name)).setText("");
            ((TextView) findViewById(R.id.text_company_name)).setText("");
            ((TextView) findViewById(R.id.text_is_vegan)).setText("");
            ((TextView) findViewById(R.id.text_is_vegetarian)).setText("");
            ((TextView) findViewById(R.id.text_was_tested_on_animals)).setText("");
        }
    }

    private void showProductNotFoundDialog(final String barcode) {
        final DialogFragment productNotFoundDialog =
                ProductNotFoundDialogFragment.create(barcode);
        productNotFoundDialog.show(
                getSupportFragmentManager(),
                GENERAL_DIALOG_TAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        hasStarted = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        hasStarted = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.product_description_activity_layout);
    }

    public void onButtonClick(final View button) {
        if (button.getId() == R.id.button_scan) {
            if (App.isOnline()) {
                final IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                final AlertDialog installScannerAppTip = scanIntegrator.initiateScan();
                if (installScannerAppTip == null) {
                    showToastWith(R.string.product_description_activity_before_scan_start_message);
                }
            } else {
                showToastWith(R.string.raw_internet_connection_is_not_available);
            }
        } else if (button.getId() == R.id.button_report_error) {
            if (App.isOnline()) {
                if (currentProduct != null) {
                    final DialogFragment errorReportDialog =
                            ErrorReportDialogFragment.create(errorReportingDialogListener);
                    errorReportDialog.show(getSupportFragmentManager(), GENERAL_DIALOG_TAG);
                } else {
                    showToastWith(R.string.raw_scan_a_barcode_first);
                }
            } else {
                showToastWith(R.string.raw_internet_connection_is_not_available);
            }
        } else if (button.getId() == R.id.button_show_comment) {
            if (currentProduct != null) {
                showToastWith(currentProduct.getComment());
            } else {
                showToastWith(R.string.raw_scan_a_barcode_first);
            }
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        final IntentResult scanningResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            final String scannedBarcode = scanningResult.getContents();
            if (BarcodeToolkit.isValid(scannedBarcode)) {
                showToastWith(R.string.raw_barcode_received);
                showProgressDialog(R.string.raw_connecting_to_database);

                final ProductLoadingAsyncTask task =
                        new ProductLoadingAsyncTask(
                                scannedBarcode,
                                productLoaderListener);
                task.execute();
            } else {
                showToastWith(R.string.raw_barcode_not_received);
            }
        }
    }
}
