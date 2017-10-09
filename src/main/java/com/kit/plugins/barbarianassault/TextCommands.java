package com.kit.plugins.barbarianassault;

import com.kit.core.Session;

import java.awt.*;
import java.util.Arrays;

class TextCommands {
    private BarbarianAssaultPlugin BA;
    private String message;
    private long drawUntilTime;

    TextCommands(BarbarianAssaultPlugin BA){
        this.BA=BA;
        drawUntilTime=0;
        message="";
    }

    void ProcessCommand(String message){
        //TODO: Put in try catch block
        String quickCommand=message.substring(0,2);
        switch(quickCommand){
            case "Rx":
                if(message.length()!=2){ return;}
                BA.points.setRole(-1);
                setMessage("Set role for points: Untracked",6000000000L);
                return;
            case "Ra":
                if(message.length()!=2){ return;}
                BA.points.setRole(Cons.A);
                setMessage("Set role for points: Attacker",6000000000L);
                return;
            case "Rc":
                if(message.length()!=2){ return;}
                BA.points.setRole(Cons.C);
                setMessage("Set role for points: Collector",6000000000L);
                return;
            case "Rd":
                if(message.length()!=2){ return;}
                BA.points.setRole(Cons.D);
                setMessage("Set role for points: Defender",6000000000L);
                return;
            case "Rh":
                if(message.length()!=2){ return;}
                BA.points.setRole(Cons.H);
                setMessage("Set role for points: Healer",6000000000L);
                return;
            case "P0":
                if(message.length()!=2){ return;}
                BA.points.setCurrentPoints(0,0,0,0);
                setMessage("Set current points: (A 0) (C 0) (D 0) (H 0)",6000000000L);
                return;
            case "Pa":
                if(":".equals(message.substring(2,3))){
                    int neededPoints=Integer.parseInt(message.substring(3));
                    BA.points.setCurrentPoint(Cons.A,neededPoints);
                    setMessage("Set current points: (A "+neededPoints+")",6000000000L);
                }
                return;
            case "Pc":
                if(":".equals(message.substring(2,3))){
                    int neededPoints=Integer.parseInt(message.substring(3));
                    BA.points.setCurrentPoint(Cons.C,neededPoints);
                    setMessage("Set current points: (C "+neededPoints+")",6000000000L);
                }
                return;
            case "Pd":
                if(":".equals(message.substring(2,3))){
                    int neededPoints=Integer.parseInt(message.substring(3));
                    BA.points.setCurrentPoint(Cons.D,neededPoints);
                    setMessage("Set current points: (D "+neededPoints+")",6000000000L);
                }
                return;
            case "Ph":
                if(":".equals(message.substring(2,3))){
                    int neededPoints=Integer.parseInt(message.substring(3));
                    BA.points.setCurrentPoint(Cons.H,neededPoints);
                    setMessage("Set current points: (H "+neededPoints+")",6000000000L);
                }
                return;
            case "P:":
                int[] numbers= Arrays.stream(message.substring(2).split(" ")).mapToInt(Integer::parseInt).toArray();
                if (numbers.length == 4) {
                    BA.points.setCurrentPoints(numbers[0],numbers[2],numbers[1],numbers[3]);
                    setMessage("Set current points: (A "+numbers[0]+") (D "+numbers[2]+") (C "+numbers[1]+") (H "+numbers[3]+")",6000000000L);
                }
                return;
            case "Ta":
                if(":".equals(message.substring(2,3))){
                    int neededPoints=Integer.parseInt(message.substring(3));
                    BA.points.setNeededPoint(Cons.A,neededPoints);
                    setMessage("Set needed points: (A "+neededPoints+")",6000000000L);
                }
                return;
            case "Tc":
                if(":".equals(message.substring(2,3))){
                    int neededPoints=Integer.parseInt(message.substring(3));
                    BA.points.setNeededPoint(Cons.C,neededPoints);
                    setMessage("Set needed points: (C "+neededPoints+")",6000000000L);
                }
                return;
            case "Td":
                if(":".equals(message.substring(2,3))){
                    int neededPoints=Integer.parseInt(message.substring(3));
                    BA.points.setNeededPoint(Cons.D,neededPoints);
                    setMessage("Set needed points: (D "+neededPoints+")",6000000000L);
                }
                return;
            case "Th":
                if(":".equals(message.substring(2,3))){
                    int neededPoints=Integer.parseInt(message.substring(3));
                    BA.points.setNeededPoint(Cons.H,neededPoints);
                    setMessage("Set needed points: (H "+neededPoints+")",6000000000L);
                } else if("t".equals(message.substring(2,3))){
                    if(message.length()!=3){ return;}
                    BA.points.setNeededPoints(265,270,270,275);
                    setMessage("Set needed points: (A 265) (D 270) (C 270) (H 275)",6000000000L);
                }
                return;
            case "T:":
                int[] numbers2= Arrays.stream(message.substring(2).split(" ")).mapToInt(Integer::parseInt).toArray();
                if (numbers2.length == 4) {
                    BA.points.setNeededPoints(numbers2[0],numbers2[2],numbers2[1],numbers2[3]);
                    setMessage("Set needed points: (A "+numbers2[0]+") (D "+numbers2[2]+") (C "+numbers2[1]+") (H "+numbers2[3]+")",6000000000L);
                }
                return;
            case "Tt":
                if(message.length()!=2){ return;}
                BA.points.setNeededPoints(375,365,370,360);
                BA.points.showEggsOn();
                setMessage("Set needed points: (A 375) (D 370) (C 365) (H 360) (show eggs)",6000000000L);
                return;
            case "Se":
                if(message.length()!=2){ return;}
                BA.points.showEggsToggle();
                setMessage("Toggle display needed eggs ("+(BA.points.showEggs()?"On":"Off")+")",6000000000L);
                return;
            case "Kh":
                if(message.length()!=2){ return;}
                BA.points.kandarinHardToggle();
                setMessage("Toggle kandarin hard ("+(BA.points.isKandarinHard()?"On":"Off")+")",6000000000L);
                return;

            default:
                return;
        }
    }

    void drawCommandMessage(Graphics2D g2d, long currentTime){
        if(currentTime<drawUntilTime){
            g2d.setFont(g2d.getFont().deriveFont(14.0f));
            g2d.setColor(Color.CYAN);
            g2d.drawString(message, 40,Session.get().client().getViewportHeight()/2-30);
        }
    }
    void setMessage(String message, long messageDuration){
        this.message=message;
        drawUntilTime=messageDuration+System.nanoTime();
    }
}
