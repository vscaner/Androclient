package vscanner.android.network;

import android.os.AsyncTask;
import android.webkit.URLUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vscanner.android.App;

abstract class ProductAsyncTaskBase<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
    private static final String ENCODING = "UTF-8";
    private final String url;

    /**
     * @param url must be a valid url, ie (URLUtil.isValidUrl(url) == true)
     * @throws IllegalArgumentException if any argument is not valid
     */
    protected ProductAsyncTaskBase(final String url) throws IllegalArgumentException {
        if (!URLUtil.isValidUrl(url)) {
            throw new IllegalArgumentException("given url (" + url + ") is not valid");
        }
        this.url = url;
    }

    /**
     * @return server's reply
     * @throws IOException if something went wrong during querying.
     */
    protected final String queryServer() throws IOException {
        final HttpPost request = formPostRequest();
        final HttpResponse response = send(request);
        return decode(response);
    }

    private HttpPost formPostRequest() throws IOException {
        final UrlEncodedFormEntity encodedFormEntity;
        try {
            encodedFormEntity = new UrlEncodedFormEntity(getValidPostParameters(), ENCODING);
        } catch (final UnsupportedEncodingException e) {
            throw new IOException("encoding wasn't parsed: " + e.getMessage());
        }

        final HttpPost request;
        try {
            request = new HttpPost(url);
        } catch (final IllegalArgumentException e) {
            throw new IOException("received URL (" + url + ") is invalid: " + e.getMessage());
        }
        request.setEntity(encodedFormEntity);
        return request;
    }

    private List<NameValuePair> getValidPostParameters() {
        final List<NameValuePair> inputPostParameters = getPostParameters();
        App.assertCondition(inputPostParameters != null);
        if (inputPostParameters == null) {
            return new ArrayList<NameValuePair>(0);
        }

        final List<NameValuePair> validatedParameters
                = new ArrayList<NameValuePair>(inputPostParameters);

        final Iterator<NameValuePair> it = validatedParameters.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                it.remove();
            }
        }

        return validatedParameters;
    }

    protected abstract List<NameValuePair> getPostParameters();

    private HttpResponse send(final HttpPost request) throws IOException {
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (final IOException e) {
            throw new IOException("error during request executing: " + e.getMessage());
        }

        final StatusLine statusLine = response.getStatusLine();
        if (statusLine == null
                || statusLine.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("response has a bad status");
        }
        return response;
    }

    private String decode(final HttpResponse response) throws IOException {
        try {
            return EntityUtils.toString(response.getEntity(), ENCODING);
        } catch (IOException e) {
            throw new IOException("an error occurred during response decoding: " + e.getMessage());
        }
    }
}
