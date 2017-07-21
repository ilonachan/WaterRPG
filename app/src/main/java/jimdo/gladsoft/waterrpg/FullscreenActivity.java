package jimdo.gladsoft.waterrpg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

import jimdo.gladsoft.waterrpg.game.GameLogic;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements View.OnTouchListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    public View mCanvasView;
    public ImageView[] ctrls = new ImageView[5];
    public Button[] answers = new Button[4];
    public View mCtrlButtons;
    public View mTextBubble;
    public View mAnswerButtons;
    public TextView mTextBubbleContentTxt;
//    private View mCanvasClearButton;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mCanvasView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            mCanvasClearButton.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mCanvasView = findViewById(R.id.owCanvas);
        mCtrlButtons = findViewById(R.id.ctrlButtons);
        mTextBubble = findViewById(R.id.textBubble);
        mAnswerButtons = findViewById(R.id.questionAnswerSelButtons);
        mTextBubbleContentTxt = (TextView) findViewById(R.id.textBubbleContentTxt);
        ctrls[0] = (ImageView) findViewById(R.id.ctrlCenter);
        ctrls[1] = (ImageView) findViewById(R.id.btnUp);
        ctrls[2] = (ImageView) findViewById(R.id.btnDown);
        ctrls[3] = (ImageView) findViewById(R.id.btnLeft);
        ctrls[4] = (ImageView) findViewById(R.id.btnRight);

        answers[0] = (Button) findViewById(R.id.answer1Btn);
        answers[1] = (Button) findViewById(R.id.answer2Btn);
        answers[2] = (Button) findViewById(R.id.answer3Btn);
        answers[3] = (Button) findViewById(R.id.answer4Btn);

        for(int i = 0; i < 5; i++) {
            ctrls[i].setOnTouchListener(this);
        }

        GameLogic.init(this);
//        mCanvasClearButton = findViewById(R.id.button1);

        hide();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        mCanvasView.setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        GameLogic.SaveData.writeToSaveFile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hide();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
//
//    @SuppressLint("InlinedApi")
//    private void show() {
//        // Show the system bar
//        mCanvasView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mCanvasClearButton.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void showTextBox(String talkingNPC, String message) {
        showTextBox(talkingNPC,message,null);
    }

    public void showTextBox(String talkingNPC, String message, final TextBoxProceedHandle whatAfter) {
        final FullscreenActivity self = this;

        mCtrlButtons.setVisibility(View.GONE);
        mTextBubble.setVisibility(View.VISIBLE);
        mTextBubbleContentTxt.setText(message);
        mTextBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextBubble.setVisibility(View.GONE);
                mCtrlButtons.setVisibility(View.VISIBLE);
                if(whatAfter != null) whatAfter.handle();
            }
        });
    }

    public void showQuestionBox(String talkingNPC, String message, String[] sAnswers, final AskBoxProceedHandle whatAfter) {
        final FullscreenActivity self = this;

        mCtrlButtons.setVisibility(View.GONE);
        mTextBubble.setVisibility(View.VISIBLE);
        mAnswerButtons.setVisibility(View.VISIBLE);

        mTextBubbleContentTxt.setText(message);

        View.OnClickListener list = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int answerSel = -1;
                switch(view.getId()) {
                    case R.id.answer1Btn: answerSel = 0; break;
                    case R.id.answer2Btn: answerSel = 1; break;
                    case R.id.answer3Btn: answerSel = 2; break;
                    case R.id.answer4Btn: answerSel = 3; break;
                }

                mAnswerButtons.setVisibility(View.GONE);
                mTextBubble.setVisibility(View.GONE);
                mCtrlButtons.setVisibility(View.VISIBLE);

                if(whatAfter != null) whatAfter.handle(answerSel);
            }
        };

        for(int i = 0; i < 4; i++) {
            if(i < sAnswers.length) {
                answers[i].setVisibility(View.VISIBLE);
                answers[i].setText(sAnswers[i]);
                answers[i].setOnClickListener(list);
            } else {
                answers[i].setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        GameLogic.setContext(this);
        switch(view.getId()) {
            case R.id.ctrlCenter:
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    GameLogic.playerInteract();
                return true;
            case R.id.btnUp:
                if(motionEvent.getAction() == MotionEvent.ACTION_UP && GameLogic.getMoveDir() == 0) {
//                    view.setBackgroundColor(0xffff7777);
                    GameLogic.setMoveDir(-1);
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    view.setBackgroundColor(0xff77ff77);
                    GameLogic.setMoveDir(0);
                    return true;
                }
                break;
            case R.id.btnDown:
                if(motionEvent.getAction() == MotionEvent.ACTION_UP && GameLogic.getMoveDir() == 1) {
//                    view.setBackgroundColor(0xffff7777);
                    GameLogic.setMoveDir(-1);
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    view.setBackgroundColor(0xff77ff77);
                    GameLogic.setMoveDir(1);
                    return true;
                }
                break;
            case R.id.btnLeft:
                if(motionEvent.getAction() == MotionEvent.ACTION_UP && GameLogic.getMoveDir() == 2) {
//                    view.setBackgroundColor(0xffff7777);
                    GameLogic.setMoveDir(-1);
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    view.setBackgroundColor(0xff77ff77);
                    GameLogic.setMoveDir(2);
                    return true;
                }
                break;
            case R.id.btnRight:
                if(motionEvent.getAction() == MotionEvent.ACTION_UP && GameLogic.getMoveDir() == 3) {
//                    view.setBackgroundColor(0xffff7777);
                    GameLogic.setMoveDir(-1);
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    view.setBackgroundColor(0xff77ff77);
                    GameLogic.setMoveDir(3);
                    return true;
                }
                break;
        }
        return false;
    }
}
