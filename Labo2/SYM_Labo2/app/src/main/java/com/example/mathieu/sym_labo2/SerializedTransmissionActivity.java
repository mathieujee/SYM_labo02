package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;

public class SerializedTransmissionActivity extends AppCompatActivity {

    private EditText editFirstname;
    private EditText editLastname;
    private TextView serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serialized_transmission);

        editFirstname = (EditText) findViewById(R.id.firstnameField);
        editLastname = (EditText) findViewById(R.id.lastnameField);
        serverResponse = (TextView) findViewById(R.id.SerializedResponseFromServer);

    }

    public void sendJsonPayload(View view) {
        JSONObject jsonObject = null;
        try {
            jsonObject = buildJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new AsyncSendRequest(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                if (response != null) {

                    return displayServerResponse(response);
                }
                return false;
            }
        }).execute("http://sym.iict.ch/rest/json/", jsonObject.toString(), SymComManager.JSON);
    }

    public void sendXMLPayload() {

    }

    private boolean displayServerResponse(final String response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverResponse.setText(response);
            }
        });
        return true;
    }

    private JSONObject buildJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (editFirstname != null && editLastname != null) {
            jsonObject.accumulate("firstName", editFirstname.getText().toString());
            jsonObject.accumulate("lastName", editLastname.getText().toString());
        }
        jsonObject.accumulate("alo", "coucou");
        return jsonObject;
    }
}
