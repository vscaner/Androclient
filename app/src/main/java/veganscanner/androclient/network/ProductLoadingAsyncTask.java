package veganscanner.androclient.network;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import veganscanner.androclient.App;

public class ProductLoadingAsyncTask
        extends AsyncTask<String, Void, ProductLoaderResultHolder> {
    private static final String REQUEST_URL = "http://lumeria.ru/vscaner/index.php";
    private static final String NO_SUCH_PRODUCT_RESPONSE = "731";
    private static final String BARCODE_POST_PARAMETER = "bcod";
    private static final String ENCODING = "UTF-8";
    private final Listener listener;

    public static interface Listener {
        void onResult(final ProductLoaderResultHolder resultHolder);
    }

    public ProductLoadingAsyncTask(final Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(final ProductLoaderResultHolder resultHolder) {
        if (listener != null) {
            listener.onResult(resultHolder);
        }
    }

    @Override
    protected ProductLoaderResultHolder doInBackground(final String... barcodes) {
        if (barcodes.length == 0) {
            App.logError(this, "need a barcode");
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.TOO_FEW_ARGUMENTS);
        } else if (barcodes.length > 1) {
            App.logError(this, "too many arguments, only the first one will be processed");
        }

        final String barcode = barcodes[0];

        final String serverResponse;
        try {
            serverResponse = requestServerAbout(barcode);
        } catch (final IOException e) {
            App.logError(this, e.getMessage());
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.NETWORK_ERROR);
        }

        if (serverResponse.equals(NO_SUCH_PRODUCT_RESPONSE)) {
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.NO_SUCH_PRODUCT);
        }

        try {
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.SUCCESS,
                    ServersProductsParser.parse(serverResponse, barcode));
        } catch (final ParseException e) {
            App.logError(this, e.getMessage());
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.SERVER_RESPONSE_PARSING_ERROR);
        }
    }

    private String requestServerAbout(final String barcode) throws IOException {
        final HttpPost request = formPostRequest(barcode);
        final HttpResponse response = send(request);
        return decode(response);
    }

    private String decode(final HttpResponse response) throws IOException {
        try {
            return EntityUtils.toString(response.getEntity(), ENCODING);
        } catch (IOException e) {
            throw new IOException("an error occurred during response decoding: " + e.getMessage());
        }
    }

    private HttpPost formPostRequest(final String barcode) throws IOException {
        final List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair(BARCODE_POST_PARAMETER, barcode));

        final UrlEncodedFormEntity encodedFormEntity;
        try {
            encodedFormEntity = new UrlEncodedFormEntity(postParameters, ENCODING);
        } catch (final UnsupportedEncodingException e) {
            throw new IOException("encoding wasn't parsed: " + e.getMessage());
        }

        final HttpPost request = new HttpPost(REQUEST_URL);
        request.setEntity(encodedFormEntity);
        return request;
    }

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
}
