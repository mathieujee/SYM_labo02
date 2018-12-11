package com.example.olivier.sym_labo3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class BarScanActivity extends AppCompatActivity {

    private static final String[] permissionsNeeded = {Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_scan);

        checkPermissions(permissionsNeeded);

        new IntentIntegrator(this).initiateScan();
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            // Empty QRcode
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            }

            else {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
}