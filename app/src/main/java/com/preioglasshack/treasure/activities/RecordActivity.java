package com.preioglasshack.treasure.activities;

import android.app.Activity;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.preioglasshack.treasure.R;
import com.preioglasshack.treasure.adapters.RecordsAdapter;
import com.preioglasshack.treasure.tools.PreferencesHelper;
import com.preioglasshack.treasure.tools.SoundsHelper;
import com.preioglasshack.treasure.ui.RecordCard;
import com.preioglasshack.treasure.ui.RecordListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by g123k on 21/06/14.
 */
public class RecordActivity extends BaseCardScrollActivity implements RecordListener {

    private String mBookId;

    private RecordsAdapter mRecordsAdapter;

    private AudioRecord mRecorder;
    private Thread mRecordingThread;
    private boolean mIsRecording;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBookId = getIntent().getStringExtra(PlaylistActivity.EXTRA_BOOK_ID);
        if (PreferencesHelper.BOOK_DEFAULT_VALUE.equals(mBookId)) {
            finish();
            return;
        }

        PreferencesHelper.setCurrentBook(this, mBookId);

        mRecordsAdapter = new RecordsAdapter(this, this);
        setAdapter(mRecordsAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mIsRecording = false;

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int firstPosition = mCardScrollView.getFirstVisiblePosition();
        int wantedChild = position - firstPosition;

        if (wantedChild < 0 || wantedChild >= mCardScrollView.getChildCount()) {
            return;
        }

        RecordCard wantedView = (RecordCard) (mCardScrollView.getChildAt(wantedChild)).findViewById(R.id.card);
        wantedView.nextState();
    }

    @Override
    public void onStartRecording(int position) {
        startRecording(position);
    }

    @Override
    public void onStopRecording(int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCardScrollView.setSelection(mCardScrollView.getFirstVisiblePosition() + 1);
            }
        }, 1000);

        stopRecording();
    }

    private void startRecording(final int position) {

        mIsRecording = true;

        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SoundsHelper.RECORDER_SAMPLERATE, SoundsHelper.RECORDER_CHANNELS,
                SoundsHelper.RECORDER_AUDIO_ENCODING, SoundsHelper.BufferElements2Rec * SoundsHelper.BytesPerElement);

        mRecorder.startRecording();
        mRecordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile(position);
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }

    private void writeAudioDataToFile(int position) {
        // Write the output audio in byte

        String filePath = SoundsHelper.getFilePath(this, mBookId, position);
        short sData[] = new short[SoundsHelper.BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (mIsRecording) {
            // gets the voice output from microphone to byte format

            mRecorder.read(sData, 0, SoundsHelper.BufferElements2Rec);
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, SoundsHelper.BufferElements2Rec * SoundsHelper.BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void stopRecording() {
        // stops the recording activity
        if (null != mRecorder) {
            mIsRecording = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mRecordingThread = null;
        }
    }
}
