//Denis Ladychenko


package edu.wscc.dxladychenko.snake_game;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class HighScoreActivity extends AppCompatActivity {

    private HighScoreView view;
    private SQLiteDatabase highScoresDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //opens or creates DB
        try {
            highScoresDB = openOrCreateDatabase(getResources().getString(R.string.high_score), MODE_PRIVATE, null);
        }catch (SQLException ex){
            ex.printStackTrace();
        }

        view = new HighScoreView(this, this);
        setContentView(view);

        //play sound
        MediaPlayer ring = MediaPlayer.create(this, R.raw.fanfare);
        ring.start();
    }

    /**
     *Gets screen size
     * @return size The screen size
     */
    public Point getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     *Gets HighScore DB
     * @return highScoresDB The high score DB
     */
    public SQLiteDatabase getHighScoresDB(){
        return highScoresDB;
    }
}
