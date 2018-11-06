package com.example.mathieu.sym_labo2;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SymComManager {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
    public static final MediaType TEXT = MediaType.parse("plain/text; charset=utf-8");
    private final static OkHttpClient client = new OkHttpClient();
    private CommunicationEventListener l;

    public void sendRequest(String url, String payload, MediaType type) {
        RequestBody body = RequestBody.create(type, payload);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(response != null) {
                l.handleServerResponse(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCommunicationEventListener (CommunicationEventListener l){
        this.l = l;
    }
}
