package com.kit.plugins.barbarianassault;

import com.kit.api.wrappers.Tile;

import java.awt.*;

class Runner {
    //Draws tiles to stand on to block an Eastie
    static void DrawEastieBlock(Graphics2D g2d, BATile baTile){
        for(Tile tile: baTile.getTiles(BATile.locationsList.EASTIE_BLOCK_GOOD_TILES)){
            g2d.setColor(new Color(0, 255, 0, 50));
            g2d.fill(tile.getPolygon());
        }
        for(Tile tile: baTile.getTiles(BATile.locationsList.EASTIE_BLOCK_BACKUP_TILES)){
            g2d.setColor(new Color(255, 255, 0, 50));
            g2d.fill(tile.getPolygon());
        }
    }
}
