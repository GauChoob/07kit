package com.kit.plugins.barbarianassault;

import java.awt.*;

import com.kit.api.wrappers.Widget;
import com.kit.core.Session;

//Methods related to the inventory
class InventoryGraphics {
    private static final int WidgetInventoryParent=149;
    private static final int WidgetInventoryChild=0;

    private Widget inventoryWidget;

    void startedNewWave(){
        return;
    }

    //Draws a box representing where you can spam destroy
    static void DrawDestroyFoodBox(Graphics2D g2d){
        g2d.setColor(new Color(255,255,255,128));
        if(Session.get().inResizableMode()){
            g2d.fillRect(Session.get().client().getViewportWidth()-200,Session.get().client().getViewportHeight()-48,158,5);
        } else {
            g2d.fillRect(563,460-5,158,5);
        }
    }

    //Returns true if inventory tab is open
    boolean isInventoryOpen(){
        if(inventoryWidget==null||inventoryWidget.unwrap()==null){
            inventoryWidget=Session.get().widgets.find(WidgetInventoryParent,WidgetInventoryChild);
        }
        if(inventoryWidget==null){
            return true;    //If unable to find, err on the side of drawing things
        }
        return inventoryWidget.isValid();
    }
}
