package vscanner.android.network.http;

import vscanner.android.App;

public final class HttpRequestResult {
    private final ResultType resultType;
    private final String requestId;
    private final String response;
    private final String errorMessage;

    public static enum ResultType {
        SUCCESS,
        NETWORK_ERROR
    }

    /**
     * @param requestId must be not null
     * @param response must be not null
     * @throws IllegalArgumentException if any argument is not valid
     */
    public static HttpRequestResult createWithSuccess(
            final String requestId,
            final String response) throws IllegalArgumentException {
        if (requestId == null) {
            throw new IllegalArgumentException("requestId must not be null");
        } else if (response == null) {
            throw new IllegalArgumentException("response must not be null");
        }
        return new HttpRequestResult(requestId, response, null);
    }

    /**
     * @param requestId must be not null
     * @param errorMessage must be not null
     * @throws IllegalArgumentException if any argument is not valid
     */
    public static HttpRequestResult createWithNetworkError(
            final String requestId,
            final String errorMessage) throws IllegalArgumentException {
        if (requestId == null) {
            throw new IllegalArgumentException("requestId must not be null");
        } else if (errorMessage == null) {
            throw new IllegalArgumentException("errorMessage must not be null");
        }
        return new HttpRequestResult(requestId, null, errorMessage);
    }

    private HttpRequestResult(
            final String requestId,
            final String response,
            final String errorMessage) {
        App.assertCondition(requestId != null);
        App.assertCondition(response != null || errorMessage != null);

        this.requestId = requestId;

        if (response != null) {
            this.response = response;
            this.errorMessage = null;
            this.resultType = ResultType.SUCCESS;
        } else {
            this.response = null;
            this.errorMessage = errorMessage;
            this.resultType = ResultType.NETWORK_ERROR;
        }
    }

    /**
     * @return not null
     */
    public ResultType getResultType() {
        return resultType;
    }

    /**
     * @return not null
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @return not null if (getResultType() == ResultType.SUCCESS)<br>
     * null otherwise
     */
    public String getResponse() {
        return response;
    }

    /**
     * @return not null if (getResultType() == ResultType.NETWORK_ERROR)<br>
     * null otherwise
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}