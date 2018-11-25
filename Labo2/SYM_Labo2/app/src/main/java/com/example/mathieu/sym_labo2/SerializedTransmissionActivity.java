package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SerializedTransmissionActivity extends AppCompatActivity {

    private EditText editFirstName;
    private EditText editLastName;
    private TextView serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serialized_transmission);

        editFirstName  = findViewById(R.id.firstnameField);
        editLastName   = findViewById(R.id.lastnameField);
        serverResponse = findViewById(R.id.SerializedResponseFromServer);

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
        /*String payload =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE directory SYSTEM \"http://sym.iict.ch/directory.dtd\">\n" +
                        "<directory>" +
                        "<person>" +
                        "<name>name1</name>" +
                        "<firstname>firstname1</firstname>" +
                        "<gender></gender>" +
                        "<phone type='home'>523324234</phone>" +
                        "</person>" +
                        "</directory>";*/

        Document document = buildXml();

        new AsyncSendRequest(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                if (response != null) {
                    return displayServerResponse(response);
                }
                return false;
            }
        }).execute("http://sym.iict.ch/rest/xml/", new XMLOutputter().outputString(document), SymComManager.XML);
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

    private Document buildXml() {
        String firstName = editFirstName.getText().toString();
        String lastName  = editLastName.getText().toString();
        Person p = new Person(firstName, lastName);

        Element root = new Element("directory");
        Document document = new Document(root);

        Element person = new Element("person");
        person.addContent(new Element("name").setText(p.getLastName()));
        person.addContent(new Element("firstname").setText(p.getFirstName()));
        person.addContent(new Element("gender").setText(p.getGender()));
        Element phone = new Element("phone");
        phone.setAttribute("type", "private");
        person.addContent(phone.setText(p.getPhoneNumber()));

        root.addContent(person);

        return document;
    }

    private JSONObject buildJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (editFirstName != null && editLastName != null) {
            jsonObject.accumulate("firstName", editFirstName.getText().toString());
            jsonObject.accumulate("lastName", editLastName.getText().toString());
        }
        return jsonObject;
    }

    private class Person {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String gender;

        public Person(String firstName, String lastName) {
            this(firstName, lastName, "neutral" , "000-000-000");
        }

        public Person(String firstName, String lastName, String gender, String phoneNumber) {
            this.firstName   = firstName;
            this.lastName    = lastName;
            this.gender      = gender;
            this.phoneNumber = phoneNumber;
        }

        public String getFirstName() { return firstName; }

        public String getLastName() { return lastName; }

        public String getGender() { return gender; }

        public String getPhoneNumber() { return phoneNumber; }
    }
}
