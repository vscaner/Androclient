package vscanner.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import vscanner.android.R;

// TODO: return using of this dialog?
public class InternetUnavailableDialogFragment extends DialogFragment {
    private Runnable onPositiveButtonRunnable;
    private Runnable onNegativeButtonRunnable;

    public static InternetUnavailableDialogFragment create(
            final Runnable onPositiveButtonRunnable,
            final Runnable onNegativeButtonRunnable) {
        final InternetUnavailableDialogFragment instance = new InternetUnavailableDialogFragment();
        instance.onPositiveButtonRunnable = onPositiveButtonRunnable;
        instance.onNegativeButtonRunnable = onNegativeButtonRunnable;
        return instance;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.internet_unavailable_dialog_title);
        builder.setMessage(R.string.internet_unavailable_dialog_text);
        builder.setPositiveButton(
                R.string.internet_unavailable_positive_button_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dismiss();
                        if (onPositiveButtonRunnable != null) {
                            onPositiveButtonRunnable.run();
                        }
                    }
                });
        builder.setNegativeButton(
                R.string.internet_unavailable_negative_button_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dismiss();
                        if (onNegativeButtonRunnable != null) {
                            onNegativeButtonRunnable.run();
                        }
                    }
                });

        return builder.create();
    }
}
