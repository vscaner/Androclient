package vscanner.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.Arrays;

import vscanner.android.ui.MyActivityBase;

public abstract class AppImpl {
    protected AppImpl() {
    }

    public Context getContext() {
        return null;
    }

    public String getStringWith(final int stringId) {
        return "";
    }

    public String getName() {
        return "";
    }

    public void logError(final Object requester, final String message) {
        Log.e(getName(), requester.getClass().toString() + ": " + message);
    }

    public void logDebug(final Object requester, final String message) {
        Log.d(getName(), requester.getClass().toString() + ": " + message);
    }

    public void logInfo(final Object requester, final String message) {
        Log.i(getName(), requester.getClass().toString() + ": " + message);
    }

    public void logInfo(final Object requester, final String message, final Exception e) {
        Log.i(getName(), requester.getClass().toString() + ": " + message, e);
    }

    public void wtf(final Object requester, final String message) {
        Log.wtf(getName(), requester.getClass().toString() + ": " + message);
    }

    public void assertCondition(final boolean condition) {
        if (condition == false) {
            if (BuildConfig.DEBUG) {
                throw new AssertionError();
            } else {
                Crashlytics.log(
                        "ASSERTATION FAILED!\n"
                                + Arrays.toString(Thread.currentThread().getStackTrace()));
            }
        }
    }

    public void assertCondition(final boolean condition, final String message) {
        if (condition == false) {
            logError(App.class, message);
            if (BuildConfig.DEBUG) {
                throw new AssertionError();
            } else {
                Crashlytics.log(
                        "ASSERTATION FAILED! message: '" + message + "'\n"
                                + Arrays.toString(Thread.currentThread().getStackTrace()));
            }
        }
    }

    public void error(final String message) {
        logError(App.class, message);
        if (BuildConfig.DEBUG) {
            throw new Error(message);
        } else {
            Crashlytics.log(
                    "ERROR! message: '" + message + "'\n"
                            + Arrays.toString(Thread.currentThread().getStackTrace()));
        }
    }

    public void error(final Object requester, final String message) {
        logError(requester, message);
        error(message);
    }

    public boolean isOnline() {
        return false;
    }

    public void onActivityPause(final MyActivityBase activity) {
    }

    public void onActivityResumeFragments(final MyActivityBase activity) {
    }

    public MyActivityBase getFrontActivity() {
        return null;
    }
}
