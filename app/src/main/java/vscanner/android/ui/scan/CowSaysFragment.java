package vscanner.android.ui.scan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vscanner.android.App;
import vscanner.android.R;

public class CowSaysFragment extends Fragment {
    private CowState cowState = new CowState();

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cow_says_fragment_layout, container, false);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CowState.class.toString(), cowState);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            final CowState cowState = (CowState) savedInstanceState.getSerializable(CowState.class.toString());
            if (cowState != null) {
                this.cowState = cowState;
            }
        }
        displayCurrentCowStateOn(view);
    }

    private void displayCurrentCowStateOn(final View root) {
        if (root != null) {
            final TextView cowSaysTextView = (TextView) root.findViewById(R.id.textview_cow_says);
            cowSaysTextView.setText(cowState.getPhrase());

            final ImageView cowFacePic = (ImageView) root.findViewById(R.id.image_cow_face);

            switch (cowState.getMood()) {
                case NEUTRAL:
                    cowFacePic.setImageResource(R.drawable.cow_mood_neutral);
                    break;
                case BAD:
                    cowFacePic.setImageResource(R.drawable.cow_mood_bad);
                    break;
                case OK:
                    cowFacePic.setImageResource(R.drawable.cow_mood_ok);
                    break;
                case GOOD:
                    cowFacePic.setImageResource(R.drawable.cow_mood_good);
                    break;
                default:
                    cowFacePic.setImageResource(R.drawable.cow_mood_neutral);
                    App.logError(this, "an enum element is not handled, cow will be neutral");
            }

            if (cowState.isBackgroundVisible()) {
                cowFacePic.setBackgroundResource(R.drawable.cow_background);
            } else {
                cowFacePic.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }
    }

    public void setCowsText(final String text) {
        this.cowState = new CowState(cowState, text);
        displayCurrentCowStateOn(getView());
    }

    public void setCowMood(final CowState.Mood cowMood) {
        this.cowState = new CowState(cowState, cowMood);
        displayCurrentCowStateOn(getView());
    }

    public void setCowBackgroundVisibility(final boolean isVisible) {
        this.cowState = new CowState(cowState, isVisible);
        displayCurrentCowStateOn(getView());
    }
}
