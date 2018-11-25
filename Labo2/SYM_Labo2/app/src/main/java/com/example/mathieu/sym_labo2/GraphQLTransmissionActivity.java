/**
 * File : GraphQLTransmissionActivity.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 25.11.2018
 *
 * This activity allow users to send graphQL request to a server and display the response.
 * In our case, we allow user to select an author from a spinner and the app will display all texts linked to that author
 */
package com.example.mathieu.sym_labo2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private final static String ALL_AUTHORS_QUERY = "{\"query\": \"{allAuthors{first_name last_name}}\"}";

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

        // Check if there is a savedInstance that can be restored. If it's the case, get server response
        // from the bundle, extract the authors' list and update the spinner
        if(savedInstanceState != null){
            authResp = savedInstanceState.getString("authResp");
            adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, authors);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterSpinner);
            fillAuthors(authResp);
        }else {

            // Create a new Async request to get all the authors' names from the server.
            // use the method fillAuthors to fill the spinner with the names
            new AsyncSendRequest(new CommunicationEventListener() {
                @Override
                public boolean handleServerResponse(byte[] response) {
                    authResp = new String(response);
                    return fillAuthors(new String(response));
                }
            }).execute(API_URL, ALL_AUTHORS_QUERY, SymComManager.JSON);

            // Add a text in the spinner to show the user that the datas are being requested
            authors.add("Loading...");
            adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, authors);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterSpinner);
        }

        spinner.setOnItemSelectedListener(
                // When an author is selected from the spinner, send a request to the server to get
                // all his posts by using his position in the list as ID.
                // Give the response to the method managePosts that update the scrollView
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int authId = position + 1;
                        new AsyncSendRequest(new CommunicationEventListener() {
                            @Override
                            public boolean handleServerResponse(byte[] response) {
                                return managePosts(new String(response));
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
        // Save the server's response containing all the authors' names
        outState.putString("authResp", authResp);
    }

    // Extract the authors' names from the response string, add them to the adapter then update the
    // spinner. Return true or false depending on the success or failure of the operation.
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

    // Format the posts in the response string and add them to the textView.
    // Once all the posts are treated, add the textView to the scrollView so the posts can be
    // displayed on screen.
    //  Return true or false depending on the success or failure of the operation.
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