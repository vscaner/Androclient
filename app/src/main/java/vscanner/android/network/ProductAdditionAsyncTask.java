package vscanner.android.network;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vscanner.android.App;
import vscanner.android.Product;

public final class ProductAdditionAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private static final String URL = "http://lumeria.ru/vscaner/tobase.php";
    private final Product product;
    private final Listener listener;
    private final ServerQuerier serverQuerier;

    public static interface Listener {
        void onResult(final boolean success);
    }

    /**
     * @param product must be not null and (product.isFullyInitialized() == true)
     * @throws IllegalArgumentException any argument is invalid
     */
    public ProductAdditionAsyncTask(
            final Product product,
            final Listener listener) throws IllegalArgumentException {
        if (product == null || !product.isFullyInitialized()) {
            throw new IllegalArgumentException("product argument is invalid");
        }

        this.product = product;
        this.listener = listener;
        this.serverQuerier = new ServerQuerier(URL, createPostParameters());
    }

    protected List<NameValuePair> createPostParameters() {
        final List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("bcod", product.getBarcode()));
        postParameters.add(new BasicNameValuePair("name", product.getName()));
        postParameters.add(new BasicNameValuePair("companyname", product.getCompany()));

        addLogicalInversedParametersTo(postParameters);

        postParameters.add(new BasicNameValuePair("gmo", "0"));
        postParameters.add(
                new BasicNameValuePair(
                    "animals",
                    String.valueOf(product.wasTestedOnAnimals())));
        postParameters.add(new BasicNameValuePair("comment", "")); // TODO: empty comment? Maybe not pass it at all?

        return postParameters;
    }

    private void addLogicalInversedParametersTo(final List<NameValuePair> postParameters) {
        // NOTE: Logical inversion with the 2 below parameters is intentional,
        // because server mistakenly uses negative naming convention for these 2.
        // (Like that: isNotVegan instead of isVegan.)
        postParameters.add(new BasicNameValuePair(
                "veganstatus",
                String.valueOf(!product.isVegan())));
        postParameters.add(new BasicNameValuePair(
                "vegetstatus",
                String.valueOf(!product.isVegetarian())));
    }

    @Override
    protected Boolean doInBackground(final Void... voids) {
        try {
            serverQuerier.queryServer();
        } catch (final IOException e) {
            App.logError(this, "product addition query failed for some reason: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result)
    {
        if (listener != null) {
            listener.onResult(result);
        }
    }
}
