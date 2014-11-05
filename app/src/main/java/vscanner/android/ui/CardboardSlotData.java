package vscanner.android.ui;

import android.view.View;
import android.view.ViewGroup;

import vscanner.android.App;
import vscanner.android.R;

final class CardboardSlotData {
    static enum Position { TOP, MIDDLE }

    private static final String TOP_FRAGMENT_TAG = "TOP_FRAGMENT_TAG";
    private static final String MIDDLE_FRAGMENT_TAG = "MIDDLE_FRAGMENT_TAG";

    private final String fragmentTag;
    private final int containerId;
    private final ViewGroup container;

    /**
     * @param position must not be null
     * @throws IllegalArgumentException <br>
     * if any parameter is null <br>
     * if position is unknown <br>
     * if root doesn't contain needed container
     */
    CardboardSlotData(final Position position, final View root) {
        if (position == null) {
            throw new IllegalArgumentException("position must not be null");
        } else if (root == null) {
            throw new IllegalArgumentException("root must not be null");
        }

        switch (position) {
            case TOP:
                fragmentTag = TOP_FRAGMENT_TAG;
                containerId = R.id.top_content_container;
                break;
            case MIDDLE:
                fragmentTag = MIDDLE_FRAGMENT_TAG;
                containerId = R.id.middle_content_container;
                break;
            default:
                throw new IllegalArgumentException("position is unknown");
        }

        container = (ViewGroup) root.findViewById(containerId);
        if (container == null) {
            throw new IllegalArgumentException("root doesn't contain needed container");
        }
    }

    /**
     * @return not null
     */
    String getFragmentTag() {
        return fragmentTag;
    }

    int getContainerId() {
        return containerId;
    }

    /**
     * @return not null
     */
    ViewGroup getContainer() {
        return container;
    }
}
