package com.preioglasshack.treasure.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.preioglasshack.treasure.adapters.PlaylistAdapter;
import com.preioglasshack.treasure.service.MusicPlayerService;
import com.preioglasshack.treasure.service.MusicPlayerInterface;
import com.preioglasshack.treasure.tools.PreferencesHelper;
import com.preioglasshack.treasure.tools.SoundsHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by g123k on 21/06/14.
 */
public class PlaylistActivity extends BaseCardScrollActivity implements AdapterView.OnItemSelectedListener, MusicPlayerInterface {

    public static final String EXTRA_BOOK_ID = "book_id";

    private String mBookId;

    private MusicPlayerService mService;
    private PowerManager.WakeLock mWakeLock;

    private int mLastPosition = -1;
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            mBookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        } else if (savedInstanceState != null) {
            mBookId = savedInstanceState.getString(EXTRA_BOOK_ID);
        } else {
            finish();
            return;
        }

        if (mBookId.equals(PreferencesHelper.BOOK_DEFAULT_VALUE)) {
            finish();
            return;
        }

        String path = SoundsHelper.getFolderPath(this, mBookId);
        mCount = new File(path).listFiles().length;

        if (mCount == 0) {
            finish();
            return;
        }

        mCardScrollView.setOnItemSelectedListener(this);
        setAdapter(new PlaylistAdapter(this, mCount));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent serviceIntent = new Intent(this, MusicPlayerService.class);

        bindService(serviceIntent,
                mServiceConnection,
                Context.BIND_AUTO_CREATE);

        startService(serviceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopService(new Intent(this, MusicPlayerService.class));

        if (mService != null) {
            mService.setMusicPlayerInterface(null);
        }

        try {
            unbindService(mServiceConnection);
        } catch (Exception e) {}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_BOOK_ID, mBookId);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // nothing
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (mLastPosition != position && mService != null) {

            mLastPosition = position;

            if (mService != null) {
                mService.stopPlaying();

                try {
                    mService.startPlaying(mBookId, position);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MusicPlayerService.MusicPlayerBinder) service).getService();
            mService.setMusicPlayerInterface(PlaylistActivity.this);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onItemSelected(null, null, 0, 0);
                }
            });
        }
    };

    @Override
    public void onMusicPlayEnded(final int position) {

        if (position + 1 < mCount) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCardScrollView.setSelection(position + 1);
                    mCardScrollView.setSelected(true);
                }
            });

        } else {
            ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).playSoundEffect(Sounds.SUCCESS);

            Intent intent = new Intent(this, SonEndActivity.class);
            intent.putExtra(EXTRA_BOOK_ID, mBookId);

            startActivity(intent);
            finish();
        }
    }
}
