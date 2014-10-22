package vscanner.android.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import vscanner.android.App;

public abstract class MyActivityBase extends ActionBarActivity {
    private static final String PROGRESS_DIALOG_TAG = "progress_dialog";
    private Toast toast;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        toast = null;
    }

    protected final void showToastWith(final int stringId) {
        if (toast != null) {
            toast.setText(stringId);
            toast.show();
        }
    }

    protected final void showToastWith(final String string) {
        if (toast != null) {
            toast.setText(string);
            toast.show();
        }
    }

    protected final void showProgressDialog(final int stringId) {
        final DialogFragment progressDialog =
                ProgressDialogFragment.create(getString(stringId));
        progressDialog.show(getSupportFragmentManager(), PROGRESS_DIALOG_TAG);
    }

    protected final void hideProgressDialog() {
        App.assertCondition(getSupportFragmentManager() != null);

        final Fragment progressDialog =
                getSupportFragmentManager().findFragmentByTag(PROGRESS_DIALOG_TAG);
        if (progressDialog != null) {
            final FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.remove(progressDialog);
            transaction.commit();
        }
    }
}
