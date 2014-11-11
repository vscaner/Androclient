package vscanner.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import vscanner.android.App;
import vscanner.android.R;

// TODO: create getMiddleFragment, .. methods - they're needed for blinkless states changing in ScanActivity
public abstract class CardboardActivityBase extends MyActivityBase {
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

    public final void cleanTopSlot() {
        final CardboardSlotData slotData =
                new CardboardSlotData(
                        CardboardSlotData.Position.TOP,
                        findViewById(android.R.id.content));
        clean(slotData);
    }

    public final void cleanMiddleSlot() {
        final CardboardSlotData slotData =
                new CardboardSlotData(
                        CardboardSlotData.Position.MIDDLE,
                        findViewById(android.R.id.content));
        clean(slotData);
    }

    public final void putToTopSlot(final View view) {
        if (view != null) {
            putView(view, CardboardSlotData.Position.TOP);
        } else {
            cleanTopSlot();
        }
    }

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

    public final void putToTopSlot(final Fragment fragment) {
        if (fragment != null) {
            putFragment(fragment, CardboardSlotData.Position.TOP);
        } else {
            cleanTopSlot();
        }
    }

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

    public final void setNewScanButtonVisibility(final int visibility) {
        findViewById(R.id.button_new_scan).setVisibility(visibility);
    }

    public final void removeBottomButtons() {
        ((ViewGroup) findViewById(R.id.buttons_container)).removeAllViews();
    }

    /**
     * @return created button, not null
     */
    public final Button addBottomButtonWith(
            final View.OnClickListener onClickListener,
            final int stringId) {
        final Button button = createGreenButtonWith(onClickListener, stringId);
        validateButtonsPositions();
        return button;
    }

    private Button createGreenButtonWith(final View.OnClickListener onClickListener, final int stringId) {
        final Button button = new Button(this);

        button.setBackgroundResource(R.drawable.green_button_selector);
        button.setOnClickListener(onClickListener);
        button.setText(stringId);
        button.setTextSize(14);
        button.setTextColor(getResources().getColor(android.R.color.white));
        RelativeLayout.LayoutParams buttonParams =
                new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        button.setLayoutParams(buttonParams);

        final RelativeLayout singleButtonContainer = new RelativeLayout(this);

        final LinearLayout.LayoutParams singleButtonContainerParams =
                new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        singleButtonContainerParams.gravity = Gravity.CENTER_VERTICAL;
        singleButtonContainer.setLayoutParams(singleButtonContainerParams);
        singleButtonContainer.addView(button);

        ((ViewGroup) findViewById(R.id.buttons_container)).addView(singleButtonContainer);
        return button;
    }

    private void validateButtonsPositions() {
        final LinearLayout buttonsContainer = (LinearLayout) findViewById(R.id.buttons_container);

        final int buttonsCount = buttonsContainer.getChildCount();
        final float weight = 1.0f / (float) buttonsCount;

        for (int index = 0; index < buttonsCount; ++index) {
            final View button = buttonsContainer.getChildAt(index);

            final LinearLayout.LayoutParams params =
                    (LinearLayout.LayoutParams) button.getLayoutParams();

            params.weight = weight;
        }
    }
}
