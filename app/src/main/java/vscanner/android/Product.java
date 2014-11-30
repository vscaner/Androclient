package vscanner.android;

import java.io.Serializable;

public final class Product implements Serializable {
    public static enum Status { NOT_VEGETARIAN, VEGETARIAN, VEGAN }
    private final String barcode;
    private final String name;
    private final String company;
    private final Status status;
    private final boolean wasTestedOnAnimals;
    private final boolean isFullyInitialized;

    /**
     * @param barcode must be valid (BarcodeToolkit.isValid(..) == true).
     * @throws java.lang.IllegalArgumentException if barcode is not valid.
     */
    public Product(final String barcode) throws IllegalArgumentException {
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode is not valid");
        }
        this.barcode = barcode;
        this.name = "";
        this.company = "";
        this.status = Status.NOT_VEGETARIAN;
        this.wasTestedOnAnimals = true;
        this.isFullyInitialized = false;
    }

    /**
     * @param barcode must be valid (BarcodeToolkit.isValid(..) == true).
     * @param status must be not null
     * @throws java.lang.IllegalArgumentException if any parameter is invalid.
     */
    public Product(
            final String barcode,
            final String name,
            final String company,
            final Status status,
            final boolean wasTestedOnAnimals) throws IllegalArgumentException  {
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode is not valid");
        } else if (status == null) {
            throw new IllegalArgumentException("status is null");
        }
        App.assertCondition(name != null);
        App.assertCondition(company != null);
        this.barcode = barcode;
        this.name = name;
        this.company = company;
        this.status = status;
        this.wasTestedOnAnimals = wasTestedOnAnimals;
        this.isFullyInitialized = true;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public boolean isVegan() {
        return status == Status.VEGAN;
    }

    public boolean isVegetarian() {
        return status == Status.VEGAN || status == Status.VEGETARIAN;
    }

    /**
     * @return not null.
     */
    public Status getStatus() {
        return status;
    }

    public boolean wasTestedOnAnimals() {
        return wasTestedOnAnimals;
    }

    /**
     * Most likely a product will not be fullyInitialized if it was not created by a user or by the server.
     * Product's barcode is guaranteed to be valid.
     */
    public boolean isFullyInitialized() {
        return isFullyInitialized;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\"Product\": {").append("\n")
               .append("\t\"name\": ").append(name).append("\n")
               .append("\t\"barcode\": ").append(barcode).append("\n")
               .append("\t\"isFullyInitialized\": ").append(isFullyInitialized).append("\n")
               .append("}");
        return builder.toString();
    }
}
