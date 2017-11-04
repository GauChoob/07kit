package com.kit.plugins.barbarianassault;

import com.kit.Application;
import com.kit.api.event.*;
import com.kit.api.plugin.Option;
import com.kit.api.plugin.Plugin;
import com.kit.api.plugin.Schedule;
import com.kit.api.util.PaintUtils;
import com.kit.api.wrappers.*;
import com.kit.core.control.PluginManager;

import java.awt.*;

import static com.google.common.collect.Lists.newArrayList;

import com.kit.game.engine.renderable.entity.INpc;


/**
 */



public class BarbarianAssaultPlugin extends Plugin {
    @Option(label = "Round timer (during wave and after-round stats)", value = "true", type = Option.Type.TOGGLE)
    boolean showRoundTimer;
    @Option(label = "Wave timer (during wave)", value = "true", type = Option.Type.TOGGLE)
    boolean showWaveTimer;
    @Option(label = "Points tracker (controlled with commands)", value = "true", type = Option.Type.TOGGLE)
    boolean showPointsTracker;
    @Option(label = "Spam destroy overlay (healer/collector)", value = "true", type = Option.Type.TOGGLE)
    boolean showDestroyFoodBox;
    @Option(label = "Defender: Ground food indicator", value = "true", type = Option.Type.TOGGLE)
    boolean showDefenderFoodOnGround;
    @Option(label = "Collector: Eggs indicator", value = "true", type = Option.Type.TOGGLE)
    boolean showCollectorEggsOnGround;
    @Option(label = "Healer: Eastie W1-3 marker", value = "true", type = Option.Type.TOGGLE)
    boolean showHealerEasties;
    @Option(label = "Healer: Left-side death info-panel", value = "true", type = Option.Type.TOGGLE)
    boolean showHealerSummary;
    @Option(label = "Healer: Call overlay", value = "true", type = Option.Type.TOGGLE)
    boolean showHealerCallOverlay;
    @Option(label = "Healer: Display infobox (if false, no infoboxes)", value = "true", type = Option.Type.TOGGLE)
    boolean showHealerInfobox;
    @Option(label = "Healer: Display advanced infobox (if false, just stack order/#s)", value = "true", type = Option.Type.TOGGLE)
    boolean showHealerAdvancedInfobox;

    private boolean pluginloaded;
    CallWidget callWidget;
    InventoryGraphics invGraphics;
    GameState gameState;
    BATile baTile;
    WaveRoundData waveRoundData;
    AttackRole attackRole;
    DefenderRole defenderRole;
    HealerRole healerRole;
    CollectorRole collectorRole;
    Points points;
    TextCommands textCommands;
    Role role;

    public BarbarianAssaultPlugin(PluginManager manager) {
        super(manager);
    }

    @Override
    public String getName() {
        return "BA Helper 1.04";
    }   //Interface name

    @Override
    public void start() {       //Initialize all
        pluginloaded=false;
        callWidget = new CallWidget();
        invGraphics=new InventoryGraphics();
        gameState =new GameState(this);
        baTile=new BATile();
        waveRoundData=new WaveRoundData(this);
        attackRole=new AttackRole(this);
        collectorRole=new CollectorRole(this);
        defenderRole=new DefenderRole(this);
        healerRole=new HealerRole(this);
        points=new Points(this);
        textCommands=new TextCommands(this);
        role=new Role(this);
    }

    @Override
    public void stop() {

    }

    @Schedule(200)
    public void mainCycle() {
        pluginloaded=true;
        if (!isLoggedIn()) {
            return;
        }
        long currentTime=System.nanoTime();
        gameState.updateLocation();
        if (gameState.isAtBA()) {
            if (gameState.isIngame()) {
                if (gameState.justStartedWave()) {
                    callWidget.startedNewWave();
                    invGraphics.startedNewWave();
                    baTile.startedNewWave();
                    waveRoundData.startedNewWave(gameState.getWave(),currentTime);
                    attackRole.startedNewWave();
                    collectorRole.startedNewWave();
                    defenderRole.startedNewWave();
                    healerRole.startedNewWave();
                    points.startedNewWave();
                    role.startedNewWave();
                }

                callWidget.updateFoodCall();

                long waveProgressTime=currentTime-waveRoundData.getWaveStartTime();
                switch(role.getPlayerRole()){
                //switch(callWidget.getPlayerRole()){
                    case Cons.A:
                        attackRole.Cycle(waveProgressTime);
                        break;
                    case Cons.C:
                        collectorRole.Cycle(waveProgressTime);
                        break;
                    case Cons.D:
                        defenderRole.Cycle(waveProgressTime);
                        break;
                    case Cons.H:
                        healerRole.Cycle(waveProgressTime);
                        break;
                    default:
                        break;
                }
            } else {
                if (gameState.justEndedWave()) {
                    waveRoundData.endedWave(gameState.getEndedWave(),currentTime);
                    points.endedWave();
                }
                if (gameState.isInWaveLobby()) {
                    points.getWaveData();
                }
                if (gameState.isInWaveLobby()&&gameState.getWave()==0) {
                    waveRoundData.resetForNewRound();
                    points.resetForNewRound();
                }
                if(gameState.justWentUpstairs()){
                    waveRoundData.wentUpstairs(gameState.getWave());
                }
            }
        } else {
            waveRoundData.leftBA();
        }
        //logger.info(Long.toString((System.nanoTime()-currentTime)/1000000L)); //Test
    }

    /*@EventHandler
    public void onActionEvent(ActionEvent event){
        logger.info("ActionEvent "+System.nanoTime());
        logger.info(""+event.getOpcode()+"/"+event.getRawOpcode()+":"+event.getInteraction()+":"+event.getVar0()+"/"+event.getVar1());
    }*/


    @EventHandler
    public void onPaintEvent(PaintEvent event) {
        if (!isLoggedIn()) {
            return;
        }
        Graphics2D g2d = (Graphics2D) event.getGraphics().create();

        //callWidget.Debug(g2d);
        //gameState.Debug(g2d);
        //baTile.Debug(g2d); //Shows current x/y coordinate
        //waveRoundData.Debug(g2d); //Shows current x/y coordinate
        //points.drawRoundInfo(g2d);
        //role.Debug(g2d);

        if (pluginloaded){
            if(!gameState.isAtBA()){
                return;
            }
            long currentTime=System.nanoTime();
            long waveProgressTime=currentTime-waveRoundData.getWaveStartTime();
            if(gameState.isIngame()){
                switch(role.getPlayerRole()){
                //switch(callWidget.getPlayerRole()) {
                    case Cons.A:
                        attackRole.PaintEvent(g2d,waveProgressTime);
                        break;
                    case Cons.C:
                        collectorRole.PaintEvent(g2d,waveProgressTime);
                        break;
                    case Cons.D:
                        defenderRole.PaintEvent(g2d,waveProgressTime);
                        break;
                    case Cons.H:
                        healerRole.PaintEvent(g2d,waveProgressTime);
                        break;
                    default:
                        break;
                }
                if(showWaveTimer){waveRoundData.drawWaveTimer(g2d, waveProgressTime);}
                if(showPointsTracker){
                    points.drawPointsToGo(g2d);
                    if(waveProgressTime<5000000000L){
                        points.drawRoundInfo(g2d);
                    }
                }
            }
            if(showRoundTimer){gameState.drawWaveNumberOrLobby(g2d);}
            if(showPointsTracker){textCommands.drawCommandMessage(g2d,currentTime);}
            if (gameState.isDownstairs()) {
                if(showPointsTracker){
                    points.drawPointsToGo(g2d);
                    if (waveRoundData.getInProgress()) {
                        points.drawRoundInfo(g2d);
                    }
                }
            }
            if (waveRoundData.getInProgress()) {
                if(showRoundTimer){waveRoundData.drawRoundTime(g2d,currentTime);}
            }
            if (waveRoundData.getDisplayRoundInfo()) {
                if(showRoundTimer){waveRoundData.drawRoundInfo(g2d);}
                if(showPointsTracker){points.drawRoundInfo(g2d);}
            }

        } else  {
            g2d.setFont(g2d.getFont().deriveFont(14.0f));
            g2d.setColor(Color.RED);
            g2d.drawString("Loading BA plugin...", 10, 70);
        }
        g2d.dispose();
    }

    @EventHandler
    public void onMessage(MessageEvent event) {
        if (!gameState.isAtBA()) {
            return;
        }
        if(event.getSender().equals(player.getName())){
            textCommands.ProcessCommand(event.getMessage());
        }
        switch(role.getPlayerRole()){
        //switch(callWidget.getPlayerRole()) {
            case Cons.A:
                break;
            case Cons.C:
                collectorRole.MessageEvent(event);
                break;
            case Cons.D:
                break;
            case Cons.H:
                healerRole.MessageEvent(event);
                break;
            default:
                break;
        }
        //if(message.equals("All the Penance Healers have been killed!")) Runners Fighters Rangers
        /*:All of the Penance Fighters have been killed!:null::0 [MESSAGE_SERVER]
:All of the Penance Runners have been killed!:null::0 [MESSAGE_SERVER]
:All of the Penance Healers have been killed!:null::0 [MESSAGE_SERVER]
:All of the Penance Rangers have been killed!:null::0 [MESSAGE_SERVER]
*/
    }
}