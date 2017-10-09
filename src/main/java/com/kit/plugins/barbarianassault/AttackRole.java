package com.kit.plugins.barbarianassault;

import com.kit.core.Session;

import java.awt.*;

class AttackRole {
    private final BarbarianAssaultPlugin BA;
    private final Session session;
    AttackRole(BarbarianAssaultPlugin BA){
        this.BA=BA;
        this.session= Session.get();
    }
    void startedNewWave(){

    }
    void Cycle(long waveProgressTime){

    }
    void PaintEvent(Graphics2D g2d, long waveProgressTime){

    }
}
