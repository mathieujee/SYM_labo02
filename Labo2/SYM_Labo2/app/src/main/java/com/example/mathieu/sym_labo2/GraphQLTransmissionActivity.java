package com.example.mathieu.sym_labo2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GraphQLTransmissionActivity extends AppCompatActivity {

    final String API_URL = "http://sym.iict.ch/api/graphql";
    List<String> authors = new ArrayList<>();
    ArrayAdapter<String> adapterSpinner;
    Context context = this;
    String authResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph_qltransmission);
        Spinner spinner = findViewById(R.id.graphSpinner);

        if(savedInstanceState != null){
            authResp = savedInstanceState.getString("authResp");
            adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, authors);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterSpinner);
            fillAuthors(authResp);
        }else {

            new AsyncSendRequest(new CommunicationEventListener() {
                @Override
                public boolean handleServerResponse(String response) {
                    authResp = response;
                    return fillAuthors(response);
                }
            }).execute(API_URL, "{\"query\": \"{allAuthors{first_name last_name}}\"}", SymComManager.JSON);

            authors.add("Loading...");
            adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, authors);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterSpinner);
        }

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int authId = position + 1;
                        new AsyncSendRequest(new CommunicationEventListener() {
                            @Override
                            public boolean handleServerResponse(String response) {
                                return managePosts(response);
                            }
                        }).execute(API_URL, "{\"query\": \"{allPostByAuthor(authorId: " + authId + "){title content}}\"}", SymComManager.JSON);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("authResp", authResp);
    }

    private boolean fillAuthors(String resp) {
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
                    adapterSpinner.clear();
                    adapterSpinner.addAll(authors);
                    adapterSpinner.notifyDataSetChanged();
                }
            });

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean managePosts(String resp) {
        try {
            JSONObject json = new JSONObject(resp);
            JSONArray posts = json.getJSONObject("data").getJSONArray("allPostByAuthor");

            final String[] postsTab = new String[posts.length()];
            String textViewContent = "";
            for (int i = 0; i < posts.length(); ++i) {
                String title = posts.getJSONObject(i).getString("title");
                String content = posts.getJSONObject(i).getString("content");
                String post = title + "\n\n" + content + "\n\n\n";
                postsTab[i] = post;
                textViewContent += post;
            }

            final String finalTextViewContent = textViewContent;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        ScrollView scrollView = findViewById(R.id.graphScrollView);
                        TextView textView = new TextView(context);
                        textView.setText(finalTextViewContent);

                        scrollView.removeAllViews();
                        scrollView.addView(textView );
                }
            });

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}