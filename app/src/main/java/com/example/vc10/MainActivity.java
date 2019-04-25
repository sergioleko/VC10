package com.example.vc10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_ERR_NO = "Error_number";

    View controlsView, starterView;
    ProgressBar progressBar;
    TextInputLayout inputIP;
    TextView progressTextView, positionText;
    Button startButton, irCorrButton, zoomInButton, zoomOutButton, zoomAutoButton, focusInButton, focusOutButton, expositionPlusButton,
            expositionMinusButton, expositionAutoButton, gainPlusButton, gainMinusButton, gainAutoButton;
    ImageView   picturePlace;
    String stationIP;
    WifiManager wfm;
    InetAddress stationAddress;
    starterAST sAST;
    Socket axsSocket,
            irVideoSocket, irOmuSocket, irLensSocket, irCamSocket,
            tvoVideoSocket, tvoOmuSocket, tvoLensSocket, tvoCamSocket,
            tviVideoSocket, tviOmuSocket, tviLensSocket, tviCamSocket;

    Thread WiFiThread;

    TCPOperations tcpo = new TCPOperations();

    Bitmap frames, bufFrame;
    ImageView frameHolder;
    Thread VideoThread;
    private Socket activeVideoSocket;
    private Socket inputSocket;


    enum activeChan {TVO, TVI, IR}
    activeChan activeCam;
    boolean stationreachable;

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

    //TextView progressTextView;

    int progress = 0;


    String progressText;

    int xs = 0, ys = 0;

    axsController axsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //switching on fullscreen, setting display always on
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        starterView = findViewById(R.id.starter);
        controlsView = findViewById(R.id.controls);
        progressBar = findViewById(R.id.progressBar);
        progressTextView = findViewById(R.id.progressText);
        startButton = findViewById(R.id.startButton);
        inputIP = findViewById(R.id.inputIP);

        wfm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        controlsView.setVisibility(View.INVISIBLE);

    }

    public void startConnection (View view) throws UnknownHostException {

        stationIP = inputIP.getEditText().getText().toString();

        progressTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        stationAddress = InetAddress.getByName(stationIP);

        new starterAST().execute();

        WiFiThread = new Thread(new Runnable() {

            @Override

            public void run() {

                while (wfm != null && wfm.isWifiEnabled()) {

                    if (stationreachable) {
                        try {
                            if (stationAddress.isReachable(1000)) {
                                stationreachable = true;

                            } else {
                                stationreachable = false;
                                startError(getApplicationContext(), "Station unreachable");

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }


            }
        });




        VideoThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (stationreachable) {
                   // Log.i("act c", String.valueOf(activeCam));
                    switch (activeCam) {
                        case IR:
                            activeVideoSocket = irVideoSocket;
                            break;
                        case TVI:
                            activeVideoSocket = tviVideoSocket;
                            break;
                        case TVO:
                            activeVideoSocket = tvoVideoSocket;
                            break;
                    }

                    final Bitmap settingFrame = getVideo(activeVideoSocket);
                    bufFrame = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bufFrame != settingFrame) {
                                setFrame(settingFrame);
                               // Log.i("vt", "started");
                                bufFrame = settingFrame;
                            }

                        }


                    });

                }
            }
        }
        );





    }

         class starterAST extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                progressText = "Getting WiFi State";
                publishProgress();
                if (wfm != null && wfm.isWifiEnabled()) {
                    progressText = "Pinging station";
                    progress += 10;
                    publishProgress();
                    try {
                        if (stationAddress.isReachable(200)){
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

                    /*tcpo.sendTCP(irCamSocket, po.makeCREQ(cameraMid));
                    IRCameradata = po.parseCrep(tcpo.recieveTCP(irCamSocket), "Cam");*/
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
                            activeCam = activeChan.TVO;

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
                starterView.setVisibility(View.INVISIBLE);
                controlsView.setVisibility(View.VISIBLE);
                frameHolder = findViewById(R.id.picPlace);
                stationreachable = true;
                positionText = findViewById(R.id.positionTextView);
                VideoThread.start();

                WiFiThread.setDaemon(true);
                WiFiThread.start();
                super.onPostExecute(aVoid);
            }


    }





    public Bitmap getVideo(Socket inputSocket) {
        if (inputSocket != null) {
            byte[] pic = new byte[0];
            try {
                pic = tcpo.recieveTCP(inputSocket);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            frames = BitmapFactory.decodeByteArray(pic, 0, pic.length);
           // Log.i("frame ", "recieved");
        }
        return frames;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void setFrame(Bitmap picture) {

        frameHolder.setImageBitmap(picture);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // pass the events to the gesture detector
                // a return value of true means the detector is handling it
                // a return value of false means the detector didn't
                // recognize the event
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    Log.d("TouchTest", "Touch down");
                    if (event.getX() < (((v.getWidth() / 2) - (v.getWidth() * 0.1))) || event.getX() > ((v.getWidth() / 2) + (v.getWidth() * 0.1))) {

                        if (event.getX() > (v.getWidth() / 2)) {
                            xs = 1;

                        } else {
                            xs = -1;

                        }
                    } else {
                        xs = 0;

                    }
                    if (event.getY() < (((v.getHeight() / 2) - (v.getHeight() * 0.1))) || event.getY() > ((v.getHeight() / 2) + (v.getHeight() * 0.1))) {

                        if (event.getY() > (v.getHeight() / 2)) {
                            ys = 1;
                        } else {
                            ys = -1;
                        }
                    } else {
                        ys = 0;
                    }

               axsTask = (axsController) new  axsController().execute();
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    Log.d("TouchTest", "Touch up");
                    axsTask.cancel(true);

                }
                return onTouchEvent(event);


                }


        };
        frameHolder.setOnTouchListener(touchListener);
    }

    public void startError(Context curContext, String error) {
        Intent errorIntent = new Intent(curContext, error_window.class);
        errorIntent.putExtra(EXTRA_MESSAGE_ERR_NO, error);
        curContext.startActivity(errorIntent);
    }


    class axsController extends AsyncTask<Void, Void, Void>{
        String text;


        @Override
        protected Void doInBackground(Void... voids) {
        while (stationreachable) {
            try {
                tcpo.sendTCP(axsSocket, po.AXSmakeMreq(AXSdata, po.AxsCrep, 2, xs, ys));
                //String inText = po.parseMrep(tcpo.recieveTCP(axsSocket));

                String[] positions = po.parseMrep(tcpo.recieveTCP(axsSocket)).split(";");
                if (positions.length > 1) {
                    String curPosX = positions[0].substring(0, positions[0].indexOf(".") + 2);
                    String curPosY = positions[1].substring(0, positions[1].indexOf(".") + 2);
                    text = "X position: " + curPosX + "\t" + "Y position: " + curPosY;

                                    }

                publishProgress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            positionText.setText(text);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            try {
                tcpo.sendTCP(axsSocket, po.AXSmakeMreq(AXSdata, po.AxsCrep, 2, 0, 0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onCancelled();
        }
    }


}
