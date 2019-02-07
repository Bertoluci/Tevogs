package com.aero4te.tevogs;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aero4te.tevogs.model.BodyRecordWrapper;
import com.aero4te.tevogs.model.CipherKey;
import com.aero4te.tevogs.model.CipherUtil;
import com.aero4te.tevogs.model.DistributionType;
import com.aero4te.tevogs.model.HeaderRecordWrapper;
import com.aero4te.tevogs.model.MessageWrapper;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Locale;

public class WriteActivity extends AppCompatActivity {

    //<editor-fold desc="Properties">
    private HeaderRecordWrapper hrw = null;
    private BodyRecordWrapper brw = null;
    //</editor-fold>

    //<editor-fold desc="Components">
    private EditText authKeyEditText;
    private EditText clientIdEditText;
    private EditText componentIdEditText;
    private EditText distributionPortEditText;
    private Spinner distributionTypeSpinner;
    private EditText multicastGroupEditText;
    private EditText serverIPv4EditText;
    private EditText wifiPasswordEditText;
    private EditText wifiSSIDEditText;
    //</editor-fold>

    //<editor-fold desc="Adapters">
    private NfcAdapter nfcAdapter;
    private ArrayAdapter<DistributionType> distributionPortAdapter;
    //</editor-fold>

    //<editor-fold desc="Override methods">
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, WriteActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        init(json);
        Toast.makeText(this, R.string.approachTag, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        SecureRandom secureRandom = new SecureRandom();
//        CipherKey.key = null;
        if (CipherKey.key == null) {
            CipherKey.key = new byte[16];
            secureRandom.nextBytes(CipherKey.key);
            try {
                MainActivity.sharedPreferences.edit().putString("key", new String(CipherKey.key, "ISO-8859-1")).apply();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
        try {
            if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                hrw = new HeaderRecordWrapper(
                        HeaderRecordWrapper.DEFAULT_ID,
                        HeaderRecordWrapper.DEFAULT_FORMAT_VERSION,
                        iv
                );

                byte[] bodyCipher = null;
                String headTxtJson = hrw.toString();
                byte[] bodyBytesJson = brw.toString().getBytes("UTF-8");

                try {
                    CipherUtil cipherUntil = new CipherUtil(CipherKey.key, iv);
                    bodyCipher = cipherUntil.cipher(bodyBytesJson);
                } catch (Exception e) {
                    Log.e("cipher", e.getMessage());
                    e.printStackTrace();
                }

                NdefRecord headNdefRecord = createTextRecord(headTxtJson);
                NdefRecord bodyNdefRecord = createUnknownRecord(bodyCipher);
                NdefRecord aaRecord = NdefRecord.createApplicationRecord(this.getPackageName());
                NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] { headNdefRecord, bodyNdefRecord, aaRecord });
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                boolean writeResult = writeNdefMessage(tag, ndefMessage);
                if (writeResult) {
                    Toast.makeText(this, "Tag written!", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(this, MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    Toast.makeText(this, "Tag write failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("onNewIntent", e.getMessage());
            e.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Private support methods">
    private void init(String json) {
        if (json == null) {
            brw = new BodyRecordWrapper(
                    "192.168.10.0",
                    DistributionType.Multicast,
                    "5555",
                    "192.168.10.1",
                    "007",
                    "666",
                    "authkey",
                    "MyWlan",
                    "wifiPassword"
            );
        } else {
            brw = new MessageWrapper(json).getBodyRecordWrapper();
        }

        authKeyEditText = findViewById (R.id.etAuthKey); {
            authKeyEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    brw.setAuthkey(authKeyEditText.getText().toString());
                }
            });
            authKeyEditText.setText (brw.getAuthkey());
        }

        clientIdEditText = findViewById (R.id.etClientId); {
            clientIdEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    brw.setClientId(clientIdEditText.getText().toString());
                }
            });
            clientIdEditText.setText(brw.getClientId());
        }

        componentIdEditText = findViewById (R.id.etComponentId); {
            componentIdEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    brw.setComponentID(componentIdEditText.getText().toString());
                }
            });
            componentIdEditText.setText(brw.getComponentID());
        }

        distributionPortEditText = findViewById (R.id.etDistributionPort); {
            distributionPortEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    brw.setDistributionPort(distributionPortEditText.getText().toString());
                }
            });
            distributionPortEditText.setText(brw.getDistributionPort());
        }

        distributionTypeSpinner = (Spinner) findViewById(R.id.sprDistributionPort);{
            distributionPortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DistributionType.values());
            distributionPortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            distributionTypeSpinner.setAdapter(distributionPortAdapter);
            distributionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DistributionType item = (DistributionType) parent.getItemAtPosition(position);
                    if (DistributionType.Multicast == item) {
                        multicastGroupEditText.setEnabled(true);
                    } else {
                        multicastGroupEditText.setText("");
                        multicastGroupEditText.setEnabled(false);
                    }
                    brw.setDistributionType(item);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            distributionTypeSpinner.setSelection(distributionPortAdapter.getPosition(brw.getDistributionType()));
        }

        multicastGroupEditText = (EditText) findViewById(R.id.etMulticastGroup); {
            multicastGroupEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    brw.setMulticastGroup(multicastGroupEditText.getText().toString());
                }
            });
            multicastGroupEditText.setText(brw.getMulticastGroup());
        }

        serverIPv4EditText = (EditText) findViewById(R.id.etServerIPv4); {
            serverIPv4EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    brw.setServerIPv4(serverIPv4EditText.getText().toString());
                }
            });
            serverIPv4EditText.setText(brw.getServerIPv4());
        }

        wifiPasswordEditText = (EditText) findViewById(R.id.etWifiPassword); {
            wifiPasswordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    brw.setWifiPassword(wifiPasswordEditText.getText().toString());
                }
            });
            wifiPasswordEditText.setText(brw.getWifiPassword());
        }

        wifiSSIDEditText = (EditText) findViewById(R.id.etWifiSSID); {
            wifiSSIDEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    brw.setWifiSSID(wifiSSIDEditText.getText().toString());
                }
            });
            wifiSSIDEditText.setText(brw.getWifiSSID());
        }
    }

    private NdefRecord createTextRecord(String content) {
        byte[] language;
        language = Locale.getDefault().getLanguage().getBytes();
        final byte[] text = content.getBytes();
        final int languageSize = language.length;
        final int textLength = text.length;
        final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);
        payload.write((byte) (languageSize & 0x1F));
        payload.write(language, 0, languageSize);
        payload.write(text, 0, textLength);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
    }

    private NdefRecord createUnknownRecord(byte[] content) {
        return new NdefRecord(NdefRecord.TNF_UNKNOWN, new byte[0], new byte[0], content);
    }

    private boolean writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if (tag != null) {
                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {
                    return formatTag(tag, ndefMessage);
                } else {
                    ndef.connect();
                    if (ndef.isWritable()) {
                        ndef.writeNdefMessage(ndefMessage);
                        ndef.close();
                        return true;
                    }
                    ndef.close();
                }
            }
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
        return false;
    }

    private boolean formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormat = NdefFormatable.get(tag);
            if (ndefFormat != null) {
                ndefFormat.connect();
                ndefFormat.format(ndefMessage);
                ndefFormat.close();
                return true;
            }
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
        return false;
    }
    //</editor-fold>
}