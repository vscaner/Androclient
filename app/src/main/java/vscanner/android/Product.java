package vscanner.android;

public final class Product {
    private final String barcode;
    private final String name;
    private final String company;
    private final boolean isVegan;
    private final boolean isVegetarian;
    private final boolean wasTestedOnAnimals;
    private final String comment;
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
        this.isVegan = false;
        this.isVegetarian = false;
        this.wasTestedOnAnimals = true;
        this.comment = "";
        this.isFullyInitialized = false;
    }

    /**
     * @param barcode must be valid (BarcodeToolkit.isValid(..) == true).
     * @throws java.lang.IllegalArgumentException if barcode is not valid.
     */
    public Product(
            final String barcode,
            final String name,
            final String company,
            final boolean isVegan,
            final boolean isVegetarian,
            final boolean wasTestedOnAnimals,
            final String comment) throws IllegalArgumentException  {
        if (!BarcodeToolkit.isValid(barcode)) {
            throw new IllegalArgumentException("barcode is not valid");
        }
        App.assertCondition(name != null);
        App.assertCondition(company != null);
        App.assertCondition(comment != null);
        this.barcode = barcode;
        this.name = name;
        this.company = company;
        this.isVegan = isVegan;
        this.isVegetarian = isVegetarian;
        this.wasTestedOnAnimals = wasTestedOnAnimals;
        this.comment = comment;
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
        return isVegan;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public boolean wasTestedOnAnimals() {
        return wasTestedOnAnimals;
    }

    public String getComment() {
        return comment;
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
