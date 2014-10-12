package veganscanner.androclient.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import veganscanner.androclient.R;

// TODO: return using of this dialog?
public class InternetUnavailableDialogFragment extends DialogFragment {
    private Runnable onPositiveButtonRunnable = null;

    public static InternetUnavailableDialogFragment create(final Runnable onPositiveButtonRunnable) {
        final InternetUnavailableDialogFragment instance = new InternetUnavailableDialogFragment();
        instance.onPositiveButtonRunnable = onPositiveButtonRunnable;
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
                    }
                });

        return builder.create();
    }
}
