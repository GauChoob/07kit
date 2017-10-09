package com.kit.plugins.barbarianassault;

import com.kit.api.wrappers.Npc;

class Penance {
    private static final int FIGHTER_ID_W1=5739;
    private static final int FIGHTER_ID_W9=5747;
    private static final int RUNNER_ID_W1=5748;
    private static final int RUNNER_ID_W9=5756;
    private static final int RANGER_ID_W1=5757;
    private static final int RANGER_ID_W9=5765;
    private static final int HEALER_ID_W1=5766;
    private static final int HEALER_ID_W9=5774;

    static boolean isFighter(Npc mob){
        int id=mob.getId();
        return id >= FIGHTER_ID_W1 && id <= FIGHTER_ID_W9;
    }
    static boolean isRunner(Npc mob){
        int id=mob.getId();
        return id >= RUNNER_ID_W1 && id <= RUNNER_ID_W9;
    }
    static boolean isRanger(Npc mob){
        int id=mob.getId();
        return id >= RANGER_ID_W1 && id <= RANGER_ID_W9;
    }
    static boolean isHealer(Npc mob){
        int id=mob.getId();
        return id >= HEALER_ID_W1 && id <= HEALER_ID_W9;
    }
    static boolean isCombatMob(Npc mob){
        int id=mob.getId();
        return (id >= FIGHTER_ID_W1 && id <= FIGHTER_ID_W9) || (id >= RANGER_ID_W1 && id <= RANGER_ID_W9);
    }
}
