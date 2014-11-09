package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.network.ProductLoader;
import vscanner.android.network.ProductLoaderResultHolder;
import vscanner.android.ui.CardboardActivityBase;

class ActivityLoadingState
        extends ScanActivityState
        implements LoaderManager.LoaderCallbacks<ProductLoaderResultHolder> {
    private static final String BARCODE_EXTRA = "LoadingState.BARCODE_EXTRA";
    private static final String PRODUCT_EXTRA = "LoadingState.PRODUCT_EXTRA";
    private String barcode;
    private Product loadedProduct;

    /**
     * must be called only before onCreate() call
     */
    public ActivityLoadingState(final ScanActivityState parent) {
        super(parent);
        App.assertCondition(App.getCurrentActivity() != getActivity());
    }

    /**
     * @param barcode must be a valid barcode (ie BarcodeToolkit.isValid(barcode) == true)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public ActivityLoadingState(final ScanActivityState parent, final String barcode) {
        super(parent);
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode is not valid");
        }
        this.barcode = barcode;

        startLoader();
        initView();
    }

    private void startLoader() {
        final Bundle loaderBundle = new Bundle();
        loaderBundle.putString(PRODUCT_EXTRA, barcode);
        getActivity().getSupportLoaderManager().initLoader(0, loaderBundle, this).forceLoad();
    }

    private void initView() {
        final CardboardActivityBase activity = getActivity();
        if (activity != null) {
            activity.putToMiddleSlot(createProgressBar());
        } else {
            App.assertCondition(false);
        }
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
        loadedProduct = (Product) savedInstanceState.getSerializable(PRODUCT_EXTRA);
        if (loadedProduct != null) {
            if (loadedProduct.isFullyInitialized()) {
                requestStateChangeTo(new ActivityProductDescriptionState(this, loadedProduct));
            } else {
                App.assertCondition(false);
                requestStateChangeTo(new ActivityBeforeScanState(this));
            }
            return;
        }

        barcode = savedInstanceState.getString(BARCODE_EXTRA);
        if (!BarcodeToolkit.isValid(barcode)) {
            App.assertCondition(false);
            requestStateChangeTo(new ActivityBeforeScanState(this));
            return;
        }

        if (!getActivity().getSupportLoaderManager().hasRunningLoaders()) {
            startLoader();
        }

        initView();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        App.assertCondition(false);
    }

    @Override
    public void onSaveStateData(final Bundle outState) {
        outState.putString(BARCODE_EXTRA, barcode);
        outState.putSerializable(PRODUCT_EXTRA, loadedProduct);
    }

    @Override
    public void onResumeFragments() {
        if (loadedProduct != null) {
            requestStateChangeTo(new ActivityProductDescriptionState(this, loadedProduct));
        }
    }

    @Override
    public Loader<ProductLoaderResultHolder> onCreateLoader(final int i, final Bundle bundle) {
        return new ProductLoader(barcode, getActivity());
    }

    @Override
    public void onLoadFinished(
            final Loader<ProductLoaderResultHolder> productLoader,
            final ProductLoaderResultHolder resultHolder) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final ProductLoaderResultHolder.ResultType resultType = resultHolder.getResultType();

                if (resultType == ProductLoaderResultHolder.ResultType.NO_SUCH_PRODUCT) {
                    // TODO: product addition state
                    return;
                } else if (resultType != ProductLoaderResultHolder.ResultType.SUCCESS) {
                    getActivity().showToastWith(
                            R.string.product_description_activity_product_downloading_error_message);
                    App.logError(this, "a task failed at downloading a product");
                    requestStateChangeTo(new ActivityBeforeScanState(ActivityLoadingState.this));
                    return;
                }

                if (App.getCurrentActivity() == getActivity()) {
                    requestStateChangeTo(
                            new ActivityProductDescriptionState(
                                    ActivityLoadingState.this,
                                    resultHolder.getProduct()));
                } else {
                    loadedProduct = resultHolder.getProduct();
                }
            }
        });
    }

    @Override
    public void onLoaderReset(final Loader<ProductLoaderResultHolder> resultHolder) {
        // nothing to do
    }
}
