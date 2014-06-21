package com.preioglasshack.treasure.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.moodstocks.android.AutoScannerSession;
import com.moodstocks.android.Configuration;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;
import com.preioglasshack.treasure.R;

public class IdentifyImageActivity extends Activity implements Scanner.SyncListener, AutoScannerSession.Listener {

    // Moodstocks API key/secret pair for aregnier@francedev.com account
    public static final String API_KEY    = /*"82mmoiwilxj7cv19zlnk";*/ "35eyufaohixrrvxgdznh";
    public static final String API_SECRET = /*"ucGOUn0smSBQ0PkQ";*/ "wY6fiSmkU2wVHceZ";

    private static final int TYPES = Result.Type.IMAGE | Result.Type.QRCODE ;

    private boolean compatible = false;
    private Scanner scanner;
    private AutoScannerSession session = null;
    private View resultView;
    private TextView resultID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        Configuration.platform = Configuration.Platform.GOOGLE_GLASS;

        compatible = Scanner.isCompatible();
        if (compatible) {
            try {
                scanner = Scanner.get();
                String path = Scanner.pathFromFilesDir(this, "scanner.db");
                scanner.open(path, API_KEY, API_SECRET);
                scanner.setSyncListener(this);
                scanner.sync();
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }

            SurfaceView preview = (SurfaceView)findViewById(R.id.preview);

            try {
                session = new AutoScannerSession(this, Scanner.get(), this, preview);
                session.setResultTypes(TYPES);
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }

        resultView = findViewById(R.id.result);
        resultID = (TextView) findViewById(R.id.result_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        session.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compatible) {
            try {
                scanner.close();
                scanner.destroy();
                scanner = null;
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSyncStart() {
        Log.d("Moodstocks SDK", "Sync will start.");
    }

    @Override
    public void onSyncComplete() {
        try {
            Log.d("Moodstocks SDK", "Sync succeeded ("+ scanner.count() + " images)");
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSyncFailed(MoodstocksError e) {
        Log.d("Moodstocks SDK", "Sync error #" + e.getErrorCode() + ": " + e.getMessage());
    }

    @Override
    public void onSyncProgress(int total, int current) {
        int percent = (int) ((float) current / (float) total * 100);
        Log.d("Moodstocks SDK", "Sync progressing: " + percent + "%");
    }

    @Override
    public void onCameraOpenFailed(Exception e) {
        // Implemented in a few steps
    }

    @Override
    public void onWarning(String debugMessage) {
        // Implemented in a few steps
    }

    @Override
    public void onResult(Result result) {
        ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).playSoundEffect(Sounds.SUCCESS);

        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra(PlaylistActivity.EXTRA_BOOK_ID, result.getValue());

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && resultView.getVisibility() == View.VISIBLE) {
            resultView.setVisibility(View.INVISIBLE);
            session.resume();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}