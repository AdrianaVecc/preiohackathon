package com.preioglasshack.treasure.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.preioglasshack.treasure.R;

/**
 * Created by g123k on 21/06/14.
 */
public abstract class BaseCardScrollActivity extends Activity implements AdapterView.OnItemClickListener {

    protected CardScrollView mCardScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scroll);

        mCardScrollView = (CardScrollView) findViewById(R.id.content);
        mCardScrollView.setOnItemClickListener(this);

        // Scrollbars always visible
        mCardScrollView.setHorizontalScrollBarEnabled(true);
        mCardScrollView.setScrollbarFadingEnabled(false);
        mCardScrollView.setScrollBarFadeDuration(0);
    }

    protected void onCreate(Bundle savedInstanceState, boolean fake) {
        super.onCreate(savedInstanceState);
    }

    protected void setAdapter(CardScrollAdapter adapter) {
        mCardScrollView.setAdapter(adapter);
        mCardScrollView.activate();
    }
}
