package com.preioglasshack.treasure.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by g123k on 21/06/14.
 */
public class CustomCardInner extends TextView {
    public CustomCardInner(Context context) {
        this(context, null);
    }

    public CustomCardInner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCardInner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large);
        setGravity(Gravity.CENTER);
    }
}
