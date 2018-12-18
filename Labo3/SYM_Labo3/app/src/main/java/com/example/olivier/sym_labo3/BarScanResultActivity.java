/**
 * File : BarScanResultActivity.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 16.12.2018
 *
 * This activity chow the result of a scan
 */
package com.example.olivier.sym_labo3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BarScanResultActivity extends AppCompatActivity {

    private TextView scanResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_scan_result);

        scanResultText = findViewById(R.id.scanResult);

        String result = getIntent().getStringExtra("SCAN_RESULT");

        scanResultText.setText(result);
    }
}
