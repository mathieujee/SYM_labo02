package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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

        editFirstname = findViewById(R.id.firstnameField);
        editLastname = findViewById(R.id.lastnameField);
        serverResponse = findViewById(R.id.SerializedResponseFromServer);
        if(savedInstanceState != null){
            this.editFirstname.setText(savedInstanceState.getString("editFirstname",""));
            this.editLastname.setText(savedInstanceState.getString("editLastname",""));
            this.serverResponse.setText(savedInstanceState.getString("serverResponse",""));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("editFirstname", editFirstname.getText().toString());
        outState.putString("editLastName", editLastname.getText().toString());
        outState.putString("serverResponse", serverResponse.getText().toString());
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
                    try {
                        return displayServerResponse(new JSONObject(response).toString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        }).execute("http://sym.iict.ch/rest/json/", jsonObject.toString(), SymComManager.JSON);
    }

    public void sendXMLPayload(View view) {
        String payload =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE directory SYSTEM \"http://sym.iict.ch/directory.dtd\">\n" +
                        "<directory>" +
                        "<person>" +
                        "<name>name1</name>" +
                        "<firstname>firstname1</firstname>" +
                        "<gender></gender>" +
                        "<phone type='home'>523324234</phone>" +
                        "</person>" +
                        "</directory>";

        new AsyncSendRequest(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                if (response != null) {
                    return displayServerResponse(response);
                }
                return false;
            }
        }).execute("http://sym.iict.ch/rest/xml", payload, SymComManager.XML);
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

    private String buildXml(Object o) throws JsonProcessingException {
        /*XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(o);*/
        return "";
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

    private class Person {
        private String fullName;
        private String phoneNumber;

        public Person(String fullName, String phoneNumber) {
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
        }

        public String getFullName() {
            return fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
}
