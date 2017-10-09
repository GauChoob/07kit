package com.kit.plugins.barbarianassault;

import com.kit.api.event.MessageEvent;
import com.kit.api.wrappers.Deque;
import com.kit.api.wrappers.GroundLayer;
import com.kit.api.wrappers.Loot;
import com.kit.api.wrappers.Tile;
import com.kit.core.Session;
import com.kit.game.engine.collection.IDeque;
import com.kit.game.engine.renderable.ILoot;
import com.kit.game.engine.scene.tile.IGroundLayer;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.util.ListIterator;

import static com.google.common.collect.Lists.newArrayList;

class CollectorRole {
    private final BarbarianAssaultPlugin BA;
    private final Session session;

    private Egg egg;

    CollectorRole(BarbarianAssaultPlugin BA){
        this.BA=BA;
        this.session= Session.get();
        this.egg=new Egg(BA);
    }
    void startedNewWave(){
        egg.resetForNewWave();
    }
    void Cycle(long waveProgressTime){

    }
    void PaintEvent(Graphics2D g2d, long waveProgressTime){
        if(BA.showCollectorEggsOnGround){egg.drawGroundItems(g2d);}
        if(BA.invGraphics.isInventoryOpen()){
            if(BA.showDestroyFoodBox){InventoryGraphics.DrawDestroyFoodBox(g2d);}
        }
    }
    void MessageEvent(MessageEvent event){
        String message=event.getMessage();
        if(event.getType()== MessageEvent.Type.MESSAGE_SERVER && message.equals("The Queen has arrived and you can no longer use the horn of glory!")){
            egg.queenSpawning();
        }
    }
}
