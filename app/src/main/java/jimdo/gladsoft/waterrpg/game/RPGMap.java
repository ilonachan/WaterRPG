package jimdo.gladsoft.waterrpg.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.util.ArrayList;

import jimdo.gladsoft.waterrpg.game.maps.OaksLabMap;
import jimdo.gladsoft.waterrpg.game.maps.PalletTownMap;
import jimdo.gladsoft.waterrpg.game.maps.PirateIslandMap;

import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by osboxes on 7/18/17.
 */

public abstract class RPGMap {

//    int mapWidth, mapHeight;
protected int[][] defaultTilePattern;
    protected int[][] graphTileDef;
    protected int[][] tileBehaviorDef;
    protected TileEventHandler[][] tileInteractions;
    protected TileEventHandler[][] tileTryStepEvent;
    protected TileEventHandler[][] tileStepEvent;

    public static RPGMap[] allMaps = new RPGMap[] {new PalletTownMap(), new OaksLabMap(), new PirateIslandMap()};

    protected ArrayList<Entity> entities;

    public Bitmap fieldTilemapBitmap;
    public Bitmap fieldTilemapOverlayBitmap;

    public void drawToCanvas(Canvas canvas) {
        canvas.drawColor(0xff000000);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Rect screenRect = new Rect(0,0,width,height);

        int offsetX = (int) (screenRect.centerX()-32-GameLogic.playerData.getXPos()*64);
        int offsetY = (int) (screenRect.centerY()-32-GameLogic.playerData.getYPos()*64);

        switch(GameLogic.playerData.getMoveDir()) {
            case 0: offsetY -= ((GameLogic.playerData.getMoveTick()+1)%2)*32; break;
            case 1: offsetY += ((GameLogic.playerData.getMoveTick()+1)%2)*32; break;
            case 2: offsetX -= ((GameLogic.playerData.getMoveTick()+1)%2)*32; break;
            case 3: offsetX += ((GameLogic.playerData.getMoveTick()+1)%2)*32; break;
        }

        drawAllTiles(offsetX,offsetY,width,height,screenRect,canvas,false);

        GameLogic.playerData.drawInCanvas(canvas, screenRect.centerX()-32,screenRect.centerY()-40);

        for (Entity ent : entities) {
            int xPos = offsetX + 64*ent.getXPos();
            int yPos = offsetY + 64*ent.getYPos()-8;
            switch(ent.getFaceDir()) {
                case 0: yPos += ((ent.getMoveTick()+1)%2)*32; break;
                case 1: yPos -= ((ent.getMoveTick()+1)%2)*32; break;
                case 2: xPos += ((ent.getMoveTick()+1)%2)*32; break;
                case 3: xPos -= ((ent.getMoveTick()+1)%2)*32; break;
            }

            ent.drawInCanvas(canvas, xPos, yPos);
        }

        drawAllTiles(offsetX,offsetY,width,height,screenRect,canvas,true);
    }

    public void drawAllTiles(int offsetX, int offsetY, int width, int height, Rect screenRect, Canvas canvas, boolean overlay) {
        for(int j = min(1,(int) -floor(offsetY/64))-1; j < graphTileDef.length+height/64+2; j++) {
            for (int i = min(1,(int) -floor(offsetX/64))-1; i < graphTileDef[0].length+width/64+2; i++) {
                Rect tileRect = new Rect(64*i+offsetX, 64*j+offsetY, 64*i+64+offsetX, 64*j+64+offsetY);
                if(!tileRect.intersect(screenRect)) continue;
                int tileNum = 127;
                if(j < 0 || j >= graphTileDef.length || i < 0 || i >= graphTileDef[0].length) {
                    int xSize = defaultTilePattern[0].length,
                            ySize = defaultTilePattern   .length;
                    int xTile = i%xSize;
                    if(xTile < 0) xTile += xSize;
                    int yTile = j%ySize;
                    if(yTile < 0) yTile += ySize;
                    tileNum = defaultTilePattern[yTile][xTile];
                } else
                    tileNum = graphTileDef[j][i];
                canvas.drawBitmap(getBitmapForTileID(tileNum, overlay), null, tileRect, null);
            }
        }
    }

    private Bitmap getBitmapForTileID(int i, boolean overlay) {
        int x = i%8, y = i/8, size = fieldTilemapBitmap.getWidth()/8;
        return Bitmap.createBitmap(overlay?fieldTilemapOverlayBitmap:fieldTilemapBitmap, x*size + 0, y*size + 0, size, size);
    }

    private void loadTilemap(String id) {
        fieldTilemapBitmap = ((BitmapDrawable)GameLogic.context.getResources().getDrawable(GameLogic.context.getResources().getIdentifier("tilemap_"+id, "drawable", GameLogic.context.getPackageName()))).getBitmap();
        fieldTilemapOverlayBitmap = ((BitmapDrawable)GameLogic.context.getResources().getDrawable(GameLogic.context.getResources().getIdentifier("tilemap_"+id+"_overlay", "drawable", GameLogic.context.getPackageName()))).getBitmap();

    }

    public RPGMap() {
        loadTilemap(getMapName());

        initTiles();
        this.tileInteractions = new TileEventHandler[graphTileDef.length][graphTileDef[0].length];
        this.tileTryStepEvent = new TileEventHandler[graphTileDef.length][graphTileDef[0].length];
        this.tileStepEvent = new TileEventHandler[graphTileDef.length][graphTileDef[0].length];

        entities = new ArrayList<Entity>();
        loadEntities();

        addTileHandlers();
    }

    protected abstract void loadEntities();
    protected abstract void addTileHandlers();
    protected abstract void initTiles();
    protected abstract String getMapName();

    public boolean canEntityMoveTo(Entity entity, int tileX, int tileY) {
        if(tileX < 0 || tileY < 0 || tileX >= graphTileDef[0].length || tileY >= graphTileDef.length) return false;
        if(this.tileTryStepEvent[tileY][tileX] != null && !this.tileTryStepEvent[tileY][tileX].handle(GameLogic.playerData.getFaceDir())) return false;
        if(tileBehaviorDef[tileY][tileX] == 1) return false;
        if(tileBehaviorDef[tileY][tileX] == 4) return false;
        if(GameLogic.playerData.getXPos() == tileX && GameLogic.playerData.getYPos() == tileY) return false;
        for (Entity ent : entities) {
            if(ent.getXPos() == tileX && ent.getYPos() == tileY) return false;
        }
        return true;
    }

    public void notifyEntitySteppedOn(Entity ent, int tileX, int tileY) {
        if(ent != GameLogic.playerData) return;

        Log.v("RPGMap: handle Step","("+tileX+"|"+tileY+")");
        TileEventHandler handler = this.tileStepEvent[tileY][tileX];
        if(handler != null) handler.handle(GameLogic.playerData.getFaceDir());
    }

    public void entityInteractWith(int tileX, int tileY) {
        Log.v("RPGMap: handle Interact","("+tileX+"|"+tileY+")");
        for (Entity ent : entities) {
            if(ent.getXPos() == tileX && ent.getYPos() == tileY) {
                ent.handlePlayerInteract();
                return;
            }
        }
        TileEventHandler handler = this.tileInteractions[tileY][tileX];
        if(handler != null) handler.handle(GameLogic.playerData.getFaceDir());
    }

    public static RPGMap loadMap(int mapId) {
        if(mapId >= 0 && mapId < allMaps.length) return allMaps[mapId];
        return null;
    }

    public static final int mapCount = 2;

    public abstract String serializeFlags();

    public abstract void deserializeFlags(String text);

    public interface TileEventHandler {
        boolean handle(int direction);
    }
}
