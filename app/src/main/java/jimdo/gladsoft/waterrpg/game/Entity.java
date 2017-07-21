package jimdo.gladsoft.waterrpg.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import jimdo.gladsoft.waterrpg.R;

/**
 * Created by osboxes on 7/18/17.
 */

public abstract class Entity {
    private int xPos, yPos;
    private int facingDirection;
    public int moveRequest;
    private int moveTick;
    private Bitmap entBitmap;

    public Entity(int x, int y, int dir){
        setPosition(x,y);
        setFaceDir(1);
        setMoveTick(-1);

        this.moveRequest = -1;

        reloadBitmap();
    }

    public void reloadBitmap(){
        entBitmap = loadBitmap();
    }

    protected abstract Bitmap loadBitmap();

    public Bitmap getBitmap() {
        return entBitmap;
    }

    public void drawInCanvas(Canvas canvas, int x, int y) {
        int row = 0;
        switch(facingDirection) {
            case 0: row = 1; break;
            case 1: row = 0; break;
            case 2: row = 2; break;
            case 3: row = 3; break;
        }
        int col = 0;
        switch(moveTick) {
            case -1: col = 0; break;
            case  0: col = 1; break;
            case  1: col = 0; break;
            case  2: col = 2; break;
        }

        canvas.drawBitmap(getEntitySpriteAtRowAndCol(row,col), null, new Rect(x,y,x+64,y+64), null);
//        canvas.drawBitmap(GameLogic.playerData.getBitmap(), new Rect(20,10,88,114), new Rect(x,y,x+45,y+65), null);
    }

    public abstract Bitmap getEntitySpriteAtRowAndCol(int row, int col);

    public void setFaceDir(int i) {
        if(i >= 0 && i < 4) facingDirection = i;
    }

    public int getFaceDir() {
        return facingDirection;
    }

    public void setPosition(int x, int y) {
        xPos=x;
        yPos=y;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setMoveTick(int moveTick) {
        this.moveTick = moveTick;
    }

    public int getMoveTick() {
        return moveTick;
    }

    public void move() {
        int i = xPos, j = yPos;
        switch(facingDirection) {
            case 0: j--; break;
            case 1: j++; break;
            case 2: i--; break;
            case 3: i++; break;
        }

        if(GameLogic.aktuelleKarte.canEntityMoveTo(this,i,j)) {
            setPosition(i,j);
            GameLogic.aktuelleKarte.notifyEntitySteppedOn(this,i,j);
        } else {
            setMoveTick(-1);
        }

        i = getXPos(); j = getYPos();
        switch(getFaceDir()) {
            case 0: j--; break;
            case 1: j++; break;
            case 2: i--; break;
            case 3: i++; break;
        }
    }

    public void setMoveDir(int i) {
        moveRequest = i;
    }

    public int getMoveDir() {
        return moveRequest;
    }

    public void tick8persec(int tickNum) {
        if(tickNum%2 == 1) return;
        Log.v("GameLogic: Tick "+tickNum,"MoveTick: "+getMoveTick());
        if((getMoveTick()+2)%2 == 1) {
            Log.v("GameLogic: Tick "+tickNum,"Valid Tick for movement change; applying "+moveRequest);
            setFaceDir(moveRequest);
            if (moveRequest == -1) setMoveTick(-1);
            else setMoveTick((getMoveTick()+1)%4);
            Log.v("GameLogic: Tick "+tickNum,"MoveTick: "+getMoveTick());

            if(getMoveTick() != -1) {
                Log.v("GameLogic: Tick "+tickNum,"Moving Player");
                move();
            }

            GameLogic.context.mCanvasView.postInvalidate();
            return;
        }
        if(getMoveTick() != -1) {
            setMoveTick((getMoveTick()+1)%4);
            Log.v("GameLogic: Tick "+tickNum,"Increasing Move Tick");
        }

//        context.ctrls[0].setText("("+playerData.getXPos()+"|"+playerData.getYPos()+")");
        GameLogic.context.mCanvasView.postInvalidate();
    }

    public abstract void handlePlayerInteract();
}
