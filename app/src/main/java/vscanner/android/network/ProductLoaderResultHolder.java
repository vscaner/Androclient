package vscanner.android.network;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;

public final class ProductLoaderResultHolder {
    private final ResultType resultType;
    private final Product product;

    public static enum ResultType {
        SUCCESS,
        NO_SUCH_PRODUCT,
        NETWORK_ERROR,
        SERVER_RESPONSE_PARSING_ERROR
    };

    /**
     * @param product must be be product.isFullyInitialized()
     * @throws IllegalArgumentException if any argument is not valid
     */
    public static ProductLoaderResultHolder createWithSuccess(
            final Product product) throws IllegalArgumentException {
        if (!product.isFullyInitialized()) {
            throw new IllegalArgumentException("received not fully initialized product: " + product);
        }
        return new ProductLoaderResultHolder(ResultType.SUCCESS, product);
    }

    /**
     * @param barcode must be valid, ie (BarcodeToolkit.isValid(barcode) == true)
     * @throws IllegalArgumentException if any argument is not valid
     */
    public static ProductLoaderResultHolder createWithNoSuchProduct(
            final String barcode) throws IllegalArgumentException {
        return createWith(ResultType.NO_SUCH_PRODUCT, barcode);
    }

    /**
     * @param barcode must be valid, ie (BarcodeToolkit.isValid(barcode) == true)
     * @throws IllegalArgumentException if any argument is not valid
     */
    public static ProductLoaderResultHolder createWithNetworkError(
            final String barcode) throws IllegalArgumentException {
        return createWith(ResultType.NETWORK_ERROR, barcode);
    }

    /**
     * @param barcode must be valid, ie (BarcodeToolkit.isValid(barcode) == true)
     * @throws IllegalArgumentException if any argument is not valid
     */
    public static ProductLoaderResultHolder createWithServerErrorParsingError(
            final String barcode) throws IllegalArgumentException {
        return createWith(ResultType.SERVER_RESPONSE_PARSING_ERROR, barcode);
    }

    private static ProductLoaderResultHolder createWith(
            final ResultType resultType,
            final String barcode) {
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("received an invalid barcode");
        }
        return new ProductLoaderResultHolder(resultType, new Product(barcode));
    }

    private ProductLoaderResultHolder(final ResultType resultType, final Product product) {
        App.assertCondition(resultType != null);
        App.assertCondition(product != null);
        if (resultType == ResultType.SUCCESS) {
            App.assertCondition(product.isFullyInitialized());
        } else {
            App.assertCondition(!product.isFullyInitialized());
        }

        this.resultType = resultType;
        this.product = product;
    }

    /**
     * @return not null.
     */
    public ResultType getResultType() {
        return resultType;
    }

    /**
     * @return not null, <br>
     * but Product.isFullyInitialized() will be false <br>
     * if getResultType() != ResultType.SUCCESS
     */
    public Product getProduct() {
        return product;
    }
}