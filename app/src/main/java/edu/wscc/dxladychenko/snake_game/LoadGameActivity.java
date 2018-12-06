//Denis Ladychenko


package edu.wscc.dxladychenko.snake_game;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.wscc.dxladychenko.snake_game.gameEngine.GameEngine;

public class LoadGameActivity extends AppCompatActivity {

    private SQLiteDatabase savedGamesDB;
    private RelativeLayout layout;
    private ScrollView scrView;
    private TableLayout gameTable;
    private String loadGameName;
    private Map<TextView, TextView> gameNames;      //text vies collection
    private Button loadBtn,
            deleteBtn,
            deleteAllBtn,
            mainMenuBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedGamesDB = openOrCreateDatabase(getResources().getString(R.string.saved_games),MODE_PRIVATE,null);

        setContentView(R.layout.activity_load_game);
        //initialize controls
        layout = (RelativeLayout)findViewById(R.id.topContainer);
        gameTable = (TableLayout)findViewById(R.id.savedGameTable);
        loadBtn = (Button)findViewById(R.id.BtnLoad);
        deleteBtn = (Button)findViewById(R.id.BtnDelete);
        deleteAllBtn = (Button)findViewById(R.id.BtnDeleteAll);
        mainMenuBtn = (Button)findViewById(R.id.BtnMainMenu);

        gameNames = new HashMap<>();
        Cursor rs = savedGamesDB.rawQuery("Select * from SavedGames", null);
        int numItems = rs.getCount();
        rs.moveToFirst();

        //creates table of saved games
        createGamesTable(rs, numItems);

       mainMenuBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent MainMenuIntent = new Intent(getApplicationContext(), MainMenuActivity.class);
               startActivity(MainMenuIntent);
           }
       });

       loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoadSavedGameIntent = new Intent(getApplicationContext(), GameActivity.class);
                LoadSavedGameIntent.putExtra(getResources().getString(R.string.game), loadGame());
                startActivity(LoadSavedGameIntent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGameFile();
                deleteDBRecord();
                updateSavedGamesTable();
            }
        });

        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllSavedGames();
            }
        });
    }

    /**
     *Loads saved game from the file
     * @return ge The game engine
     */
    private GameEngine loadGame(){
        GameEngine ge = null;
        try
        {
            String name = getApplicationContext().getFilesDir() + "/" + loadGameName + ".ser";
            File file = new File(name);
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ge = (GameEngine)ois.readObject();
            ois.close();
            fis.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ge;
    }

    /**
     *Deletes the game file
     */
    private void deleteGameFile(){

        File file = new File(getApplicationContext().getFilesDir()+ "/" + loadGameName + ".ser");

        if(file.exists()){
            try {
                file.delete();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else{
            //file does not exist
        }

    }
    /**
     *Overloaded method to delete game file
     * @param filename The name of the file
     */
    private void deleteGameFile(String filename){

        File file = new File(getApplicationContext().getFilesDir()+ "/" + filename + ".ser");

        if(file.exists()){
            try {
                file.delete();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else{

        }

    }

    /**
     *Deletes DB record
     */
    private void deleteDBRecord(){
        try {
            savedGamesDB.execSQL("DELETE FROM SavedGames WHERE Filename ='" + loadGameName + "';");
        }catch (SQLException ex){

        }
    }

    /**
     *Deletes all saved games
     */
    private void deleteAllSavedGames(){
        try {
            //delete database table
            savedGamesDB.execSQL("DROP TABLE SavedGames");
            //recreate database table
            savedGamesDB.execSQL("CREATE TABLE IF NOT EXISTS SavedGames(Filename VARCHAR, Date VARCHAR);");
            for(Map.Entry<TextView, TextView> entry: gameNames.entrySet()){
                deleteGameFile(entry.getKey().getText().toString());
            }
            gameNames.clear();

            Intent LoadSavedGameIntent = new Intent(getApplicationContext(), LoadGameActivity.class);
            startActivity(LoadSavedGameIntent);
        }catch (SQLException ex){
            ex.printStackTrace();
        }

    }

    /**
     *Creates table to display saved games
     * @param rs The result set
     * @param numItems The number of items in DB
     */
    private void createGamesTable(Cursor rs, int numItems){
        for(int i = 0; i < numItems; i++){
            final TextView name;
            final TextView date;         //date the game was saved on
            TableRow tr;

            date = new TextView(this);
            name = new TextView(this);

            //add text view to the collection of text views
            gameNames.put(name, date);
            name.setClickable(true);

            name.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    loadGameName = name.getText().toString();
                    for(Map.Entry<TextView, TextView> entry: gameNames.entrySet()){
                        if(!loadGameName.equals(entry.getKey())){
                            entry.getKey().setTextColor(Color.LTGRAY);
                            entry.getValue().setTextColor(Color.LTGRAY);
                        }
                    }
                    date.setTextColor(Color.GREEN);
                    name.setTextColor(Color.GREEN);
                }
            });

            name.setText(rs.getString(0));
            date.setText(rs.getString(1));
            rs.moveToNext();
            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(name);
            tr.addView(date);
            gameTable.addView(tr);
        }
    }
    /**
     *Updates saved games table
     */
    private void updateSavedGamesTable(){

        TextView name = null;        //key to remove from the map

        for(Map.Entry<TextView, TextView> entry: gameNames.entrySet()){
            if(entry.getKey().getText().equals(loadGameName)){
                gameTable.removeView(entry.getKey());
                gameTable.removeView(entry.getValue());
                name = entry.getKey();
            }
        }
        gameNames.remove(name);

        Intent LoadSavedGameIntent = new Intent(getApplicationContext(), LoadGameActivity.class);
        startActivity(LoadSavedGameIntent);
    }
}
