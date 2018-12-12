package com.example.olivier.sym_labo3;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class NFCScanActivity extends AppCompatActivity {

    private EditText usernameEditText = null;
    private EditText passwordEditText = null;
    private ImageView nfcDetectedImage = null;
    private TextView nfcDetectedText = null;
    private Button loginButton = null;

    private boolean nfcDetected = false;
    private String nfcData = null;

    private int seconds;

    private static final String[] permissionsNeeded = {Manifest.permission.NFC};

    // Hard coded credentials (Only for exercise purpose)
    private final String CREDENTIAL_USERNAME = "toto";
    private final String CREDENTIAL_PASSWORD = "tata";
    private final String CREDENTIAL_NFC_TAG  = "jeeeeeeeeeeeeee";

    private final int NFC_TIMER = 30;
    public static final String TAG = "NfcDemo";

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcscan);

        checkPermissions(permissionsNeeded);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        nfcDetectedImage = findViewById(R.id.nfcImage);
        loginButton = findViewById(R.id.loginButton);
        nfcDetectedText = findViewById(R.id.nfc);
        nfcDetectedText.setTextColor(Color.RED);

        seconds = NFC_TIMER;

        // listener for loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // check that nfc tag is used
                if (!nfcDetected) {
                    Toast.makeText(NFCScanActivity.this, getResources().getString(R.string.nfcDetectionFalse), Toast.LENGTH_LONG).show();
                    return;
                }

                // verify username / password / NFC tag
                if (!checkCredentials(username, password, nfcData)) {
                    Toast.makeText(NFCScanActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(NFCScanActivity.this, NFCSCanHomePageActivity.class);
                startActivity(intent);
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here if NFC is not available
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled. Please enable it.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions(permissionsNeeded);
        setupForegroundDispatch();
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(); // This call is done before 'onPause()', otherwise an IllegalArgumentException is thrown
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if ("text/plain".equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... tags) {
            Tag tag = tags[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /**
             * bit_7: encoding
             * bit_6: reserved for future use, must be 0
             * bit_5..0: length of IANA language code
             *
             * http://www.nfc-forum.org/specs/
             */

            byte[] payload = record.getPayload();

            // Get text encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get language code
            int languageCodeLength = payload[0] & 0063;

            // Get text
            String textData = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);;
            return textData;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                nfcDetectedText.setText(R.string.nfcDetectionTrue);
                nfcDetectedText.setTextColor(Color.GREEN);
                nfcDetectedImage.setImageDrawable(getResources().getDrawable(R.drawable.nfc_detected));
                nfcData = result;
                nfcDetected = true;
                timer();
            }
        }
    }

    private void setupForegroundDispatch() {
        if (mNfcAdapter == null)
            return;
        final Intent intent = new Intent(this.getApplicationContext(), this.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e(TAG, "MalformedMimeTypeException", e);
        }
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
    }

    private void stopForegroundDispatch() {
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    private boolean checkCredentials(String username, String password, String nfcData) {
        if (username.equals(CREDENTIAL_USERNAME) && password.equals(CREDENTIAL_PASSWORD) && nfcData.equals(CREDENTIAL_NFC_TAG)) {
            return true;
        }
        return false;
    }

    protected void checkPermissions(String... permissions){
        ArrayList<String> permissionsNotGranted = new ArrayList<>();
        int missingPermissions = 0;
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission);
                    missingPermissions++;
                }
            }
        }
        if(missingPermissions == 0)
            return;
        String[] permissionsToAsk = new String[missingPermissions];
        for(int i = 0; i < missingPermissions; i++){
            permissionsToAsk[i] = permissionsNotGranted.get(i);
        }
        ActivityCompat.requestPermissions(this, permissionsToAsk, 1);
    }

    private void timer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(seconds > 0) {
                    seconds--;
                    handler.postDelayed(this, 1000);
                }
                else {
                    // Timer over => reset nfc data
                    nfcData = null;
                    seconds = NFC_TIMER;
                    nfcDetectedImage.setImageDrawable(getResources().getDrawable(R.drawable.no_nfc_detected));
                    nfcDetectedText.setText(R.string.nfcDetectionFalse);
                    nfcDetectedText.setTextColor(Color.RED);
                    nfcDetected = false;
                }
            }
        });
    }
}
