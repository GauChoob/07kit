package com.kit.plugins.barbarianassault;

import com.kit.core.Session;

import java.awt.*;

class WaveRoundData {
    private final BarbarianAssaultPlugin BA;
    private final long wavetimes[]=new long[20];  //[wave1start,wave1end,wave2start,wave2end,...]
    private final long wavetimes_processed[]=new long[12]; //[wave1,...,wave10,round,lobbytime]
    private long waveStartTime;     //start time of current wave
    private boolean displayRoundInfo;
    private boolean gameInProgress;

    WaveRoundData(BarbarianAssaultPlugin BA){
        this.BA=BA;
        resetForNewRound();
    }
    void resetForNewRound(){
        gameInProgress=false;
        displayRoundInfo=false;
        waveStartTime =System.nanoTime();
        for (int i=0;i<20; i++){
            wavetimes[i]= waveStartTime;
        }
    }
    void startedNewWave(int wave,long currentTime){
        waveStartTime=currentTime;
        if(wave>=0){
            wavetimes[wave * 2] = currentTime;
            if (!gameInProgress) {
                gameInProgress=true;
                for(int i=0;i<wave;i++){        //scrub earlier waves to all be 0 if you start mid-round
                    wavetimes[i*2]=currentTime;
                    wavetimes[i*2+1]=currentTime;
                }
            }
        } else {

        }
    }
    long getWaveStartTime(){
        return waveStartTime;
    }
    boolean getInProgress(){
        return gameInProgress;
    }
    boolean getDisplayRoundInfo(){
        return displayRoundInfo;
    }
    void endedWave(int endedWave,long currentTime){
        if(endedWave>=0){
            wavetimes[endedWave*2+1]=currentTime;
            if (endedWave == 9&&!BA.gameState.isInWaveLobby()) {    //fix death wave 10
                processWaveTimes();
                gameInProgress=false;
                displayRoundInfo=true;
            }
        }
    }
    void leftBA(){
        displayRoundInfo=false;
    }
    void wentUpstairs(int currentWave){
        if(gameInProgress&&currentWave>0){
            gameInProgress=false;
            long endTime=wavetimes[(currentWave-1)*2+1]; //end time of previous wave
            for(int i=currentWave;i<10;i++){        //scrub later waves to last wave end time if you end mid-round
                wavetimes[i*2]=endTime;
                wavetimes[i*2+1]=endTime;
            }
            displayRoundInfo=true;
        }
    }

    //Draws the wave timer
    void drawWaveTimer(Graphics2D g2d,long waveProgressTime){
        if(waveProgressTime%30000000000L>26999999999L) {
            g2d.setColor(Color.RED);
            g2d.setFont(g2d.getFont().deriveFont(20.0f));
        } else{
            g2d.setFont(g2d.getFont().deriveFont(14.0f));
            g2d.setColor(Color.CYAN);
        }
        g2d.drawString(String.format("%01d.%d",waveProgressTime/1000000000L,(waveProgressTime%1000000000L)/100000000L), Session.get().client().getViewportWidth()/2,Session.get().client().getViewportHeight()/2);
    }
    void drawRoundTime(Graphics2D g2d, long currentTime){
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.CYAN);
        long roundTime = currentTime - wavetimes[0];
        g2d.drawString(String.format("Round: %02d:%02d.%d",roundTime/60000000000L,(roundTime%60000000000L)/1000000000L,(roundTime%1000000000L)/100000000L),10,70-16);
    }
    void drawRoundInfo(Graphics2D g2d){
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.CYAN);
        g2d.drawString(String.format("Round: %02d:%02d.%d",wavetimes_processed[10]/60000000000L,(wavetimes_processed[10]%60000000000L)/1000000000L,(wavetimes_processed[10]%1000000000L)/100000000L),10,70+12);
        for(int i=0;i<10;i++){
            g2d.drawString("Wave "+(i+1)+": "+wavetimes_processed[i]/100000000L/10.0, 10, 70+12*2+12*i);
        }
        g2d.drawString("Lobby time: "+wavetimes_processed[11]/100000000L/10.0, 10, 70+12*2+12*10);
    }

    //Displays debug info
    void Debug(Graphics2D g2d){
        processWaveTimes();
        drawRoundInfo(g2d);
    }

    private void processWaveTimes(){
        long totalWaveTime=0;
        for(int i=0;i<10;i++){
            long thiswavetime=(wavetimes[2*i+1]-wavetimes[2*i]);
            wavetimes_processed[i]=thiswavetime;
            totalWaveTime+=thiswavetime;
        }
        wavetimes_processed[10]=wavetimes[2*9+1]-wavetimes[0];
        wavetimes_processed[11]=wavetimes_processed[10]-totalWaveTime;
    }
}
