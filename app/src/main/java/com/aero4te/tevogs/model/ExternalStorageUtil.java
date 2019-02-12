package com.aero4te.tevogs.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExternalStorageUtil {

    private final static String LOG_STORAGE = "storage";

    private Context context;
    private ExternalPartition partition;

    public ExternalStorageUtil(Context context, ExternalPartition partition) {
        this.context = context;
        this.partition = partition;
    }

    public ExternalStorageUtil(Context context) {
        this(context, ExternalPartition.Private);
    }

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

    // Vytvoření adresáře ve veřejném úložišti
    public File getPublicStorageDir(String folder, String environment) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                environment), folder);
        if (!dir.mkdirs()) {
            Log.e(LOG_STORAGE, "Directory not created");
        }
        return dir;
    }

    // Vytvoření adresáře v soukromém úložišti
    private File getPrivateStorageDir(String folder, String environment) {
        File dir = new File(context.getExternalFilesDir(
                environment), folder);
        if (!dir.mkdirs()) {
            Log.e(LOG_STORAGE, "Directory not created");
        }
        return dir;
    }

    public void save(String folder, String filename, String environment, String fileContents) {
        if (isExternalStorageWritable()) {

            File dir = null;
            if (ExternalPartition.Public == partition) {
                dir = getPublicStorageDir(folder, environment);
            } else {
                dir = getPrivateStorageDir(folder, environment);
            }

            File file = new File(dir, filename);
            try(FileWriter fw = new FileWriter(file)) {
                fw.append(fileContents);
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_STORAGE, "External storage is not writable.");
        }
    }

    public void save(String folder, String filename, String fileContents) {
        save(folder, filename, null, fileContents);
    }
}
