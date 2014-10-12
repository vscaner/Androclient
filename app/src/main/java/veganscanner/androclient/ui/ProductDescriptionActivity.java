package veganscanner.androclient.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import veganscanner.androclient.App;
import veganscanner.androclient.HumanizingToolkit;
import veganscanner.androclient.Product;
import veganscanner.androclient.R;
import veganscanner.androclient.network.ErrorReportingAsyncTask;
import veganscanner.androclient.network.ProductLoaderResultHolder;
import veganscanner.androclient.network.ProductLoadingAsyncTask;

public class ProductDescriptionActivity extends ActionBarActivity {
    private static final String PROGRESS_DIALOG_TAG = "progress_dialog";
    private static final String GENERAL_DIALOG_TAG = "dialog";
    private Product currentProduct;
    private boolean hasStarted;
    private Toast toast;

    private final ErrorReportDialogFragment.Listener errorReporterListener =
            new ErrorReportDialogFragment.Listener() {
                @Override
                public void onSendClicked(final String errorReportText) {
                    final ErrorReportingAsyncTask task =
                            new ErrorReportingAsyncTask(
                                    currentProduct.getBarcode(),
                                    errorReportText);
                    task.execute();
                    // TODO: memorize the message
                    // I.o. check whether the internet is connected,
                    // if it is, then send the message
                    // else save the message (for sending it later) and notify the user.
                }
            };

    private final ProductLoadingAsyncTask.Listener productLoaderListener =
            new ProductLoadingAsyncTask.Listener() {
                @Override
                public void onResult(final ProductLoaderResultHolder resultHolder) {
                    final Product resultProduct = resultHolder.getProduct();
                    currentProduct = resultProduct.isValid() ? resultProduct : null;

                    if (hasStarted) {
                        displayCurrentProduct();

                        final ProductLoaderResultHolder.ResultType resultType =
                                resultHolder.getResultType();

                        if (resultType == ProductLoaderResultHolder.ResultType.NO_SUCH_PRODUCT) {
                            showProductNotFoundDialog(resultProduct.getBarcode());
                        } else if (resultType != ProductLoaderResultHolder.ResultType.SUCCESS) {
                            showToastWith(R.string.product_activity_product_downloading_error_message);
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

    private void showToastWith(final int stringId) {
        toast.setText(stringId);
        toast.show();
    }

    private void showToastWith(final String string) {
        toast.setText(string);
        toast.show();
    }

    private void hideProgressDialog() {
        final Fragment progressDialog =
                getSupportFragmentManager().findFragmentByTag(PROGRESS_DIALOG_TAG);
        if (progressDialog != null) {
            final FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.remove(progressDialog);
            transaction.commit();
        }
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
        setContentView(R.layout.product_description_activity_layout);
        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    }

    public void onButtonClick(final View button) {
        if (button.getId() == R.id.button_scan) {
            if (App.isOnline()) {
                final IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                final AlertDialog installScannerAppTip = scanIntegrator.initiateScan();
                if (installScannerAppTip == null) {
                    showToastWith(R.string.product_activity_before_scan_start_message);
                }
            } else {
                showToastWith(R.string.internet_connection_is_not_available);
            }
        } else if (button.getId() == R.id.button_report_error) {
            if (App.isOnline()) {
                if (currentProduct != null) {
                    final DialogFragment errorReportDialog =
                            ErrorReportDialogFragment.create(errorReporterListener);
                    errorReportDialog.show(getSupportFragmentManager(), GENERAL_DIALOG_TAG);
                } else {
                    showToastWith(R.string.raw_scan_a_barcode_first);
                }
            } else {
                showToastWith(R.string.internet_connection_is_not_available);
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
            if (scannedBarcode != null) {
                showToastWith(R.string.raw_barcode_received);

                final DialogFragment progressDialog =
                        DatabaseConnectionProgressDialogFragment.create();
                progressDialog.show(getSupportFragmentManager(), PROGRESS_DIALOG_TAG);

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
