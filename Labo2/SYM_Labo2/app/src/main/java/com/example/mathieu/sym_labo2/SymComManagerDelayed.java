/**
 * File : SymComManagerDelayed.java
 *
 * Authors : Jee Mathieu, Kopp Olivier, Silvestri Romain
 *
 * Date : 25.11.2018
 *
 * This class is used by the delaySendRequest activity in order to delay the request.
 */
package com.example.mathieu.sym_labo2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;

public class SymComManagerDelayed extends SymComManager {
    private static List<PendingRequest> pendingRequestList = new ArrayList<>();
    private static final int MAX_TRIES = 10;
    private static final int MS_BETWEEN_TRIES = 10000;
    private static final String URL = "http://sym.iict.ch/";
    private static boolean isIniatialise = false;
    private static Timer T;


    public SymComManagerDelayed(){
        super();
        if(!isIniatialise){
            isIniatialise = true;
            T = new Timer("delayedTransmission", true);
        }
    }

    public void sendRequestWithoutHeaders(String url, String payload, MediaType type) {
        if(isURLReachable(DelaySendRequestActivity.getContext())){
            super.sendRequestWithoutHeaders(url, payload, type);
        }
        else {
            PendingRequest pr = new PendingRequest(payload, url, type);
            pendingRequestList.add(pr);
            //si c'est la premiere requete echouée, on demarre la routine d'execution
            if(pendingRequestList.size() == 1){
                T.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        retry();
                    }
                }, new Date(), MS_BETWEEN_TRIES);
            }
        }
    }

    /**
     * fonction qui essaye d'envoyer les requetes en attente
     */
    public void retry(){
        for(int i = 0; i < pendingRequestList.size(); i++) {
            PendingRequest pr = pendingRequestList.get(i);

            //si on depasse le nombre d'essai maximal, on retire la requete de la liste
            if (pr.getNumberOfTries() > MAX_TRIES) {
                pendingRequestList.remove(i);
                i--;
            } else {
                if (isURLReachable(DelaySendRequestActivity.getContext())) {
                    pendingRequestList.remove(i);
                    i--;
                    super.sendRequestWithoutHeaders(pr.getUrl(), pr.getPayload(), pr.getMediaType());
                } else {
                    pendingRequestList.set(i, pr.incTries());
                }
            }
        }
        //si on a envoyé toutes les requetes, on arrete la routine d'execution
        if(pendingRequestList.size() == 0){
            T.cancel();
            T.purge();
            T = new Timer("delayedTransmission", true);
        }
    }

    /**
     * check si l'url est accessible
     * https://stackoverflow.com/questions/1443166/android-how-to-check-if-the-server-is-available
     */
    public static boolean isURLReachable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL(URL);
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(10 * 1000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (MalformedURLException e1) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }
            return false;

    }

    //Classe permettant d'encapsuler les requetes en attente
    private class PendingRequest {
        private String payload;
        private String url;
        private MediaType mediaType;
        private int numberOfTries = 0;

        public PendingRequest(String payload, String url, MediaType mediaType){
            this.payload = payload;
            this.url = url;
            this.mediaType = mediaType;
        }

        public String getPayload() {
            return payload;
        }

        public String getUrl() {
            return url;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

        public int getNumberOfTries() {
            return numberOfTries;
        }

        public PendingRequest incTries(){
            numberOfTries++;
            return this;
        }
    }
}
