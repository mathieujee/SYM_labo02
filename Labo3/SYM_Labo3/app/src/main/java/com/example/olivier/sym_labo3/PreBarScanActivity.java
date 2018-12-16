/**
 * File : PreBarScanActivity.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 16.12.2018
 *
 */

package com.example.olivier.sym_labo3;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.olivier.sym_labo3.utils.PermissionUtils;

public class PreBarScanActivity extends AppCompatActivity {

    private static final String[] permissionsNeeded = {Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_bar_scan);

        PermissionUtils.checkPermissions(this, permissionsNeeded);
    }

    public void startBarScanActivity(View view) {
        Intent intent = new Intent(this, BarScanActivity.class);
        startActivity(intent);
    }
}
