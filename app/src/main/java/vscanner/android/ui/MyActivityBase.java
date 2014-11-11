package vscanner.android.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import vscanner.android.App;

public abstract class MyActivityBase extends ActionBarActivity {
    private static final String PROGRESS_DIALOG_TAG = "progress_dialog";
    private Toast toast;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        App.onActivityResumeFragments(this);
        App.assertCondition(this == App.getFrontActivity());
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.onActivityPause(this);
        App.assertCondition(this != App.getFrontActivity());
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        toast = null;
    }

    public final void showToastWith(final int stringId) {
        if (toast != null) {
            toast.setText(stringId);
            toast.show();
        }
    }

    public final void showToastWith(final String string) {
        if (toast != null) {
            toast.setText(string);
            toast.show();
        }
    }
}
