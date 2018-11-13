package com.example.mathieu.sym_labo2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GraphQLTransmissionActivity extends AppCompatActivity {

    final String AUTHORS_URL = "{\"query\": \"{allAuthors{first_name last_name}}\"}";
    List<String> authors = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AsyncSendRequest(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                fillAuthors(response);
                return true;
            }
        }).execute("http://sym.iict.ch/api/graphql", AUTHORS_URL, SymComManager.JSON);

        setContentView(R.layout.activity_graph_qltransmission);

        Spinner spinner = findViewById(R.id.graphSpinner);
        authors.add("Loading...");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, authors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int authId = position + 1;
                        Log.e("TAG", adapter.getItem(position));
                        Log.e("TAG", String.valueOf(authId));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    private void fillAuthors(String resp) {
        Log.e("TAG", resp);
        try {
            JSONObject json = new JSONObject(resp);
            JSONArray auth = json.getJSONObject("data").getJSONArray("allAuthors");
            final String[] authors = new String[auth.length()];
            for (int i = 0; i < auth.length(); ++i) {
                String first = auth.getJSONObject(i).getString("first_name");
                String last = auth.getJSONObject(i).getString("last_name");
                String name = first + " " + last;
                authors[i] = name;
            }

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    adapter.addAll(authors);
                    adapter.notifyDataSetChanged();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
