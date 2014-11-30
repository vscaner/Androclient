package vscanner.android.ui.scan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vscanner.android.App;
import vscanner.android.Product;
import vscanner.android.R;

public class ProductDescriptionFragment extends Fragment {
    private Product product;

    /**
     * @param product must be not null and isFullyInitialized() == true
     * @throws java.lang.IllegalArgumentException if product is invalid
     */
    public static ProductDescriptionFragment create(final Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product must not be null");
        } else if (!product.isFullyInitialized()) {
            throw new IllegalArgumentException("product must be fully initialized");
        }
        final ProductDescriptionFragment fragment = new ProductDescriptionFragment();
        fragment.product = product;
        return fragment;
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        final View root =
                inflater.inflate(
                        R.layout.product_description_fragment_layout,
                        container,
                        false);

        if (savedInstanceState != null) {
            product = (Product) savedInstanceState.getSerializable(Product.class.toString());
        }

        if (product != null) {
            ((TextView) root.findViewById(R.id.text_barcode)).
                    setText(product.getBarcode());
            ((TextView) root.findViewById(R.id.text_product_name)).
                    setText(product.getName());
            ((TextView) root.findViewById(R.id.text_company_name)).
                    setText(product.getCompany());

            final ImageView animalTestsImage = (ImageView) root.findViewById(R.id.imageview_tests_on_animals);
            final TextView animalsTestsText = (TextView) root.findViewById(R.id.text_tests_on_animals);
            if (product.wasTestedOnAnimals()) {
                animalTestsImage.setImageResource(R.drawable.tested_on_animals_true);
                animalsTestsText.setText(R.string.product_description_fragment_tested_on_animals_title);
            } else {
                animalTestsImage.setImageResource(R.drawable.tested_on_animals_false);
                animalsTestsText.setText(R.string.product_description_fragment_not_tested_on_animals_title);
            }
        } else {
            App.error(this, "product is null, invalid data will be shown");
            ((TextView) root.findViewById(R.id.text_barcode)).setText(R.string.raw_internal_error);
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Product.class.toString(), product);
    }
}
