package vscanner.android.network;

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

final class HTTP {
    private static final String ENCODING = "UTF-8";

    private HTTP() {
    }

    /**
     * @param url must be a valid url, ie (URLUtil.isValidUrl(url) == true)
     * @return server's reply
     * @throws IOException if something went wrong during querying.
     * @throws IllegalArgumentException if any argument is not valid
     */
    public static String post(
            final String url,
            final List<NameValuePair> postParameters) throws IllegalArgumentException, IOException {
        if (!URLUtil.isValidUrl(url)) {
            throw new IllegalArgumentException("given url (" + url + ") is not valid");
        }
        final List<NameValuePair> validPostParameters = validate(postParameters);

        final org.apache.http.client.methods.HttpPost request =
                formPostRequest(url, validPostParameters);
        final HttpResponse response = send(request);
        return decode(response);
    }

    private static List<NameValuePair> validate(final List<NameValuePair> postParameters) {
        App.assertCondition(postParameters != null);
        if (postParameters == null) {
            return new ArrayList<NameValuePair>(0);
        }

        final List<NameValuePair> validatedParameters
                = new ArrayList<NameValuePair>(postParameters);

        final Iterator<NameValuePair> it = validatedParameters.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                it.remove();
            }
        }

        return validatedParameters;
    }

    private static org.apache.http.client.methods.HttpPost formPostRequest(
            final String url,
            final List<NameValuePair> postParameters) throws IOException {
        final UrlEncodedFormEntity encodedFormEntity;
        try {
            encodedFormEntity = new UrlEncodedFormEntity(postParameters, ENCODING);
        } catch (final UnsupportedEncodingException e) {
            throw new IOException("encoding wasn't parsed: " + e.getMessage());
        }

        final org.apache.http.client.methods.HttpPost request;
        try {
            request = new org.apache.http.client.methods.HttpPost(url);
        } catch (final IllegalArgumentException e) {
            throw new IOException("received URL (" + url + ") is invalid: " + e.getMessage());
        }
        request.setEntity(encodedFormEntity);
        return request;
    }

    private static HttpResponse send(final org.apache.http.client.methods.HttpPost request) throws IOException {
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

    private static String decode(final HttpResponse response) throws IOException {
        try {
            return EntityUtils.toString(response.getEntity(), ENCODING);
        } catch (IOException e) {
            throw new IOException("an error occurred during response decoding: " + e.getMessage());
        }
    }
}
