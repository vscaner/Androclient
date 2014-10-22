package vscanner.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import vscanner.android.R;

public class ProductNotFoundDialogFragment extends DialogFragment {
    private String productBarcode;

    public static ProductNotFoundDialogFragment create(final String productBarcode) {
        final ProductNotFoundDialogFragment dialogFragment = new ProductNotFoundDialogFragment();
        dialogFragment.productBarcode = productBarcode;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle(R.string.product_not_found_dialog_title);
        dialogBuilder.setMessage(R.string.product_not_found_dialog_text);

        dialogBuilder.setPositiveButton(
                R.string.product_not_found_positive_button_text,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dismiss();

                Intent myIntent = new Intent(getActivity(), ProductAdditionActivity.class);
                myIntent.putExtra(ProductAdditionActivity.BARCODE_EXTRA, productBarcode);
                getActivity().startActivity(myIntent);
            }
        });

        dialogBuilder.setNegativeButton(
                R.string.product_not_found_negative_button_text,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dismiss();
            }
        });

        return dialogBuilder.create();
    }
}
