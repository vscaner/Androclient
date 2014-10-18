package vscanner.android.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
    private String text;

    private void setText(final String text) {
        this.text = text;
    }

    public static ProgressDialogFragment create(final String text) {
        final ProgressDialogFragment instance =
                new ProgressDialogFragment();
        instance.setText(text);
        return instance;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        if (text != null) {
            dialog.setMessage(text);
        }
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }
}
