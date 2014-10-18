package vscanner.android;

public class HumanizingToolkit {
    private HumanizingToolkit() {
    }

    public static String booleanToAnswer(final boolean value) {
        if (value == true) {
            return App.getStringWith(R.string.raw_YES);
        } else {
            return App.getStringWith(R.string.raw_NO);
        }
    }
}
