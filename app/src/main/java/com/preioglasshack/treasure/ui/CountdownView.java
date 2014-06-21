package com.preioglasshack.treasure.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

/**
 * Created by g123k on 21/06/14.
 */
public class CountdownView extends TextView {

    private CountDownTimer mCountDownTimer;
    private CountdownListener mCountdownListener;
    private final int DURATION = 4;

    private ViewPropertyAnimator mViewPropertyAnimator;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCountdownListener(CountdownListener listener) {
        this.mCountdownListener = listener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mCountDownTimer = new Countdown(DURATION * 1000, 1000);
        mCountDownTimer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mCountDownTimer.cancel();
        mCountDownTimer = null;
    }

    public void setAnimatedText(String text) {

        if (mViewPropertyAnimator != null) {
            mViewPropertyAnimator.cancel();
        }

        setText(text);

        mViewPropertyAnimator = animate().alpha(0.1f).scaleY(0.1f).scaleX(0.1f).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                setAlpha(1);
                setScaleX(1);
                setScaleY(1);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationStart(Animator animation) {}
        });
    }

    private class Countdown extends CountDownTimer {

        public Countdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (mCountdownListener != null) {
                mCountdownListener.countdownEnded();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setAnimatedText(String.valueOf(Math.round(millisUntilFinished / 1000)));

            if (mCountdownListener != null) {
                mCountdownListener.countdownMinus();
            }
        }
    }
}
