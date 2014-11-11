package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vscanner.android.App;
import vscanner.android.R;
import vscanner.android.ui.CardboardActivityBase;

final class ActivityProductNotFoundState extends ScanActivityState {
    private boolean isViewInitialized;

    protected ActivityProductNotFoundState(final ScanActivityState parent) {
        super(parent);
        if (App.getFrontActivity() == getActivity()) {
            initView();
        }
    }

    private void initView() {
        final CardboardActivityBase activity = getActivity();

        activity.setNewScanButtonVisibility(View.VISIBLE);
        activity.putToTopSlot(createCowFragment(activity));
        activity.putToMiddleSlot(createProductAdditionRequest(activity));

        activity.removeBottomButtons();
        // TODO: should the listener also change a state? Cause a user eventually may return to the activity
        activity.addBottomButtonWith(
                null,
                R.string.scan_activity_product_data_add_product_button_text);

        isViewInitialized = true;
    }

    private CowSaysFragment createCowFragment(CardboardActivityBase activity) {
        final CowSaysFragment cowSaysFragment = new CowSaysFragment();
        cowSaysFragment.setCowMood(CowState.Mood.NEUTRAL);
        cowSaysFragment.setCowsText(activity.getString(R.string.scan_activity_product_unknown));
        cowSaysFragment.setCowBackgroundVisibility(false);
        return cowSaysFragment;
    }

    private TextView createProductAdditionRequest(CardboardActivityBase activity) {
        final TextView additionRequestView = new TextView(activity);

        final int margin =
                (int) activity.getResources().getDimension(R.dimen.activity_content_horizontal_margin);

        additionRequestView.setText(R.string.scan_activity_product_addition_request);
        additionRequestView.setTextSize(18);
        additionRequestView.setBackgroundResource(R.drawable.text_cloud);
        additionRequestView.setPadding(margin, margin, margin, margin);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        params.setMargins(margin, margin, margin, margin);

        additionRequestView.setLayoutParams(params);

        return additionRequestView;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        initView();
    }

    @Override
    public void onResumeFragments() {
        if (!isViewInitialized) {
            initView();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        App.assertCondition(false);
    }

    @Override
    public void onSaveStateData(final Bundle outState) {
        isViewInitialized = false;
    }
}
