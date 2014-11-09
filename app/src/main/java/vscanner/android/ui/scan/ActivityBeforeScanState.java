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
    protected ActivityBeforeScanState(final ScanActivityState parent) {
        super(parent);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // onResumeFragments() will take care of things
    }

    @Override
    public void onResumeFragments() {
        final CardboardActivityBase activity = getActivity();
        final View root = activity.findViewById(android.R.id.content);

        if (root != null) {
            root.findViewById(R.id.button_new_scan).setVisibility(View.GONE);

            final CowSaysFragment cowSaysFragment = new CowSaysFragment();
            cowSaysFragment.setCowBackgroundVisibility(false);
            cowSaysFragment.setCowMood(CowState.Mood.NEUTRAL);
            cowSaysFragment.setCowsText(getResources().getString(R.string.raw_touch_to_scan));

            activity.putToTopSlot(cowSaysFragment);

            activity.putToMiddleSlot(createPackageButton());
        } else {
            App.assertCondition(false, "can't set up view without a root");
        }
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
                    final IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                    final AlertDialog installScannerAppTip = scanIntegrator.initiateScan();
                    if (installScannerAppTip == null) {
                        getActivity().showToastWith(R.string.product_description_activity_before_scan_start_message);
                    }
                } else {
                    getActivity().showToastWith(R.string.raw_internet_connection_is_not_available);
                }
            }
        });
        return startScanButton;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        final IntentResult scanningResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        // TODO: do something if result is invalid. Maybe show the NEW COOL STATE WITHOUT DATA?
        if (scanningResult != null) {
            final String scannedBarcode = scanningResult.getContents();
            if (BarcodeToolkit.isValid(scannedBarcode)) {
                getActivity().showToastWith(R.string.raw_barcode_received);

                requestStateChangeTo(new ActivityLoadingState(this, scannedBarcode));
            } else {
                getActivity().showToastWith(R.string.raw_barcode_not_received);
            }
        }
    }

    @Override
    public void onSaveStateData(final Bundle outState) {
        // nothing to do
    }
}
