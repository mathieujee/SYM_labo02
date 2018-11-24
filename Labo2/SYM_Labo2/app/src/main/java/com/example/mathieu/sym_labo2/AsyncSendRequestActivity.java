package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AsyncSendRequestActivity extends AppCompatActivity {

    private EditText text;
    private TextView serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_send_request);

        text = findViewById(R.id.textField);
        serverResponse = findViewById(R.id.SerializedResponseFromServer);

        if(savedInstanceState != null){
            this.text.setText(savedInstanceState.getString("text",""));
            this.serverResponse.setText(savedInstanceState.getString("serverResponse",""));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("text", text.getText().toString());
        outState.putString("serverResponse", serverResponse.getText().toString());
    }

    public void sendText(View view) {
        new AsyncSendRequest(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                return setResponse(response);
            }
        }).execute("http://sym.iict.ch/rest/txt", text.getText().toString(), SymComManager.TEXT);
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
