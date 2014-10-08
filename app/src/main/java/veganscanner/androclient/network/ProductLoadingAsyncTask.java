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
import veganscanner.androclient.Product;

public class ProductLoadingAsyncTask
extends AsyncTask<String, Void, ProductLoadingAsyncTask.ResultHolder> {
    private static final String REQUEST_URL = "http://lumeria.ru/vscaner/index.php";
    private static final String NO_SUCH_PRODUCT_RESPONSE = "731";
    private static final String BARCODE_POST_PARAMETER = "bcod";
    private static final String ENCODING = "UTF-8";
    private final Listener listener;

    public static enum ResultType {
        SUCCESS,
        NO_SUCH_PRODUCT,
        TOO_FEW_ARGUMENTS,
        NETWORK_ERROR,
        SERVER_RESPONSE_PARSING_ERROR
    };

    public static interface Listener {
        void onResult(final ResultHolder resultHolder);
    }

    // TODO: no nested classes
    public static class ResultHolder {
        private final ResultType resultType;
        private final Product product;

        private ResultHolder(final ResultType resultType, final Product product) {
            this.resultType = resultType;
            this.product = product;
        }

        public ResultType getResultType() {
            return resultType;
        }

        public Product getProduct() {
            return product;
        }
    }

    public ProductLoadingAsyncTask(final Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(final ResultHolder resultHolder) {
        if (listener != null) {
            listener.onResult(resultHolder);
        }
    }

    @Override
    protected ResultHolder doInBackground(final String... barcodes) {
        if (barcodes.length == 0) {
            App.logError(this, "need a barcode");
            return new ResultHolder(ResultType.TOO_FEW_ARGUMENTS, null);
        } else if (barcodes.length > 1) {
            App.logError(this, "too many arguments, only the first one will be processed");
        }

        final String barcode = barcodes[0];

        final String serverResponse;
        try {
            serverResponse = requestServerAbout(barcode);
        } catch (final IOException e) {
            App.logError(this, e.getMessage());
            // TODO: maybe use null-object instead of nulls?
            return new ResultHolder(ResultType.NETWORK_ERROR, null);
        }

        if (serverResponse == NO_SUCH_PRODUCT_RESPONSE) {
            return new ResultHolder(ResultType.NO_SUCH_PRODUCT, null);
        }

        try {
            return new ResultHolder(
                    ResultType.SUCCESS,
                    ServersProductsParser.parse(serverResponse, barcode));
        } catch (final ParseException e) {
            App.logError(this, e.getMessage());
            return new ResultHolder(ResultType.SERVER_RESPONSE_PARSING_ERROR, null);
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
