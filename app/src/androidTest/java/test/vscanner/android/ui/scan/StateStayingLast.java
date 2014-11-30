package test.vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;

import vscanner.android.ui.scan.ScanActivity;
import vscanner.android.ui.scan.ScanActivityState;

final class StateStayingLast extends ScanActivityState {
    public StateStayingLast(ScanActivity scanActivity) {
        super(scanActivity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
    }
    @Override
    public void onResumeFragments() {
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }
    @Override
    public void onSaveStateData(Bundle outState) {
    }
    private static final class Restorer implements ScanActivityState.Restorer {
        @Override
        public ScanActivityState restoreFor(final ScanActivity activity) {
            return new StateStayingLast(activity);
        }
        @Override
        public boolean doesStayLast() {
            return true;
        }
    }
    @Override
    public ScanActivityState.Restorer save() {
        return new Restorer();
    }
}