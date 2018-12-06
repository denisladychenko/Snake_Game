//Denis Ladychenko

package edu.wscc.dxladychenko.snake_game;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.wscc.dxladychenko.snake_game.enums.GameState;
import edu.wscc.dxladychenko.snake_game.gameEngine.GameEngine;

public class GameActivity extends AppCompatActivity {

    private SnakeCanvas canvas;         //snake field
    private ButtonView buttonCanv;      //view that contains buttons
    private FrameLayout frmLayout;      //base layout
    private GameEngine engine;
    private long delay = 150;           //refresh rate
    private final Handler handler = new Handler();
    private HandlerThread thread;       //sound handler
    private Handler soundHandler;
    private String playerName;          //player name
    private SQLiteDatabase savedGamesDB;
    private SQLiteDatabase highScoresDB;
    private MediaPlayer ring;                   //start game sound
    private MediaPlayer GameOverRing;           //game over sound
    private int counter = 0;         // counter to determine what paint to use for invitation msg



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSavedGamesDB();
        highScoresDB = openOrCreateDatabase(getResources().getString(R.string.high_score), MODE_PRIVATE,null);
        //highScoresDB.execSQL("DROP TABLE HighScores");

        canvas = new SnakeCanvas(this);

        //get game engine object from the calling activity
        Intent intent = getIntent();
        GameEngine tempEngine = (GameEngine) intent.getSerializableExtra(getResources().getString(R.string.game));


        //if tempEngine is not null then start saved game
        //otherwise start new game
        if(tempEngine != null){
            engine = tempEngine;
            engine.setCanvas(canvas);
        }
        else{
            engine = new GameEngine(canvas);
            engine.initGame();
        }
        //view with buttons
        buttonCanv = new ButtonView(this, engine, this);


        //get display height
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int height = size. y;
        //set tile size depending on the screen height
        int tileSize = height > 800 ? 25 : 8;
        canvas.setTileSize(tileSize);

        //sound thread
        thread = new HandlerThread(getResources().getString(R.string.sound_thread));
        thread.start();
        soundHandler = new Handler(thread.getLooper());

        //base layout to hold snake view and a button view
        frmLayout = new FrameLayout(this);
        frmLayout.addView(canvas);
        frmLayout.addView(buttonCanv);
        frmLayout.setBackgroundColor(getResources().getColor(R.color.frameBackground));
        buttonCanv.setFrmLayout(frmLayout);
        setContentView(frmLayout);

        ring= MediaPlayer.create(this, R.raw.game_start);
        GameOverRing = MediaPlayer.create(this, R.raw.game_over);

        //UI thread
        startUpdateHandler();
        //sound thread
        startSoundHandler();

    }

    /**
     *Gets counter for invitation message
     * @return  counter  The counter
     */
    public int getCounter() {
        return counter;
    }

    private void startUpdateHandler(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(engine.getGameState() == GameState.Ready){
                    ++counter;
                    handler.postDelayed(this, delay);
                }

                if(engine.getGameState() == GameState.Running){
                    engine.update(engine.getCurrentDirection());
                    engine.setGameMap();
                    handler.postDelayed(this, delay);


                }
                if(engine.getGameState() == GameState.Lost){
                    GameOverRing.start();
                }
                if(engine.getGameState() == GameState.Paused){
                    handler.postDelayed(this, delay);

                }
                canvas.setSnakeViewMap(engine.getGameMap());
                canvas.invalidate();
                buttonCanv.invalidate();


            }
        }, delay);
    }
    /**
     *Runnable for sounds
     */
    private class SoundRunnable implements Runnable{

        @Override
        public void run() {
            ring.start();
            //makeSound(getBaseContext(), R.raw.game_start);
            if(engine.getGameState() == GameState.Ready){
                soundHandler.postDelayed( new SoundRunnable(), delay);
            }
            if(engine.getGameState() != GameState.Ready){
                ring.stop();
            }
        }
    }
    /**
     *Handler for the sounds
     */
    public void startSoundHandler(){
        soundHandler.postDelayed( new SoundRunnable(), delay);
    }
    /**
     *Gets the DB of saved games
     * @return savedGamesDB The DB of samed games
     */
    public SQLiteDatabase getSavedGamesDB(){
        return savedGamesDB;
    }

    /**
     *Initializes SavedGames DB
     */
    private void initSavedGamesDB(){
        try {
            savedGamesDB = openOrCreateDatabase(getResources().getString(R.string.saved_games), MODE_PRIVATE, null);
            savedGamesDB.execSQL("CREATE TABLE IF NOT EXISTS SavedGames(Filename VARCHAR, Date VARCHAR);");
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    /**
     *Sets the name of the player
     * @param playerName The name of the player
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    /**
     *Gets HighScores DB
     */
    public SQLiteDatabase getHighScoresDB(){
        return highScoresDB;
    }
}
