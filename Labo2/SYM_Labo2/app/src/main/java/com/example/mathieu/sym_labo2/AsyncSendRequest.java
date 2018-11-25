package com.example.mathieu.sym_labo2;

import android.os.AsyncTask;
import android.util.Pair;

import java.util.List;

import okhttp3.MediaType;

public class AsyncSendRequest extends AsyncTask<String, Void, String> {

    private CommunicationEventListener com;
    private boolean isDelayed = false;
    private List<Pair<String, String>> headers;

    public AsyncSendRequest(CommunicationEventListener com, boolean isDelayed, List<Pair<String, String>> headers){
        this.com = com;
        this.isDelayed = isDelayed;
        this.headers = headers;
    }

    public AsyncSendRequest(CommunicationEventListener com, boolean isDelayed){
        this(com, isDelayed, null);
    }

    public AsyncSendRequest(CommunicationEventListener com){
        this(com, false, null);
    }

    @Override
    protected String doInBackground(String... params) {
        if(params.length < 3){
            return null;
        }
        SymComManager manager;
        if(isDelayed){
            manager = new SymComManagerDelayed();
        }
        else {
            manager = new SymComManager();
        }

        manager.setCommunicationEventListener(com);
        if(headers == null || headers.size() == 0) {
            manager.sendRequestWithoutHeaders(params[0], params[1], MediaType.parse(params[2]));
        }
        else{
            manager.sendRequestWithHeaders(params[0], params[1], MediaType.parse(params[2]), headers);
        }
        return "";
    }
}
