package vscanner.android;

public class BarcodeToolkit {
    private BarcodeToolkit() {
    }

    // TODO: add real validation
    public static boolean isValid(final String barcode) {
        return barcode != null && barcode.length() > 0;
    }
}
