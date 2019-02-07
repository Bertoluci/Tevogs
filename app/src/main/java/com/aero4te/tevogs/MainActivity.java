package com.aero4te.tevogs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aero4te.tevogs.model.CipherKey;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
