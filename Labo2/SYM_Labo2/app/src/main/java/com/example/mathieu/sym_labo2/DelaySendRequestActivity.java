package com.example.mathieu.sym_labo2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DelaySendRequestActivity extends AppCompatActivity {

    private EditText text;
    private TextView serverResponse;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_send_request);

        text = findViewById(R.id.textField);
        serverResponse = findViewById(R.id.SerializedResponseFromServer);
        context = getApplicationContext();
    }

    public void sendText(View view) {
        new AsyncSendRequest(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                return setResponse(response);
            }
        }, true).execute("http://sym.iict.ch/rest/txt", text.getText().toString(), SymComManager.TEXT);
    }

    public static Context getContext() {
        return context;
    }

    public boolean setResponse(final String response){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverResponse.setText(response);
            }
        });
        return true;
    }
}
