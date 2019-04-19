package com.example.vc10;

import android.net.wifi.WifiManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    View controlsView, starterView;
    ProgressBar progressBar;
    TextInputLayout inputIP;
    TextView progressText;
    Button startButton, irCorrButton, zoomInButton, zoomOutButton, zoomAutoButton, focusInButton, focusOutButton, expositionPlusButton,
            expositionMinusButton, expositionAutoButton, gainPlusButton, gainMinusButton, gainAutoButton;
    ImageView   picturePlace;
    String stationIP;
    WifiManager wfm;
    InetAddress stationAddress;
    starterAST sAST;


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

        progressText = findViewById(R.id.progressText);

        startButton = findViewById(R.id.startButton);

        inputIP = findViewById(R.id.inputIP);

        wfm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

    }

    public void startConnection (View view) throws UnknownHostException {

        stationIP = inputIP.getEditText().getText().toString();

        progressText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        stationAddress = InetAddress.getByName(stationIP);

        sAST = (starterAST) new starterAST(wfm, progressText, progressBar, stationAddress).execute();
    }

}
