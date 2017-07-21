package jimdo.gladsoft.waterrpg.game.maps;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import jimdo.gladsoft.waterrpg.AskBoxProceedHandle;
import jimdo.gladsoft.waterrpg.TextBoxProceedHandle;
import jimdo.gladsoft.waterrpg.R;
import jimdo.gladsoft.waterrpg.game.Entity;
import jimdo.gladsoft.waterrpg.game.GameLogic;
import jimdo.gladsoft.waterrpg.game.RPGMap;

/**
 * Created by osboxes on 7/18/17.
 */

public class OaksLabMap extends RPGMap{

    static boolean plrCanLeave;
    static boolean oakRotates;

    @Override
    protected void loadEntities() {
        this.entities.add(new Entity(5,2,1) {
            @Override
            protected Bitmap loadBitmap() {
                return ((BitmapDrawable)GameLogic.context.getResources().getDrawable(R.drawable.ent_oak)).getBitmap();
            }

            @Override
            public Bitmap getEntitySpriteAtRowAndCol(int row, int col) {
//                return Bitmap.createBitmap(this.getBitmap(), 22*col, 22*row, 22, 22);
                return Bitmap.createBitmap(this.getBitmap(), 44*col, 44*row, 44, 44);
            }

            @Override
            public void tick8persec(int tickNum) {
                if(oakRotates) {
                    if(getXPos() == 5 && getYPos() == 2) setMoveDir(1);
                    if(getXPos() == 5 && getYPos() == 4) setMoveDir(3);
                    if(getXPos() == 7 && getYPos() == 4) setMoveDir(0);
                    if(getXPos() == 7 && getYPos() == 2) setMoveDir(2);
                } else {
                    this.setMoveDir(-1);
                }

                super.tick8persec(tickNum);
            }

            @Override
            public void handlePlayerInteract() {
                oakRotates = false;

                switch(GameLogic.playerData.getFaceDir()){
                    case 0: this.setFaceDir(1); break;
                    case 1: this.setFaceDir(0); break;
                    case 2: this.setFaceDir(3); break;
                    case 3: this.setFaceDir(2); break;
                }

                GameLogic.context.showTextBox("Prof. Oak", "Congratulations, " + GameLogic.playerData.playerName + "!\nYou found the secret treasure lot!\nAll the chocolate coins are yours!", new TextBoxProceedHandle() {
                    @Override
                    public void handle() {

                        GameLogic.context.showTextBox("Prof.Oak", "(....which is few, considering I\nlocked myself in and got hungry.....)", new TextBoxProceedHandle() {
                            @Override
                            public void handle() {

                                oakRotates = true;
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    protected void addTileHandlers() {
        /*
        this.tileInteractions[7][4] = new TileEventHandler(){
            @Override
            public boolean handle(int direction) { if(direction!=0) return false; GameLogic.context.showTextBox("",GameLogic.playerData.playerName+"'s home"); return true; }
        };
        this.tileInteractions[11][9] = new TileEventHandler(){
            @Override
            public boolean handle(int direction) { if(direction!=0) return false; GameLogic.context.showTextBox("", "Pirate Bay!\nTreasured dreams lie here"); return true; }
        };
        this.tileInteractions[7][13] = new TileEventHandler(){
            @Override
            public boolean handle(int direction) { if(direction!=0) return false; GameLogic.context.showTextBox("","Gary's home"); return true; }
        };
        */

        this.tileStepEvent[13][6] = new TileEventHandler(){
            @Override
            public boolean handle(int direction) { GameLogic.warpPlayerToPosition(2,30,23); return true; }
        };

//        this.tileTryStepEvent[13][6] = new TileEventHandler(){
//            @Override
//            public boolean handle(int direction) { if(plrCanLeave) GameLogic.warpPlayerToPosition(0,16,14); else GameLogic.context.showTextBox("","Wait, "+GameLogic.playerData.playerName+"! Don't go yet!"); return false; }
////            public boolean handle(int direction) { if(plrCanLeave) GameLogic.warpPlayerToPosition(2,30,23); else GameLogic.context.showTextBox("","Wait, "+GameLogic.playerData.playerName+"! Don't go yet!"); return false; }
//        };

    }

    @Override
    protected void initTiles() {
        this.defaultTilePattern = new int[][] {{50}};
        this.graphTileDef = new int[][]
                {{0, 1, 1, 2, 1, 1, 3, 3, 4, 5, 6, 5, 6},
                {7, 8, 9, 10, 11, 12, 13, 13, 14, 15, 16, 15, 16},
                {17, 18, 19, 20, 19, 20, 21, 21, 21, 22, 23, 22, 23},
                {24, 25, 26, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27},
                {24, 28, 29, 27, 27, 27, 27, 27, 30, 31, 32, 27, 27},
                {33, 34, 35, 27, 27, 27, 27, 27, 36, 37, 38, 27, 27},
                {33, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27},
                {39, 40, 41, 40, 41, 27, 27, 27, 40, 41, 40, 41, 41},
                {15, 15, 16, 15, 16, 27, 27, 27, 15, 16, 15, 16, 16},
                {42, 22, 23, 22, 23, 27, 27, 27, 22, 23, 22, 23, 23},
                {33, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27},
                {43, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 44},
                {45, 27, 27, 27, 27, 46, 47, 48, 27, 27, 27, 27, 49},
                {50, 50, 50, 50, 50, 51, 52, 53, 50, 50, 50, 50, 50}};
        this.tileBehaviorDef = new int[][]
                {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0},
                {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1}};
    }

    @Override
    protected String getMapName() {
        return "oaks_lab";
    }

    @Override
    public String serializeFlags() {
        return (plrCanLeave?"1":"0")+(oakRotates?"1":"0");
    }

    @Override
    public void deserializeFlags(String text) {
        if(text == null) text = "01";
        plrCanLeave = text.startsWith("1");
        oakRotates = text.endsWith("1");
    }
}
