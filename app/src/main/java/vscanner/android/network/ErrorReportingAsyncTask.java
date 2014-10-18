package vscanner.android.network;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;

public final class ErrorReportingAsyncTask
        extends ProductAsyncTaskBase<Void, Void, Boolean> {
    public static final int MINIMUM_COMMENT_LENGTH = 3;

    private static final String REQUEST_URL = "http://lumeria.ru/vscaner/er.php";
    private final String barcode;
    private final String comment;
    private final Listener listener;

    public static interface Listener {
        void onResult(final boolean success);
    }

    /**
     * @param barcode must be a valid barcode, ie (BarcodeToolkit.isValid(barcode) == true)
     * @param comment must be not null and comment.length() >= MINIMUM_COMMENT_LENGTH
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public ErrorReportingAsyncTask(
            final String barcode,
            final String comment,
            final Listener listener) throws IllegalArgumentException {
        super(REQUEST_URL);

        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode is not valid");
        } else if (comment == null) {
            throw new IllegalArgumentException("comment is null");
        } else if (comment.length() < MINIMUM_COMMENT_LENGTH) {
            throw new IllegalArgumentException("comment is too short");
        }
        this.barcode = barcode;
        this.comment = comment;
        this.listener = listener;
    }

    @Override
    protected List<NameValuePair> getPostParameters() {
        App.assertCondition(barcode != null);
        final List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("bcod", barcode));
        postParameters.add(new BasicNameValuePair("comment", comment));
        return postParameters;
    }

    @Override
    protected Boolean doInBackground(final Void... voids) {
        try {
            queryServer();
        } catch (final IOException e) {
            App.logError(this, "error query failed for some reason: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result)
    {
        if (listener != null) {
            listener.onResult(result);
        }
    }
}
