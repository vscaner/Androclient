package vscanner.android.network.http;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class MemoryOfHttpService {
    private static final int memoryLengthInSeconds = 10;

    private final Map<String, HttpRequestResult> memorizedRequests = new HashMap<String, HttpRequestResult>();
    private final ScheduledExecutorService forgettingExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> lastScheduledFuture;
    private final Runnable actionForgetRequests = new Runnable() {
        @Override
        public void run() {
            synchronized (MemoryOfHttpService.this) {
                memorizedRequests.clear();
            }
        }
    };

    /**
     * if requestResult is null, a call will do nothing
     */
    public synchronized void memorize(final HttpRequestResult requestResult) {
        if (requestResult == null) {
            return;
        }

        if (lastScheduledFuture != null) {
            lastScheduledFuture.cancel(false);
        }

        memorizedRequests.put(requestResult.getRequestId(), requestResult);
        lastScheduledFuture =
                forgettingExecutor.schedule(
                        actionForgetRequests, memoryLengthInSeconds, TimeUnit.SECONDS);
    }

    public synchronized HttpRequestResult forgetRequestWith(final String requestId) {
        return memorizedRequests.remove(requestId);
    }
}
