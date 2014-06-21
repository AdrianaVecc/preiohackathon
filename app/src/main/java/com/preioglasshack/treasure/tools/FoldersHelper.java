package com.preioglasshack.treasure.tools;

import java.io.File;

/**
 * Created by g123k on 21/06/14.
 */
public class FoldersHelper {

    public static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) purgeDirectory(file);
            file.delete();
        }
    }

}
