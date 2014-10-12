package veganscanner.androclient.network;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import veganscanner.androclient.App;

public class ProductLoadingAsyncTask
        extends ProductAsyncTaskBase<Void, Void, ProductLoaderResultHolder> {
    private static final String REQUEST_URL = "http://lumeria.ru/vscaner/index.php";
    private final Listener listener;
    private final String barcode;

    @Override
    protected List<NameValuePair> getPostParameters() {
        final List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair(ServerConstants.POST_PARAMETER_BARCODE, barcode));
        return postParameters;
    }

    public static interface Listener {
        void onResult(final ProductLoaderResultHolder resultHolder);
    }

    /**
     * @param barcode must not be null.
     */
    public ProductLoadingAsyncTask(final String barcode, final Listener listener) {
        super(REQUEST_URL);
        this.listener = listener;
        if (barcode == null) {
            throw new IllegalArgumentException("barcode must not be null");
        }
        App.assertCondition(barcode.length() > 0);
        this.barcode = barcode;
    }

    @Override
    protected void onPostExecute(final ProductLoaderResultHolder resultHolder) {
        if (listener != null) {
            listener.onResult(resultHolder);
        }
    }

    @Override
    protected ProductLoaderResultHolder doInBackground(final Void... voids) {
        final String serverResponse;
        try {
            serverResponse = queryServer();
        } catch (final IOException e) {
            App.logError(this, e.getMessage());
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.NETWORK_ERROR, barcode);
        }

        if (serverResponse.equals(ServerConstants.RESPONSE_NO_SUCH_PRODUCT)) {
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.NO_SUCH_PRODUCT, barcode);
        }

        try {
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.SUCCESS,
                    ServersProductsParser.parse(serverResponse, barcode));
        } catch (final ParseException e) {
            App.logError(this, e.getMessage());
            return new ProductLoaderResultHolder(
                    ProductLoaderResultHolder.ResultType.SERVER_RESPONSE_PARSING_ERROR, barcode);
        }
    }
}
