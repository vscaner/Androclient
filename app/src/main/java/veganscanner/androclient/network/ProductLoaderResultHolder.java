package veganscanner.androclient.network;

import veganscanner.androclient.App;
import veganscanner.androclient.Product;

public class ProductLoaderResultHolder {
    private final ResultType resultType;
    private final Product product;

    public static enum ResultType {
        SUCCESS,
        NO_SUCH_PRODUCT,
        TOO_FEW_ARGUMENTS,
        NETWORK_ERROR,
        SERVER_RESPONSE_PARSING_ERROR
    };

    /**
     * Should only be use for presenting resultType != ResultType.SUCCESS.
     */
    ProductLoaderResultHolder(final ResultType resultType) {
        this.resultType = resultType;
        this.product = null;
        App.assertCondition(resultType != ResultType.SUCCESS);
    }

    ProductLoaderResultHolder(final ResultType resultType, final Product product) {
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
