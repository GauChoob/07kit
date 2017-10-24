package com.kit.plugins.barbarianassault;

import com.kit.api.wrappers.Widget;
import com.kit.core.Session;
import com.kit.game.engine.media.IWidget;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;

public class Points {
    //M=Message (static), N=Number, P=Points
    //DATA=changeable message
    //Q=Queen-wave only
    private final static int POINTS_WIDGET=497;
    private final static int BLANK_00=0;
    private final static int BLANK_01=1;
    private final static int BLANK_02=2;

    //Advanced breakdown screen
    private final static int M_WAVE_COMPLETE=3;
    private final static int M_REWARD=4;
    private final static int M_RUNNERS_PAST=5;
    private final static int M_RANGERS_KILLED=6;
    private final static int M_FIGHTERS_KILLED=7;
    private final static int M_HEALERS_KILLED=8;
    private final static int M_RUNNERS_KILLED=9;
    private final static int M_HP_REPLENISHED=10;
    private final static int M_WRONG_POISON_PACKS=11;
    private final static int M_EGGS_COLLECTED=12;
    private final static int M_FAILED_ATTACKS=13;

    private final static int N_RUNNERS_PAST=14; //numbers
    private final static int N_RANGERS_KILLED=15;
    private final static int N_FIGHTERS_KILLED=16;
    private final static int N_HEALERS_KILLED=17;
    private final static int N_RUNNERS_KILLED=18;
    private final static int N_HP_REPLENISHED=19;
    private final static int N_WRONG_POISON_PACKS=20;
    private final static int N_EGGS_COLLECTED=21;
    private final static int N_FAILED_ATTACKS=22;

    private final static int M_TOTAL=23;

    private final static int P_RUNNERS_PAST=24; //points gained/lost
    private final static int P_RANGERS_KILLED=25;
    private final static int P_FIGHTERS_KILLED=26;
    private final static int P_HEALERS_KILLED=27;
    private final static int P_RUNNERS_KILLED=28;
    private final static int P_HP_REPLENISHED=29;
    private final static int P_WRONG_POISON_PACKS=30;
    private final static int P_EGGS_COLLECTED=31;
    private final static int P_FAILED_ATTACKS=32;
    private final static int P_TOTAL_WAVE_TEAM_WITHOUT_BONUS=33;

    private final static int M_NUM_SYMBOL_FOR_N_COLUMN=34;
    private final static int M_TEAM_HONOR_POINTS_FOR_P_COLUMN=35;
    private final static int M_ROLE=36;

    private final static int DATA_ROLE_NAME=37; //"Attacker",

    private final static int P_BONUS_RUNNERS_PAST=38; //Only has value if player is in the right role where the points are relevant
    private final static int P_BONUS_HEALERS_KILLED=39;
    private final static int P_BONUS_RUNNERS_KILLED=40;
    private final static int P_BONUS_FIGHTERS_KILLED=41;
    private final static int P_BONUS_RANGERS_KILLED=42;
    private final static int P_BONUS_HP_REPLENISHED=43;
    private final static int P_BONUS_EGGS_COLLECTED=44;
    private final static int P_BONUS_WRONG_POISON_PACKS=45;
    private final static int P_BONUS_FAILED_ATTACKS=46;
    private final static int P_BONUS_TOTAL_WAVE_INDIVIDUAL=47;

    private final static int M_HONOUR_POINTS_REWARD_ADV=48;

    private final static int P_TOTAL_WAVE_RECEIVED_ADV=49;

    private final static int M_TOTAL_HONOUR_POINTS_IN_ROLE_ADV=50;

    private final static int P_PLAYERS_TOTAL_POINTS_ADV=51; //i.e. blackboard number

    private final static int M_INDIVIDUAL_HONOUR_POINTS_FOR_P_BONUS_COLUMN=52;

    private final static int BLANK_53=53;
    private final static int BLANK_54=54;

    private final static int Q_M_THE_QUEEN_IS_DEAD=55;
    private final static int Q_M_REWARD=56; //?queen
    private final static int Q_M_POINTS_OUTPUT=57; //?queen //80 Healer points<br>5 Defender points<br>5 Collector points<br>5 Attacker points<br>The option to spend your points for<br>special armour and gambling options.
    private final static int Q_DATA_ROLE_FOR_QUEEN_POINTS=58;  //?? queen

    private final static int M_SIMPLE_BREAKDOWN_BUTTON=59;

    //Simple breakdown screen

    private final static int M_HONOUR_POINTS_REWARD_SIM=60;

    private final static int P_TOTAL_WAVE_RECEIVED_SIM=61;
    private final static int P_PLAYERS_TOTAL_POINTS_SIM=62;

    private final static int BLANK_63=63;

    private final static int M_ADVANCED_BREAKDOWN_BUTTON=64;
    private final static int M_TOTAL_HONOUR_POINTS_IN_ROLE_SIM=65;
    private final static int M_CLOSE_BUTTON=66;

    private final static int M_LEVEL_UP_BUTTON=67;
    private final static int M_LEVEL_UP_TEXT=68;
    private final static int BLANK_69=69;

    private final static int M_LEVEL_UP_BUTTON_2=70; //I'm assuming this is for  after you press level up but unknown
    private final static int M_LEVEL_UP_TEXT_2=71;
    private final static int BLANK_72=72;

    //------------------------------------------------------------------------------------------------------------

    private final static int[] basePoints={10,14,18,19,22,25,26,29,35,80};
    private final static int[][] bonusPoints;
    private final static int[][] basePointsFromRemainingWaves;
    private final static int[][] basePointsFromRemainingWavesKandHard;
    static{
        bonusPoints=new int[4][10];
        bonusPoints[Cons.A]=new int[]{6,7,9,10,10,11,12,13,14,0};
        bonusPoints[Cons.C]=new int[]{0,0,0,0,0,0,0,0,0,0};
        bonusPoints[Cons.D]=new int[]{3,5,7,7,9,10,10,12,16,0};
        bonusPoints[Cons.H]=new int[]{1,2,2,2,3,4,4,4,5,0};
        basePointsFromRemainingWaves=new int[4][10];
        basePointsFromRemainingWaves[Cons.A]=new int[]{370,354,333,306,277,245,209,171,129,80};
        basePointsFromRemainingWaves[Cons.C]=new int[]{278,268,254,236,217,195,170,144,115,80};
        basePointsFromRemainingWaves[Cons.D]=new int[]{357,344,325,300,274,243,208,172,131,80};
        basePointsFromRemainingWaves[Cons.H]=new int[]{305,294,278,258,237,212,183,153,120,80};
        basePointsFromRemainingWavesKandHard=new int[4][10];
        basePointsFromRemainingWavesKandHard[Cons.A]=new int[]{402,385,362,333,302,267,228,187,141,88};
        basePointsFromRemainingWavesKandHard[Cons.C]=new int[]{301,290,275,256,236,212,185,157,126,88};
        basePointsFromRemainingWavesKandHard[Cons.D]=new int[]{389,375,355,328,300,266,228,189,144,88};
        basePointsFromRemainingWavesKandHard[Cons.H]=new int[]{333,321,304,282,259,232,201,168,132,88};
    }
    private final static int Egg2PointsRatio=435;

    //------------------------------------------------------------------------------------------------------------
    private final static int INFO_RUNNERS_PAST=0; //numbers for nInfo and pInfo array
    private final static int INFO_HP_REPLENISHED=1;
    private final static int INFO_WRONG_POISON_PACKS=2;
    private final static int INFO_EGGS_COLLECTED=3;
    private final static int INFO_FAILED_ATTACKS=4;

    //------------------------------------------------------------------------------------------------------------

    private final BarbarianAssaultPlugin BA;

    private int[] nInfo= new int[5];
    private int[] pInfo= new int[5];
    private int[] nInfoTotal= new int[5];
    private int[] pInfoTotal= new int[5];
    private int waveBasePoints;
    private int roleTotalPoints;

    private int role;
    private int[] currentPoints=new int[4];
    private int[] neededPoints=new int[4];

    private boolean needWaveData;
    private boolean showEggs;
    private boolean kandarinHard;

    private String pointsScrapedFileName;

    Points(BarbarianAssaultPlugin BA){
        this.BA=BA;
        roleTotalPoints=0;
        role=-1;
        for(int i=0;i<4;i++){
            neededPoints[i]=0;
            currentPoints[i]=0;
        }
        showEggs=false;
        kandarinHard=false;
        pointsScrapedFileName=Long.toString(System.nanoTime())+".txt";
        resetForNewRound();
    }

    void resetForNewRound(){
        for(int i=0;i<5;i++){
            nInfo[i]=0;
            pInfo[i]=0;
            nInfoTotal[i]=0;
            pInfoTotal[i]=0;
        }
        waveBasePoints=0;
    }

    void endedWave(){
        if(BA.gameState.getEndedWave() == 9&&!BA.gameState.isInWaveLobby()){
            handleWave10End();
        } else {
            needWaveData=true;
        }
    }
    void startedNewWave(){
        if(needWaveData){
            needWaveData=false;
            if(role!=-1){
                BA.textCommands.setMessage("Unable to get points from ended wave",2000000000L);
            }
        }
    }

    private void handleWave10End(){
        if (role != -1) {
            if(kandarinHard){
                for(int i=0;i<4;i++){
                    if(i==role){
                        currentPoints[i]+=88;
                    } else {
                        currentPoints[i]+=5;
                    }
                }
            } else {
                for(int i=0;i<4;i++){
                    if(i==role){
                        currentPoints[i]+=80;
                    } else {
                        currentPoints[i]+=5;
                    }
                }
            }
        }
    }
    private void givePoints(){
        if(role!=-1){
            //System.out.println("givePoints");
            int addedPoints=Math.max(0,basePoints[BA.gameState.getWave()-1]+pInfo[INFO_RUNNERS_PAST]+pInfo[INFO_HP_REPLENISHED]+
                    pInfo[INFO_WRONG_POISON_PACKS]+pInfo[INFO_EGGS_COLLECTED]+pInfo[INFO_FAILED_ATTACKS]);
            addedPoints+=bonusPoints[role][BA.gameState.getWave()-1];
            switch(role) {
                case Cons.A:
                    addedPoints += pInfo[INFO_FAILED_ATTACKS];
                    //System.out.println("A:"+pInfo[INFO_FAILED_ATTACKS]);
                    break;
                case Cons.C:
                    addedPoints += pInfo[INFO_EGGS_COLLECTED];
                    //System.out.println("C:"+pInfo[INFO_EGGS_COLLECTED]);
                    break;
                case Cons.D:
                    addedPoints += pInfo[INFO_RUNNERS_PAST];
                    //System.out.println("D:"+pInfo[INFO_RUNNERS_PAST]);
                    break;
                case Cons.H:
                    addedPoints += pInfo[INFO_HP_REPLENISHED] + pInfo[INFO_WRONG_POISON_PACKS];
                    //System.out.println("H:"+ pInfo[INFO_HP_REPLENISHED] + "/"+pInfo[INFO_WRONG_POISON_PACKS]);
                    break;
            }
            //System.out.println(""+waveBasePoints+"+"+(addedPoints-waveBasePoints)+"="+(addedPoints*11/10));
            addedPoints=Math.max(0,addedPoints);
            currentPoints[role]+=kandarinHard?addedPoints*11/10:addedPoints;
        }
    }

    boolean getWaveData(){
        if(needWaveData){
            if (role != -1) {
                IWidget[] parent = Session.get().client().getWidgets()[POINTS_WIDGET];
                if(parent==null){
                    return false;
                }
                scrapePrintData(parent);  //TODO temp remove one day
                nInfo[INFO_RUNNERS_PAST]=Integer.parseInt(new Widget(Session.get(),parent[N_RUNNERS_PAST],POINTS_WIDGET,N_RUNNERS_PAST).getText());
                nInfo[INFO_HP_REPLENISHED]=Integer.parseInt(new Widget(Session.get(),parent[N_HP_REPLENISHED],POINTS_WIDGET,N_HP_REPLENISHED).getText());
                nInfo[INFO_WRONG_POISON_PACKS]=Integer.parseInt(new Widget(Session.get(),parent[N_WRONG_POISON_PACKS],POINTS_WIDGET,N_WRONG_POISON_PACKS).getText());
                nInfo[INFO_EGGS_COLLECTED]=Integer.parseInt(new Widget(Session.get(),parent[N_EGGS_COLLECTED],POINTS_WIDGET,N_EGGS_COLLECTED).getText());
                nInfo[INFO_FAILED_ATTACKS]=Integer.parseInt(new Widget(Session.get(),parent[N_FAILED_ATTACKS],POINTS_WIDGET,N_FAILED_ATTACKS).getText());
                pInfo[INFO_RUNNERS_PAST]=Integer.parseInt(new Widget(Session.get(),parent[P_RUNNERS_PAST],POINTS_WIDGET,P_RUNNERS_PAST).getText());
                pInfo[INFO_HP_REPLENISHED]=Integer.parseInt(new Widget(Session.get(),parent[P_HP_REPLENISHED],POINTS_WIDGET,P_HP_REPLENISHED).getText());
                pInfo[INFO_WRONG_POISON_PACKS]=Integer.parseInt(new Widget(Session.get(),parent[P_WRONG_POISON_PACKS],POINTS_WIDGET,P_WRONG_POISON_PACKS).getText());
                pInfo[INFO_EGGS_COLLECTED]=Integer.parseInt(new Widget(Session.get(),parent[P_EGGS_COLLECTED],POINTS_WIDGET,P_EGGS_COLLECTED).getText());
                pInfo[INFO_FAILED_ATTACKS]=Integer.parseInt(new Widget(Session.get(),parent[P_FAILED_ATTACKS],POINTS_WIDGET,P_FAILED_ATTACKS).getText());
                if(IntStream.of(nInfo).sum()+IntStream.of(pInfo).sum()==0){     //It's happened that all the scraped data is 0
                    return false;
                }
                needWaveData=false;
                for(int i=0;i<5;i++){
                    nInfoTotal[i]+=nInfo[i];
                    pInfoTotal[i]+=pInfo[i];
                }
                waveBasePoints=Integer.parseInt(new Widget(Session.get(),parent[P_TOTAL_WAVE_TEAM_WITHOUT_BONUS],POINTS_WIDGET,P_TOTAL_WAVE_TEAM_WITHOUT_BONUS).getText());
                roleTotalPoints=Integer.parseInt(new Widget(Session.get(),parent[P_PLAYERS_TOTAL_POINTS_ADV],POINTS_WIDGET,P_PLAYERS_TOTAL_POINTS_ADV).getText());
                //neededPoints[role]-=wavePoints(role)

                givePoints();
                return true;
            } else {
                IWidget[] parent = Session.get().client().getWidgets()[POINTS_WIDGET];
                if(parent==null){
                    return false;
                }
                //scrapePrintData(parent);  //TODO temp remove one day
                needWaveData=false;
                return false;
            }
        }

        return false;
    }

    void scrapePrintData(IWidget[] parent){
        BufferedWriter bw=null;
        try {
            bw = new BufferedWriter(new FileWriter(pointsScrapedFileName, true));
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";RUNNERS_PAST;" + new Widget(Session.get(), parent[N_RUNNERS_PAST], POINTS_WIDGET, N_RUNNERS_PAST).getText() + ";" + new Widget(Session.get(), parent[P_RUNNERS_PAST], POINTS_WIDGET, P_RUNNERS_PAST).getText());
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";RANGERS_KILLED;" + new Widget(Session.get(), parent[N_RANGERS_KILLED], POINTS_WIDGET, N_RANGERS_KILLED).getText() + ";" + new Widget(Session.get(), parent[P_RANGERS_KILLED], POINTS_WIDGET, P_RANGERS_KILLED).getText());
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";FIGHTERS_KILLED;" + new Widget(Session.get(), parent[N_FIGHTERS_KILLED], POINTS_WIDGET, N_FIGHTERS_KILLED).getText() + ";" + new Widget(Session.get(), parent[P_FIGHTERS_KILLED], POINTS_WIDGET, P_FIGHTERS_KILLED).getText());
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";HEALERS_KILLED;" + new Widget(Session.get(), parent[N_HEALERS_KILLED], POINTS_WIDGET, N_HEALERS_KILLED).getText() + ";" + new Widget(Session.get(), parent[P_HEALERS_KILLED], POINTS_WIDGET, P_HEALERS_KILLED).getText());
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";RUNNERS_KILLED;" + new Widget(Session.get(), parent[N_RUNNERS_KILLED], POINTS_WIDGET, N_RUNNERS_KILLED).getText() + ";" + new Widget(Session.get(), parent[P_RUNNERS_KILLED], POINTS_WIDGET, P_RUNNERS_KILLED).getText());
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";HP_REPLENISHED;" + new Widget(Session.get(), parent[N_HP_REPLENISHED], POINTS_WIDGET, N_HP_REPLENISHED).getText() + ";" + new Widget(Session.get(), parent[P_HP_REPLENISHED], POINTS_WIDGET, P_HP_REPLENISHED).getText());
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";WRONG_POISON_PACKS;" + new Widget(Session.get(), parent[N_WRONG_POISON_PACKS], POINTS_WIDGET, N_WRONG_POISON_PACKS).getText() + ";" + new Widget(Session.get(), parent[P_WRONG_POISON_PACKS], POINTS_WIDGET, P_WRONG_POISON_PACKS).getText());
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";EGGS_COLLECTED;" + new Widget(Session.get(), parent[N_EGGS_COLLECTED], POINTS_WIDGET, N_EGGS_COLLECTED).getText() + ";" + new Widget(Session.get(), parent[P_EGGS_COLLECTED], POINTS_WIDGET, P_EGGS_COLLECTED).getText());
            bw.newLine();
            bw.write("W" + (BA.gameState.getWave()) + ";FAILED_ATTACKS;" + new Widget(Session.get(), parent[N_FAILED_ATTACKS], POINTS_WIDGET, N_FAILED_ATTACKS).getText() + ";" + new Widget(Session.get(), parent[P_FAILED_ATTACKS], POINTS_WIDGET, P_FAILED_ATTACKS).getText());
            bw.flush();
        }catch (IOException e){

        } finally {
            if(bw!=null) try{
                bw.close();
            } catch (IOException e2){

            }
        }
    }

    boolean needWaveData(){
        return needWaveData;
    }
    void setRole(int role){ //Role that is getting points
        this.role=role;
    }
    void setNeededPoint(int role,int points){
        neededPoints[role]=points;
    }
    void setNeededPoints(int pointA, int pointC, int pointD, int pointH){
        neededPoints[Cons.A]=pointA;
        neededPoints[Cons.C]=pointC;
        neededPoints[Cons.D]=pointD;
        neededPoints[Cons.H]=pointH;
    }
    void setCurrentPoint(int role,int points){
        currentPoints[role]=points;
    }
    void setCurrentPoints(int pointA, int pointC, int pointD, int pointH){
        currentPoints[Cons.A]=pointA;
        currentPoints[Cons.C]=pointC;
        currentPoints[Cons.D]=pointD;
        currentPoints[Cons.H]=pointH;
    }
    void kandarinHardToggle(){
        kandarinHard=!kandarinHard;
    }
    boolean isKandarinHard(){
        return kandarinHard;
    }
    void showEggsToggle(){
        showEggs=!showEggs;
    }
    void showEggsOn(){
        showEggs=true;
    }
    void showEggsOff(){
        showEggs=false;
    }
    boolean showEggs(){
        return showEggs;
    }
    void drawPointsToGo(Graphics2D g2d){
        if(role==-1){ return;}
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.CYAN);
        int x=Session.get().client().getViewportWidth()-100;
        g2d.drawString(Cons.Role(role)+(kandarinHard?"*":"")+": "+Integer.toString(currentPoints[role]),x,12);
        int currentWave=BA.gameState.getWave();
        if(showEggs && currentWave!=-1 && currentWave!=9){
            g2d.drawString("Eggs left: "+neededEggs(currentWave),x,12*2);
        }
    }
    private int neededEggs(int currentWave){
        int basePointsFromRemaining=kandarinHard?basePointsFromRemainingWavesKandHard[role][currentWave]:basePointsFromRemainingWaves[role][currentWave];
        int neededEggs=(neededPoints[role]-currentPoints[role]-basePointsFromRemaining)*Egg2PointsRatio+Egg2PointsRatio*(9-currentWave)/2;
        if(role==Cons.C){
            neededEggs=neededEggs/2;
        }
        return neededEggs/100;
    }

    void drawRoundInfo(Graphics2D g2d){
        if(role==-1){ return;}
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.CYAN);
        int x=Session.get().client().getViewportWidth()-100;
        g2d.drawString(String.format("Raa: %d|%d",nInfoTotal[INFO_RUNNERS_PAST],pInfoTotal[INFO_RUNNERS_PAST]),x,0+12*5);
        g2d.drawString(String.format("Vial: %d|%d",nInfoTotal[INFO_HP_REPLENISHED],pInfoTotal[INFO_HP_REPLENISHED]),x,0+12*6);
        g2d.drawString(String.format("Pois: %d|%d",nInfoTotal[INFO_WRONG_POISON_PACKS],pInfoTotal[INFO_WRONG_POISON_PACKS]),x,0+12*7);
        g2d.drawString(String.format("Eggs: %d|%d",nInfoTotal[INFO_EGGS_COLLECTED],pInfoTotal[INFO_EGGS_COLLECTED]),x,0+12*8);
        g2d.drawString(String.format("Atk: %d|%d",nInfoTotal[INFO_FAILED_ATTACKS],pInfoTotal[INFO_FAILED_ATTACKS]),x,0+12*9);
    }

    //TODO
    //Kandarin hard mechanics
    //Number of eggs to get 375 for leech (need to do collector module then)
    //display round info=points


}
