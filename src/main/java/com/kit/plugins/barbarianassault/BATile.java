package com.kit.plugins.barbarianassault;

import com.kit.api.wrappers.GameObject;
import com.kit.api.wrappers.Tile;
import com.kit.core.Session;
import static com.kit.api.wrappers.GameObjectType.INTERACTABLE;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//Converts coordinates of BA into general game coordinates
//Has predefined important BA coordinates
class BATile {
    private static final int ATTACKER_ITEM_MACHINE_ID=20241;
    private static final BACoords RUNNER_EAST=new BACoords(34,43);
    private static final BACoords SECRET_WALL=new BACoords(24,28);
    private static final List<BACoords> EASTIE_BLOCK_GOOD_TILES=Arrays.asList(new BACoords(34,41),new BACoords(35,40),new BACoords(36,39),new BACoords(37,38),new BACoords(38,37),new BACoords(39,36),new BACoords(40,35));
    private static final List<BACoords> EASTIE_BLOCK_BACKUP_TILES=Arrays.asList(new BACoords(35,41),new BACoords(36,40),new BACoords(37,39),new BACoords(38,38),new BACoords(39,37),new BACoords(40,36));

    private int xoffset;    //current offsets for current wave
    private int yoffset;
    private final Session context;

    enum locationList{
        RUNNER_EAST(BATile.RUNNER_EAST),
        SECRET_WALL(BATile.SECRET_WALL);
        private BACoords relativeTile;
        locationList(BACoords relativeTile){
            this.relativeTile=relativeTile;
        }
    }
    enum locationsList{
        EASTIE_BLOCK_GOOD_TILES(BATile.EASTIE_BLOCK_GOOD_TILES),
        EASTIE_BLOCK_BACKUP_TILES(BATile.EASTIE_BLOCK_BACKUP_TILES);
        private List<BACoords> relativeTiles;
        locationsList(List<BACoords> relativeTiles){
            this.relativeTiles=relativeTiles;
        }
    }

    BATile(){
        xoffset=0;
        yoffset=0;
        context=Session.get();
    }

    //Run on first tick of new wave
    void startedNewWave(){
        //updateRelativeLocation(); //-> now run by GameState to check position automatically so unnecessary here
        return;
    }

    //Figures out ba xy coordinates relative to fixed point. Needs to be run every wave. (Centered (30,10) on attack item machine)
    //Takes 50 ms of processing time* TODO long processing time
    boolean updateRelativeLocation(){
        Tile attackSlotTile=findAttackSlot();
        if(attackSlotTile!=null){
            xoffset=attackSlotTile.getX()-30;
            yoffset=attackSlotTile.getY()-10;
            return true;
        }
        return false;
    }
    //Returns a tile based on the BACoord tile system
    Tile getTile(int x, int y){
        return new Tile(context,x+xoffset,y+yoffset,context.client().getPlane());
    }
    //Returns a predefined tile
    Tile getTile(locationList location){
        return getTile(location.relativeTile.x,location.relativeTile.y);
    }
    //Returns a list of predefined tiles
    List<Tile> getTiles(locationsList location){
        return location.relativeTiles.stream().map(relativeTile->getTile(relativeTile.x,relativeTile.y)).collect(Collectors.toList());
    }

    //Show current x/y ba coordinate
    void Debug(Graphics2D g2d){
        Debug(g2d,10,100);
    }
    void Debug(Graphics2D g2d, int x, int y){
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.GREEN);
        g2d.drawString("Position BA: ("+(context.player.getX()-xoffset)+","+(context.player.getY()-yoffset)+")",x,y+12*2);
        g2d.drawString("Position Real: ("+(context.player.getX())+","+(context.player.getY())+")",x,y+12*3);
    }

    //Finds the real tile for Attack item machine to be used as reference point
    private Tile findAttackSlot(){
        for (GameObject object: context.objects.find().id(ATTACKER_ITEM_MACHINE_ID).type(INTERACTABLE).asList()){
            return object.getTile();
        }
        return null;
    }
}
//Contains a relative x/y coordinate to be passed to BATile to be converted into a game tile
class BACoords{
    int x;
    int y;
    BACoords(int x,int y){
        this.x=x;
        this.y=y;
    }
}