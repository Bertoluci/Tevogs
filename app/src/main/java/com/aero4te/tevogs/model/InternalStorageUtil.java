package com.aero4te.tevogs.model;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class InternalStorageUtil {

    private Context context;
    private String[] path = null;
    private String dir = null;
    private String filename = null;
    private boolean append = false;
    private String content = null;
    private File file = null;

    public InternalStorageUtil(Context context) {
        this.context = context;
    }

    public void save(String content, String filename) {
        this.content = content;
        this.filename = filename;
        save();
    }

    public void save(String content, String filename, String ... folders) {
        switch (folders.length) {
            case 0:
                save(content, filename);
                break;
            case 1:
                dir = folders[0];
                save(content, filename);
                break;
            default:
                dir = folders[folders.length - 1];
                path = new String[folders.length - 1];
                for (int i = 0; i < folders.length - 1; i++) {
                    path[i] = folders[i];
                }
                save(content, filename);
                break;
        }
    }

    public void save(String content, KnownFile kind) {
        path = kind.getParentFolders();
        dir = kind.getDir();
        filename = kind.getFilename();
        append = kind.getAppend();
        this.content = content;
        save();
    }

    private void save() {
        if (dir != null) {
            String internalDirectory = null;
            StringBuilder sb = new StringBuilder();
            sb.append(context.getFilesDir());
            sb.append(File.separator);
            if (path != null && path.length > 1) {
                for (int i = 0; i < path.length; i++) {
                    sb.append(path[i]);
                    sb.append(File.separator);
                }
            } else if (path != null && path.length == 1) {
                sb.append(path[0]);
                sb.append(File.separator);
            }
            internalDirectory = sb.toString();
            File directory = new File(internalDirectory, dir);
            directory.mkdirs();
            file = new File(directory, filename);
        } else {
            file = new File(context.getFilesDir(), filename);
        }
        try {
            boolean newFile = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getCanonicalPath(), append), "UTF-8"))) {
            bw.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        path = null;
        dir = null;
        filename = null;
        content = null;
        append = false;
    }
}
