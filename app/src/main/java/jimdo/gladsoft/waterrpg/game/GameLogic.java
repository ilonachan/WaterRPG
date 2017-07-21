package jimdo.gladsoft.waterrpg.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import jimdo.gladsoft.waterrpg.FullscreenActivity;
import jimdo.gladsoft.waterrpg.R;
import jimdo.gladsoft.waterrpg.game.RPGMap;

/**
 * Created by osboxes on 7/18/17.
 */

public class GameLogic {
    public static FullscreenActivity context;
    public static int tickNum;

    public static RPGMap aktuelleKarte;
    public static PlayerData playerData;

    private GameLogic(){};

    public static void init(final FullscreenActivity context) {
        setContext(context);
        final Handler h = new Handler();
        final int delay = 125; //milliseconds
        tickNum = 0;

        Log.d("GameLogic: init()","Initializing");

        SaveData.readFromSaveFile();

        changeMap();

        Log.d("GameLogic: init()","Map initialized");
        if(aktuelleKarte == null)
            Log.wtf("GameLogic: init()","Map is null!!!");

        h.postDelayed(new Runnable(){
            public void run(){
                //do something

//                Log.v("GameLogic: tick "+tickNum,"Working");
                if(aktuelleKarte != null) {
                    for (Entity entity : aktuelleKarte.entities) {
                        entity.tick8persec(tickNum);
                    }
                }

                playerData.tick8persec(tickNum);
                tickNum++;

                h.postDelayed(this, delay);
            }
        }, delay);
        Log.v("GameLogic: init()","Started tick counter with "+1000/delay+" ticks/sec");

    }

    public static void changeMap() {
        aktuelleKarte = RPGMap.loadMap(playerData.mapId);
        context.mCanvasView.postInvalidate();
    }

    public static void warpPlayerToPosition(int mapId, int xPos, int yPos) {
        playerData.mapId = mapId;
        playerData.setPosition(xPos, yPos);
        changeMap();
    }

    public static void playerInteract() {
//        Toast.makeText(context,"Player Interaction detected",Toast.LENGTH_SHORT).show();
        playerData.interact();
    }

    public static void setMoveDir(int i) {
//        Toast.makeText(context,"Moving in direction "+i,Toast.LENGTH_SHORT).show();
        playerData.setMoveDir(i);
    }

    public static int getMoveDir() {
        return playerData.getMoveDir();
    }

    public static void setContext(FullscreenActivity cont) {
        context = cont;
    }

    public static class SaveData {
        static String[] flags;

        public static void writeToSaveFile() {
            flags = new String[RPGMap.mapCount];

            for(int i = 0; i < flags.length; i++)
                flags[i] = RPGMap.loadMap(i).serializeFlags();

            String filename = "savegame";
            FileOutputStream outputStream;

            try {
                outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);

                oos.writeObject(playerData);
                oos.writeObject(flags);

                oos.flush();
                oos.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void readFromSaveFile() {
            GameLogic.playerData = new PlayerData(2,30,23,1,"Timmy_Larence", false);

            flags = new String[] {null,"01","0000000"};

            for(int i = 0; i < flags.length; i++)
                RPGMap.loadMap(i).deserializeFlags(flags[i]);
        }
    }
}
