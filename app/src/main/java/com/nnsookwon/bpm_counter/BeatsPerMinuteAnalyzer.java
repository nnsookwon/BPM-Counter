package com.nnsookwon.bpm_counter;

import java.util.ArrayList;

/**
 * Created by nnsoo on 9/14/2016.
 */
public class BeatsPerMinuteAnalyzer {

    private ArrayList<Long> times; //in ms
    private double bpm;

    public BeatsPerMinuteAnalyzer(){
        times = new ArrayList<Long>();
        bpm = 60;
    }

    public void setBPM(double nBPM){
        bpm = nBPM;
    }

    public double getBPM(){
        return bpm;
    }

    public ArrayList<Long> getTimes(){
        return times;
    }

    public void recordTime(){
        times.add(System.currentTimeMillis());
    }

    public void analyze(){
        long difference;
        double min;
        double beatsPerMin = 0;
        int beats = 0;

        for (int i = times.size()-1; i > times.size()/4; i--){ //only consider the last three-fourths
            difference = times.get(i) - times.get(i-1);
            min = difference / 6E4;
            beatsPerMin += 1/min;
            beats++;
        }

        bpm = beatsPerMin/beats; //average
    }

    public void reset(){
        times.removeAll(times);
    }


}
