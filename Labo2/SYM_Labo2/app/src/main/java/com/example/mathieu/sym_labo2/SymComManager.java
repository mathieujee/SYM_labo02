/**
 * File : SymComManager.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 25.11.2018
 *
 * This class is used to send request with okhttp3 library.
 */
package com.example.mathieu.sym_labo2;

import android.util.Pair;

import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SymComManager {
    public static final String JSON = "application/json; charset=utf-8";
    public static final String XML = "application/xml; charset=utf-8";
    public static final String TEXT = "plain/text; charset=utf-8";
    private final static OkHttpClient client = new OkHttpClient();
    private CommunicationEventListener l;

    public void sendRequest(Request request) {
        Response response;
        try {
            response = client.newCall(request).execute();
            if(response != null) {
                l.handleServerResponse(response.body().bytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            l.handleServerResponse("ERROR, the server did not answer to the request".getBytes());
        }
    }

    public void sendRequest2(Request request) {
        Response response;
        try {
            response = client.newCall(request).execute();
            if(response != null) {
                l.handleServerResponse(response.body().bytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            l.handleServerResponse("ERROR, the server did not answer to the request".getBytes());
        }
    }

    public void sendRequestWithoutHeaders(String url, String payload, MediaType type) {
        RequestBody body = RequestBody.create(type, payload);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        sendRequest(request);
    }

    public void sendRequestWithHeaders(String url, String payload, MediaType type, List<Pair<String, String>> headers) {
        RequestBody body = RequestBody.create(type, payload);
        Headers.Builder h = new Headers.Builder();
        for (int i = 0; i < headers.size(); i++){
            h.add(headers.get(i).first, headers.get(i).second);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(h.build())
                .build();
        sendRequest(request);
    }

    public void sendRequestWithHeaders(String url, byte[] payload, MediaType type, List<Pair<String, String>> headers) {
        RequestBody body = RequestBody.create(type, payload);
        Headers.Builder h = new Headers.Builder();
        for (int i = 0; i < headers.size(); i++){
            h.add(headers.get(i).first, headers.get(i).second);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(h.build())
                .build();
        sendRequest(request);
    }

    public void setCommunicationEventListener (CommunicationEventListener l){
        this.l = l;
    }
}
