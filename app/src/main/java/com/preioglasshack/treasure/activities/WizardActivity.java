package com.preioglasshack.treasure.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.glass.app.Card;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.preioglasshack.treasure.R;
import com.preioglasshack.treasure.tools.PreferencesHelper;

/**
 * Wizard on first launch
 */
public class WizardActivity extends Activity implements GestureDetector.BaseListener {

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getWizardView());

        PreferencesHelper.setIsNotFirstLaunchAnymore(this);

        mGestureDetector = new GestureDetector(this);
        mGestureDetector.setBaseListener(this);
    }

    private View getWizardView() {
        Card card = new Card(this);

        card.setText(getString(R.string.wizard_content));

        return card.getView();
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        if (gesture == Gesture.TAP) {
            finish();
            return true;
        }

        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }
}
