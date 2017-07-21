package jimdo.gladsoft.waterrpg.game;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import jimdo.gladsoft.waterrpg.R;

/**
 * Created by osboxes on 7/18/17.
 */

public class PlayerData extends Entity{
    public String playerName;
    public int mapId;
    public boolean isFemale;

    public PlayerData(int map, int x, int y, int dir, String name, boolean female){
        super(x,y,dir);
        this.mapId = map;
        this.setMoveTick(-1);
        this.playerName = name;
        this.isFemale = female;

        reloadBitmap();
    }

    public Bitmap loadBitmap(){
        return ((BitmapDrawable)GameLogic.context.getResources().getDrawable(isFemale?R.drawable.ent_player_f:R.drawable.ent_player)).getBitmap();
    }
    @Override
    public Bitmap getEntitySpriteAtRowAndCol(int row, int col) {
//        if(isFemale) return Bitmap.createBitmap(this.getBitmap(), 22*col, 22*row, 22, 22);
        if(isFemale) return Bitmap.createBitmap(this.getBitmap(), 44*col, 44*row, 44, 44);
//        return Bitmap.createBitmap(this.getBitmap(), 25*col, 25*row, 25, 25);
        return Bitmap.createBitmap(this.getBitmap(), 50*col, 50*row, 50, 50);
    }

    @Override
    public void handlePlayerInteract() {
        Log.wtf("Player","...Something has gone wrong if I should interact with myself...");
    }


    public void interact() {
        int i = getXPos(), j = getYPos();
        switch(getFaceDir()) {
            case 0: j--; break;
            case 1: j++; break;
            case 2: i--; break;
            case 3: i++; break;
        }

        GameLogic.aktuelleKarte.entityInteractWith(i, j);
    }

    @Override
    public void tick8persec(int tickNum){
        super.tick8persec(tickNum);

        int i = getXPos(), j = getYPos();
        switch(getFaceDir()) {
            case 0: j--; break;
            case 1: j++; break;
            case 2: i--; break;
            case 3: i++; break;
        }

        if(GameLogic.aktuelleKarte.tileInteractions[j][i] == null)
            GameLogic.context.ctrls[0].setImageDrawable(GameLogic.context.getResources().getDrawable(R.drawable.noaction));
        else
            GameLogic.context.ctrls[0].setImageDrawable(GameLogic.context.getResources().getDrawable(R.drawable.action_generic));

        for(Entity e : GameLogic.aktuelleKarte.entities) {
            if(e.getXPos() == i && e.getYPos() == j) {
                GameLogic.context.ctrls[0].setImageDrawable(GameLogic.context.getResources().getDrawable(R.drawable.talk));
                break;
            }
        }
    }
}
