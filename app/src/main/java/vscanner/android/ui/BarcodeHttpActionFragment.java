package vscanner.android.ui;

import android.support.v4.app.Fragment;

public abstract class BarcodeHttpActionFragment<ActionResult> extends Fragment {
    /**
     * @return error message if user input is not valid for some reason, null otherwise
     */
    public abstract String getErrorMessage();
    /**
     * @return null if user input is invalid
     */
    public abstract ActionResult getResult();
}
