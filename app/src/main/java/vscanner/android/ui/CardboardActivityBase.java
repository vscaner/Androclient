package vscanner.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import vscanner.android.App;
import vscanner.android.R;

public abstract class CardboardActivityBase extends MyActivityBase implements CardboardUI {
    @Override
    public final void cleanTopSlot() {
        final CardboardSlotData slotData =
                new CardboardSlotData(
                        CardboardSlotData.Position.TOP,
                        findViewById(android.R.id.content));
        clean(slotData);
    }

    @Override
    public final void cleanMiddleSlot() {
        final CardboardSlotData slotData =
                new CardboardSlotData(
                        CardboardSlotData.Position.MIDDLE,
                        findViewById(android.R.id.content));
        clean(slotData);
    }

    @Override
    public final void putToTopSlot(final View view) {
        if (view != null) {
            putView(view, CardboardSlotData.Position.TOP);
        } else {
            cleanTopSlot();
        }
    }

    @Override
    public final void putToMiddleSlot(final View view) {
        if (view != null) {
            putView(view, CardboardSlotData.Position.MIDDLE);
        } else {
            cleanMiddleSlot();
        }
    }

    private void putView(final View view, final CardboardSlotData.Position position) {
        final CardboardSlotData slotData =
                new CardboardSlotData(position, findViewById(android.R.id.content));

        clean(slotData);
        slotData.getContainer().addView(view);
    }

    private void clean(final CardboardSlotData slot) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment fragment = fragmentManager.findFragmentByTag(slot.getFragmentTag());
        if (fragment != null) {
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }

        slot.getContainer().removeAllViews();
    }

    @Override
    public final void putToTopSlot(final Fragment fragment) {
        if (fragment != null) {
            putFragment(fragment, CardboardSlotData.Position.TOP);
        } else {
            cleanTopSlot();
        }
    }

    @Override
    public final void putToMiddleSlot(final Fragment fragment) {
        if (fragment != null) {
            putFragment(fragment, CardboardSlotData.Position.MIDDLE);
        } else {
            cleanMiddleSlot();
        }
    }

    private void putFragment(final Fragment fragment, final CardboardSlotData.Position position) {
        final CardboardSlotData slotData =
                new CardboardSlotData(position, findViewById(android.R.id.content));

        clean(slotData);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(slotData.getContainerId(), fragment, slotData.getFragmentTag());
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.cardboard_activity_layout);
    }

    @Override
    public final void setContentView(final int layoutId) {
        App.logError(this, "no child is allowed to use its own layout");
        App.assertCondition(false);
    }

    @Override
    public final View getRoot() {
        return findViewById(android.R.id.content);
    }
}
