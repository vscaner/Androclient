package vscanner.android.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;

public class ProductLoader extends AsyncTaskLoader<ProductLoaderResultHolder> {
    private static final String REQUEST_URL = "http://lumeria.ru/vscaner/index.php";
    private final String barcode;
    private final ServerQuerier serverQuerier;

    /**
     * @param barcode must be valid, ie (BarcodeToolkit.isValid(barcode) == true)
     * @throws IllegalArgumentException if any argument is not valid
     */
    public ProductLoader(
            final String barcode,
            final Context context) throws IllegalArgumentException {
        super(context);
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode must be valid");
        }
        this.barcode = barcode;
        this.serverQuerier = new ServerQuerier(REQUEST_URL, createPostParameters());
    }

    protected List<NameValuePair> createPostParameters() {
        final List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("bcod", barcode));
        return postParameters;
    }


    @Override
    public ProductLoaderResultHolder loadInBackground() {
        final String serverResponse;
        try {
            serverResponse = serverQuerier.queryServer();
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
