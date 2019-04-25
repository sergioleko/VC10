/*
package com.example.vc10;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class starterAST extends AsyncTask<Void, Void, Void> {

    TCPOperations tcpo = new TCPOperations();

    WifiManager appWFM;

    TextView progressTextView;
    String progressText;
    ProgressBar progressBar;
    int progress = 0;

    InetAddress appIA;
    String stationIP;

    Socket axsSocket,
            irVideoSocket, irOmuSocket, irLensSocket, irCamSocket,
            tvoVideoSocket, tvoOmuSocket, tvoLensSocket, tvoCamSocket,
            tviVideoSocket, tviOmuSocket, tviLensSocket, tviCamSocket;

    String AXSPort = "55555";
    String IRVideoPort = "30001";
    String IROMUPort = "30101";
    String IRLensPort = "30201";
    String IRCamPort = "30301";
    String TVOVideoPort = "30000";
    String TVOCamPort = "30300";
    String TVOLensPort = "30200";
    String TVOOMUPort = "30100";
    String TVIVideoPort = "30002";
    String TVICamPort = "30302";
    String TVILensPort = "30202";
    String TVIOMUPort = "30102";

    int videoMid = 1398292803,
            aefMid = 1178943811,
            lensMid = 1313164355,
            cameraMid = 1296122691,
            axsMid = 1398292803;


    boolean axsExists, irExists, tvoExists, tviExists;

    protobufOperations po = new protobufOperations();

    List<Integer> AXSdata = new ArrayList<>();
    List<Integer> IRCameradata = new ArrayList<>();
    List<Integer> IROMUdata = new ArrayList<>();
    List<Integer> IRLensdata = new ArrayList<>();
    List<Integer> TVICameradata = new ArrayList<>();
    List<Integer> TVIOMUdata = new ArrayList<>();
    List<Integer> TVILensdata = new ArrayList<>();
    List<Integer> TVOCameradata = new ArrayList<>();
    List<Integer> TVOOMUdata = new ArrayList<>();
    List<Integer> TVOLensdata = new ArrayList<>();

    View controls, starter;
    starterStruct starterOut = new starterStruct();

    public starterAST(WifiManager wfm, TextView inProgressTextView, ProgressBar inProgressBar, InetAddress inInetAddress, String inIP, View inCon, View inStart){
        appWFM = wfm;
        progressTextView = inProgressTextView;
        progressBar = inProgressBar;
        appIA = inInetAddress;
        stationIP = inIP;
        controls = inCon;
        starter = inStart;
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
                    progress += 10;
                    progressText = "Starting Sockets";
                    publishProgress();
                    axsSocket = tcpo.getSocket(stationIP, AXSPort);

                    irVideoSocket = tcpo.getSocket(stationIP, IRVideoPort);
                    irOmuSocket = tcpo.getSocket(stationIP, IROMUPort);
                    irLensSocket = tcpo.getSocket(stationIP, IRLensPort);
                    irCamSocket = tcpo.getSocket(stationIP, IRCamPort);

                    tvoVideoSocket = tcpo.getSocket(stationIP, TVOVideoPort);
                    tvoOmuSocket = tcpo.getSocket(stationIP, TVOOMUPort);
                    tvoLensSocket = tcpo.getSocket(stationIP, TVOLensPort);
                    tvoCamSocket = tcpo.getSocket(stationIP, TVOCamPort);

                    tviVideoSocket = tcpo.getSocket(stationIP, TVIVideoPort);
                    tviOmuSocket = tcpo.getSocket(stationIP, TVIOMUPort);
                    tviLensSocket = tcpo.getSocket(stationIP, TVILensPort);
                    tviCamSocket = tcpo.getSocket(stationIP, TVICamPort);


                }
                else {
                    //stop sequence here!
                }
                progress += 10;
                progressText = "Reading AXS Configuration";
                publishProgress();

                if (axsSocket == null){
                    axsExists = false;
                }
                else {
                    axsExists = true;
                    tcpo.sendTCP(axsSocket, po.makeCREQ(axsMid));
                    AXSdata = po.parseCrep(tcpo.recieveTCP(axsSocket), "AXS");
                }
                progress += 10;
                progressText = "Reading IR Configuration";
                publishProgress();
                if (irCamSocket == null){
                    irExists = false;
                }
                else {
                    irExists = true;

                    */
/*tcpo.sendTCP(irCamSocket, po.makeCREQ(cameraMid));
                    IRCameradata = po.parseCrep(tcpo.recieveTCP(irCamSocket), "Cam");*//*

                    Log.i("here", "1");
                    tcpo.sendTCP(irOmuSocket, po.makeCREQ(aefMid));
                    IROMUdata = po.parseCrep(tcpo.recieveTCP(irOmuSocket), "OMU");
                    Log.i("here", "2");
                    tcpo.sendTCP(irLensSocket, po.makeCREQ(lensMid));
                    IRLensdata = po.parseCrep(tcpo.recieveTCP(irLensSocket), "Lens");
                    Log.i("here", "3");
                }
                progress += 10;
                progressText = "Reading TVO Configuration";
                publishProgress();
                if (tvoCamSocket == null){
                    tvoExists = false;
                }
                else {
                    tvoExists = true;

                    tcpo.sendTCP(tvoCamSocket, po.makeCREQ(cameraMid));
                    TVOCameradata = po.parseCrep(tcpo.recieveTCP(tvoCamSocket), "Cam");

                    tcpo.sendTCP(tvoOmuSocket, po.makeCREQ(aefMid));
                    TVOOMUdata = po.parseCrep(tcpo.recieveTCP(tvoOmuSocket), "OMU");

                    tcpo.sendTCP(tvoLensSocket, po.makeCREQ(lensMid));
                    TVOLensdata = po.parseCrep(tcpo.recieveTCP(tvoLensSocket), "Lens");
                }
                progress += 10;
                progressText = "Reading TVI Configuration";
                publishProgress();
                if (tviCamSocket == null){
                    tviExists = false;
                }
                else {
                    tviExists = true;

                    tcpo.sendTCP(tviCamSocket, po.makeCREQ(cameraMid));
                    TVICameradata = po.parseCrep(tcpo.recieveTCP(tviCamSocket), "Cam");

                    tcpo.sendTCP(tviOmuSocket, po.makeCREQ(aefMid));
                    TVIOMUdata = po.parseCrep(tcpo.recieveTCP(tviOmuSocket), "OMU");

                    tcpo.sendTCP(tviLensSocket, po.makeCREQ(lensMid));
                    TVILensdata = po.parseCrep(tcpo.recieveTCP(tviLensSocket), "Lens");
                }

                progress = 100;
                progressText = "Building UI";
                publishProgress();

            } catch (IOException | NoSuchAlgorithmException e) {
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
        starter.setVisibility(View.INVISIBLE);
        controls.setVisibility(View.VISIBLE);

        super.onPostExecute(starterStruct);
    }
}
*/
