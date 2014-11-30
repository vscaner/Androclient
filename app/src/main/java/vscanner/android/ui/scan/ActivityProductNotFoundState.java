package vscanner.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.R;
import vscanner.android.ui.CardboardActivityBase;
import vscanner.android.ui.UIConstants;
import vscanner.android.ui.addition.ProductAdditionActivity;

// TODO: display the barcode
final class ActivityProductNotFoundState extends ScanActivityState {
    private static final String BARCODE_EXTRA = "ActivityProductNotFoundState.BARCODE_EXTRA";
    private String barcode;
    private boolean isViewInitialized;

    /**
     * must be called only before onCreate() call
     */
    protected ActivityProductNotFoundState(final ScanActivityState parent) {
        super(parent);
        if (App.getFrontActivity() == getActivity()) {
            initView();
        }
    }

    /**
     * @param barcode must be a valid barcode (ie BarcodeToolkit.isValid(barcode) == true)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public ActivityProductNotFoundState(final ScanActivityState parent, final String barcode) {
        super(parent);
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode is invalid");
        }
        this.barcode = barcode;

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
                R.string.scan_activity_product_data_add_product_button_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProductAdditionActivity.startFor(barcode, activity);
                    }
                }
        );

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
        additionRequestView.setTextSize(UIConstants.MEDIUM_TEXT_SIZE);
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
        if (savedInstanceState != null) {
            barcode = savedInstanceState.getString(BARCODE_EXTRA);
        }
        App.assertCondition(BarcodeToolkit.isValid(barcode));
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
        outState.putString(BARCODE_EXTRA, barcode);
    }

    private static final class Restorer implements ScanActivityState.Restorer {
        private final String barcode;
        public Restorer(final String barcode) {
            this.barcode = barcode;
        }
        @Override
        public ScanActivityState restoreFor(final ScanActivity activity) {
            if (activity == null) {
                throw new IllegalArgumentException("activity must not be null");
            } else if (activity != App.getFrontActivity()) {
                throw new IllegalStateException("restoring state while activity is not in front!");
            }
            return new ActivityProductNotFoundState(activity, barcode);
        }
        @Override
        public boolean doesStayLast() {
            return false;
        }
    }

    private ActivityProductNotFoundState(final ScanActivity activity, final String barcode) {
        super(activity);
        this.barcode = barcode;
        initView();
    }

    public Restorer save() {
        return new Restorer(barcode);
    }
}
