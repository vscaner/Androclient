package veganscanner.androclient;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class App extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static String getName() {
        return context.getString(R.string.app_name);
    }

    public static void logError(final Object requester, final String message) {
        Log.e(getName(), requester.getClass().toString() + ": " + message);
    }

    public static void logDebug(final Object requester, final String message) {
        Log.e(getName(), requester.getClass().toString() + ": " + message);
    }

    public static void assertCondition(final boolean condition) {
        assert(condition);
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }
}
