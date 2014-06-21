package com.preioglasshack.treasure.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.preioglasshack.treasure.ui.RecordCard;
import com.preioglasshack.treasure.ui.RecordListener;
import com.preioglasshack.treasure.ui.RecordStateChangeListener;

import java.util.ArrayList;

public class RecordsAdapter extends CardScrollAdapter implements RecordStateChangeListener {

    private ArrayList<Short> mStates;
    private Context mContext;
    private RecordListener mRecordListener;

    public RecordsAdapter(Context context, RecordListener recordListener) {
        super();

        this.mContext = context;
        this.mStates = new ArrayList<Short>();
        this.mRecordListener = recordListener;
    }

    @Override
    public Object getItem(int position) {
        return mStates.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return Card.getViewTypeCount();
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final RecordCard recordCard;

        if (view == null) {
            recordCard = new RecordCard(mContext);
        } else {
            recordCard = (RecordCard) view;
        }

        if (i == mStates.size()) {
            mStates.add(RecordCard.STATE_WAITING);
        }

        if (mStates.get(i) == RecordCard.STATE_COUNTDOWN) {
            mStates.set(i, RecordCard.STATE_WAITING);
        }

        recordCard.setRecordListener(mRecordListener);
        recordCard.setRecordStateChangeListener(this);
        recordCard.setStateAndPosition(mStates.get(i), i);

        return recordCard;
    }

    @Override
    public void onStateChanged(short state, int position) {
        mStates.set(position, state);
    }
}
