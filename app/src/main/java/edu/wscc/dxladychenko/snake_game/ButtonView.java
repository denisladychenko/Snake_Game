//Denis Ladychenko

package edu.wscc.dxladychenko.snake_game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.icu.text.Normalizer2;
import android.text.InputType;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.wscc.dxladychenko.snake_game.enums.Direction;
import edu.wscc.dxladychenko.snake_game.enums.GameState;
import edu.wscc.dxladychenko.snake_game.gameEngine.GameEngine;

import static android.content.Context.MODE_PRIVATE;

public class ButtonView extends View{


    private int buttonSize;
    private int upButtonXCoord,
            upButtonYCoord;
    private int leftButtonXCoord,
            leftButtonYCoord;
    private int rightButtonXCoord,
            rightButtonYCoord;
    private int downButtonXCoord,
            downButtonYCoord;
    private GameEngine gameEngine;           //game engine object
    private GameActivity gameActivity;
    private FrameLayout frmLayout;
    private transient String savedGameStr;
    private boolean alreadyCalled;
    private String playerName;
    private SQLiteDatabase highScoresDB;
    private Paint paint;
    private Rect button;

    private float touchXCoord,
            touchYCoord;



        public ButtonView(Context context, GameEngine engine, Activity ga) {
            super(context);
            paint = new Paint();
            button = new Rect();
            paint.setColor(Color.RED);
            gameEngine = engine;                                         //set game engine
            gameActivity = (GameActivity)ga;                             //set the activity object
            highScoresDB = gameActivity.getHighScoresDB();               //get database of high scores
            playerName = getResources().getString(R.string.unknown);    //name is "Unknown" on default

        }

        @Override
        protected void onDraw(Canvas canvas) {
            //if screen size is greater than 764 pixels than use large buttons
            buttonSize = canvas.getHeight() > 764 ? 180 : 88;

            button.set(((canvas.getWidth()/2) - (buttonSize/2)), 500, ((canvas.getWidth()/2) - (buttonSize/2)) + buttonSize, 500 + buttonSize);
            Bitmap bitmapUpArrow = BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow);
            Bitmap bitmapRightArrow = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow);
            Bitmap bitmapLeftArrow = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow);
            Bitmap bitmapDownArrow = BitmapFactory.decodeResource(getResources(), R.drawable.down_arrow);


            upButtonXCoord = ((canvas.getWidth()/2) - (buttonSize/2));
            upButtonYCoord = (canvas.getHeight()/5) * 3;
            leftButtonXCoord = canvas.getWidth()/2 -(buttonSize/2) - buttonSize;
            leftButtonYCoord = ((canvas.getHeight()/5) * 3) + buttonSize;
            downButtonXCoord = (canvas.getWidth()/2) - (buttonSize/2);
            downButtonYCoord = (((canvas.getHeight()/5) * 3) + (buttonSize*2));
            rightButtonXCoord = canvas.getWidth()/2 + (buttonSize/2);
            rightButtonYCoord = ((canvas.getHeight()/5) * 3) + buttonSize;

            canvas.drawBitmap(bitmapUpArrow, upButtonXCoord , upButtonYCoord, paint);
            canvas.drawBitmap(bitmapLeftArrow, leftButtonXCoord, leftButtonYCoord, paint);
            canvas.drawBitmap(bitmapDownArrow, downButtonXCoord, downButtonYCoord, paint);
            canvas.drawBitmap(bitmapRightArrow,rightButtonXCoord, rightButtonYCoord, paint);

            Paint pauseBtnPaint = new Paint();
            pauseBtnPaint.setColor(Color.GRAY);
            TextPaint txtPaint = new TextPaint();
            txtPaint.setColor(Color.BLACK);
            txtPaint.setTextSize(30);
            TextPaint invitationTextPaint = new TextPaint();
            invitationTextPaint.setColor(Color.RED);
            invitationTextPaint.setTextSize(30);

            //draw Pause/Resume button
            if(gameEngine.getGameState() == GameState.Paused){
                canvas.drawText(getResources().getString(R.string.resume),10, 754, txtPaint );
            }
            else {
                canvas.drawText(getResources().getString(R.string.pause),10, 754, txtPaint );
            }

            //draw Main Menu button
            canvas.drawText(getResources().getString(R.string.main_menu),330, 754, txtPaint );

            //draw Player Name button
            canvas.drawText(getResources().getString(R.string.player_name),10, 25, txtPaint );

            //draw Score tag
            canvas.drawText(getResources().getString(R.string.score) + " " + gameEngine.getPoints(),80, 90, txtPaint );
            drawSaveButton(canvas, txtPaint);
            //draw invitation text
           if(gameEngine.getGameState() == GameState.Ready) {
                printInvText(canvas, gameActivity.getCounter(), txtPaint, invitationTextPaint);
            }
            if(gameEngine.getGameState() == GameState.Lost){
               drawLostMessage(canvas);
            }
            if((gameEngine.getGameState() == GameState.Lost) && !alreadyCalled && (playerName != null)){
                drawLostMessage(canvas);
                addScoreToDB();
                gameActivity.setPlayerName(playerName);
                alreadyCalled = true;
            }

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

           int action = event.getAction();

            switch (action) {

                case MotionEvent.ACTION_DOWN:
                    touchXCoord = event.getX();
                    touchYCoord = event.getY();

                    //if game is not yet started and user taps anywhere on the canvas
                    //then change game state to running (start the game)
                    if(gameEngine.getGameState() == GameState.Ready && touchXCoord < 480
                            && touchXCoord > 200 && touchYCoord < 764 && touchYCoord > 40 ){
                        gameEngine.setGameState(GameState.Running);

                    }
                    //up button tap
                    if(touchXCoord > upButtonXCoord && touchXCoord < (upButtonXCoord + buttonSize)
                            && touchYCoord > upButtonYCoord && touchYCoord < (upButtonYCoord + buttonSize)) {
                        if(gameEngine.getCurrentDirection() != Direction.Down){
                            gameEngine.setCurrentDirection(Direction.Up);
                        }

                    }
                    //left button tap
                    else if(touchXCoord > leftButtonXCoord && touchXCoord < (leftButtonXCoord + buttonSize)
                            && touchYCoord > leftButtonYCoord && touchYCoord < (leftButtonYCoord + buttonSize)) {
                        if(gameEngine.getCurrentDirection() != Direction.Right){
                            gameEngine.setCurrentDirection(Direction.Left);
                        }

                    }
                    //right button tap
                    else if(touchXCoord > rightButtonXCoord && touchXCoord < (rightButtonXCoord + buttonSize)
                            && touchYCoord > rightButtonYCoord && touchYCoord < (rightButtonYCoord + buttonSize)) {
                        if(gameEngine.getCurrentDirection() != Direction.Left){
                            gameEngine.setCurrentDirection(Direction.Right);
                        }

                    }
                    //down button tap
                    else if(touchXCoord > downButtonXCoord && touchXCoord < (downButtonXCoord + buttonSize)
                            && touchYCoord > downButtonYCoord && touchYCoord < (downButtonYCoord + buttonSize)) {
                        if(gameEngine.getCurrentDirection() != Direction.Up){
                            gameEngine.setCurrentDirection(Direction.Down);
                        }

                    }
                    //Pause/Resume button tap
                    else if(touchXCoord < 120 && touchXCoord > 0 && touchYCoord < 764 && touchYCoord > 724){
                        if(gameEngine.getGameState() == GameState.Running){
                            gameEngine.setGameState(GameState.Paused);
                        }
                        else if(gameEngine.getGameState() == GameState.Paused){
                            gameEngine.setGameState(GameState.Running);
                        }

                    }
                    //Mane Menu button tap
                    else if(touchXCoord < 480 && touchXCoord > 330 && touchYCoord < 764 && touchYCoord > 724){
                        Intent mainMenuIntent = new Intent(getContext(), MainMenuActivity.class);
                        getContext().startActivity(mainMenuIntent);

                        gameEngine.setGameState(GameState.Paused);
                        //finish game activity and go back to main menu
                        gameActivity.finish();
                    }
                    //save button tap
                    else if(touchXCoord < 480 && touchXCoord > 370 && touchYCoord < 30 && touchYCoord > 0){

                        if(gameEngine.getGameState() != GameState.Lost) {
                            gameEngine.setGameState(GameState.Paused);
                            getSaveName();
                        }

                    }
                    //Player Name button click
                    else if(touchXCoord < 200 && touchXCoord > 0 && touchYCoord < 40 && touchYCoord > 0){
                        if(gameEngine.getGameState() == GameState.Ready){
                            getPlayerName();
                        }


                    }
                    break;

            }

            return super.onTouchEvent(event);

        }

    /**
     *Sets the layout
     * @param frmLayout The layout
     */
    public void setFrmLayout(FrameLayout frmLayout) {
        this.frmLayout = frmLayout;
    }
    /**
     *Prints invitation text
     * @param canvas The canvas
     * @param counter The counter that determines paint color
     * @param paint one paint
     * @param paint2 another paint
     */
    private void printInvText(Canvas canvas, int counter, TextPaint paint, TextPaint paint2){
        //interchange colors depending on the counter
            if(counter % 2 == 0)
                canvas.drawText(getResources().getText(R.string.inv_string).toString(), 80, 390, paint);
            else
                canvas.drawText(getResources().getText(R.string.inv_string).toString(), 80, 390, paint2);

    }
    /**
     * Draws the message "You lost"
     * @param canvas The canvas
     */
    private void drawLostMessage(Canvas canvas){
            TextPaint paint = new TextPaint();
            paint.setColor(Color.RED);
            paint.setTextSize(50);
        canvas.drawText(getResources().getText(R.string.lost_str).toString(), 140, 230, paint);
    }
    /**
     *draws the save button
     * @param canvas The canvas
     * @param paint The paint
     */
    private void drawSaveButton(Canvas canvas, TextPaint paint){
        //canvas.drawRect(370, 0, 480, 30, p);
        canvas.drawText(getResources().getText(R.string.save_str).toString(),390, 25, paint );
    }
    /**
     *Displays input dialog, gets the game name
     */
    private void getSaveName(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.save_under));

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.ok_str), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user defined name to save game
                savedGameStr = input.getText().toString();
                saveGame();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
            builder.show();

    }
    /**
     *Displays input dialog, gets player name
     */
    private void getPlayerName(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.player_name));

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.ok_str), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //player name
                playerName = input.getText().toString();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    /**
     *Saves game in the file, and stores the filename in a DB
     */
    private void saveGame(){
        try {
            File file = new File(getContext().getFilesDir(), savedGameStr + ".ser");
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gameEngine);
            out.close();
            fileOut.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        addToDB(gameActivity.getSavedGamesDB(), savedGameStr);
    }
    /**
     *Adds record to DB
     * @param db The database
     * @param value The name for the game
     */
    private void addToDB(SQLiteDatabase db, String value){
        //get current date and time
        Calendar cal = Calendar.getInstance();
        Date dt = cal.getTime();
        //format date and time as a string "MM/dd/yy hh-mm-ss"
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy hh-mm-ss");
        String timeStamp = df.format(dt);    //formatted date string

        db.execSQL("INSERT INTO SavedGames VALUES('"+ value +"','" + timeStamp + "');");


    }
    /**
     *Adds record to HighScores DB if number of records there is less than 5
     *Replaces lowest score in DB if the score is greater than the lowest score in DB
     */
    public void addScoreToDB(){
        Cursor rs = highScoresDB.rawQuery("SELECT * FROM HighScores ORDER BY Score DESC", null);
        int numEntries = rs.getCount();
        int id = -1;

        if(rs != null){
            int score = gameEngine.getPoints();
            if(numEntries < 5){
                insertScoreRecord(score);
            }
            else if(numEntries == 5){
                rs.moveToLast();
                if(rs.getInt(1) < score){
                    id = rs.getInt(0);
                    updateScoreRecord(id, score);
                }
            }
            else{

            }
        }

    }

    /***
     * Updates DB record
     * @param id The record's id
     * @param score The score to update
     */
    private void updateScoreRecord(int id, int score){
        try {
            highScoresDB.execSQL("UPDATE HighScores SET PlayerName ='" + playerName + "', Score = '" + score + "' WHERE Id = '" + id + "';");
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    /**
     *Inserts new record into DB
     * @param score The score to insert
     */
    private void insertScoreRecord(int score){
        try {
            highScoresDB.execSQL("INSERT INTO HighScores VALUES(null, '" + playerName + "','" + score + "');");
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
