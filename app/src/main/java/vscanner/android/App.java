package vscanner.android;

import android.app.Application;
import android.content.Context;

import vscanner.android.ui.MyActivityBase;

public class App extends Application {
    public static interface ImplCreator {
        AppImpl create();
    }

    private static final class DefaultImplCreator implements ImplCreator {
        @Override
        public AppImpl create() {
            return new DefaultAppImpl(applicationInstance);
        }
    }

    private static Application applicationInstance;
    private static ImplCreator implCreator = new DefaultImplCreator();
    private static AppImpl impl;

    public static void setImplCreator(final ImplCreator implCreator) {
        if (implCreator != null) {
            App.implCreator = implCreator;
        }
    }

    @Override
    public void onCreate() {
        applicationInstance = this;
        impl = implCreator.create();
        if (impl == null) {
            throw new Error("wtf, implCreator created null!");
        }
    }

    public static Context getContext() {
        return impl.getContext();
    }

    public static String getStringWith(final int stringId) {
        return impl.getStringWith(stringId);
    }

    public static String getName() {
        return impl.getName();
    }

    public static void logError(final Object requester, final String message) {
        impl.logError(requester, message);
    }

    public static void logDebug(final Object requester, final String message) {
        impl.logDebug(requester, message);
    }

    public static void logInfo(final Object requester, final String message) {
        impl.logInfo(requester, message);
    }

    public static void logInfo(final Object requester, final String message, final Exception e) {
        impl.logInfo(requester, message, e);
    }

    public static void wtf(final Object requester, final String message) {
        impl.wtf(requester, message);
    }

    public static void assertCondition(final boolean condition) {
        impl.assertCondition(condition);
    }

    public static void assertCondition(final boolean condition, final String message) {
        impl.assertCondition(condition, message);
    }

    public static void error(final String message) {
        impl.error(message);
    }

    public static boolean isOnline() {
        return impl.isOnline();
    }

    public static void onActivityPause(final MyActivityBase activity) {
        impl.onActivityPause(activity);
    }

    public static void onActivityResumeFragments(final MyActivityBase activity) {
        impl.onActivityResumeFragments(activity);
    }

    /**
     * NOTE that an Activity IS NOT considered as the front one
     * before its 'onResumeFragments()' is called<br>
     * This means that there's is NO front activity during a 'onCreate()' call of an initializing activity
     *
     * @return front activity
     */
    public static MyActivityBase getFrontActivity() {
        return impl.getFrontActivity();
    }
}
