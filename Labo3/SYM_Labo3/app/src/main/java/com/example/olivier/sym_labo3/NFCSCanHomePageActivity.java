package com.example.olivier.sym_labo3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class NFCSCanHomePageActivity extends AppCompatActivity {

    private int securityLevel;
    private int seconds;

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
