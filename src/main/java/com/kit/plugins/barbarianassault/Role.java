package com.kit.plugins.barbarianassault;

import com.kit.core.Session;

import java.awt.*;

class Role {
    private final BarbarianAssaultPlugin BA;
    private final Session session;
    private int playerRole;


    Role(BarbarianAssaultPlugin BA) {
        this.BA = BA;
        this.session = Session.get();
        this.playerRole = -1;
    }

    //Resets values in preparation for next wave
    void startedNewWave() {
        playerRole = -1;
    }

    int getPlayerRole() {
        if (playerRole == -1) {
            findPlayerRole();
        }
        return playerRole;
    }

    private void findPlayerRole(){
        if(session.equipment.contains(Items.A_ICON)){
            playerRole=Cons.A;
        } else if(session.equipment.contains(Items.C_ICON)){
            playerRole=Cons.C;
        } else if(session.equipment.contains(Items.D_ICON)){
            playerRole=Cons.D;
        } else if(session.equipment.contains(Items.H_ICON)){
            playerRole=Cons.H;
        } else {
            playerRole=-1;
        }
    }

    void Debug(Graphics2D g2d){
        Debug(g2d,10,100);
    }
    void Debug(Graphics2D g2d, int x, int y){
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.GREEN);
        g2d.drawString("Role Debug",x,y);
        y+=12;
        g2d.drawString("Role: "+Cons.Role(playerRole),x,y);
    }
}