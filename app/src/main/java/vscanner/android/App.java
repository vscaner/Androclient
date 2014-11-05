package vscanner.android;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class App extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static String getStringWith(final int stringId) {
        return context.getString(stringId);
    }

    public static String getName() {
        return context.getString(R.string.app_name);
    }

    public static void logError(final Object requester, final String message) {
        Log.e(getName(), requester.getClass().toString() + ": " + message);
    }

    public static void logDebug(final Object requester, final String message) {
        Log.d(getName(), requester.getClass().toString() + ": " + message);
    }

    public static void wtf(final Object requester, final String message) {
        Log.wtf(getName(), requester.getClass().toString() + ": " + message);
    }

    /**
     * MUST DO NOTHING IN RELEASE VERSION
     */
    public static void assertCondition(final boolean condition) {
        if (condition == false) {
            throw new AssertionError();
        }
    }

    /**
     * MUST DO NOTHING IN RELEASE VERSION
     */
    public static void assertCondition(final boolean condition, final String message) {
        if (condition == false) {
            logError(App.class, message);
            throw new AssertionError();
        }
    }

    public static boolean isOnline() {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        com.google.zxing.integration.android.IntentIntegrator.titleStringId =
                R.string.barcode_app_install_request_title;
        com.google.zxing.integration.android.IntentIntegrator.messageStringId =
                R.string.barcode_app_install_request_message;
        com.google.zxing.integration.android.IntentIntegrator.yesStringId =
                R.string.barcode_app_install_request_reply_yes;
        com.google.zxing.integration.android.IntentIntegrator.noStringId =
                R.string.barcode_app_install_request_reply_no;
    }
}
