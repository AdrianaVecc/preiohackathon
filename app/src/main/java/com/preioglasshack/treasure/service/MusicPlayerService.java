package com.preioglasshack.treasure.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.preioglasshack.treasure.tools.SoundsHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by g123k on 21/06/14.
 */
public class MusicPlayerService extends Service {

    private final IBinder mBinder = new MusicPlayerBinder();
    private MusicPlayerInterface mMusicPlayerInterface;

    private AudioTrack mAudioTrack;

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void startPlaying(final String bookId, final int position) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    _startPlaying(bookId, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void _startPlaying(String bookId, int position) throws IOException
    {

        String filePath = SoundsHelper.getFilePath(this, bookId, position);
        // We keep temporarily filePath globally as we have only two sample sounds now..
        if (filePath==null)
            return;

        //Reading the file..
        byte[] byteData = null;
        File file = null;
        file = new File(filePath);
        byteData = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read( byteData );
            in.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Set and push to audio track..
        int intSize = android.media.AudioTrack.getMinBufferSize(SoundsHelper.RECORDER_SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO,
                SoundsHelper.RECORDER_AUDIO_ENCODING);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SoundsHelper.RECORDER_SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO,
                SoundsHelper.RECORDER_AUDIO_ENCODING, intSize, AudioTrack.MODE_STREAM);
        if (mAudioTrack != null) {
            mAudioTrack.play();

            // Write the byte array to the track
            mAudioTrack.write(byteData, 0, byteData.length);
            mAudioTrack.stop();
            mAudioTrack.release();
        }
        else {
            Log.d("TCAudio", "audio track is not initialised ");
        }

        if (mMusicPlayerInterface != null) {
            mMusicPlayerInterface.onMusicPlayEnded(position);
        }

    }

    public void stopPlaying() {
        if (mAudioTrack != null) {
            try {
                mAudioTrack.flush();
                mAudioTrack.stop();
                mAudioTrack.release();
            } catch (Exception e) {}
        }
    }

    public void setMusicPlayerInterface(MusicPlayerInterface musicPlayerInterface) {
        this.mMusicPlayerInterface = musicPlayerInterface;
    }

    public class MusicPlayerBinder extends Binder {

        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }

    }
}
