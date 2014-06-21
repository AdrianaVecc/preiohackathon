package com.preioglasshack.treasure.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.preioglasshack.treasure.R;
import com.preioglasshack.treasure.adapters.MotherAdapter;
import com.preioglasshack.treasure.ui.CustomCard;

/**
 * Created by g123k on 21/06/14.
 */
public class SonEndActivity extends BaseCardScrollActivity {

    private String mBookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            mBookId = getIntent().getStringExtra(PlaylistActivity.EXTRA_BOOK_ID);
        } else if (savedInstanceState != null) {
            mBookId = savedInstanceState.getString(PlaylistActivity.EXTRA_BOOK_ID);
        } else {
            finish();
            return;
        }

        setAdapter(new MotherAdapter(getCards()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PlaylistActivity.EXTRA_BOOK_ID, mBookId);
    }

    private CustomCard[] getCards() {
        CustomCard[] cards = new CustomCard[3];

        cards[0] = new CustomCard(this);
        cards[0].setText(R.string.son_replay);

        cards[1] = new CustomCard(this);
        cards[1].setText(R.string.son_new_scan);

        cards[2] = new CustomCard(this);
        cards[2].setText(R.string.son_exit);

        return cards;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).playSoundEffect(Sounds.TAP);

        if (position == 0) {
            Intent intent = new Intent(this, PlaylistActivity.class);
            intent.putExtra(PlaylistActivity.EXTRA_BOOK_ID, mBookId);

            startActivity(intent);
        } else if (position == 1) {
            startActivity(new Intent(this, IdentifyImageActivity.class));
        }

        finish();
    }
}
