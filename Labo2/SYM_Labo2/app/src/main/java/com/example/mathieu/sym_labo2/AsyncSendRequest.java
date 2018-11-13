package com.example.mathieu.sym_labo2;

import android.os.AsyncTask;

import okhttp3.MediaType;

public class AsyncSendRequest extends AsyncTask<String, Void, String> {

    private CommunicationEventListener com;

    public void AsyncSendRequest(CommunicationEventListener com){
        this.com = com;
    }

    @Override
    protected String doInBackground(String... params) {
        SymComManager manager = new SymComManager();
        manager.setCommunicationEventListener(com);

        manager.sendRequest(params[0], params[1], MediaType.parse(params[2]));

        return "0";
    }
}
