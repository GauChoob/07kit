package com.kit.plugins.barbarianassault;

import com.kit.api.wrappers.Area;
import com.kit.api.wrappers.Tile;
import com.kit.api.wrappers.Widget;
import com.kit.core.Session;

import java.awt.*;


//Gets player's game location
class GameState {
    private final BarbarianAssaultPlugin BA;
    private static final Area LOBBY_XY=new Area(2576,5259,2610-2576+1,5300-5259+1,0);
    private static final Area UPSTAIRS_BA=new Area(2522,3560,2538-2522+1,3579-3560+1,0);
    private static final Area LOBBY_WAVE_XY[]={new Area(2576,5291,8,8,0),
            new Area(2584,5291,8,8,0),
            new Area(2595,5291,8,8,0),
            new Area(2603,5291,8,8,0),
            new Area(2576,5281,8,8,0),
            new Area(2584,5281,8,8,0),
            new Area(2595,5281,8,8,0),
            new Area(2603,5281,8,8,0),
            new Area(2576,5271,8,8,0),
            new Area(2584,5271,8,8,0)};
    private static final int WidgetWaveParent=488;
    private static final int WidgetWaveChild=1;

    //0=previous state, 1=current state
    private boolean basement[]=new boolean[2];
    private boolean upstairs[]=new boolean[2];
    private boolean ingame[]=new boolean[2];
    private boolean waveLobby[]=new boolean[2];
    private int oldWave;
    private int currentWave;
    /*private Widget waveWidget;*/

    GameState(BarbarianAssaultPlugin BA){
        this.BA=BA;
        oldWave=-1;
        currentWave=-1;
        shiftVariablesNewUpdate();
        shiftVariablesNewUpdate();
    }

    //Checks to see where player is standing, and updates wave if necessary
    boolean updateLocation(){
        Tile pos = Session.get().player.getTile();
        if(pos.getX()==0&&pos.getY()==0){
            return false;   //loading screen, failed to update
        }
        shiftVariablesNewUpdate();
        if(LOBBY_XY.contains(pos)){
            basement[1]=true;
            for (int i = 0; i < 10; i++) {      //Determine which wave standing in if any
                if (LOBBY_WAVE_XY[i].contains(pos)) {
                    waveLobby[1]=true;
                    oldWave=currentWave;
                    currentWave=i;
                    break;
                }
            }
            if(!isInWaveLobby()&&justEndedWave()&&currentWave==9) {   //wave 10 end
                oldWave = 9;
                currentWave = -1;
            }
        } else if (UPSTAIRS_BA.contains(pos)) {
            upstairs[1]=true;
        } else {
            if(ingame[0]){
                ingame[1]=true; //Assume you can't teleport from in-game to outside of ba. If you were in-game last tick and next tick you aren't in ba zone, assume you are still playing and not in Lumbridge
            } else {
                if(waveLobby[0]&&BA.baTile.updateRelativeLocation()){ //Do costly attacker check if was in lobby the tick before, or else just assume the player walked outside/teleported out
                    ingame[1]=true;
                }
            }
        }
        return true;
    }

    boolean isAtBA(){
        return upstairs[1]||basement[1]||ingame[1];
    }
    boolean isUpstairs(){
        return upstairs[1];
    }
    boolean isDownstairs(){
        return basement[1];
    }
    boolean isInWaveLobby(){
        return waveLobby[1];
    }
    boolean isIngame(){
        return ingame[1];
    }

    boolean justWentUpstairs(){
        return upstairs[1]&&basement[0];
    }
    boolean justGotToBA(){
        return upstairs[1]&&!(upstairs[0]||basement[0]);
    }
    boolean justWentDownstairs(){
        return basement[1]&&upstairs[0];
    }
    boolean justLeftWaveLobby(){
        return waveLobby[0]&&!waveLobby[1];
    }
    boolean justEnteredWaveLobby(){
        return waveLobby[1]&&!waveLobby[0];
    }
    boolean justEndedWave(){
        return (ingame[0]&&!ingame[1]);
    }
    boolean justStartedWave(){
        return ingame[1]&&!ingame[0];
    }
    //True if died or exited via ladder. Only valid the tick you leave the game
    boolean isWaveInterrupted(){
        return oldWave==currentWave;
    } //TODO: Make sure function works
    //only valid the tick you leave the game
    int getEndedWave(){
        return oldWave;
    }
    int getWave(){
        return currentWave;
    }

    //Displays debug info
    void Debug(Graphics2D g2d){
        Debug(g2d,10,100);
    }
    void Debug(Graphics2D g2d, int x, int y){
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.GREEN);
        g2d.drawString("GameState Debug",x,y);
        String widgetText="Widget: ";
        /*if (waveWidget == null) {
            widgetText+="null";
        } else {
            widgetText+= waveWidget.hashCode();
        }*/
        g2d.drawString(widgetText,x,y+12*1);
        g2d.drawString("basement[1]: "+basement[1],x,y+12*2);
        g2d.drawString("upstairs[1]: "+upstairs[1],x,y+12*3);
        g2d.drawString("ingame[1]: "+ingame[1],x,y+12*4);
        g2d.drawString("waveLobby[1]: "+waveLobby[1],x,y+12*5);
        g2d.drawString("oldWave: "+oldWave,x,y+12*6);
        g2d.drawString("currentWave: "+currentWave,x,y+12*7);
        g2d.drawString("isAtBA(): "+isAtBA(),x,y+12*8);
        g2d.drawString("isUpstairs(): "+isUpstairs(),x,y+12*9);
        g2d.drawString("isDownstairs(): "+isDownstairs(),x,y+12*10);
        g2d.drawString("isInWaveLobby(): "+isInWaveLobby(),x,y+12*11);
        g2d.drawString("isIngame(): "+isIngame(),x,y+12*12);
        g2d.drawString("justWentUpstairs(): "+justWentUpstairs(),x,y+12*13);
        g2d.drawString("justGotToBA(): "+justGotToBA(),x,y+12*14);
        g2d.drawString("justWentDownstairs(): "+justWentDownstairs(),x,y+12*15);
        g2d.drawString("justLeftWaveLobby(): "+justLeftWaveLobby(),x,y+12*16);
        g2d.drawString("justEnteredWaveLobby(): "+justEnteredWaveLobby(),x,y+12*17);
        g2d.drawString("justEndedWave(): "+justEndedWave(),x,y+12*18);
        g2d.drawString("justStartedWave(): "+justStartedWave(),x,y+12*19);
        g2d.drawString("isWaveInterrupted(): "+isWaveInterrupted(),x,y+12*20);
    }

    void drawWaveNumberOrLobby(Graphics2D g2d){
        String output_info;
        if(BA.gameState.isIngame()|| BA.gameState.isInWaveLobby()) {
            output_info="Wave "+(BA.gameState.getWave()+1);
            //output_info += ": " + waveprogresstime / 100000000L;
            //output_info += String.format(": %01d.%d",waveprogresstime/1000000000L,(waveprogresstime%1000000000L)/100000000L);
        } else {
            output_info = "Lobby";
        }
        g2d.setFont(g2d.getFont().deriveFont(14.0f));
        g2d.setColor(Color.CYAN);
        g2d.drawString(output_info, 10, 70);
    }

    //When a new tick occurs, excluding loading screens, put all the old information into [0] so that new information can be drawn to [1]
    private void shiftVariablesNewUpdate(){
        basement[0]=basement[1];
        upstairs[0]=upstairs[1];
        ingame[0]=ingame[1];
        waveLobby[0]=waveLobby[1];
        basement[1]=false;
        upstairs[1]=false;
        ingame[1]=false;
        waveLobby[1]=false;
    }
}