package com.aero4te.tevogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.aero4te.tevogs.model.KnownFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class LogActivity extends AppCompatActivity {

    private WebView logArea;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logArea = (WebView) findViewById(R.id.wvLog);

        StringBuffer sb = new StringBuffer();
        sb.append("<html><body>");
        boolean fileExist = true;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(getFilesDir() + KnownFile.TAG_HISTORY.getFilePath()), "UTF8"))) {
            String str;
            while ((str = in.readLine()) != null) {
                sb.append("<p>" + str + "</p>");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            fileExist = false;
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sb.append("</body></Html>");
        if (fileExist) {
            logArea.loadData(sb.toString(), "text/html", "utf-8");
        }
    }

    public void handleBtnMenuOnClick(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void handleBtnCleanOnClick(View view) {
        File file = new File(getFilesDir() + KnownFile.TAG_HISTORY.getFilePath());
        boolean isdeleted = file.delete();
        if (isdeleted) {
            logArea.loadData(null, "text/html", "utf-8");
        }
    }
}
