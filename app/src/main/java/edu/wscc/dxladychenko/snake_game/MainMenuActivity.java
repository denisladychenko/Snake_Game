//Denis Ladychenko


package edu.wscc.dxladychenko.snake_game;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.FrameLayout;

public class MainMenuActivity extends AppCompatActivity {

    private MainMenuView view;
    private FrameLayout frmLayout;
    private MainMenuButtonView buttonView;
    private SQLiteDatabase savedGamesDB;
    private SQLiteDatabase highScoresDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainMenuView(this);
        buttonView = new MainMenuButtonView(this);
        frmLayout = new FrameLayout(this);
        frmLayout.addView(view);
        frmLayout.addView(buttonView);
        setContentView(frmLayout);

        //initialize databases
        initSavedGamesDB();
        initHighScoresDB();
    }

    /**
     *Initialized saved games DB
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
     *initializes high scores DB
     */
    private void initHighScoresDB(){
        try {
            highScoresDB = openOrCreateDatabase(getResources().getString(R.string.high_score), MODE_PRIVATE, null);
            highScoresDB.execSQL("CREATE TABLE IF NOT EXISTS HighScores(Id INTEGER PRIMARY KEY, PlayerName VARCHAR, Score NUMBER);");
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }


}
