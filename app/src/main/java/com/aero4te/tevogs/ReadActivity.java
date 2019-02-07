package com.aero4te.tevogs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.aero4te.tevogs.model.BodyRecordWrapper;
import com.aero4te.tevogs.model.CipherKey;
import com.aero4te.tevogs.model.CipherUtil;
import com.aero4te.tevogs.model.HeaderRecordWrapper;
import com.aero4te.tevogs.model.InternalStorageUtil;
import com.aero4te.tevogs.model.KnownFile;
import com.aero4te.tevogs.model.MessageWrapper;
import com.aero4te.tevogs.model.StorageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class ReadActivity extends AppCompatActivity {

    //<editor-fold desc="Properties">
    private NfcAdapter nfcAdapter;
    private MessageWrapper messageWrapper = null;
    private HeaderRecordWrapper headerRecordWrapper = null;
    private BodyRecordWrapper bodyRecordWrapper = null;
    private NdefRecord headNdefRecord = null;
    private NdefRecord bodyNdefRecord = null;
    private NdefRecord aaNdefRecord = null;
    private String aarPackageName = null;
    //</editor-fold>

    //<editor-fold desc="Components">
    private WebView headWebView;
    private WebView bodyWebView;
    //</editor-fold>

    //<editor-fold desc="Override methods">
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        headWebView = (WebView) findViewById(R.id.headWebView);
        bodyWebView = (WebView) findViewById(R.id.bodyWebView);
        Toast.makeText(this, R.string.approachTag, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean hasFeature = getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
        boolean isEnabled = (nfcAdapter != null && nfcAdapter.isEnabled());
        if (hasFeature && isEnabled) {
            Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            IntentFilter[] intentFilter = new IntentFilter[] {};
            String[][] techList = new String[][] { { android.nfc.tech.Ndef.class.getName() }, { android.nfc.tech.NdefFormatable.class.getName() } };
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, techList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean hasFeature = getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
        boolean isEnabled = (nfcAdapter != null && nfcAdapter.isEnabled());
        if (hasFeature && isEnabled) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            if (CipherKey.key == null) {
                Toast.makeText(this, getString(R.string.no_key), Toast.LENGTH_LONG).show();
                return;
            }
            NdefMessage ndefMessage = null;
            Parcelable[] extra = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (extra != null && extra.length > 0) {
                ndefMessage = (NdefMessage) extra[0];
            }
            if (ndefMessage != null) {
                NdefRecord[] ndefRecords = ndefMessage.getRecords();
                if (ndefRecords != null && ndefRecords.length > 2) {
                    headNdefRecord = ndefRecords[0];
                    bodyNdefRecord = ndefRecords[1];
                    aaNdefRecord = ndefRecords[2];
                }
                //<editor-fold desc="Header record">
                if (headNdefRecord != null ) {
                    boolean isTextRecord = headNdefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(headNdefRecord.getType(), NdefRecord.RTD_TEXT);
                    if (isTextRecord) {
                        String tagContent = null;
                        try {
                            byte[] payload = headNdefRecord.getPayload();
                            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                            int languageSize = payload[0] & 0063;
                            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
                        } catch (UnsupportedEncodingException e) {
                            Log.e("charset", e.getMessage());
                        }
                        String text = "<html><body style=\"text-align:justify\"> %s </body></Html>";
                        String replace1 = tagContent.replace("{", "{<br />");
                        String replace2 = replace1.replace("}", "<br />}");
                        String replace = replace2.replace(",\"", ",<br />&nbsp\"");
                        headWebView.loadData(String.format(text, replace), "text/html", "utf-8");

                        headerRecordWrapper = new HeaderRecordWrapper(tagContent);

                    } else {
                        Toast.makeText(this, "Header record is not Text formatted.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Header record not found.", Toast.LENGTH_LONG).show();
                }
                //</editor-fold>

                //<editor-fold desc="Body record">
                if (bodyNdefRecord != null ) {
                    byte[] payload = bodyNdefRecord.getPayload();
                    CipherUtil cipherUntil = new CipherUtil(CipherKey.key, headerRecordWrapper.getIv());
                    byte[] decipher = null;
                    String decipherTxt = null;
                    try {
                        decipher = cipherUntil.decipher(payload);
                        decipherTxt = new String(decipher);
                    } catch (Exception e) {
                        Log.e("charset", "Chyba dešifrování v ReadActivity");
                        e.printStackTrace();
                    }
                    String text = "<html><body style=\"text-align:justify\"> %s </body></Html>";
                    String replace1 = decipherTxt/*tagContent*/.replace("{", "{<br />");
                    String replace2 = replace1.replace("}", "<br />}");
                    String replace = replace2.replace(",", ",<br />&nbsp");
                    bodyWebView.loadData(String.format(text, replace), "text/html", "utf-8");

                    bodyRecordWrapper = new BodyRecordWrapper(decipherTxt);
                } else {
                    Toast.makeText(this, "Body record not found.", Toast.LENGTH_LONG).show();
                }
                //</editor-fold>

                //<editor-fold desc="AAR record">
                if (aaNdefRecord != null ) {
                    boolean isAarRecord = aaNdefRecord.getTnf() == NdefRecord.TNF_EXTERNAL_TYPE && Arrays.equals(aaNdefRecord.getType(), "android.com:pkg".getBytes());
                    if (isAarRecord) {
                        aarPackageName = new String(aaNdefRecord.getPayload());
                    } else {
                        Toast.makeText(this, "Record is not AAR type.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "AAR not found.", Toast.LENGTH_LONG).show();
                }
                //</editor-fold>

                if (headerRecordWrapper != null & bodyRecordWrapper != null && aarPackageName != null) {
                    messageWrapper = new MessageWrapper(headerRecordWrapper, bodyRecordWrapper, aarPackageName);
                    String content = messageWrapper.toString();
                    InternalStorageUtil storage = new InternalStorageUtil(this);
                    storage.save(content.toString(), KnownFile.NFC_LAST_CONFIG);

                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    storage.save(date + "\t[ save the last configuration ]" + System.lineSeparator(), KnownFile.TAG_HISTORY);
                }
            } else {
                Toast.makeText(this, "Ndef message not found.", Toast.LENGTH_LONG).show();
            }
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
