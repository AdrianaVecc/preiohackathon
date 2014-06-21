package com.preioglasshack.treasure.tools;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by g123k on 21/06/14.
 */
public class SoundsHelper {

    private static final String FOLDER_SOUNDS = "/sounds/";

    public static final int RECORDER_SAMPLERATE = 8000;
    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public static final int BufferElements2Rec = 1024;
    public static final int BytesPerElement = 2;

    public static final String getFilePath(Context cOntext, String id, int position) {
        return SoundsHelper.getFolderPath(cOntext, id) + position + ".pcm";
    }

    public static final String getFolderPath(Context context, String id) {
        ContextWrapper contextWrapper = new ContextWrapper(context);

        new File(contextWrapper.getFilesDir() + FOLDER_SOUNDS + id).mkdirs();

        return contextWrapper.getFilesDir() + FOLDER_SOUNDS + id;
    }

    public static final void removeBookSounds(Context context, String id) {
        FoldersHelper.purgeDirectory(new File(getFolderPath(context, id)));
    }

    public static byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

}
