/**
 * File : NFCScanHomePageActivity.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 16.12.2018
 *
 */

package com.example.olivier.sym_labo3;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.olivier.sym_labo3.utils.PermissionUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NFCSCanHomePageActivity extends AppCompatActivity {
    private static final String[] permissionsNeeded = {Manifest.permission.NFC};

    private static final String TAG = "NfcDemo";

    private final String CREDENTIAL_NFC_TAG  = "jeeeeeeeeeeeeee";

    private int securityLevel;
    private int seconds;

    private NfcAdapter mNfcAdapter;

    private final int INITIAL_SECURITY_TIMER = 40;
    private final int MAX_SECURITY_TIMER = 30;
    private final int MEDIUM_SECURITY_TIMER = 20;
    private final int LOW_SECURITY_TIMER = 10;
    private final int MAX_SECURITY_LEVEL = 3;
    private final int MEDIUM_SECURITY_LEVEL = 2;
    private final int LOW_SECURITY_LEVEL = 1;
    private final int MINIMAL_SECURITY_LEVEL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcscan_home_page);

        seconds = INITIAL_SECURITY_TIMER;
        securityLevel = MAX_SECURITY_LEVEL;
        timer();

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

    public void executeMaxSecurity(View view) {
        if(securityLevel < MAX_SECURITY_LEVEL) {
            showAlertDialog(getString(R.string.maxSecurity), getString(R.string.maxSecurityMessageNo));
            return;
        }
        showAlertDialog(getString(R.string.maxSecurity), getString(R.string.maxSecurityMessageYes));

        // here do stuff that requires MAX_SECURITY_LEVEL
    }

    public void executeMediumSecurity(View view) {
        if(securityLevel < MEDIUM_SECURITY_LEVEL) {
            showAlertDialog(getString(R.string.mediumSecurity), getString(R.string.mediumSecurityMessageNo));
            return;
        }
        showAlertDialog(getString(R.string.mediumSecurity), getString(R.string.mediumSecurityMessageYes));

        // here do stuff that requires MEDIUM_SECURITY_LEVEL
    }

    public void executeLowSecurity(View view) {
        if(securityLevel < LOW_SECURITY_LEVEL) {
            showAlertDialog(getString(R.string.lowSecurity), getString(R.string.lowSecurityMessageNo));
            return;
        }
        showAlertDialog(getString(R.string.lowSecurity), getString(R.string.lowSecurityMessageYes));

        // here do stuff that requires LOW_SECURITY_LEVEL
    }

    private void showAlertDialog(String alertTitle, String alertMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NFCSCanHomePageActivity.this);
        alertDialogBuilder.setTitle(alertTitle).setMessage(alertMessage);
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionUtils.checkPermissions(this, permissionsNeeded);
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

        public String readText(NdefRecord record) throws UnsupportedEncodingException {
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
            if (result != null && result.equals(CREDENTIAL_NFC_TAG)) {
                seconds = INITIAL_SECURITY_TIMER;
                securityLevel = MAX_SECURITY_LEVEL;
                Toast.makeText(NFCSCanHomePageActivity.this, "NFC tag detected. Security level back to MAX.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void timer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(seconds > 0) {
                    seconds--;

                    if(seconds < LOW_SECURITY_TIMER) {
                        securityLevel = MINIMAL_SECURITY_LEVEL;
                    }
                    else if(seconds < MEDIUM_SECURITY_TIMER) {
                        securityLevel = LOW_SECURITY_LEVEL;
                    }
                    else if(seconds < MAX_SECURITY_TIMER) {
                        securityLevel = MEDIUM_SECURITY_LEVEL;
                    }

                    handler.postDelayed(this, 1000);
                }
                else {
                    // Timer over => reset nfc data

                }
            }
        });
    }
}
