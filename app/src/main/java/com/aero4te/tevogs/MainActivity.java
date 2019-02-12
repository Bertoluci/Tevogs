package com.aero4te.tevogs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.aero4te.tevogs.model.CipherKey;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReadActivity.wifiConfig = new WifiConfiguration();
        ReadActivity.wifiConfig.SSID = null;
        ReadActivity.wifiConfig.preSharedKey = null;

        sharedPreferences = getPreferences(MODE_PRIVATE);
        String prefKey = sharedPreferences.getString("key", null);
        if (prefKey != null) {
            try {
                CipherKey.key = prefKey.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e("charset", "Chyba kódování.");
            }
        } else {
            Log.e("key", "Nepodařilo se získat šifrovací klíč.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_log:
                showLog();
                return true;
            case R.id.visit_website:
                visitWebsite();
                return true;
            case R.id.exit:
                this.finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showLog() {
        Intent logIntent = new Intent(this, LogActivity.class);
        startActivity(logIntent);
    }

    private void visitWebsite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.aero4te.com/te-vogs/"));
        startActivity(browserIntent);
    }

    public void handleBtnWriteOnClick(View view) {
        Intent writeIntent = new Intent(this, WriteActivity.class);
        startActivity(writeIntent);
    }

    public void handleBtnReadOnClick(View view) {
        Intent readIntent = new Intent(this, ReadActivity.class);
        startActivity(readIntent);
    }

    public void handleBtnLastConfigOnClick(View view) {
        Intent lastConfigIntent = new Intent(this, LastConfigActivity.class);
        startActivity(lastConfigIntent);
    }
}
