package veganscanner.androclient.network;

import veganscanner.androclient.App;
import veganscanner.androclient.BarcodeToolkit;
import veganscanner.androclient.Product;

public class ProductLoaderResultHolder {
    private final ResultType resultType;
    private final Product product;

    public static enum ResultType {
        SUCCESS,
        NO_SUCH_PRODUCT,
        NETWORK_ERROR,
        SERVER_RESPONSE_PARSING_ERROR
    };

    /**
     * Should only be use for presenting resultType != ResultType.SUCCESS.
     */
    ProductLoaderResultHolder(final ResultType resultType, final String barcode) {
        App.assertCondition(resultType != null);
        App.assertCondition(BarcodeToolkit.isValid(barcode));
        this.resultType = resultType;
        this.product = new Product(barcode);
        App.assertCondition(resultType != ResultType.SUCCESS);
    }

    ProductLoaderResultHolder(final ResultType resultType, final Product product) {
        App.assertCondition(resultType != null);
        App.assertCondition(product != null);
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
     * @return not null.
     */
    public Product getProduct() {
        return product;
    }
}
