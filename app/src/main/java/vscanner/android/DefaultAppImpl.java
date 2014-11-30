package vscanner.android;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import vscanner.android.ui.MyActivityBase;

class DefaultAppImpl extends AppImpl {
    protected Context context;
    private MyActivityBase currentActivity;

    DefaultAppImpl(final Application application) {
        super();
        this.context = application;
        com.google.zxing.integration.android.IntentIntegrator.titleStringId =
                R.string.barcode_app_install_request_title;
        com.google.zxing.integration.android.IntentIntegrator.messageStringId =
                R.string.barcode_app_install_request_message;
        com.google.zxing.integration.android.IntentIntegrator.yesStringId =
                R.string.barcode_app_install_request_reply_yes;
        com.google.zxing.integration.android.IntentIntegrator.noStringId =
                R.string.barcode_app_install_request_reply_no;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String getStringWith(final int stringId) {
        return context.getString(stringId);
    }

    @Override
    public String getName() {
        return context.getString(R.string.app_name);
    }

    @Override
    public void onActivityPause(final MyActivityBase activity) {
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    @Override
    public void onActivityResumeFragments(final MyActivityBase activity) {
        currentActivity = activity;
    }

    @Override
    public MyActivityBase getFrontActivity() {
        return currentActivity;
    }

    @Override
    public boolean isOnline() {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
