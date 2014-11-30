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
import vscanner.android.ui.BarcodeHttpActionActivity;
import vscanner.android.ui.BarcodeHttpActionFragment;
import vscanner.android.ui.CardboardActivityBase;
import vscanner.android.ui.UIConstants;
import vscanner.android.ui.scan.ScanActivity;

public class ProductAdditionActivity extends BarcodeHttpActionActivity {
    public ProductAdditionActivity() {
        super(
                "http://lumeria.ru/vscaner/tobase.php",
                R.string.product_addition_activity_submit_request_sent_toast,
                R.string.product_addition_activity_on_request_successfully_delivered,
                R.string.product_addition_activity_title);
        setFinalAction(new Runnable() {
            @Override
            public void run() {
                ScanActivity.startBy(ProductAdditionActivity.this);
            }
        });
    }

    @Override
    protected final List<ParcelableNameValuePair> createPostParametersFor(final Object actionResult) {
        final List<ParcelableNameValuePair> postParameters = new ArrayList<ParcelableNameValuePair>();

        if (actionResult instanceof Product) {
            final Product product = (Product) actionResult;

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
        } else {
            App.error(this, "(actionResult instanceof Product) == false!");
        }

        return postParameters;
    }

    @Override
    protected BarcodeHttpActionFragment createFragment() {
        return ProductAdditionFragment.createFor(getBarcode());
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
        intent.putExtra(BarcodeHttpActionActivity.BARCODE_EXTRA, barcode);
        context.startActivity(intent);
    }
}
