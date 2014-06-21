package com.preioglasshack.treasure.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.preioglasshack.treasure.R;

/**
 * Created by g123k on 21/06/14.
 */
public class RecordCard extends RelativeLayout implements CountdownListener {

    public static final short STATE_NOT_SET = -1;
    public static final short STATE_WAITING = 0;
    public static final short STATE_COUNTDOWN = 1;
    public static final short STATE_RECORDING = 2;
    public static final short STATE_RECORDING_ENDED = 3;

    private static final int SOUND_PRIORITY = 1;
    private static final int MAX_STREAMS = 1;

    public short mState = STATE_NOT_SET;
    private int mPosition = -1;

    private RecordListener mRecordListener;
    private RecordStateChangeListener mRecordStateChangeListener;

    // Sounds ID visible for testing.
    final int mCountDownFinishSoundId;
    final int mCountDownSoundId;
    final int mFinishSoundId;

    private final SoundPool mSoundPool;

    public RecordCard(Context context) {
        super(context);

        int padding = getResources().getDimensionPixelSize(R.dimen.glass_card_margin);
        setPadding(padding, padding, padding, padding);
        setId(R.id.card);

        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        mCountDownFinishSoundId = mSoundPool.load(context, R.raw.start, SOUND_PRIORITY);
        mCountDownSoundId = mSoundPool.load(context, R.raw.countdown_bip, SOUND_PRIORITY);
        mFinishSoundId = mSoundPool.load(context, R.raw.timer_finished, SOUND_PRIORITY);
    }

    public void setPosition(int position) {
        if (mPosition == position) {
            return;
        }

        this.mPosition = position;
        recreate();
    }

    public void setState(short state) {
        if (this.mState == state) {
            return;
        }

        this.mState = state;

        if (mRecordListener != null) {
            if (state == STATE_RECORDING) {
                mRecordListener.onStartRecording(mPosition);
            }
        }

        recreate();
    }

    public void setStateAndPosition(short state, int position) {
        if (this.mState == state && this.mPosition == position) {
            return;
        }

        this.mState = state;
        this.mPosition = position;
        recreate();
    }


    private void recreate() {

        if (mRecordStateChangeListener != null) {
            mRecordStateChangeListener.onStateChanged(mState, mPosition);
        }

        switch(mState) {
            case STATE_WAITING:
                addCustomView(useCenterText(getContext().getString(R.string.record_start_recording, mPosition), R.drawable.ic_start_recording));
                break;
            case STATE_COUNTDOWN:
                addCustomView(useCountdown());
                break;
            case STATE_RECORDING:
                addCustomView(useChronometer());
                break;
            case STATE_RECORDING_ENDED:
                addCustomView(useCenterText(getContext().getString(R.string.record_start_recorded, mPosition), R.drawable.ic_stop_recording));
                break;
        }
    }

    public void nextState() {
        if (mState == STATE_RECORDING_ENDED) {
            mState = STATE_COUNTDOWN;
        } else {
            mState++;

            if (mState == STATE_RECORDING_ENDED) {
                recordingEnded();
            }
        }

        recreate();
    }

    private void addCustomView(View view) {
        removeAllViews();
        addView(view);
    }

    private View useChronometer() {

        final View v = LayoutInflater.from(getContext()).inflate(R.layout.card_centertext_chronometer, null);
        v.setLayoutParams(getCenteredLayoutParams());

        ((Chronometer)v.findViewById(R.id.chronometer)).start();

        return v;
    }

    private View useCountdown() {
        final CountdownView v = (CountdownView) LayoutInflater.from(getContext()).inflate(R.layout.card_countdown, null);
        v.setLayoutParams(getCenteredLayoutParams());
        v.setCountdownListener(this);

        return v;
    }

    private View useCenterText(String text, int drawableLeft) {
        final TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.card_centertext, null);
        LayoutParams params = getCenteredLayoutParams();

        textView.setLayoutParams(params);

        if (drawableLeft != 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, 0, 0, 0);
        }

        textView.setText(text);

        return textView;
    }

    private LayoutParams getCenteredLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(CENTER_IN_PARENT);
        return params;
    }

    @Override
    public void countdownEnded() {
        setState(STATE_RECORDING);
        playSound(mCountDownFinishSoundId);
    }

    @Override
    public void countdownMinus() {
        playSound(mCountDownSoundId);
    }

    private void recordingEnded() {
        playSound(mFinishSoundId);

        if (mRecordListener != null) {
            mRecordListener.onStopRecording(mPosition);
        }
    }

    public void setRecordListener(RecordListener listener) {
        this.mRecordListener = listener;
    }

    public void setRecordStateChangeListener(RecordStateChangeListener listener) {
        this.mRecordStateChangeListener = listener;
    }

    protected void playSound(int soundId) {
        mSoundPool.play(soundId,
                1 /* leftVolume */,
                1 /* rightVolume */,
                SOUND_PRIORITY,
                0 /* loop */,
                1 /* rate */);
    }
}
