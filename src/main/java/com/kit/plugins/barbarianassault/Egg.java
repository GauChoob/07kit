package com.kit.plugins.barbarianassault;

import com.kit.api.wrappers.Loot;
import com.kit.api.wrappers.Tile;
import com.kit.core.Session;

import java.awt.*;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.List;

class Egg {
    static final int GREEN=0;
    static final int RED=1;
    static final int BLUE=2;

    static final Color[] eggColors={new Color(0, 255, 0),new Color(255, 0, 0),new Color(0, 0, 255)};

    private static final int checkDistance=20;

    private final BarbarianAssaultPlugin BA;
    private final Session session;

    EggStack[][] eggMap=new EggStack[checkDistance*2+1][checkDistance*2+1];
    private boolean queenSpawning;

    Egg(BarbarianAssaultPlugin BA){
        this.BA=BA;
        this.session= Session.get();
        for(int i=0;i<checkDistance*2+1;i++){
            for(int j=0;j<checkDistance*2+1;j++){
                eggMap[i][j]=new EggStack();
            }
        }
        queenSpawning=false;
    }

    void queenSpawning(){
        queenSpawning=true;
    }
    void resetForNewWave(){
        queenSpawning=false;
    }

    static int Egg2Id(int id){
        if((id==Items.C_GREEN_EGG)||(id==Items.C_RED_EGG)||(id==Items.C_BLUE_EGG)){
            return id-Items.C_GREEN_EGG;
        }
        return -1;
    }

    void drawGroundItems(Graphics2D g2d){
        if(!queenSpawning){
            int eggCall=Egg2Id(BA.callWidget.getCall());
            //System.out.println(eggCall);
            int x=session.player.getX();
            int y=session.player.getY();
            for(int i=0;i<checkDistance*2+1;i++){
                for(int j=0;j<checkDistance*2+1;j++){
                    eggMap[i][j].invalidate();
                }
            }
            List<Loot> lootList=session.loot.find().distance(checkDistance).asList();
            ListIterator li=lootList.listIterator(lootList.size());
            int test=0;
            while(li.hasPrevious()){       //order: first in list is last dropped = bottom of pile
                Loot loot=(Loot) li.previous();
                int relativeX=loot.getX()-x+checkDistance;
                int relativeY=loot.getY()-y+checkDistance;
                test+=1;
                eggMap[relativeX][relativeY].addItem(Egg2Id(loot.getId()));
            /*
            Point pos=loot.getBasePoint();
            if(pos.x==-1||pos.y==-1){ continue;}
            System.out.println(loot.getName()+":"+loot.getX()+"/"+loot.getY());
            g2d.drawString(loot.getName(),pos.x,pos.y+y);
            y+=10;*/
            }
            //System.out.println(test);
            g2d.setFont(g2d.getFont().deriveFont(14.0f));
            for(int i=0;i<checkDistance*2+1;i++){
                for(int j=0;j<checkDistance*2+1;j++){
                    int absolutex=x+i-checkDistance;
                    int absolutey=y+j-checkDistance;
                    eggMap[i][j].draw(g2d,absolutex,absolutey,eggCall);
                }
            }
        } else {
            //draw yellow egg
            List<Loot> lootList=session.loot.find().id(Items.Q_YELLOW_EGG).asList();
            for(Loot loot: lootList) {
                Polygon tilePoly=loot.getTile().getPolygon();
                g2d.setColor(new Color(255,255,0,128));
                g2d.fill(tilePoly);
            }
        }
    }



    class EggStack{
        private boolean valid;
        private boolean atTop;
        private int topID;
        private int topEggs;
        private int[] nonTopStackedEggs=new int[3];
        private int[] allStackedEggs=new int[3];
        EggStack(){
            valid=false;
        }
        void invalidate(){
            valid=false;
        }
        private void reset(){
            valid=true;
            atTop=true;
            topID=-2;
            topEggs=0;
            nonTopStackedEggs[Egg.GREEN]=0;
            nonTopStackedEggs[Egg.RED]=0;
            nonTopStackedEggs[Egg.BLUE]=0;
            allStackedEggs[Egg.GREEN]=0;
            allStackedEggs[Egg.RED]=0;
            allStackedEggs[Egg.BLUE]=0;
        }
        void addItem(int eggID){
            if(!valid){
                reset();
            }
            if(topID==-2){
                topID=eggID;
                if(eggID != -1){
                    topEggs+=1;
                    allStackedEggs[eggID]+=1;
                }
            } else if(topID==eggID&&atTop){
                if(eggID != -1){
                    topEggs+=1;
                    allStackedEggs[eggID]+=1;
                }
            } else {
                atTop=false;
                if (eggID != -1) {
                    nonTopStackedEggs[eggID]+=1;
                    allStackedEggs[eggID]+=1;
                }
            }
        }
        void draw(Graphics2D g2d, int x, int y, int eggCall){
            if(!valid){return;}
            Polygon tilePoly=new Tile(session,x,y,session.client().getPlane()).getPolygon();
            int ax= Arrays.stream(tilePoly.xpoints).sum()/4;
            int ay=Arrays.stream(tilePoly.ypoints).sum()/4;
            //System.out.println("Tile: ("+x+","+y+") ["+allStackedEggs[0]+","+allStackedEggs[1]+","+allStackedEggs[2]+"] - "+eggCall+" - "+ax+","+ay);
            if(eggCall>=0){
                if(allStackedEggs[eggCall]>0){
                    switch(eggCall){
                        case Egg.GREEN:
                            g2d.setColor(new Color(0,255,0,Math.min(128,32+32*(allStackedEggs[eggCall]))));
                            break;
                        case Egg.RED:
                            g2d.setColor(new Color(255,0,0,Math.min(128,32+32*(allStackedEggs[eggCall]))));
                            break;
                        case Egg.BLUE:
                            g2d.setColor(new Color(0,0,255,Math.min(128,32+32*(allStackedEggs[eggCall]))));
                            break;
                        default:
                            g2d.setColor(new Color(0,0,0,0));
                            break;
                    }
                    g2d.fill(tilePoly);
                }
                int[] order=new int[3];
                switch(eggCall) {
                    case Egg.GREEN:
                        order[0]=Egg.GREEN;
                        order[1]=Egg.BLUE;
                        order[2]=Egg.RED;
                        break;
                    case Egg.RED:
                        order[0]=Egg.RED;
                        order[1]=Egg.BLUE;
                        order[2]=Egg.GREEN;
                        break;
                    case Egg.BLUE:
                        order[0]=Egg.BLUE;
                        order[1]=Egg.GREEN;
                        order[2]=Egg.RED;
                        break;
                    default:
                        throw new java.lang.Error();
                }
                if(allStackedEggs[order[0]]>0){
                    String output=(topID==order[0]?""+topEggs+"+":"0+")+nonTopStackedEggs[order[0]];
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(output,ax-10,ay-7);
                }
                if(allStackedEggs[order[1]]>0){
                    String output=""+allStackedEggs[order[1]];
                    g2d.setColor(eggColors[order[1]]);
                    g2d.drawString(output,ax-12,ay+13);
                }
                if(allStackedEggs[order[2]]>0){
                    String output=""+allStackedEggs[order[2]];
                    g2d.setColor(eggColors[order[2]]);
                    g2d.drawString(output,ax+8,ay+13);
                }
            } else {
                if(allStackedEggs[Egg.RED]+allStackedEggs[Egg.BLUE]+allStackedEggs[Egg.GREEN]>0){
                    g2d.setColor(new Color(255,255,255,32));
                    g2d.fill(tilePoly);
                }
                if(allStackedEggs[Egg.RED]>0){
                    String output=""+allStackedEggs[Egg.RED];
                    g2d.setColor(eggColors[Egg.RED]);
                    g2d.drawString(output,ax-10,ay-7);
                }
                if(allStackedEggs[Egg.BLUE]>0){
                    String output=""+allStackedEggs[Egg.BLUE];
                    g2d.setColor(eggColors[Egg.BLUE]);
                    g2d.drawString(output,ax-12,ay+13);
                }
                if(allStackedEggs[Egg.GREEN]>0){
                    String output=""+allStackedEggs[Egg.GREEN];
                    g2d.setColor(eggColors[Egg.GREEN]);
                    g2d.drawString(output,ax+8,ay+13);
                }
            }
        }
    }
}
