package com.kit.plugins.barbarianassault;

import com.kit.core.Session;

import java.awt.*;

class DefenderRole {
    private final BarbarianAssaultPlugin BA;
    private final Session session;

    private boolean drawEastieBlock;

    private Food food;

    DefenderRole(BarbarianAssaultPlugin BA){
        this.BA=BA;
        this.session= Session.get();
        this.food=new Food(BA);
    }
    void startedNewWave(){
        drawEastieBlock=false;
    }
    void Cycle(long waveProgressTime){

    }
    void PaintEvent(Graphics2D g2d, long waveProgressTime){
        if(BA.showDefenderFoodOnGround){food.drawGroundItems(g2d);}

    }
}
