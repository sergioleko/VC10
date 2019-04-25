package com.example.vc10;

import android.util.Log;


import com.google.protobuf.InvalidProtocolBufferException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import Linkos.RTC.Message.AEF.Aef;
import Linkos.RTC.Message.AXS.Axs;
import Linkos.RTC.Message.Camera.Camera;
import Linkos.RTC.Message.GenericOuterClass;
import Linkos.RTC.Message.Lens.Lens;

public class protobufOperations {

    Lens.CREP LensCrep;
    Aef.CREP OMUCrep;
    Camera.CREP CamCrep;
    Axs.CREP AxsCrep;

    byte[] makeCREQ(int mid) {
        GenericOuterClass.Generic.Builder gocB = GenericOuterClass.Generic.newBuilder();
        gocB.getDefaultInstanceForType();
        gocB.setMid(mid);
        GenericOuterClass.CREQ.Builder creqB = GenericOuterClass.CREQ.newBuilder();
        creqB.getDefaultInstanceForType();
        gocB.setCreq(creqB);
        return gocB.build().toByteArray();
    }

    List<Integer> parseCrep(byte[] incoming, String crepType) throws NoSuchAlgorithmException, InvalidProtocolBufferException {
        GenericOuterClass.Generic input = GenericOuterClass.Generic.parseFrom(incoming);
        if (input.hasCrep()){
            GenericOuterClass.CREP crep = input.getCrep();
            List<Integer> crepHash = new ArrayList<>();
            byte[] hash = MessageDigest.getInstance("MD5").digest(incoming);
            for (int i = 0; i < hash.length; i += 4) {
                Log.i("i: ", String.valueOf(i));
                int floatBits = hash[i] & 0xFF |
                        (hash[i + 1] & 0xFF) << 8 |
                        (hash[i + 2] & 0xFF) << 16 |
                        (hash[i + 3] & 0xFF) << 24;

                crepHash.add(floatBits);


            }
            switch (crepType){
                case "AXS":
                    AxsCrep = crep.getAxs();
                    break;
                case "OMU":
                    OMUCrep = crep.getAef();
                    break;
                case "Lens":
                    LensCrep = crep.getLens();
                    break;
                case "Cam":
                    CamCrep = crep.getCamera();
                    break;
            }
            return crepHash;
        }
        else {
            Log.e("No crep", "recieved");
            return null;
        }

    }

    public byte[] AXSmakeMreq(List<Integer> hashData, Axs.CREP axsCrep, float Kspeed, float xs, float xy) {

        GenericOuterClass.Generic.Builder gocB = GenericOuterClass.Generic.newBuilder();
        gocB.getDefaultInstanceForType();
        gocB.setMid(1398292803);
        GenericOuterClass.MREQ.Builder mreq = GenericOuterClass.MREQ.newBuilder();

        mreq.setMd5A(hashData.get(0));
        //Log.i("MD5A", String.valueOf(dataList.get(0)));
        mreq.setMd5B(hashData.get(1));
        //Log.i("MD5B", String.valueOf(dataList.get(1)));
        mreq.setMd5C(hashData.get(2));
        //Log.i("MD5C", String.valueOf(dataList.get(2)));
        mreq.setMd5D(hashData.get(3));
        //Log.i("MD5D", String.valueOf(dataList.get(3)));
        mreq.setPriority(0);


        Axs.MREQ.Builder axsMreq = Axs.MREQ.newBuilder();
        double xpseed = axsCrep.getXspeed().getMax() / Kspeed;
        double yspeed =  axsCrep.getYspeed().getMax()/ Kspeed;
        if (xs != 0){
            if (xs > 0){
                axsMreq.setXspeed(xpseed);
            }
            else {
                if (xs < 0) {
                    axsMreq.setXspeed(-xpseed);
                }}}
        else {
            axsMreq.setXspeed(0);
        }
        if (xy != 0){
            if (xy > 0){
                axsMreq.setYspeed(yspeed);
            }
            else {
                if (xy < 0){
                    axsMreq.setYspeed(-yspeed);
                }}}
        else {
            axsMreq.setYspeed(0);
        }



        mreq.setAxs(axsMreq.build());
        gocB.setMreq(mreq.build());
        return gocB.build().toByteArray();

    }


    public String parseMrep(byte[] bytes) throws InvalidProtocolBufferException {


        GenericOuterClass.Generic input = GenericOuterClass.Generic.parseFrom(bytes);
        if (input.hasMrep()) {
            GenericOuterClass.SREP mrep = input.getMrep();

            Axs.SREP axsSrep = mrep.getAxs();
            //  curXpos = 0;
            // curYpos = 0;

            double curXpos = axsSrep.getXposition();
            double curYpos = axsSrep.getYposition();

            //Log.i("Cur pos:", curXpos + "\t" + curYpos);
            //   Log.i("Status:", String.valueOf(curXpos) + ";" + String.valueOf(curYpos));
            return String.valueOf(curXpos) + ";" + String.valueOf(curYpos);
        }

        // Log.i("Status", String.valueOf(input.getMrep().getReady()) +  String.valueOf(input.getMrep().getBusy()));
        //  Log.i ("Status:", String.valueOf(mrep.getReady()) + "\t" + String.valueOf(mrep.getBusy()));

        else {
            Log.i("Status:", "No mrep");
            return "0;0";
        }
    }




}
