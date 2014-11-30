package vscanner.android.ui.scan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.R;

public class ProductNotFoundFragment extends Fragment {
    private static final String BARCODE_EXTRA =
            ProductNotFoundFragment.class.getCanonicalName() + ".BARCODE_EXTRA";
    private String barcode;

    /**
     * @param barcode must be a valid barcode (i.e. BarcodeToolkit.isValid(barcode) == true)
     * @throws java.lang.IllegalArgumentException if barcode is invalid
     */
    public static ProductNotFoundFragment createFor(final String barcode) {
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode must be valid");
        }
        final ProductNotFoundFragment fragment = new ProductNotFoundFragment();
        fragment.barcode = barcode;
        return fragment;
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        final View root =
                inflater.inflate(
                        R.layout.product_not_found_fragment_layout,
                        container,
                        false);

        if (savedInstanceState != null) {
            barcode = savedInstanceState.getString(BARCODE_EXTRA);
        }

        if (barcode != null) {
            ((TextView) root.findViewById(R.id.text_barcode)).setText(barcode);
        } else {
            App.error(this, "barcode is null, invalid data will be shown");
            ((TextView) root.findViewById(R.id.text_barcode)).setText(R.string.raw_internal_error);
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BARCODE_EXTRA, barcode);
    }
}
