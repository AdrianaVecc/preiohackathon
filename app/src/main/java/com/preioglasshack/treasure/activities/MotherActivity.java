package com.preioglasshack.treasure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.preioglasshack.treasure.R;

import com.preioglasshack.treasure.adapters.MotherAdapter;
import com.preioglasshack.treasure.tools.PreferencesHelper;
import com.preioglasshack.treasure.tools.SoundsHelper;
import com.preioglasshack.treasure.ui.CustomCard;

/**
 * Main activity :
 *
 * (Wizard on first launch)
 *   |
 *   v
 * New story
 *
 * .....
 *   |
 *   ^
 * Save
 */
public class MotherActivity extends BaseCardScrollActivity {

    public static final int REQUEST_CODE_BOOK_ID = 1;
    private String mCodeBookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (PreferencesHelper.isFirstLaunch(this)) {
            super.onCreate(savedInstanceState, false);
            startActivity(new Intent(this, WizardActivity.class));
            // don't finish this one here !
            return;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCodeBookId = PreferencesHelper.getCurrentBook(this);

        if (codeBookNotNull()) {
            PreferencesHelper.removeCurrentBook(this);
        }

        setAdapter(new MotherAdapter(!codeBookNotNull() ? getFirstLaunchCards() : getLastRecordCards()));
        mCardScrollView.setSelection(1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Use something better than position
        switch (position) {
            case 0:
                if (codeBookNotNull()) {
                    SoundsHelper.removeBookSounds(this, mCodeBookId);
                }

                startActivity(new Intent(this, ScanActivity.class));
                break;
            case 1:
                finish();
                break;
            case 2:
                if (codeBookNotNull()) {
                    SoundsHelper.removeBookSounds(this, mCodeBookId);
                }

                finish();

                break;
        }
    }

    private boolean codeBookNotNull() {
        return !mCodeBookId.equals(PreferencesHelper.BOOK_DEFAULT_VALUE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_BOOK_ID) {
            mCardScrollView.setSelection(1);
        }
    }


    private CustomCard[] getFirstLaunchCards() {
        CustomCard[] cards = new CustomCard[1];

        cards[0] = new CustomCard(this);
        cards[0].setText(R.string.mother_action_new);
        cards[0].setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_save, 0, 0, 0);

        return cards;
    }

    private CustomCard[] getLastRecordCards() {
        CustomCard[] cards = new CustomCard[3];

        cards[0] = new CustomCard(this);
        cards[0].setText(R.string.mother_action_new);
        //cards[0].setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_save, 0, 0, 0);

        cards[1] = new CustomCard(this);
        cards[1].setText(R.string.mother_action_save);
        cards[1].setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_save, 0, 0, 0);

        cards[2] = new CustomCard(this);
        cards[2].setText(R.string.mother_action_exit);
        cards[2].setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_exit, 0, 0, 0);

        return cards;
    }
}
