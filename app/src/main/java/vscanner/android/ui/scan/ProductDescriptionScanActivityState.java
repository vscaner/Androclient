package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vscanner.android.App;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.ui.CardboardUI;

public class ProductDescriptionScanActivityState extends ScanActivityState {
    private static final String EXTRA_PRODUCT = "ProductDescriptionScanActivityState.EXTRA_PRODUCT";
    private final Product product;

    /**
     * @param product must be a valid product (product != null && product.isFullyInitialized())
     * @throws java.lang.IllegalArgumentException if product is invalid
     */
    protected ProductDescriptionScanActivityState(final ScanActivityState parent, final Product product) {
        super(parent);
        if (product == null || !product.isFullyInitialized()) {
            throw new IllegalArgumentException("product must not be valid");
        }
        this.product = product;

        // TODO: is it a correct way to check whether the activity is initialized?
        if (getActivity().findViewById(android.R.id.content) != null) {
            initializeViewBy(product);
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        App.assertCondition(savedInstanceState != null);
        initializeViewBy(product);
    }

    private void initializeViewBy(final Product product) {
        App.assertCondition(product != null);

        final CardboardUI cardboardUI = getCardboardUI();
        cardboardUI.putToMiddleSlot(ProductDescriptionFragment.create(product));
        cardboardUI.putToTopSlot(createCowSaysFragmentFor(product));
    }

    private CowSaysFragment createCowSaysFragmentFor(final Product product) {
        App.assertCondition(product != null);

        final CowSaysFragment cowSaysFragment = new CowSaysFragment();

        if (product.isVegan()) {
            cowSaysFragment.setCowsText(
                    getActivity().getString(
                            R.string.product_description_activity_product_status_vegan));
            cowSaysFragment.setCowMood(CowState.Mood.GOOD);
        } else if (product.isVegetarian()) {
            cowSaysFragment.setCowsText(
                    getActivity().getString(
                            R.string.product_description_activity_product_status_vegetarian));
            cowSaysFragment.setCowMood(CowState.Mood.OK);
        } else {
            cowSaysFragment.setCowsText(
                    getActivity().getString(
                            R.string.product_description_activity_product_status_bad));
            cowSaysFragment.setCowMood(CowState.Mood.BAD);
        }

        return cowSaysFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO: this
    }

    @Override
    public void onSaveStateData(final Bundle outState) {
        outState.putSerializable(EXTRA_PRODUCT, product);
    }

    public static Product parseProductFrom(final Bundle bundle) {
        return (Product) bundle.getSerializable(EXTRA_PRODUCT);
    }
}
