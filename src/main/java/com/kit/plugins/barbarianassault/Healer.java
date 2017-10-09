package com.kit.plugins.barbarianassault;


import com.kit.api.wrappers.Npc;

import java.awt.*;

class Healer {
    private static final int MaxHealersOut[]={2,3,2,3,4,4,4,5,6,4}; //Healers after are reserves
    private static final int ReserveCount[]={0,0,1,1,1,2,3,2,2,3}; //Healers after are reserves
    private static final Color HealerMainColors[]={new Color(255,127,39),//oj
            new Color(63,72,204),//blue
            new Color(155,148,0), //dark yellow
            new Color(0,100,17), //forest green
            new Color(0,0,0), //black
            new Color(120,120,120)};//grey
    private static final Color HealerReserveColors[]={
            new Color(128,0,128), //purple
            new Color(255,0,128),//dark pink
            new Color(255,128,169)};//light pink

    Npc npc;
    int uniqueid;
    int hpfraction;
    int predictedhpfraction;
    long hpupdatetime;
    int poison;
    int currentpoisontick;
    long lastpoisontime;
    long firstpoisontime; //the time for 3s tick cycles
    long spawntime;
    boolean loaded;
    boolean visible;
    int deathcounter;
    int modelhash;
    boolean confirmedDead;

    private static final int[] Wave2HP = {27,32,37,43,49,55,60,67,76,60};


    Healer(Npc npc, int uniqueid, long spawnTime){
        this.npc=npc;
        this.uniqueid=uniqueid;
        hpfraction=-1;
        predictedhpfraction=-1;
        hpupdatetime=-1;
        poison=0;
        lastpoisontime =-1;
        this.firstpoisontime=spawnTime;
        this.spawntime=spawnTime;
        loaded=true;
        deathcounter=-1;
        modelhash=-1;
        visible=false;   //safer to assume false on first tick
        currentpoisontick=-5;
        confirmedDead=false;
    }

    //Based on an hp number, gets the hp bar fraction on 30
    int getHPFraction(int wave,int hp){
        if(hp==-1){
            return 30;
        }
        if(hp==0){
            return 0;
        }
        return hp*29/getMaxHP(wave)+1;
        //Old equation: return Math.max(hp*30/getMaxHP(wave),1); //always at least 1 hp slice unless hp=0
    }
    //Based on the last known hp bar value, returns the maximum possible hp of the healer at that time https://www.reddit.com/r/2007scape/comments/50q80x/the_hp_bars_still_arent_fixed/
    int getMaxCurHP(int wave){
        if(hpfraction==0){
            return 0;
        }
        if (hpfraction == -1) {
            return getMaxHP(wave);
        }
        if (hpfraction == 30) {
            return getMaxHP(wave);
        }
        //double hp=(hpfraction+1)*Wave2HP[wave]/30.0d-0.0001; Old equation
        double hp=(hpfraction+1-1)*Wave2HP[wave]/29.0d-0.0001;
        return (int) hp;
    }
    //Based on the last known hp bar value, returns the minimum possible hp of the healer at that time
    int getMinCurHP(int wave){
        if(hpfraction==0){
            return 0;
        }
        if (hpfraction == -1) {
            return getMaxHP(wave);
        }
        if (hpfraction == 1) {
            return 1;
        }
       // double hp=(hpfraction)*getMaxHP(wave)/30.0d-0.0001+1; Old equation
        double hp=(hpfraction-1)*getMaxHP(wave)/29.0d-0.0001+1;
        return (int) hp;
    }
    //Returns the total hp of a healer for a specific wave
    static int getMaxHP(int wave){
        return Wave2HP[wave];
    }
    //Calculates the next poison tick and returns result
    int updateCurrentTimePoisonTick(long waveProgressTime){
        //tb=ticks between
        if (poison == 0) {
            currentpoisontick=0;
            return currentpoisontick;
        }
        int tb_firstPoison_lastPoison=(int) ((this.lastpoisontime -this.firstpoisontime)/(3000000000L));
        int tb_firstPoison_waveProgressTime=(int) ((waveProgressTime-this.firstpoisontime)/3000000000L);
        currentpoisontick=Math.max(20-(tb_firstPoison_waveProgressTime-tb_firstPoison_lastPoison),0);
        return currentpoisontick;
    }
    int getNextPoisonTick(long currentTime){
        return currentpoisontick;
    }
    //Returns a String 1-6 or R1-R3 for reserves
    static String getHealerNumber(int healeri, int wave){
        if(wave<0){
            wave=8; //Error no wave //TODO Remove this eventually
        }
        if(healeri>=MaxHealersOut[wave]){
            return "R"+(healeri-MaxHealersOut[wave]+1);
        } else {
            return Integer.toString(healeri+1);
        }
    }
    static Color getHealerColor(int healeri, int wave){
        if (wave < 0) {
            wave=8; //Error no wave TODO remove
        }
        if(healeri>=MaxHealersOut[wave]){
            return HealerReserveColors[healeri-MaxHealersOut[wave]];
        }
        return HealerMainColors[healeri];
    }
    static boolean isReserve(int healeri, int wave){
        return (healeri>=MaxHealersOut[wave]);
    }

    int getDeathTime(int wave, long waveProgressTime){
        //tb=ticks between
        int hp=this.getMaxCurHP(wave);
        int tb_firstPoison_lastPoison=(int) ((this.lastpoisontime-this.firstpoisontime)/(3000000000L));
        int tb_firstPoison_hpUpdate=(int) ((this.hpupdatetime-this.firstpoisontime)/3000000000L);
        int tb_firstPoison_waveProgressTime=(int) ((waveProgressTime-this.firstpoisontime)/3000000000L);
        int tb_hpUpdate_waveProgressTime=(tb_firstPoison_waveProgressTime-tb_firstPoison_hpUpdate);
        int nextpoisontick=20-(tb_firstPoison_hpUpdate-tb_firstPoison_lastPoison); //the next poison tick representation from the last known hp time
        if (poison == 0) {
            nextpoisontick=0;
        }
        updateCurrentTimePoisonTick(waveProgressTime);
        int healer1hptick=20 - ((int) (Math.floorMod(this.spawntime-this.firstpoisontime,60000000000L)/3000000000L)+1); //tick at which healer heals 1 hp
        int tickstodiefromhpupdate=0;
        while (hp>0 && nextpoisontick>0) {
            hp = hp - PoisonTick.PoisonDamage(nextpoisontick);
            if(nextpoisontick==healer1hptick) {
                hp++   ;    //regen hp
            }
            nextpoisontick--;
            tickstodiefromhpupdate++;
        }
        if (hp > 0) {
            deathcounter=-1;
            return -1;
        }
        if(currentpoisontick<=0&&poison>0) { //display predicted hp if healer is a rp that ticked out
            predictedhpfraction = Math.min(getHPFraction(wave, hp), predictedhpfraction);
        } else {
            predictedhpfraction=-1;
        }
        int tickstodiefromtime=tickstodiefromhpupdate-tb_hpUpdate_waveProgressTime;
        if (tickstodiefromtime <= 0) {
            deathcounter=-2;
            return -2;
        }
        int timetodie=(int) ((waveProgressTime+3000000000L-Math.floorMod(waveProgressTime-this.firstpoisontime,3000000000L)+(tickstodiefromtime-1)*3000000000L)/100000000L);
        if(deathcounter<0||timetodie<deathcounter){
            deathcounter=timetodie;
        }
        return timetodie;
    }

    //Checks to see if current model is the same as the previous model. The model updates itself on a ?40-50 fps rate so if you run this more often you will get incorrect results
    void updateModelVisibility(){
        int newhash;
        if(npc.getModel()==null){
            newhash=-1;
        } else {
            newhash=npc.getModel().hashCode();
            if(newhash==modelhash){
                visible=false;
            } else{
                modelhash=newhash;
                visible=true;
            }
        }
    }

    static int getTotalReserveCount(int wave){
        return ReserveCount[wave];
    }
    //Returns false if no hp bar available
    boolean updateHP(long waveProgressTime){
        int hp = npc.getHealthRatio(npc.getCombatInfoWrapper());
        if (hp != -1) {
            this.hpfraction = hp;
            this.hpupdatetime=waveProgressTime;
            if(hp==0){
                confirmedDead=true;
            }
            return true;
        }
        return false;
    }
}
