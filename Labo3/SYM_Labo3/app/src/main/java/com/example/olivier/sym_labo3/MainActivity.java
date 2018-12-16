/**
 * File : MainActivity.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 16.12.2018
 *
 * This is the main activity that allows users to navigate between the other 5 activities
 */
package com.example.olivier.sym_labo3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startNFCScan(View view) {
        Intent intent = new Intent(this, NFCScanActivity.class);
        startActivity(intent);
    }

    public void startPreBarScan(View view) {
        Intent intent = new Intent(this, PreBarScanActivity.class);
        startActivity(intent);
    }

    public void startIBeaconInfo(View view) {
        Intent intent = new Intent(this, IBeaconActivity.class);
        startActivity(intent);
    }

    public void startCompass(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

}

