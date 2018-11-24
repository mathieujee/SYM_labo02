package com.example.mathieu.sym_labo2;

import android.os.AsyncTask;

import okhttp3.MediaType;

public class AsyncSendRequest extends AsyncTask<String, Void, String> {

    private CommunicationEventListener com;
    private boolean isDelayed = false;

    public AsyncSendRequest(CommunicationEventListener com, boolean isDelayed){
        this.com = com;
        this.isDelayed = isDelayed;
    }

    public AsyncSendRequest(CommunicationEventListener com){
        this(com, false);
    }

    @Override
    protected String doInBackground(String... params) {
        SymComManager manager;
        if(isDelayed){
            manager = new SymComManagerDelayed();
        }
        else {
            manager = new SymComManager();
        }

        manager.setCommunicationEventListener(com);
        manager.sendRequest(params[0], params[1], MediaType.parse(params[2]));

        return "0";
    }
}
