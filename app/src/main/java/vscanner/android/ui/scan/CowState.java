package vscanner.android.ui.scan;

import java.io.Serializable;

final class CowState implements Serializable {
    public static enum Mood { NEUTRAL, BAD, OK, GOOD }

    private final Mood mood;
    private final String phrase;
    private final boolean isBackgroundVisible;

    public CowState(final CowState other, final Mood mood) {
        this(mood, other.getPhrase(), other.isBackgroundVisible());
    }

    public CowState(final CowState other, final String phrase) {
        this(other.getMood(), phrase, other.isBackgroundVisible());
    }

    public CowState(final CowState other, final boolean isBackgroundVisible) {
        this(other.getMood(), other.getPhrase(), isBackgroundVisible);
    }

    public CowState() {
        this(null, null, false);
    }

    public CowState(
            final Mood mood,
            final String phrase,
            final boolean isBackgroundVisible) {
        this.mood = mood != null ? mood : Mood.NEUTRAL;
        this.phrase = phrase != null ? phrase : "";
        this.isBackgroundVisible = isBackgroundVisible;
    }

    /**
     * @return not null
     */
    public Mood getMood() {
        return mood;
    }

    /**
     * @return not null
     */
    public String getPhrase() {
        return phrase;
    }

    public boolean isBackgroundVisible() {
        return isBackgroundVisible;
    }
}
