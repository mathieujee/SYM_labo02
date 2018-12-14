package com.example.olivier.sym_labo3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.olivier.sym_labo3.utils.PermissionUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class BarScanActivity extends AppCompatActivity {

    private static final String[] permissionsNeeded = {Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_scan);

        PermissionUtils.checkPermissions(this, permissionsNeeded);

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
                    Intent intent = new Intent(this, BarScanResultActivity.class);
                    intent.putExtra("SCAN_RESULT", result.getContents());
                    startActivity(intent);
                }
            }
            finish();
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}