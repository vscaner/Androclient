package vscanner.android.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import vscanner.android.App;
import vscanner.android.network.http.HttpRequestResult;
import vscanner.android.network.http.HttpService;
import vscanner.android.network.ParcelableNameValuePair;

public abstract class MyActivityBase extends ActionBarActivity {
    private static final String EXTRA_IDS_NOT_RECEIVED_HTTP_RESULTS =
            MyActivityBase.class.getCanonicalName() + ".EXTRA_IDS_NOT_RECEIVED_HTTP_RESULTS";

    private Toast toast;
    private HttpServiceListener httpServiceListener;
    private final Set<String> idsOfNotReceivedHttpResults = new CopyOnWriteArraySet<String>();

    private final class HttpServiceListener implements HttpService.Listener {
        @Override
        public void onHttpPostResult(final HttpRequestResult resultHolder) {
            HttpService.popLastRequestResultWith(resultHolder.getRequestId());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    idsOfNotReceivedHttpResults.remove(resultHolder.getRequestId());
                    MyActivityBase.this.onHttpPostResult(resultHolder);
                }
            });
        }
    }

    /**
     * @param resultHolder never null
     * guaranteed to be called after/during onResumeFragments() and before/during onPause()
     */
    protected void onHttpPostResult(final HttpRequestResult resultHolder) {
        // Let the inheritors do things they want
    }

    /**
     * if any parameter is null, a call will do nothing
     */
    public final void sendHttpPostRequest(
            final String requestId,
            final String url,
            final List<ParcelableNameValuePair> postParameters) {
        if (requestId != null
                && url != null
                && postParameters != null) {
            idsOfNotReceivedHttpResults.add(requestId);
            HttpService.startWith(this, requestId, url, postParameters);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        toast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        idsOfNotReceivedHttpResults.clear();
        if (savedInstanceState != null) {
            final List<String> idsOfNotReceivedHttpResultsAsList =
                    savedInstanceState.getStringArrayList(EXTRA_IDS_NOT_RECEIVED_HTTP_RESULTS);
            for (final String id : idsOfNotReceivedHttpResultsAsList) {
                idsOfNotReceivedHttpResults.add(id);
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        App.onActivityResumeFragments(this);
        App.assertCondition(this == App.getFrontActivity());

        httpServiceListener = new HttpServiceListener();
        HttpService.addListener(httpServiceListener);

        for (final String requestId : idsOfNotReceivedHttpResults) {
            final HttpRequestResult httpRequestResult = HttpService.popLastRequestResultWith(requestId);
            if (httpRequestResult != null) {
                onHttpPostResult(httpRequestResult);
            }
        }
        idsOfNotReceivedHttpResults.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();

        App.onActivityPause(this);
        App.assertCondition(this != App.getFrontActivity());

        HttpService.removeListener(httpServiceListener);
        httpServiceListener = null;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(
                EXTRA_IDS_NOT_RECEIVED_HTTP_RESULTS,
                new ArrayList<String>(idsOfNotReceivedHttpResults));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
