package com.example.olivier.sym_labo3;

import android.content.Intent;
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

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, PreBarScanActivity.class);
        startActivity(intent);
    }*/
}
