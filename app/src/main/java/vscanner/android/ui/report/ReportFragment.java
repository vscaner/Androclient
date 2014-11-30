package vscanner.android.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Formatter;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.R;
import vscanner.android.ui.BarcodeHttpActionFragment;

public class ReportFragment extends BarcodeHttpActionFragment<String> {
    private static final int MIN_REPORT_TEXT_LENGTH = 5;
    private static final String KEYS_START = ReportFragment.class.getCanonicalName() + ".";
    private static final String BARCODE_EXTRA = KEYS_START + "BARCODE_EXTRA";
    private static final String REPORT_TEXT_EXTRA = KEYS_START + "REPORT_TEXT_EXTRA";

    private String barcode;

    /**
     * @param barcode must be a valid barcode (i.e. BarcodeToolkit.isValid(barcode)==true).
     * @throws java.lang.IllegalArgumentException if any argument is invalid.
     */
    public static ReportFragment createFor(final String barcode) {
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("received barcode is invalid");
        }
        final ReportFragment instance = new ReportFragment();
        instance.barcode = barcode;
        return instance;
    }

    @Override
    public final String getErrorMessage() {
        final String reportText = getReportText();
        if (reportText.length() < MIN_REPORT_TEXT_LENGTH) {
            final Formatter formatter = new Formatter();
            final String message = App.getStringWith(R.string.report_fragment_report_text_length_error_message);
            return formatter.format(message, MIN_REPORT_TEXT_LENGTH - 1).toString();
        }
        return null;
    }

    /**
     * @return never null
     */
    private String getReportText() {
        final View root = getView();
        if (root != null) {
            return ((TextView) root.findViewById(R.id.edittext_report)).getText().toString();
        } else {
            App.error(this, "can't return a valid report since the view is not initialized");
            return "";
        }
    }

    @Override
    public final String getResult() {
        if (getErrorMessage() == null) {
            return getReportText();
        } else {
            return null;
        }
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        final View root =
                inflater.inflate(
                        R.layout.report_fragment_layout,
                        container,
                        false);

        if (savedInstanceState != null) {
            barcode = savedInstanceState.getString(BARCODE_EXTRA);
            ((TextView) root.findViewById(R.id.edittext_report)).
                    setText(savedInstanceState.getString(REPORT_TEXT_EXTRA));
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BARCODE_EXTRA, barcode);
        outState.putString(REPORT_TEXT_EXTRA, getReportText());
    }
}
