package com.aero4te.tevogs.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class StorageUtil {

    private FileOutputStream outputStream;

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    // Vytvoření adresáře v soukromém úložišti
    private File getPrivateStorageDir(Context context, String filename, String environment) {
        File file = new File(context.getExternalFilesDir(
                environment), filename);
        if (!file.mkdirs()) {
            Log.e("storage", "Directory not created");
        }
        return file;
    }

    public void save(Context context, String dir, String filename, String environment, String fileContents) {
        if (isExternalStorageWritable()) {
            File file = getPrivateStorageDir(context, dir, environment);
            try {
                File tagCofig = new File(file, filename);
                FileWriter writer = new FileWriter(tagCofig);
                writer.append(fileContents);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Log.e("storage", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e("storage", "External storage is not writable.");
        }
    }

    public void save(Context context, String dir, String filename, String fileContents) {
        save(context, dir, filename, null, fileContents);
    }
}
