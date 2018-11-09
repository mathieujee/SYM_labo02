package com.example.mathieu.sym_labo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class GraphQLTransmissionActivity extends AppCompatActivity {

    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_qltransmission);
        getAuthors();

        final Spinner spinner = findViewById(R.id.graphSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, authors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        showToast("Spinner1: position=" + position + " id=" + id);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        showToast("Spinner1: unselected");
                    }
                }
        );
    }

    static String[] authors = new String[]{"Romain", "Oliver", "Jee", "Florent Nicolas Pillou"};

    private void getAuthors(){
        new AsyncRequest().execute("http://sym.iict.ch/api/graphql", "{\"query\": \"{allAuthors{first_name last_name}}\"}");

        /*
        JSONObject postData = new JSONObject();
        try {
            postData.put("query", "{allAuthors{first_name last_name}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
