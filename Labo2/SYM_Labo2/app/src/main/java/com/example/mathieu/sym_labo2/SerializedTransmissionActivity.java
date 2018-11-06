package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class SerializedTransmissionActivity extends AppCompatActivity {

    private EditText editFirstname;
    private EditText editLastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serialized_transmission);

        editFirstname = (EditText)findViewById(R.id.firstnameField);
        editLastname  = (EditText)findViewById(R.id.lastnameField);
    }

    public void sendJsonPayload() throws JSONException {
        JSONObject jsonObject = buildJson();

        SymComManager scm = new SymComManager();
        scm.setCommunicationEventListener(
                new CommunicationEventListener() {
                    @Override
                    public boolean handleServerResponse(String response) {
                        return false;
                    }
                }
        );
        scm.sendRequest("http://sym.iict.ch/rest/json", jsonObject.toString(), SymComManager.JSON);
    }

    public void sendXMLPayload() {

    }

    private JSONObject buildJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("firstName", editFirstname.getText().toString());
        jsonObject.accumulate("lastName", editLastname.getText().toString());

        return jsonObject;
    }
}
