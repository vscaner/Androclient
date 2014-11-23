package vscanner.android.network.http;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vscanner.android.App;
import vscanner.android.network.ParcelableNameValuePair;

public class HttpService extends IntentService {
    private static final String EXTRAS_START = HttpService.class.getCanonicalName() + ".";
    private static final String EXTRA_REQUEST_ID = EXTRAS_START + "EXTRA_REQUEST_ID";
    private static final String EXTRA_URL = EXTRAS_START + "EXTRA_URL";
    private static final String EXTRA_POST_PARAMETERS = EXTRAS_START + "EXTRA_POST_PARAMETERS";

    private static final List<Listener> LISTENERS = new ArrayList<Listener>();
    private static final MemoryOfHttpService MEMORY = new MemoryOfHttpService();

    public HttpService() {
        super("HttpService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final String requestId = intent.getStringExtra(EXTRA_REQUEST_ID);
        final String url = intent.getStringExtra(EXTRA_URL);
        final List<ParcelableNameValuePair> postParameters =
                intent.getParcelableArrayListExtra(EXTRA_POST_PARAMETERS);

        HttpRequestResult requestResult;
        try {
            final String response = Http.post(url, postParameters);
            requestResult = HttpRequestResult.createWithSuccess(requestId, response);
        } catch (final IOException e) {
            App.logInfo(this, "HTTP.post has failed", e);
            requestResult = HttpRequestResult.createWithNetworkError(requestId, e.getMessage());
        }

        final List<Listener> listenersCopy = new ArrayList<Listener>(LISTENERS);
        for (final Listener listener : listenersCopy) {
            listener.onHttpPostResult(requestResult);
        }

        MEMORY.memorize(requestResult);
    }

    public static HttpRequestResult popLastRequestResultWith(final String requestId) {
        return MEMORY.forgetRequestWith(requestId);
    }

    public static void startWith(
            final Context context,
            final String requestId,
            final String url,
            final List<ParcelableNameValuePair> postParameters) {
        final Intent intent = new Intent(context, HttpService.class);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_POST_PARAMETERS, new ArrayList<ParcelableNameValuePair>(postParameters));
        context.startService(intent);
    }

    public static void addListener(final Listener listener) {
        if (listener != null) {
            LISTENERS.add(listener);
        }
    }

    public static void removeListener(final Listener listener) {
        LISTENERS.remove(listener);
    }

    public static interface Listener {
        /**
         * @param resultHolder never null
         */
        void onHttpPostResult(HttpRequestResult resultHolder);
    }
}
