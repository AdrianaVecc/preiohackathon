package com.preioglasshack.treasure.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by g123k on 21/06/14.
 */
public class ScanActivity extends Activity {

    public static final String EXTRA_BOOK_ID = "book_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, "1");

        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
