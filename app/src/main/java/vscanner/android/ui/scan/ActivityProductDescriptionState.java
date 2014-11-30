package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import vscanner.android.App;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.ui.CardboardActivityBase;

class ActivityProductDescriptionState extends ScanActivityState {
    private static final String EXTRA_PRODUCT = "ProductDescriptionScanActivityState.EXTRA_PRODUCT";
    private final Product product;
    private boolean isViewInitialized;

    /**
     * @param product must be a valid product (product != null && product.isFullyInitialized())
     * @throws java.lang.IllegalArgumentException if product is invalid
     */
    protected ActivityProductDescriptionState(final ScanActivityState parent, final Product product) {
        super(parent);
        if (product == null || !product.isFullyInitialized()) {
            throw new IllegalArgumentException("product must be valid");
        }
        this.product = product;

        if (App.getFrontActivity() == getActivity() && !isViewInitialized) {
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

        final CardboardActivityBase activity = getActivity();
        activity.putToMiddleSlot(ProductDescriptionFragment.create(product));
        activity.putToTopSlot(createCowSaysFragmentFor(product));
        activity.setNewScanButtonVisibility(View.VISIBLE);
        activity.removeBottomButtons();
        activity.addBottomButtonWith(R.string.scan_activity_report_button_text, null);

        isViewInitialized = true;
    }

    private CowSaysFragment createCowSaysFragmentFor(final Product product) {
        App.assertCondition(product != null);

        final CowSaysFragment cowSaysFragment = new CowSaysFragment();

        if (product.isVegan()) {
            cowSaysFragment.setCowsText(
                    getActivity().getString(
                            R.string.scan_activity_product_status_vegan));
            cowSaysFragment.setCowMood(CowState.Mood.GOOD);
        } else if (product.isVegetarian()) {
            cowSaysFragment.setCowsText(
                    getActivity().getString(
                            R.string.scan_activity_product_status_vegetarian));
            cowSaysFragment.setCowMood(CowState.Mood.OK);
        } else {
            cowSaysFragment.setCowsText(
                    getActivity().getString(
                            R.string.scan_activity_product_status_bad));
            cowSaysFragment.setCowMood(CowState.Mood.BAD);
        }

        return cowSaysFragment;
    }

    @Override
    public void onSaveStateData(final Bundle outState) {
        outState.putSerializable(EXTRA_PRODUCT, product);
    }

    @Override
    public void onResumeFragments() {
        if (isViewInitialized) {
            initializeViewBy(product);
        }
    }

    public static Product parseProductFrom(final Bundle bundle) {
        return (Product) bundle.getSerializable(EXTRA_PRODUCT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        App.assertCondition(false);
    }

    private static final class Restorer implements ScanActivityState.Restorer {
        private final Product product;
        public Restorer(final Product product) {
            this.product = product;
        }
        @Override
        public ScanActivityState restoreFor(final ScanActivity activity) {
            if (activity == null) {
                throw new IllegalArgumentException("activity must not be null");
            } else if (activity != App.getFrontActivity()) {
                throw new IllegalStateException("restoring state while activity is not in front!");
            }
            return new ActivityProductDescriptionState(activity, product);
        }
        @Override
        public boolean doesStayLast() {
            return false;
        }
    }

    private ActivityProductDescriptionState(final ScanActivity activity, final Product product) {
        super(activity);
        this.product = product;
        initializeViewBy(product);
    }

    public Restorer save() {
        return new Restorer(product);
    }
}
