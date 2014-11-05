package vscanner.android.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public interface CardboardUI {
    public void cleanTopSlot();
    public void cleanMiddleSlot();
    public void putToTopSlot(final View view);
    public void putToMiddleSlot(final View view);
    public void putToTopSlot(final Fragment fragment);
    public void putToMiddleSlot(final Fragment fragment);
    public View getRoot();
}
