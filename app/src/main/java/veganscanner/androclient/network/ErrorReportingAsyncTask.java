package veganscanner.androclient.network;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import veganscanner.androclient.App;

public class ErrorReportingAsyncTask
        extends ProductAsyncTaskBase<Void, Void, Void> {
    private static final String REQUEST_URL = "http://lumeria.ru/vscaner/er.php";
    private final String barcode;
    private final String comment;

    public ErrorReportingAsyncTask(final String barcode, final String comment) {
        super(REQUEST_URL);
        App.assertCondition(barcode != null);
        App.assertCondition(barcode.length() > 0);
        this.barcode = barcode;
        App.assertCondition(comment != null);
        App.assertCondition(comment.length() > 0);
        this.comment = comment;
    }

    @Override
    protected List<NameValuePair> getPostParameters() {
        App.assertCondition(barcode != null);
        final List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair(ServerConstants.POST_PARAMETER_BARCODE, barcode));
        postParameters.add(new BasicNameValuePair(ServerConstants.POST_PARAMETER_COMMENT, comment));
        return postParameters;
    }

    @Override
    protected Void doInBackground(final Void... voids) {
        try {
            queryServer();
        } catch (IOException e) {
            App.logError(this, "error query failed for some reason: " + e.getMessage());
        }
        return null;
    }
}
