package vscanner.android.ui.addition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Formatter;

import vscanner.android.App;
import vscanner.android.BarcodeToolkit;
import vscanner.android.Product;
import vscanner.android.R;
import vscanner.android.ui.BarcodeHttpActionFragment;

public class ProductAdditionFragment extends BarcodeHttpActionFragment<Product> {
    private static final String BARCODE_EXTRA = "ProductAdditionFragment.BARCODE_EXTRA";
    private static final String COMPANY_NAME_EXTRA = "ProductAdditionFragment.COMPANY_NAME_EXTRA";
    private static final String PRODUCT_NAME_EXTRA = "ProductAdditionFragment.PRODUCT_NAME_EXTRA";
    private static final String CHECKED_COW_EXTRA = "ProductAdditionFragment.CHECKED_COW_EXTRA";
    private static final String WAS_TESTED_CHECKBOX_EXTRA = "ProductAdditionFragment.WAS_TESTED_CHECKBOX_EXTRA";
    private static final int MINIMUM_NAMES_LENGTH = 3;

    private static enum CheckedCow {NONE, NOT_VEGETARIAN, VEGETARIAN, VEGAN}

    private String barcode;
    private CheckedCow checkedCow = CheckedCow.NONE;

    /**
     * @param barcode must be a valid barcode (i.e. BarcodeToolkit.isValid(barcode)==true).
     * @throws java.lang.IllegalArgumentException if any argument is invalid.
     */
    public static ProductAdditionFragment createFor(final String barcode) {
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("received barcode is invalid");
        }
        final ProductAdditionFragment instance = new ProductAdditionFragment();
        instance.barcode = barcode;
        return instance;
    }

    @Override
    public final String getErrorMessage() {
        final View root = getView();
        if (root != null) {
            final Formatter formatter = new Formatter();

            final String companyName =
                    ((TextView) root.findViewById(R.id.textedit_company_name)).getText().toString();
            if (companyName.length() < MINIMUM_NAMES_LENGTH) {
                final String message =
                        getString(R.string.product_addition_fragment_company_name_length_error_message);
                return formatter.format(message, MINIMUM_NAMES_LENGTH - 1).toString();
            }

            final String productName =
                    ((TextView) root.findViewById(R.id.textedit_product_name)).getText().toString();
            if (productName.length() < MINIMUM_NAMES_LENGTH) {
                final String message =
                        getString(R.string.product_addition_fragment_product_name_length_error_message);
                return formatter.format(message, MINIMUM_NAMES_LENGTH - 1).toString();
            }

            if (checkedCow == CheckedCow.NONE) {
                return getString(R.string.product_addition_fragment_product_status_error_message);
            }

            return null;
        }
        return null;
    }

    @Override
    public final Product getResult() {
        final View root = getView();
        if (root != null && getErrorMessage() == null) {
            final String companyName =
                    ((TextView) root.findViewById(R.id.textedit_company_name)).getText().toString();

            final String productName =
                    ((TextView) root.findViewById(R.id.textedit_product_name)).getText().toString();

            final Product.Status status;
            switch (checkedCow) {
                case NOT_VEGETARIAN:
                    status = Product.Status.NOT_VEGETARIAN;
                    break;
                case VEGETARIAN:
                    status = Product.Status.VEGETARIAN;
                    break;
                case VEGAN:
                    status = Product.Status.VEGAN;
                    break;
                case NONE:
                    App.error("getErrorMessage() return null but status is invalid!");
                    status = Product.Status.NOT_VEGETARIAN;
                    break;
                default:
                    App.error("CheckedCow's element is not handled");
                    status = Product.Status.NOT_VEGETARIAN;
            }

            final boolean wasTestedOnAnimals =
                    ((CheckBox) root.findViewById(R.id.checkbox_was_tested_on_animals)).isChecked();

            try {
                return new Product(barcode, productName, companyName, status, wasTestedOnAnimals);
            } catch (final IllegalArgumentException e) {
                App.error("product validation check has passed, bad a product refused to be created\n"
                        + "exception message:\n"
                        + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        final View root = createView(inflater, container);

        if (savedInstanceState != null) {
            restoreStateWith(savedInstanceState, root);
        }

        return root;
    }

    private View createView(final LayoutInflater inflater, final ViewGroup container) {
        final View root =
                inflater.inflate(
                        R.layout.product_addition_fragment_layout,
                        container,
                        false);

        final View.OnClickListener cowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                switch (view.getId()) {
                    case R.id.layout_not_vegetarian:
                        checkedCow = CheckedCow.NOT_VEGETARIAN;
                        break;
                    case R.id.layout_vegetarian:
                        checkedCow = CheckedCow.VEGETARIAN;
                        break;
                    case R.id.layout_vegan:
                        checkedCow = CheckedCow.VEGAN;
                        break;
                    default:
                        App.assertCondition(false, "some cow mood layout is not handled");
                }
                UpdateCheckedCowViewBy(getView());
            }
        };

        root.findViewById(R.id.layout_not_vegetarian).setOnClickListener(cowClickListener);
        root.findViewById(R.id.layout_vegetarian).setOnClickListener(cowClickListener);
        root.findViewById(R.id.layout_vegan).setOnClickListener(cowClickListener);
        return root;
    }

    private void UpdateCheckedCowViewBy(final View root) {
        if (root != null) {
            final View notVegetarianCowImage = root.findViewById(R.id.imageview_not_vegetarian);
            final View vegetarianCowImage = root.findViewById(R.id.imageview_vegetarian);
            final View veganCowImage = root.findViewById(R.id.imageview_vegan);

            final int transparentColor = getResources().getColor(android.R.color.transparent);
            switch (checkedCow) {
                case NONE:
                    notVegetarianCowImage.setBackgroundColor(transparentColor);
                    vegetarianCowImage.setBackgroundColor(transparentColor);
                    veganCowImage.setBackgroundColor(transparentColor);
                    break;
                case NOT_VEGETARIAN:
                    notVegetarianCowImage.setBackgroundResource(R.drawable.cow_background);
                    vegetarianCowImage.setBackgroundColor(transparentColor);
                    veganCowImage.setBackgroundColor(transparentColor);
                    break;
                case VEGETARIAN:
                    notVegetarianCowImage.setBackgroundColor(transparentColor);
                    vegetarianCowImage.setBackgroundResource(R.drawable.cow_background);
                    veganCowImage.setBackgroundColor(transparentColor);
                    break;
                case VEGAN:
                    notVegetarianCowImage.setBackgroundColor(transparentColor);
                    vegetarianCowImage.setBackgroundColor(transparentColor);
                    veganCowImage.setBackgroundResource(R.drawable.cow_background);
                    break;
                default:
                    App.assertCondition(false, "some CheckedCow instance is not handled");
            }
        } else {
            App.error("can't set up correct cow without the root view");
        }
    }

    private void restoreStateWith(final Bundle savedInstanceState, final View root) {
        if (savedInstanceState != null && root != null) {
            barcode = savedInstanceState.getString(BARCODE_EXTRA);
            final String companyName = savedInstanceState.getString(COMPANY_NAME_EXTRA);
            final String productName = savedInstanceState.getString(PRODUCT_NAME_EXTRA);
            final int checkedCowOrdinal = savedInstanceState.getInt(CHECKED_COW_EXTRA);
            checkedCow = CheckedCow.values()[checkedCowOrdinal];
            final boolean wasTestedOnAnimals = savedInstanceState.getBoolean(WAS_TESTED_CHECKBOX_EXTRA);

            ((TextView) root.findViewById(R.id.textedit_company_name)).setText(companyName);
            ((TextView) root.findViewById(R.id.textedit_product_name)).setText(productName);
            UpdateCheckedCowViewBy(root);
            ((CheckBox) root.findViewById(R.id.checkbox_was_tested_on_animals)).setChecked(wasTestedOnAnimals);
        } else {
            App.error("can't restore state without root or saved state");
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        final View root = getView();
        if (root != null) {
            outState.putString(BARCODE_EXTRA, barcode);

            outState.putString(
                    COMPANY_NAME_EXTRA,
                    ((TextView) root.findViewById(R.id.textedit_company_name)).getText().toString());

            outState.putString(
                    PRODUCT_NAME_EXTRA,
                    ((TextView) root.findViewById(R.id.textedit_product_name)).getText().toString());

            outState.putInt(CHECKED_COW_EXTRA, checkedCow.ordinal());

            outState.putBoolean(
                    WAS_TESTED_CHECKBOX_EXTRA,
                    ((CheckBox) root.findViewById(R.id.checkbox_was_tested_on_animals)).isChecked());
        } else {
            App.error(this, "how the fragment is supposed to save its state with getView()==null?");
        }
    }
}
