package vscanner.android.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.network.ProductAdditionAsyncTask;

public class ProductAdditionActivity extends MyActivityBase {
    public static final String BARCODE_EXTRA =
            "vscanner.android.ui.ProductAdditionActivity.BARCODE_EXTRA";
    private static final int MINIMUM_NAMES_LENGTH = 4;
    private String barcode;

    public void onButtonClick(View view) {
        App.assertCondition(BarcodeToolkit.isValid(barcode));
        if (!BarcodeToolkit.isValid(barcode)) {
            showToastWith(R.string.raw_oops_an_unknown_error_occurred);
            return;
        } else if (!App.isOnline()) {
            showToastWith(R.string.raw_internet_connection_is_not_available);
            return;
        }

        final Product product = getUserFormedProduct();
        final String errorMessage = check(product);

        if (errorMessage != null) {
            showToastWith(errorMessage);
            return;
        }

//        showProgressDialog(R.string.raw_connecting_to_database);

        final ProductAdditionAsyncTask task =
                new ProductAdditionAsyncTask(product, new ProductAdditionAsyncTask.Listener() {
                    @Override
                    public void onResult(final boolean success) {
//                        hideProgressDialog();
//                        if (success) {
//                            finish();
//                        } else {
//                            showToastWith(R.string.product_addition_activity_data_sending_fail_message);
//                        }
                    }
                });
        task.execute();
    }

    private Product getUserFormedProduct() {
        final String productName =
                ((TextView) findViewById(R.id.edittext_product_name)).getText().toString();
        final String companyName =
                ((TextView) findViewById(R.id.edittext_company_name)).getText().toString();
        final boolean isVegetarian =
                !((CheckBox) findViewById(R.id.checkbox_not_vegetarian)).isChecked();
        final boolean isVegan =
                !((CheckBox) findViewById(R.id.checkbox_not_vegan)).isChecked();
        final boolean wasTestedOnAnimals =
                ((CheckBox) findViewById(R.id.checkbox_was_tested_on_animals)).isChecked();

        return new Product(
                barcode,
                productName,
                companyName,
                isVegan,
                isVegetarian,
                wasTestedOnAnimals);
    }

    /**
     * @return null if all is ok, or a user-readable error message
     */
    private String check(final Product product) {
        if (product.getName().length() < MINIMUM_NAMES_LENGTH) {
            return String.format(
                    getString(R.string.product_addition_activity_product_name_length_error_message),
                    MINIMUM_NAMES_LENGTH - 1);
        } else if (product.getCompany().length() < MINIMUM_NAMES_LENGTH) {
            return String.format(
                    getString(R.string.product_addition_activity_company_name_length_error_message),
                    MINIMUM_NAMES_LENGTH - 1);
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_addition_activity_layout);

        barcode = getIntent().getStringExtra(BARCODE_EXTRA);

        App.assertCondition(BarcodeToolkit.isValid(barcode));
        if (!BarcodeToolkit.isValid(barcode)) {
            App.logError(this, "barcode is not valid somehow");
            // TODO: we probably should handle such a situation somehow
            finish();
        }
    }
}
