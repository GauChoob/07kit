package com.kit.plugins.barbarianassault;

import com.kit.api.wrappers.Loot;
import com.kit.api.wrappers.Tile;
import com.kit.core.Session;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

class Food {
    private final BarbarianAssaultPlugin BA;
    private final Session session;

    private static final int checkDistance = 30;

    int[][] foodMap = new int[checkDistance * 2 + 1][checkDistance * 2 + 1];

    static boolean isFood(int id) {
        return ((id == Items.D_CRACKERS) || (id == Items.D_TOFU) || (id == Items.D_WORMS));
    }

    Food(BarbarianAssaultPlugin BA) {
        this.BA = BA;
        this.session = Session.get();
        resetFoodMap();
    }

    void resetFoodMap() {
        for (int i = 0; i < checkDistance * 2 + 1; i++) {
            for (int j = 0; j < checkDistance * 2 + 1; j++) {
                foodMap[i][j] = 0;
            }
        }
    }

    void drawGroundItems(Graphics2D g2d) {
        int x = session.player.getX();
        int y = session.player.getY();
        resetFoodMap();
        java.util.List<Loot> lootList = session.loot.find().distance(checkDistance).asList();
        for (Loot loot : lootList) {
            if(!Food.isFood(loot.getId())){continue;}
            int relativeX = loot.getX() - x + checkDistance;
            int relativeY = loot.getY() - y + checkDistance;
            foodMap[relativeX][relativeY]++;
        }

        g2d.setFont(g2d.getFont().deriveFont(14.0f));
        for (int i = 0; i < checkDistance * 2 + 1; i++) {
            for (int j = 0; j < checkDistance * 2 + 1; j++) {
                int absolutex = x + i - checkDistance;
                int absolutey = y + j - checkDistance;
                int foodN = foodMap[i][j];
                if (foodN > 0) {
                    Polygon tilePoly = new Tile(session, absolutex, absolutey, session.client().getPlane()).getPolygon();
                    int ax = Arrays.stream(tilePoly.xpoints).sum() / 4;
                    int ay = Arrays.stream(tilePoly.ypoints).sum() / 4;
                    g2d.setColor(new Color(255, 255, 255, 32));
                    g2d.fill(tilePoly);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(Integer.toString(foodN), ax + 2, ay);
                }
            }
        }
    }
}