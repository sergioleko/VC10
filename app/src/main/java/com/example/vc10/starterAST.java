package com.example.vc10;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static android.content.Context.WIFI_SERVICE;

public class starterAST extends AsyncTask<Void, Void, Void> {

    TCPOperations tcpo = new TCPOperations();

    WifiManager appWFM;

    TextView progressTextView;

    String progressText;

    ProgressBar progressBar;

    int progress = 0;

    InetAddress appIA;

    public starterAST(WifiManager wfm, TextView inProgressTextView, ProgressBar inProgressBar, InetAddress inInetAddress){
        appWFM = wfm;
        progressTextView = inProgressTextView;
        progressBar = inProgressBar;
        appIA = inInetAddress;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        progressText = "Getting WiFi State";
        publishProgress();
        if (appWFM != null && appWFM.isWifiEnabled()) {
            progressText = "Pinging station";
            progress += 10;
            publishProgress();
            try {
                if (appIA.isReachable(200)){



                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        progressTextView.setText(progressText);
        progressBar.setProgress(progress);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
