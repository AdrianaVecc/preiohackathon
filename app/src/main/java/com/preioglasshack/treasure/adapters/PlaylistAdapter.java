package com.preioglasshack.treasure.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.preioglasshack.treasure.ui.CustomCard;
import com.preioglasshack.treasure.ui.RecordCard;
import com.preioglasshack.treasure.ui.RecordListener;
import com.preioglasshack.treasure.ui.RecordStateChangeListener;

import java.util.ArrayList;

public class PlaylistAdapter extends CardScrollAdapter {

    private final Context mContext;
    private int mCount;

    public PlaylistAdapter(Context context, int count) {
        super();

        this.mContext = context;
        this.mCount = count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final CustomCard card;

        if (view == null) {
            card = new CustomCard(mContext);
        } else {
            card = (CustomCard) view;
        }

        card.setText(String.valueOf(i + 1));

        return card;
    }
}
