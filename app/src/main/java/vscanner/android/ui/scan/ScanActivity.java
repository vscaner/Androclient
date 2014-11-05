package vscanner.android.ui.scan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.network.ProductLoaderResultHolder;
import vscanner.android.network.ProductLoadingAsyncTask;
import vscanner.android.ui.CardboardActivityBase;

// TODO: handle back button?
public class ScanActivity extends CardboardActivityBase implements ScanActivityState.Listener {
    private ScanActivityState state = new FirstScanActivityState(this);

    public ScanActivityState getState() {
        return state;
    }

    @Override
    public void onStateRequestsChangeTo(final ScanActivityState otherState) {
        this.state = otherState;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        state.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        state.onSaveInstanceState(outState);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        state.onActivityResult(requestCode, resultCode, intent);
    }
}
