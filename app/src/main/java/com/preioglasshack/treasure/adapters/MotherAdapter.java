package com.preioglasshack.treasure.adapters;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.widget.CardScrollAdapter;
import com.preioglasshack.treasure.ui.CustomCard;
import com.preioglasshack.treasure.ui.CustomCardInner;

public class MotherAdapter extends CardScrollAdapter {

    private final CustomCard[] mCards;

    public MotherAdapter(CustomCard[] cards) {
        super();

        this.mCards = cards;
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
    public int getCount() {
        return mCards.length;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        return mCards[i];
    }
}
