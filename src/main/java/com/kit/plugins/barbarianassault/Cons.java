package com.kit.plugins.barbarianassault;

class Cons {
    static final int A=0;
    static final int C=1;
    static final int D=2;
    static final int H=3;

    static final int A_CONTROLLED_BRONZE_WIND=1;
    static final int A_ACCURATE_IRON_WATER=2;
    static final int A_AGGRESSIVE_STEEL_EARTH=3;
    static final int A_DEFENSIVE_MITHRIL_FIRE=4;

    static String Role(int role){
        switch(role){
            case 0:
                return "A";
            case 1:
                return "C";
            case 2:
                return "D";
            case 3:
                return "H";
            default:
                return "";
        }
    }

    private Cons(){
        throw new AssertionError();
    }
}

class Items {
    static final int SCROLL=10512;

    static final int D_CRACKERS=10513;
    static final int D_TOFU=10514;
    static final int D_WORMS=10515;

    static final int A_HORN_LVL_1=10516; //The attacker's horn correlates to your attack level. Might be the source of the +5 boost
    static final int A_HORN_LVL_2=10517;
    static final int A_HORN_LVL_3=10518;
    static final int A_HORN_LVL_4=10519;
    static final int A_HORN_LVL_5=10520;

    static final int C_BAG_LVL_1=10521;
    static final int C_BAG_LVL_2=10522;
    static final int C_BAG_LVL_3=10523;
    static final int C_BAG_LVL_4=10523;
    static final int C_BAG_LVL_5=10524;

    static final int H_HORN_LVL_1=10526; //The healer's horn correlates to your healer level. Might be in charge of determining how much you heal by
    static final int H_HORN_LVL_2=10527;
    static final int H_HORN_LVL_3=10528;
    static final int H_HORN_LVL_4=10529;
    static final int H_HORN_LVL_5=10530;

    static final int C_GREEN_EGG=10531;
    static final int C_RED_EGG=10532;
    static final int C_BLUE_EGG=10533;

    static final int Q_YELLOW_EGG=10534;
    static final int Q_POISONED_EGG=10535;
    static final int Q_SPIKED_POISONED_EGG=10536;
    static final int Q_OMEGA_EGG=10537;

    static final int D_HORN=10538;

    static final int H_P_TOFU=10539;
    static final int H_P_WORMS=10540;
    static final int H_P_MEAT=10541;

    static final int H_VIAL_4=10542;
    static final int H_VIAL_3=10543;
    static final int H_VIAL_2=10544;
    static final int H_VIAL_1=10545;
    static final int H_VIAL_0=10546;

    static final int I_HEALER_HAT=10547;
    static final int I_FIGHTER_HAT=10548;
    static final int I_RUNNER_HAT=10549;
    static final int I_RANGER_HAT=10550;
    static final int I_FIGHTER_TORSO=10551;
    static final int I_RUNNER_BOOTS=10552;
    static final int I_PENANCE_GLOVES=10553;
    static final int I_PENANCE_SKIRT=10555;

    static final int A_ICON=10556;  //cape slot
    static final int C_ICON=10557;
    static final int D_ICON=10558;
    static final int H_ICON=10559;

    static final int C_HORN=10560;

    static final int A_SPIKES=10561;

    static final int Q_HELP_BOOK=10562;

    static final int NO_EGGS=10563; //Black egg in egg cannon interface

    static final int I_GRANITE_BODY=10564;
    static final int I_GRANITE_BODY_NOTED=10565;

    static final int PET_PENANCE_QUEEN=12703;

    static final int D_HAMMER=2347;
    static final int D_LOGS=1511;

    private Items(){
        throw new AssertionError();
    }
}
