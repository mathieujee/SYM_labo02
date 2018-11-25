package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;

public class SerializedTransmissionActivity extends AppCompatActivity {

    private EditText editFirstName;
    private EditText editLastName;
    private TextView serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serialized_transmission);

        editFirstName = findViewById(R.id.firstnameField);
        editLastName = findViewById(R.id.lastnameField);
        serverResponse = findViewById(R.id.SerializedResponseFromServer);
        if(savedInstanceState != null){
            this.editFirstName.setText(savedInstanceState.getString("editFirstname",""));
            this.editLastName.setText(savedInstanceState.getString("editLastname",""));
            this.serverResponse.setText(savedInstanceState.getString("serverResponse",""));
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("editFirstname", editFirstName.getText().toString());
        outState.putString("editLastName", editLastName.getText().toString());
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
            public boolean handleServerResponse(byte[] response) {
                if (response != null) {
                    try {
                        return displayServerResponse(new JSONObject(new String(response)).toString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        }).execute("http://sym.iict.ch/rest/json/", jsonObject.toString(), SymComManager.JSON);
    }

    public void sendXMLPayload(View view) {

        String payload = buildXml(new Person(editFirstName.getText().toString(), editLastName.getText().toString()));

        new AsyncSendRequest(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(byte[] response) {
                if (response != null) {
                    return displayServerResponse(new String(response));
                }
                return false;
            }
        }).execute("http://sym.iict.ch/rest/xml/", payload, SymComManager.XML);
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

    private String buildXml(Person p) {
        Element directory = new Element("directory");
        Document doc = new Document(directory);
        doc.setDocType(new DocType("directory", "http://sym.iict.ch/directory.dtd"));

        Element person = new Element("person");
        person.addContent(new Element("name").setText(p.getLastName()));
        person.addContent(new Element("firstname").setText(p.getFirstName()));
        person.addContent(new Element("gender").setText(p.getGender()));
        Element phone = new Element("phone");

        phone.setAttribute(new Attribute("type", p.getPhoneType()));
        phone.setText(p.getPhoneNumber());
        person.addContent(phone);

        doc.getRootElement().addContent(person);

        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        StringWriter writer = new StringWriter();
        try {
            out.output(doc, writer);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return writer.toString();
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
        private String phoneType;
        private String phoneNumber;
        private String gender;

        public Person(String firstname, String lastName, String phoneNumber, String phoneType, String gender) {
            this.firstName = firstname;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.phoneType = phoneType;
            this.gender = gender;
        }

        public Person(String firstName, String lastName){
            this(firstName, lastName, "", "home", "");
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPhoneType() {
            return phoneType;
        }

        public String getGender() {
            return gender;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
}
