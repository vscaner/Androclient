package veganscanner.androclient.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import veganscanner.androclient.R;

public class DatabaseConnectionProgressDialogFragment extends DialogFragment {
    public static DatabaseConnectionProgressDialogFragment create() {
        return new DatabaseConnectionProgressDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.raw_connecting_to_database));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }
}
