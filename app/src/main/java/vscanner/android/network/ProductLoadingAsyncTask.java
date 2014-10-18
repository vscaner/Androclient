package vscanner.android.network;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;

public class ProductLoadingAsyncTask
        extends ProductAsyncTaskBase<Void, Void, ProductLoaderResultHolder> {
    private static final String REQUEST_URL = "http://lumeria.ru/vscaner/index.php";
    private final Listener listener;
    private final String barcode;

    @Override
    protected List<NameValuePair> getPostParameters() {
        final List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("bcod", barcode));
        return postParameters;
    }

    public static interface Listener {
        void onResult(final ProductLoaderResultHolder resultHolder);
    }

    /**
     * @param barcode must not be valid, ie (BarcodeToolkit.isValid(barcode) == true)
     * @throws IllegalArgumentException if any argument is not valid
     */
    public ProductLoadingAsyncTask(
            final String barcode,
            final Listener listener) throws IllegalArgumentException {
        super(REQUEST_URL);
        this.listener = listener;
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode must be valid");
        }
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
            return ProductLoaderResultHolder.createWithNetworkError(barcode);
        }

        if (serverResponse.equals(ServerConstants.RESPONSE_NO_SUCH_PRODUCT)) {
            return ProductLoaderResultHolder.createWithNoSuchProduct(barcode);
        }

        final Product product;
        try {
            product = ServersProductsParser.parse(serverResponse, barcode);
        } catch (final ParseException e) {
            App.logError(this, e.getMessage());
            return ProductLoaderResultHolder.createWithServerErrorParsingError(barcode);
        }

        return ProductLoaderResultHolder.createWithSuccess(product);
    }
}
