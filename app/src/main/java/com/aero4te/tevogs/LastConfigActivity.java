package com.aero4te.tevogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.aero4te.tevogs.model.KnownFile;
import com.aero4te.tevogs.model.MessageWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LastConfigActivity extends AppCompatActivity {

    private MessageWrapper messageWrapper = null;

    //<editor-fold desc="Components">
    private WebView headWebView;
    private WebView bodyWebView;
    //</editor-fold>

    //<editor-fold desc="Override methods">
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        headWebView = (WebView) findViewById(R.id.headWebView);
        bodyWebView = (WebView) findViewById(R.id.bodyWebView);

        StringBuffer sb = new StringBuffer();
        boolean fileExist = true;
        try (FileInputStream fis = new FileInputStream (new File(getFilesDir() + KnownFile.NFC_LAST_CONFIG.getFilePath()))) {
            int content;
            while ((content = fis.read()) != -1) {
                sb.append((char) content);
            }
        } catch (FileNotFoundException e) {
            fileExist = false;
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileExist) {
            messageWrapper = new MessageWrapper(sb.toString());
            String head = messageWrapper.getHeaderRecordWrapper().toString();
            String body = messageWrapper.getBodyRecordWrapper().toString();

            String text = "<html><body style=\"text-align:justify\"> %s </body></Html>";
            String replace1 = head.replace("{", "{<br />");
            String replace2 = replace1.replace("}", "<br />}");
            String replace = replace2.replace(",\"", ",<br />&nbsp\"");
            headWebView.loadData(String.format(text, replace), "text/html", "utf-8");

            replace1 = body.replace("{", "{<br />");
            replace2 = replace1.replace("}", "<br />}");
            replace = replace2.replace(",", ",<br />&nbsp");
            bodyWebView.loadData(String.format(text, replace), "text/html", "utf-8");
        } else {
            String text = "<html><body><h2>%s</h2></body></Html>";
            headWebView.loadData(String.format(text, "Last configuration not found."), "text/html", "utf-8");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ) {
        super.onSaveInstanceState(outState);
        headWebView.saveState(outState);
        bodyWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        headWebView.restoreState(savedInstanceState);
        bodyWebView.restoreState(savedInstanceState);
    }
    //</editor-fold>

    //<editor-fold desc="Handlers">
    public void handleBtnMenuOnClick(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void handleBtnWriteOnClick(View view) {
        Intent writeIntent = new Intent(this, WriteActivity.class);
        if (messageWrapper != null) {
            writeIntent.putExtra("json", messageWrapper.toString());
        }
        startActivity(writeIntent);
    }
    //</editor-fold>
}
