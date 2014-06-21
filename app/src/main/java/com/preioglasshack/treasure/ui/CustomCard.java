package com.preioglasshack.treasure.ui;

import android.content.Context;
import android.filterfw.core.Frame;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.preioglasshack.treasure.R;

/**
 * Created by g123k on 21/06/14.
 */
public class CustomCard extends FrameLayout {

    private CustomCardInner mCustomCardInner;

    public CustomCard(Context context) {
        this(context, null);
    }

    public CustomCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCustomCardInner = new CustomCardInner(getContext());

        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        mCustomCardInner.setLayoutParams(layoutParams);

        addView(mCustomCardInner);
    }

    public void setText(int textId) {
        mCustomCardInner.setText(textId);
    }

    public void setText(String text) {
        mCustomCardInner.setText(text);
    }

    public void setCompoundDrawablesWithIntrinsicBounds(int i1, int i2, int i3, int i4) {
        mCustomCardInner.setCompoundDrawablePadding(getContext().getResources().getDimensionPixelOffset(R.dimen.glass_card_drawable_padding));
        mCustomCardInner.setCompoundDrawablesWithIntrinsicBounds(i1, i2, i3, i4);
    }
}
