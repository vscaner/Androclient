package vscanner.android.ui.scan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.network.ProductLoaderResultHolder;
import vscanner.android.network.ProductLoader;
import vscanner.android.ui.CardboardActivityBase;

class ActivityBeforeScanState extends ScanActivityState {
    private boolean viewInitialized;

    protected ActivityBeforeScanState(final ScanActivityState parent) {
        super(parent);
        if (App.getCurrentActivity() == getActivity()) {
            if (!viewInitialized) {
                initView();
            }
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // onResumeFragments() will take care of things
    }

    @Override
    public void onResumeFragments() {
        if (!viewInitialized) {
            initView();
        }
    }

    private void initView() {
        final CardboardActivityBase activity = getActivity();
        activity.setNewScanButtonVisibility(View.GONE);
        activity.putToTopSlot(createCowSaysFragment());
        activity.putToMiddleSlot(createPackageButton());
        viewInitialized = true;
    }

    private CowSaysFragment createCowSaysFragment() {
        final CowSaysFragment cowSaysFragment = new CowSaysFragment();
        cowSaysFragment.setCowBackgroundVisibility(false);
        cowSaysFragment.setCowMood(CowState.Mood.NEUTRAL);
        cowSaysFragment.setCowsText(getResources().getString(R.string.raw_touch_to_scan));
        return cowSaysFragment;
    }

    private ImageButton createPackageButton() {
        final ImageButton startScanButton = new ImageButton(getActivity());
        startScanButton.setImageResource(R.drawable.product_package_selector);
        startScanButton.setBackgroundColor(
                getResources().getColor(android.R.color.transparent));

        final RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        startScanButton.setLayoutParams(params);

        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (App.isOnline()) {
                    requestStateChangeTo(new ActivityNewScanState(ActivityBeforeScanState.this, false));
                } else {
                    getActivity().showToastWith(R.string.raw_internet_connection_is_not_available);
                }
            }
        });
        return startScanButton;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        App.assertCondition(false);
    }

    @Override
    public void onSaveStateData(final Bundle outState) {
        viewInitialized = false;
        // nothing to do
    }
}
