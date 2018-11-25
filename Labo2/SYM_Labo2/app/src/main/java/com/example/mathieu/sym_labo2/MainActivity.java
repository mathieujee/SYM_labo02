package com.example.mathieu.sym_labo2;

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

    public void startSerializedTransmission(View view) {
        Intent intent = new Intent(this, SerializedTransmissionActivity.class);
        startActivity(intent);
    }

    public void startGraphQLTransmission(View view) {
        Intent intent = new Intent(this, GraphQLTransmissionActivity.class);
        startActivity(intent);
    }

    public void startAsyncTransmission(View view) {
        Intent intent = new Intent(this, AsyncSendRequestActivity.class);
        startActivity(intent);
    }

    public void startDelayTransmission(View view) {
        Intent intent = new Intent(this, DelaySendRequestActivity.class);
        startActivity(intent);
    }

    public void startCompressedTransmission(View view) {
        Intent intent = new Intent(this, CompressedTransmissionActivity.class);
        startActivity(intent);
    }
}

