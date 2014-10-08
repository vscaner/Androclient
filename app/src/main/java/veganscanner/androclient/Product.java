package veganscanner.androclient;

public final class Product {
    private final String barcode;
    private final String name;
    private final String company;
    private final boolean isVegan;
    private final boolean isVegetarian;
    private final boolean wasTestedOnAnimals;
    private final String comment;

    public Product(
            final String barcode,
            final String name,
            final String company,
            final boolean isVegan,
            final boolean isVegetarian,
            final boolean wasTestedOnAnimals,
            final String comment) {
        this.barcode = barcode;
        this.name = name;
        this.company = company;
        this.isVegan = isVegan;
        this.isVegetarian = isVegetarian;
        this.wasTestedOnAnimals = wasTestedOnAnimals;
        this.comment = comment;
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

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\"Product\": {").append("\n")
               .append("\t\"name\": ").append(name).append("\n")
               .append("\t\"barcode\": ").append(barcode).append("\n")
               .append("}");
        return builder.toString();
    }
}
