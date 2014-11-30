package vscanner.android.ui.scan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.R;

final class ActivityNewScanState extends ScanActivityState {
    ActivityNewScanState(final ScanActivityState parent, final boolean isRecreated) {
        super(parent);

        if (!isRecreated) {
            tryStartScanning();
        } else {
            App.logInfo(this, "onActivityResult() must change state");
        }
    }

    private void tryStartScanning() {
        // TODO: God knows what would happen if the XZing is not installed.
        if (App.isOnline()) {
            final IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
            final AlertDialog installScannerAppTip = scanIntegrator.initiateScan();
            if (installScannerAppTip == null) {
                getActivity().showToastWith(R.string.scan_activity_before_scan_start_message);
            } else {
                // XZing is not installed
            }
        } else {
            requestAsyncStateChangeTo(new ActivityBeforeScanState(this));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult scanningResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            final String scannedBarcode = scanningResult.getContents();
            if (BarcodeToolkit.isValid(scannedBarcode)) {
                getActivity().showToastWith(R.string.raw_barcode_received);
                requestStateChangeTo(new ActivityLoadingState(this, scannedBarcode));
                return;
            }
        }

        getActivity().showToastWith(R.string.raw_barcode_not_received);
        requestStateChangeTo(new ActivityBeforeScanState(this));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // nothing to do
    }

    @Override
    public void onSaveStateData(Bundle outState) {
        // nothing to do
    }

    @Override
    public void onResumeFragments() {
        // nothing to do
    }

    @Override
    public Restorer save() {
        return null;
    }
}
