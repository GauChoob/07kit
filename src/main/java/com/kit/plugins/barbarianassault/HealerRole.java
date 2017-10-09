package com.kit.plugins.barbarianassault;

import com.kit.Application;
import com.kit.api.event.MessageEvent;
import com.kit.api.util.PaintUtils;
import com.kit.api.wrappers.Entity;
import com.kit.api.wrappers.Npc;
import com.kit.api.wrappers.Tile;
import com.kit.core.Session;
import com.kit.game.engine.renderable.entity.INpc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

class HealerRole{
    private BarbarianAssaultPlugin BA;
    private Session session;
    private boolean drawEastieBlock;

    //NPC ids, max healers w9, coordinates of lobby area & wave rooms

    static final int MAX_HEALER_N=15;     //Should be 8 as that's the number of healers in wave 9 but I'm putting higher due to the additional healers bug for now
        //TODO: make private
    private static final long PoisonTickSafety=-250000000L; //Shift the spawn time and first poison time by this amount to protect against rounding errors when calculating expected death time

    static final int PoisonTickColors[][]={{43,22,0}, //1=red
            {43,22,0}, //2=orange
            {43,43,0}, //3=yellow
            {0,43,0}}; //4=green
        //TODO: Move to poisontick



    static final Color[] HPBar={new Color(34,177,76),new Color(136,0,21),new Color(0,255,255)}; //Green, red, cyan(unknown hp)


    HealerStackOrderHelper stackOrder;

    //private final Healer healers[]=new Healer[MAX_HEALER_N];  //List of spotted healers each wave
    //private int healercount;    //number of healers spotted so far


    private int prevTarget;     //previous tick if player was targetting mob
    private int prevInv;        //previous tick inventory count
    private boolean wrongfood;  //wrong food in chatbox


    HealerRole(BarbarianAssaultPlugin BA){
        this.BA=BA;
        this.session=Session.get();
        stackOrder =new HealerStackOrderHelper(BA);
        /*for (int i = 0; i < MAX_HEALER_N; i++) {
            healers[i]=new Healer(null, -1, false,0);
        }*/
        startedNewWave();
    }
    void startedNewWave(){
        drawEastieBlock=false;
        wrongfood=false;
        //healercount=0;
        prevTarget=-1;
        prevInv=-1;
        stackOrder.startedNewWave();
    }
    void Cycle(long waveProgressTime){

        if(waveProgressTime>20000000000L){
            drawEastieBlock=false;
        }

        stackOrder.unloadHealers();
        boolean checkForEastie=BA.gameState.getWave() < 3 && (waveProgressTime<12000000000L);

        INpc[] npcArray = session.client().getNpcs();       //Find all mobs and locate healers
        for (int uniqueid = 0; uniqueid < npcArray.length; uniqueid++) {    //apparently the npcarray might refer to a second mob somewhere but npc's wrapper hashcode should be more unique; I think npcarray though is fine for ba purposes
            if (npcArray[uniqueid] == null) {
                continue;
            }
            Npc npc = npcArray[uniqueid].getWrapper();

            if (checkForEastie && Penance.isRunner(npc)) {
                Tile mainTile=npc.getTile();
                Tile compare=BA.baTile.getTile(BATile.locationList.RUNNER_EAST);
                if(mainTile.getX()==compare.getX()&&mainTile.getY()==compare.getY()) {
                    drawEastieBlock = true;
                }
            }
            if (Penance.isHealer(npc)) {
                if(!stackOrder.findAndUpdate(uniqueid,npc,waveProgressTime)){
                    if(stackOrder.getHealerCount()<MAX_HEALER_N){
                        Healer newHealer=new Healer(npc,uniqueid,waveProgressTime+PoisonTickSafety);
                        stackOrder.AddNewHealer(stackOrder.getHealerCount(),newHealer);
                    }
                }
            }
        }
        stackOrder.UpdateLoadedHealerOrder();


        Entity targ = session.player.getInteractingEntity();
        int currentTarget;
        if (targ != null) {
            currentTarget = session.players.getLocal().getAssociatedEntity();
            //logger.info("Dist: " + Integer.toString(targ.distanceTo(player.getTile())) + "; Targ: " + players.getLocal().getAssociatedEntity() + "; Inv: " + inventory.getCount());
        } else {
            currentTarget = -1;
            //logger.info("Dist: null; Targ: null; Inv: " + inventory.getCount());
        }
        int currentInv = session.inventory.getCount();
        if(!wrongfood){
            if (prevInv - currentInv == 1) {
                int useTarget = -1;
                if (currentTarget > -1) {
                    useTarget = currentTarget;
                } else {
                    if (prevTarget > -1) {
                        useTarget = prevTarget;
                    }
                }
                if (useTarget > -1) {
                    Healer poisonedHealer=stackOrder.findLoadedHealer(useTarget);
                    if(poisonedHealer!=null){
                        if (poisonedHealer.poison == 0 && (waveProgressTime-poisonedHealer.firstpoisontime)> 2500000000L) {      //if it's been in the game for at least 2.5 seconds you missed the early poison. If you walk into its view you don't have time to poison in 2.5 seconds anyways
                            poisonedHealer.firstpoisontime = waveProgressTime+PoisonTickSafety;
                            //logger.info("Missed early poison!");
                        }
                        poisonedHealer.lastpoisontime = waveProgressTime;
                        poisonedHealer.poison++;
                        poisonedHealer.updateCurrentTimePoisonTick(waveProgressTime);
                    }
                }
            }
        } else {
            //logger.info("Wrongfood! Skipping");
            wrongfood=false;
        }
        prevTarget = currentTarget;
        prevInv = currentInv;
    }

    void PaintEvent(Graphics2D g2d, long waveProgressTime){
        //stackOrder.Debug(g2d);

        if(drawEastieBlock){
            if(BA.showHealerEasties){Runner.DrawEastieBlock(g2d,BA.baTile);}
        }
        if(BA.invGraphics.isInventoryOpen()){
            if(BA.showDestroyFoodBox){InventoryGraphics.DrawDestroyFoodBox(g2d);}
            if(BA.showHealerCallOverlay){BA.callWidget.drawInventoryFoodCall(g2d);}
        }
        stackOrder.updateModelVisibility(waveProgressTime);
        //can updated loaded healer order here if does not take too much processing time
        if(BA.showHealerInfobox){
            stackOrder.DrawAllHealerInfoBars(g2d,waveProgressTime);
            stackOrder.DrawMinimapHealers(g2d,waveProgressTime);
        }
        if(BA.showHealerSummary){stackOrder.DrawReserveTimes(g2d,waveProgressTime, BA.gameState.getWave());}
    }

    void MessageEvent(MessageEvent event){
        String message=event.getMessage();
        if(event.getType()== MessageEvent.Type.MESSAGE_SERVER && message.equals("That's the wrong type of poisoned food to use! Penalty!")){
            wrongfood=true;
        }
    }
}


//This class keeps track of what order the client saw healers, so that if healers happened to be stacked, we can refer to this object to determine what the supposed healer order is
//This class also draws infobars based on its own data and data from the Healer list
class HealerStackOrderHelper{
    private static final int infobar_height=8;  //Infobar height
    private static final int infobar_healer_number_width=11; //Width of header with healer id
    private static final int infobar_poison_width=16; //Poison tick info
    private static final long modelUpdateTimeFrequency=50000000L; //Check model hash every 50 ms (20 fps) because it appears that the model hash only changes every 20-25 ms or so (?40-50 fps)
    private final BarbarianAssaultPlugin BA;

    private int loadedhealercount;
    private int unloadedhealercount;
    private HealerStackOrderHelperUnit loadedstack[]=new HealerStackOrderHelperUnit[HealerRole.MAX_HEALER_N];  //List of spotted healers each wave sorted by top-to-bottom priority
    private HealerStackOrderHelperUnit unloadedstack[]=new HealerStackOrderHelperUnit[HealerRole.MAX_HEALER_N];  //List of healers not in range each wave
    private long lastModelUpdateTime;

    HealerStackOrderHelper(BarbarianAssaultPlugin BA){
        this.BA=BA;
        lastModelUpdateTime=0L;
        startedNewWave();
    }

    int getHealerCount(){
        return loadedhealercount+unloadedhealercount;
    }
    void updateModelVisibility(long waveProgressTime){
        if(waveProgressTime-lastModelUpdateTime>modelUpdateTimeFrequency){
            lastModelUpdateTime=waveProgressTime;
            for(int i=0;i<loadedhealercount;i++){
                loadedstack[i].healer.updateModelVisibility();
            }
        }
    }

    //Updates healer information for new tick. Returns false if healer is not known (i.e. new healer)
    boolean findAndUpdate(int uniqueid, Npc npc, long waveProgressTime){
        for (int j = 0; j < loadedhealercount; j++) {
            if (uniqueid == loadedstack[j].healer.uniqueid) {
                loadedstack[j].healer.loaded=true;
                loadedstack[j].healer.npc=npc;
                if(loadedstack[j].healer.updateHP(waveProgressTime)){
                    loadedstack[j].healer.getDeathTime(BA.gameState.getWave(),waveProgressTime);
                }
                return true;
            }
        }
        for (int j = 0; j < unloadedhealercount; j++) {
            if (uniqueid == unloadedstack[j].healer.uniqueid) {
                unloadedstack[j].healer.loaded=true;
                unloadedstack[j].healer.npc=npc;
                if(unloadedstack[j].healer.updateHP(waveProgressTime)){
                    unloadedstack[j].healer.getDeathTime(BA.gameState.getWave(),waveProgressTime);
                }
                return true;
            }
        }
        return false;
    }

    Healer findLoadedHealer(int uniqueid){
        for (int j = 0; j < loadedhealercount; j++) {
            if (uniqueid == loadedstack[j].healer.uniqueid) {
                return loadedstack[j].healer;
            }
        }
        return null;
    }

    void unloadHealers(){
        for (int j = 0; j < loadedhealercount; j++) {
            loadedstack[j].healer.loaded=false;
        }
        for (int j = 0; j < unloadedhealercount; j++) {
            unloadedstack[j].healer.loaded=false;
        }
    }

    //
    void DrawMinimapHealers(Graphics2D g2d, long waveProgressTime){
        for(int i=0;i<loadedhealercount;i++){
            if (!loadedstack[i].healer.loaded ||loadedstack[i].healer.deathcounter==-2) {
                continue;
            }
            if (loadedstack[i].healer.npc == null||loadedstack[i].healer.npc.unwrap()==null) {
                loadedstack[i].healer.loaded=false;
                continue;
            }
            Point mmLocation = Session.get().minimap.convert(loadedstack[i].healer.npc.getTile());
            String output=Healer.getHealerNumber(loadedstack[i].healern, BA.gameState.getWave());//+healers[i].getMaxCurHP(currentwave)+"/"+Healer.getMaxHP(currentwave);
            if(BA.showHealerAdvancedInfobox){
                if (waveProgressTime<60000000000L){
                    output+=" ("+loadedstack[i].healer.poison+")";
                }
                if (loadedstack[i].healer.deathcounter > 0) {
                    output+=String.format(" - %01d.%d",loadedstack[i].healer.deathcounter/10,(loadedstack[i].healer.deathcounter%10));
                }
            }
            if (mmLocation.x != -1 && mmLocation.y != -1) {
                g2d.setFont(g2d.getFont().deriveFont(10.0f));
                g2d.setColor(Healer.getHealerColor(loadedstack[i].healern, BA.gameState.getWave()));
                PaintUtils.drawString(g2d, output, mmLocation.x, mmLocation.y);
            }
        }
    }
    //Given a healer, determines which other healers are on the same tile, and calls DrawInfoBar to make a stack of infobars if the healer is the top of its tile
    void DrawAllHealerInfoBars(Graphics2D g2d, long waveprogresstime){
        for(int mainI=0;mainI<loadedhealercount;mainI++){
            if (!loadedstack[mainI].healer.loaded) {
                continue;
            }
            if (loadedstack[mainI].healer.npc == null||loadedstack[mainI].healer.npc.unwrap()==null) {
                loadedstack[mainI].healer.loaded=false;
                continue;
            }
            Tile basetile=loadedstack[mainI].healer.npc.getTile();
            HealerStackOrderHelperUnit healerprioritylistVisible[]=new HealerStackOrderHelperUnit[loadedhealercount];
            HealerStackOrderHelperUnit healerprioritylistInvisible[]=new HealerStackOrderHelperUnit[loadedhealercount];
            int hpl_n_visible=0;
            int hpl_n_invisible=0;
            for(int compareI=0;compareI<loadedhealercount;compareI++){
                if(loadedstack[compareI].healer.loaded&&basetile.getX()==loadedstack[compareI].healer.npc.getX()&&basetile.getY()==loadedstack[compareI].healer.npc.getY()){
                    if(compareI<mainI){
                        hpl_n_visible=0;  //Abort if it's not the top healer in a stack
                        hpl_n_invisible=0;
                        break;
                    }
                    if(loadedstack[compareI].healer.visible){
                        healerprioritylistVisible[hpl_n_visible]=loadedstack[compareI];
                        hpl_n_visible++;
                    } else {
                        healerprioritylistInvisible[hpl_n_invisible]=loadedstack[compareI];
                        hpl_n_invisible++;
                    }
                }
            }
            Point baseLocation=Session.get().viewport.convert(loadedstack[mainI].healer.npc.getLocalX(),loadedstack[mainI].healer.npc.getLocalY(),150);
            int y=baseLocation.y;
            if(baseLocation.x!=-1&&baseLocation.y!=-1){ //http://s15.zetaboards.com/CasualBA/single/?p=10210412&t=10255741
                for (int j = hpl_n_visible - 1; j >= 0; j--) {
                    boolean unclearStackOrder=false;
                    if (healerprioritylistVisible[j].uncertainty&&hasMultipleUncertainties(healerprioritylistVisible,hpl_n_visible,healerprioritylistVisible[j].uncertaintyid)) {
                        unclearStackOrder=true;
                    }
                    DrawInfoBar(g2d, healerprioritylistVisible[j], baseLocation.x - 20, y, waveprogresstime,unclearStackOrder);
                    y+=infobar_height;
                }
                for (int j = hpl_n_invisible - 1; j >= 0; j--) {
                    boolean unclearStackOrder=false;
                    if (healerprioritylistInvisible[j].uncertainty&&hasMultipleUncertainties(healerprioritylistInvisible,hpl_n_invisible,healerprioritylistInvisible[j].uncertaintyid)) {
                        unclearStackOrder=true;
                    }
                    DrawInfoBar(g2d, healerprioritylistInvisible[j], baseLocation.x - 20, y, waveprogresstime,unclearStackOrder);
                    y+=infobar_height;
                }
            }
        }
    }
    private boolean hasMultipleUncertainties(HealerStackOrderHelperUnit[] stack,int maxi, int uncertaintyId){
        int uncertaintyNumber=0;
        for(int i=0;i<maxi;i++){
            if(stack[i].uncertaintyid==uncertaintyId){
                uncertaintyNumber++;
                if(uncertaintyNumber>1){
                    return true;
                }
            }
        }
        return false;
    }
    //Draws the infobar for a given healer using info from Healer
    private void DrawInfoBar(Graphics2D g2d, HealerStackOrderHelperUnit helperUnit, int x, int y, long waveprogresstime, boolean unclearStackOrder){
        //Background box
        g2d.setColor(Healer.getHealerColor(helperUnit.healern, BA.gameState.getWave()));
        if(BA.showHealerAdvancedInfobox){
            g2d.fillRect(x, y, infobar_healer_number_width + 1 + 30 + 1, infobar_height);
        } else {
            g2d.fillRect(x,y,infobar_healer_number_width+1,infobar_height);
        }

        //Hp bar
        int hpwidth=(helperUnit.healer.predictedhpfraction!=-1)?helperUnit.healer.predictedhpfraction:helperUnit.healer.hpfraction;
        int nextpoistick=helperUnit.healer.currentpoisontick;
        int poisdamage=PoisonTick.PoisonDamage(nextpoistick);
        int poisdmgremain=PoisonTick.PoisonRemainingSameNumber(nextpoistick);
        if(hpwidth==-1||hpwidth==30||nextpoistick==0){
            g2d.setColor(HealerRole.HPBar[2]);
        } else {
            g2d.setColor(HealerRole.HPBar[0]);
        }
        if(hpwidth==-1){
            hpwidth=30;
        }
        if(BA.showHealerAdvancedInfobox){
            g2d.fillRect(x+infobar_healer_number_width+1,y+1,hpwidth,infobar_height-2);
            g2d.setColor(HealerRole.HPBar[1]);
            g2d.fillRect(x+infobar_healer_number_width+1+hpwidth,y+1,30-hpwidth,infobar_height-2);

            //Poison Box
            if (nextpoistick > 0) {
                g2d.setColor(new Color(HealerRole.PoisonTickColors[poisdamage-1][0]*poisdmgremain,HealerRole.PoisonTickColors[poisdamage-1][1]*poisdmgremain,HealerRole.PoisonTickColors[poisdamage-1][2]*poisdmgremain));
            } else {
                g2d.setColor(HealerRole.HPBar[2]);
            }
            g2d.fillRect(x+infobar_healer_number_width+1+30+1,y,infobar_poison_width,infobar_height);
        }

        //Healer Number
        g2d.setFont(g2d.getFont().deriveFont((float)(infobar_height+1)));
        g2d.setColor(Application.COLOUR_SCHEME.getText());
        if(Healer.isReserve(helperUnit.healern, BA.gameState.getWave())){
            g2d.drawString(Healer.getHealerNumber(helperUnit.healern, BA.gameState.getWave()), x, y+infobar_height-1);
        } else {
            g2d.drawString(Healer.getHealerNumber(helperUnit.healern, BA.gameState.getWave()), x+4, y+infobar_height-1);
        }

        if (BA.showHealerAdvancedInfobox) {
            //No. poisons + death timer
            String output="";
            if(waveprogresstime<60000000000L){
                output+="("+helperUnit.healer.poison+") ";
            }
            if (helperUnit.healer.deathcounter > 0) {
                output+=String.format("%01d",helperUnit.healer.deathcounter/10);
            }
            g2d.drawString(output, x+infobar_healer_number_width+1+4, y+infobar_height-1);

            //Poison Box Number
            if(nextpoistick>0){
                g2d.drawString(Integer.toString(poisdamage)+"x"+poisdmgremain, x+infobar_healer_number_width+1+30+1+1, y+infobar_height-1);
            }
        }

        //? if uncertainty
        if (unclearStackOrder) {
            g2d.drawString("?", x-10, y+infobar_height-1);
        }
        return;
    }
    //Checks all healers and updates the priority order for stacked healers based on the healers entering client visibility and by getting the top npc of each stack
    void UpdateLoadedHealerOrder(){
        HealerStackOrderHelperUnit tloadedstack[]=new HealerStackOrderHelperUnit[HealerRole.MAX_HEALER_N];
        HealerStackOrderHelperUnit tunloadedstack[]=new HealerStackOrderHelperUnit[HealerRole.MAX_HEALER_N];
        int loadedn=0;
        int unloadedn=0;
        int i=0;
        int uncertaintyCount=0;
        while(i<loadedhealercount){
            if (loadedstack[i].uncertainty) {
                int unclength=loadedstack[i].uncertaintylength;
                LoadUnloadN LUN=ResolveUnknownStackOrder(i, loadedstack, loadedstack[i].uncertaintylength,tloadedstack,loadedn,tunloadedstack,unloadedn,uncertaintyCount);
                uncertaintyCount++;
                loadedn+=LUN.loadn;
                unloadedn+=LUN.unloadn;
                i+= unclength;
            } else {
                if (loadedstack[i].healer.loaded) {
                    tloadedstack[loadedn]= loadedstack[i];
                    i++;
                    loadedn++;
                    //checked, works
                }else{
                    //confirmed works
                    loadedstack[i].uncertainty=false;
                    tunloadedstack[unloadedn]= loadedstack[i];
                    unloadedn++;
                    i++;
                }
            }
        }

        //All the unloaded->loaded healers as well as any newly spawned healers
        LoadUnloadN LUN=ResolveUnknownStackOrder(0,unloadedstack,unloadedhealercount,tloadedstack,loadedn,tunloadedstack,unloadedn,uncertaintyCount);
        loadedn+=LUN.loadn;
        unloadedn+=LUN.unloadn;

        loadedhealercount=loadedn;
        unloadedhealercount=unloadedn;
        loadedstack=tloadedstack;
        unloadedstack=tunloadedstack;
    }
    //Adds a newly spawned healer to the list. UpdateLoadedHealerOrder must be called right after to include this healer
    void AddNewHealer(int i, Healer healer){
        unloadedstack[unloadedhealercount]=new HealerStackOrderHelperUnit(i,healer);
        unloadedhealercount++;
    }
    //Resets values in preparation for next wave
    void startedNewWave(){
        loadedhealercount=0;
        unloadedhealercount=0;
        lastModelUpdateTime=0L;
    }
    //Called by UpdateLoadedHealerOrder. Takes all healers that were discovered on the same tick, and tried to glean info to determine which healer should be top priority, and updates list accordingly
    private LoadUnloadN ResolveUnknownStackOrder(int basei, HealerStackOrderHelperUnit[] stack, int uncertlength, HealerStackOrderHelperUnit[] loadstack, int loadedn, HealerStackOrderHelperUnit[] unloadstack, int unloadedn, int uncertaintyCount){
        int addloadn=0;
        int addunloadn=0;
        int totalnewloaded=0;
        int visibles=0;
        boolean queueSizeZero=true;
        boolean sametile=true;
        Tile basetile=null;
        for(int i=0;i<uncertlength;i++){
            if(stack[basei+i].healer.loaded){
                totalnewloaded++;
                if (basetile == null) {
                    basetile=stack[basei+i].healer.npc.getTile();
                } else {
                    if(!(basetile.getX()==stack[basei+i].healer.npc.getX()&&basetile.getY()==stack[basei+i].healer.npc.getY())){
                        sametile=false;
                    }
                }
                if(stack[basei+i].healer.npc.getQueueSize()!=0){
                    queueSizeZero=false;
                }
                if(stack[basei+i].healer.visible){
                    visibles++;
                }
            } else {
                stack[basei+i].uncertainty=false;
                stack[basei+i].uncertaintyid=-1;
                unloadstack[unloadedn+addunloadn]=stack[basei+i];
                addunloadn++;
            }
        }
        if (totalnewloaded == 1) {
            for(int i=0;i<uncertlength;i++){
                if(stack[basei+i].healer.loaded){
                    stack[basei+i].uncertainty=false;
                    stack[basei+i].uncertaintyid=-1;
                    loadstack[loadedn+0]=stack[basei+i];
                    addloadn=1;
                    break;
                }
            }
        } else if (totalnewloaded>1){
            if(visibles==1&&sametile&&queueSizeZero){
                if (totalnewloaded == 2) {
                    for(int i=0;i<uncertlength;i++){
                        if(stack[basei+i].healer.loaded){
                            stack[basei+i].uncertainty=false;
                            stack[basei+i].uncertaintyid=-1;
                            if(stack[basei+i].healer.visible){
                                loadstack[loadedn+0]=stack[basei+i];
                            } else {
                                loadstack[loadedn+1]=stack[basei+i];
                            }
                        }
                    }
                    addloadn=2;
                } else if(totalnewloaded>2){
                    for(int i=0;i<uncertlength;i++){
                        if(stack[basei+i].healer.loaded){
                            if(stack[basei+i].healer.visible){
                                stack[basei+i].uncertainty=false;
                                stack[basei+i].uncertaintyid=-1;
                                loadstack[loadedn+0]=stack[basei+i];
                            } else {
                                stack[basei+i].uncertainty=true;
                                stack[basei+i].uncertaintyid=uncertaintyCount;
                                stack[basei+i].uncertaintylength=totalnewloaded-1;
                                loadstack[loadedn+1+addloadn]=stack[basei+i];
                                addloadn+=1;
                            }
                        }
                    }
                    addloadn+=1;
                }
            } else {
                for(int i=0;i<uncertlength;i++){
                    if(stack[basei+i].healer.loaded){
                        stack[basei+i].uncertainty=true;
                        stack[basei+i].uncertaintyid=uncertaintyCount;
                        stack[basei+i].uncertaintylength=totalnewloaded;
                        loadstack[loadedn+addloadn]=stack[basei+i];
                        addloadn+=1;
                    }
                }
            }
        }
        return new LoadUnloadN(addloadn,addunloadn);
    }
    void DrawReserveTimes(Graphics2D g2d, long waveProgressTime, int wave){
        int reserves=Healer.getTotalReserveCount(wave);
        ArrayList<HealerStackOrderHelperUnit> drawList=new ArrayList<HealerStackOrderHelperUnit>();
        for(int i=0;i<loadedhealercount;i++){
            drawList.add(loadedstack[i]);
        }
        for(int i=0;i<unloadedhealercount;i++){
            drawList.add(unloadedstack[i]);
        }
        Collections.sort(drawList);
        int i=0;
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        for(HealerStackOrderHelperUnit helperUnit: drawList){
            String output=Healer.getHealerNumber(helperUnit.healern, wave);
            if(i<reserves){
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.CYAN);
            }
            i++;
            if(helperUnit.healer.confirmedDead){
                continue;
            }
            if(helperUnit.healer.deathcounter!=-1){
                if(helperUnit.healer.deathcounter==-2||(helperUnit.healer.deathcounter<(System.nanoTime()-BA.waveRoundData.getWaveStartTime())/100000000L&&!helperUnit.healer.loaded)) {
                    g2d.setColor(Color.GRAY);
                } else {
                    output+=String.format(" - %01d.%d",helperUnit.healer.deathcounter/10,(helperUnit.healer.deathcounter%10));
                }
            }
            g2d.drawString(output/*+" | "+i*/, 10, 100+12*helperUnit.healern);  //TEST TODO TEST TODO
        }

    }

    void Debug(Graphics2D g2d){
        Debug(g2d,10,100);
    }
    void Debug(Graphics2D g2d, int x, int y){
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        g2d.setColor(Color.GREEN);
        g2d.drawString("HealerStackOrderHelper Debug",x,y);
        for(int i=0;i<loadedhealercount;i++){
            String output="#"+(i+1)+": "+Healer.getHealerNumber(loadedstack[i].healern, BA.gameState.getWave());
            if(loadedstack[i].uncertainty){
                g2d.setColor(Color.YELLOW);
                output+="(+"+loadedstack[i].uncertaintylength+")";
            } else {
                g2d.setColor(Color.GREEN);
            }
            g2d.drawString(output, x, y+12*(i+1));
        }
        g2d.setColor(Color.GRAY);
        for(int i=0;i<unloadedhealercount;i++){
            String output=""+(unloadedstack[i].healern+1);
            g2d.drawString(output, x, y+12*(loadedhealercount+i+1));
        }

        //Modified DrawReserveTimes. Untested Debug TODO
        int reserves=Healer.getTotalReserveCount(BA.gameState.getWave());
        ArrayList<HealerStackOrderHelperUnit> drawList=new ArrayList<HealerStackOrderHelperUnit>();
        for(int i=0;i<loadedhealercount;i++){
            drawList.add(loadedstack[i]);
        }
        for(int i=0;i<unloadedhealercount;i++){
            drawList.add(unloadedstack[i]);
        }
        Collections.sort(drawList);
        int i=0;
        g2d.setFont(g2d.getFont().deriveFont(10.0f));
        for(HealerStackOrderHelperUnit helperUnit: drawList){
            String output=Healer.getHealerNumber(helperUnit.healern, BA.gameState.getWave());
            if(i<reserves){
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.CYAN);
            }
            i++;
            if(helperUnit.healer.confirmedDead){
                continue;
            }
            if(helperUnit.healer.deathcounter!=-1){
                if(helperUnit.healer.deathcounter==-2||(helperUnit.healer.deathcounter<(System.nanoTime()-BA.waveRoundData.getWaveStartTime())/100000000L&&!helperUnit.healer.loaded)) {
                    output += "|" + helperUnit.healer.deathcounter;
                    g2d.setColor(Color.GRAY);
                } else {
                    output+=String.format(" - %01d.%d",helperUnit.healer.deathcounter/10,(helperUnit.healer.deathcounter%10));
                }
            }
            g2d.drawString(output, x+50, y+12*helperUnit.healern);
        }

    }
    //Representation of single healer with info about uncertainty towards stack order
    class HealerStackOrderHelperUnit implements Comparable<HealerStackOrderHelperUnit>{
        int healern;
        Healer healer;
        boolean uncertainty;
        int uncertaintylength;
        int uncertaintyid;  //only uncertain with others of same id
        HealerStackOrderHelperUnit(int Healern, Healer healer){
            healern=Healern;
            this.healer=healer;
            uncertainty=false;
        }
        @Override
        public int compareTo(HealerStackOrderHelperUnit comparison){
            if(this.healer.deathcounter==-1){
                return 1000000;
            }
            if(this.healer.deathcounter==-2){
                return -1000000;
            }
            return this.healer.deathcounter-comparison.healer.deathcounter;
        }
    }
    //Used in ResolveUnknownStackOrder to tell UpdateLoadedHealerOrder how many healers were put into each stack
    class LoadUnloadN{
        int loadn;
        int unloadn;
        LoadUnloadN(int l,int ul){
            loadn=l;
            unloadn=ul;
        }
    }
}
