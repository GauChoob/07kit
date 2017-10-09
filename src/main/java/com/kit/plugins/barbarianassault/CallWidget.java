package com.kit.plugins.barbarianassault;

import com.kit.api.wrappers.Widget;
import com.kit.api.wrappers.WidgetItem;
import com.kit.core.Session;
import com.kit.game.engine.media.IWidget;

import java.awt.*;

//Everything related to the call infobox in the topright: Wave, calls and role
class CallWidget {
    private static final int WidgetCallBoxParentA=485;
    private static final int WidgetCallBoxParentC=486;
    private static final int WidgetCallBoxParentD=487;
    private static final int WidgetCallBoxParentH=488;
    private static final int WidgetCallWaveChild=1;
    private static final int WidgetCallFoodChild=3;

    private Widget callWidget;
    /*private Widget waveWidget;*/
    private int prevCall;
    private int prevCallTick;
    private int currentCall;
    private boolean is30stick;
    private boolean foodErrorFlash;
    private int playerRole;
    /*private int currentWave;*/

    CallWidget(){
        startedNewWave();
    }
    //Resets values in preparation for next wave
    void startedNewWave(){
        callWidget =null;
        prevCall=-1;
        prevCallTick=0;
        currentCall=0;
        foodErrorFlash=false;
        is30stick=false;
        playerRole=-1;
    }
    //Checks current call information and updates accordingly
    void updateFoodCall() {
        foodErrorFlash=false;
        int newcall=readCallFromWidget();
        if (newcall == 0) {
            if (currentCall > 0) {
                is30stick=true;
                prevCall=currentCall;
            } else {
                is30stick=false;
            }
            prevCallTick=currentCall;
            currentCall=0;
            return;
        }
        if(newcall==-1){
            prevCallTick=currentCall;
            currentCall=-1;
            return;
        }
        if (newcall > 0) {
            if (currentCall>0&&newcall!=currentCall) {
                foodErrorFlash=true;
            }
            prevCallTick=currentCall;
            currentCall=newcall;
            return;
        }

    }
    //Highlights current food in green, or displays error message if 1) Same call twice 2) Call revision by defender/healer
    void drawInventoryFoodCall(Graphics2D g2d){
        g2d.setColor(new Color(128, 128, 128, 64));
        if (currentCall > 0) {
            if(prevCall==currentCall){
                g2d.setColor(Color.RED);
                g2d.setFont(g2d.getFont().deriveFont(20.0f));
                g2d.drawString(String.format("SAME CALL TWICE"),Session.get().client().getViewportWidth()/2-40,Session.get().client().getViewportHeight()/2+30);
            } else {
                if (foodErrorFlash==true){
                    g2d.setColor(new Color(255,0,0,32));
                    g2d.fillRect(0,0,Session.get().client().getViewportWidth(),Session.get().client().getViewportHeight());
                    g2d.setColor(Color.RED);
                    //g2d.drawString(String.format("CALL CHANGED"),Session.get().client().getViewportWidth()/2-40,Session.get().client().getViewportHeight()/2+30);
                }

                for (WidgetItem i : Session.get().inventory.getAll()) {
                    if (i.getId() == currentCall) {
                        g2d.setColor(new Color(0, 255, 0, 128));
                        g2d.fill(i.getArea());
                    }
                }
            }
        }
    }
    int getPlayerRole(){
        if(callWidget ==null|| callWidget.unwrap()==null){
            updateCallWidget();
        }
        if(callWidget==null){
            return -1;
        }
        return playerRole;
    }

    int getCall(){
        return currentCall;
    }

    void Debug(Graphics2D g2d){
        Debug(g2d,10,100);
    }
    void Debug(Graphics2D g2d, int x, int y){
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.GREEN);
        g2d.drawString("CallWidget Debug",x,y);
        String widgettext="callWidget: ";
        if (callWidget == null) {
            widgettext+="null";
        } else {
            widgettext+= callWidget.hashCode();
        }
        g2d.drawString(widgettext,x,y+12*1);
        /*String widgettext2="waveWidget: ";
        g2d.drawString(widgettext2,x,y+12*2);*/
        g2d.drawString("prevCall: "+prevCall,x,y+12*3);
        g2d.drawString("prevCallTick: "+prevCallTick,x,y+12*4);
        g2d.drawString("currentCall: "+currentCall,x,y+12*5);
        g2d.drawString("is30stick: "+is30stick,x,y+12*6);
        g2d.drawString("foodErrorFlash: "+foodErrorFlash,x,y+12*7);
        //g2d.drawString("currentWave: "+ currentWave,x,y+12*8);
    }

    //Reads info from widget and returns item id
    private int readCallFromWidget(){
        if(callWidget ==null|| callWidget.unwrap()==null){
            updateCallWidget();
        }
        if(callWidget==null){
            return -1;
        }
        String foodcall= callWidget.getText();
        if(foodcall.equals("- - -")){
            return 0;
        }
        switch (playerRole){
            case Cons.H:
                if(foodcall.equals("Pois. Tofu")){
                    return Items.H_P_TOFU;
                }
                if(foodcall.equals("Pois. Worms")){
                    return Items.H_P_WORMS;
                }
                if(foodcall.equals("Pois. Meat")){
                    return Items.H_P_MEAT;
                }
                return 0;
            case Cons.D:
                if(foodcall.equals("Tofu")){
                    return Items.D_TOFU;
                }
                if(foodcall.equals("Crackers")){
                    return Items.D_CRACKERS;
                }
                if(foodcall.equals("Worms")){
                    return Items.D_WORMS;
                }
                return 0;
            case Cons.C:
                if(foodcall.equals("Red eggs")){
                    return Items.C_RED_EGG;
                }
                if(foodcall.equals("Green eggs")){
                    return Items.C_GREEN_EGG;
                }
                if(foodcall.equals("Blue eggs")){
                    return Items.C_BLUE_EGG;
                }
                return 0;
            case Cons.A:
                if(foodcall.equals("Controlled/")){
                    return Cons.A_CONTROLLED_BRONZE_WIND;
                }
                if(foodcall.equals("Accurate/")){
                    return Cons.A_ACCURATE_IRON_WATER;
                }
                if(foodcall.equals("Aggressive/")){
                    return Cons.A_AGGRESSIVE_STEEL_EARTH;
                }
                if(foodcall.equals("Defensive/")){
                    return Cons.A_DEFENSIVE_MITHRIL_FIRE;
                }
                return 0;
            default:
                return 0;
        }
    }

    //finds the widget and updates player role
    private void updateCallWidget(){
        IWidget[][] all = Session.get().client().getWidgets();
        if (all == null) {
            callWidget=null;
            return;
        }
        for(int i=WidgetCallBoxParentA;i<=WidgetCallBoxParentH;i++){
            IWidget[] tree = all[i];
            if (tree == null) {
                continue;
            }
            playerRole=i-WidgetCallBoxParentA;      //updates the player's role
            IWidget widget = tree[WidgetCallFoodChild];
            if (widget != null) {
                callWidget= new Widget(Session.get(), widget, i, WidgetCallFoodChild);
            } else {
                callWidget=null;
            }
            return;
        }
        callWidget=null;
        return;
    }
    /*//finds the widget
    private void updateWaveWidget(){
        IWidget[][] all = Session.get().client().getWidgets();
        if (all == null) {
            waveWidget=null;
            return;
        }
        for(int i=WidgetCallBoxParentA;i<=WidgetCallBoxParentH;i++){
            IWidget[] tree = all[i];
            if (tree == null) {
                continue;
            }
            playerRole=i-WidgetCallBoxParentA;      //updates the player's role
            IWidget widget = tree[WidgetCallFoodChild];
            if (widget != null) {
                waveWidget= new Widget(Session.get(), widget, i, WidgetCallWaveChild);
                currentWave=getInGameWave();
            } else {
                waveWidget=null;
                currentWave=-1;
            }
            return;
        }
        waveWidget=null;
        currentWave=-1;
        return;
    }*/


    //returns int 0-9 representing current wave, or -1 if unavailable from the call widget
    //TODO - verify works properly for all waves
    /*private int getInGameWave(){
        String waveText= waveWidget.getText();
        if(waveText.length()<6){
            return -2;
        }
        System.out.println("Wave text: "+waveText);
        System.out.println(waveText.substring(0,5));
        System.out.println(waveText.substring(5,6));
        System.out.println(Integer.parseInt(waveText.substring(5,6))-1);
        if(waveText.equals("Wave 10")){
            return 9;
        }
        if(waveText.substring(0,5).equals("Wave ")){
            return Integer.parseInt(waveText.substring(5,6))-1;
        }
        throw new Error();
    }*/
        /*int getCurrentWave(){
        if(waveWidget ==null|| waveWidget.unwrap()==null){
            updateWaveWidget();
        }
        if(callWidget==null){
            return -1;
        }
        if(currentWave<0){
            currentWave=getInGameWave();
        }
        return currentWave;
    }*/

    //Displays debug info
}