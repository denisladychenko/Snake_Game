//Denis Ladychenko


package edu.wscc.dxladychenko.snake_game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.ContactsContract;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;

import static android.content.Context.MODE_PRIVATE;

public class HighScoreView extends View {

   private HighScoreActivity act;       //high score activity
   private int screenWidth;
   private int screenHeight;
   private int firstColStartX;
   private int secColStartX;
   private int thirdColStartX;
   private int firstRowStartY;
   private int columnHeight;
   private int colorDeterminer;
   private int firstColWidth;
   private int secColWidth;
   private int thirdColWidth;
   private int menuBtnStartX;
   private int menuBtnStartY;
   private int buttonWidth;
   private int buttonHeight;
   private SQLiteDatabase highScoresDB;

    public HighScoreView(Context context, HighScoreActivity act) {
        super(context);
        this.act = act;
        screenWidth = act.getScreenSize().x;     //get screen width
        screenHeight = act.getScreenSize().y;    //get screen height
        firstColStartX = (screenWidth / 12);
        secColStartX = (screenWidth / 12) * 2;
        thirdColStartX = (screenWidth / 12) * 9;
        firstRowStartY = ((screenHeight /5) * 2);
        firstColWidth = ((screenWidth / 12) * 2) - (screenWidth / 12);
        secColWidth = (thirdColStartX - secColStartX);
        thirdColWidth = ((screenWidth / 12) * 11) - thirdColStartX;
        columnHeight = screenHeight / 15;
        //menu button sizes
        menuBtnStartX = (screenWidth / 12);
        menuBtnStartY = ((screenHeight / 20) * 17);
        buttonWidth = (screenWidth / 12) * 10;
        buttonHeight = screenHeight / 15;
        //colorDeterminer is just a value that determines a color picked
        //on odd values it is one color, and on even values it is another color
        //it is being incremented every time it is used
        colorDeterminer = 1;
        highScoresDB = act.getHighScoresDB();    //get DB from the activity
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);

        //draw a cup
        Bitmap cup = BitmapFactory.decodeResource(getResources(), R.drawable.champ_cup);
        canvas.drawBitmap(cup, 150, 50, paint);

        //5 top scores from database
        Cursor rs = highScoresDB.rawQuery("SELECT * FROM HighScores ORDER BY Score DESC", null);
        rs.moveToFirst();
        for(int i = 0; i < rs.getCount(); i++){
            drawTableRow(canvas, i, rs);
            rs.moveToNext();
        }

        drawMainMenuButton(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float XCoord = event.getX();
        float YCoord = event.getY();
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                //menu button click
                if(XCoord < (menuBtnStartX + buttonWidth) && XCoord > menuBtnStartX && YCoord < (menuBtnStartY + buttonHeight) && YCoord > menuBtnStartY){
                    Intent intent = new Intent(getContext(), MainMenuActivity.class);
                    getContext().startActivity(intent);
                    act.finish();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     *Draws table row in the high scores view
     * @param canvas The canvas
     * @param rowNumber The row number
     * @param rs The result set from high scores DB
     */
    private void drawTableRow(Canvas canvas, int rowNumber, Cursor rs){
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(24);
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        paint.setColor(getResources().getColor(R.color.tblColor1));
        paint2.setColor(getResources().getColor(R.color.tblColor2));
        //draw first column
        if(colorDeterminer % 2 != 0){
            canvas.drawRect(firstColStartX, firstRowStartY + (rowNumber * columnHeight), (firstColStartX + (screenWidth / 12)), firstRowStartY + (rowNumber * columnHeight) + columnHeight, paint);
            colorDeterminer++;
        }
        else{
            canvas.drawRect(firstColStartX, firstRowStartY + (rowNumber * columnHeight), (firstColStartX + (screenWidth / 12)), firstRowStartY + (rowNumber * columnHeight) + columnHeight, paint2);
            colorDeterminer++;
        }
        //draw second column
        if(colorDeterminer % 2 != 0){
            canvas.drawRect(secColStartX, firstRowStartY + (rowNumber * columnHeight), (secColStartX + ((screenWidth / 12) * 7)), firstRowStartY + columnHeight + (rowNumber * columnHeight), paint);
            colorDeterminer++;
        }
        else {
            canvas.drawRect(secColStartX, firstRowStartY + (rowNumber * columnHeight), (secColStartX + ((screenWidth / 12) * 7)), firstRowStartY + columnHeight + (rowNumber * columnHeight), paint2);
            colorDeterminer++;
        }
        //draw third column
        if(colorDeterminer % 2 != 0){
            canvas.drawRect(thirdColStartX, firstRowStartY + (rowNumber * columnHeight), (thirdColStartX + ((screenWidth / 12) * 2)), firstRowStartY + columnHeight + (rowNumber * columnHeight), paint);
            colorDeterminer++;
        }
        else {
            canvas.drawRect(thirdColStartX, firstRowStartY + (rowNumber * columnHeight), (thirdColStartX + ((screenWidth / 12) * 2)), firstRowStartY + columnHeight + (rowNumber * columnHeight), paint2);
            colorDeterminer++;
        }
        //set row's text
        canvas.drawText(Integer.toString(rowNumber + 1),(firstColStartX + (firstColWidth / 3)), (firstRowStartY + (columnHeight / 2) + (rowNumber * columnHeight)), textPaint );
        canvas.drawText(rs.getString(1),(secColStartX + (secColWidth / 5)), (firstRowStartY + (columnHeight / 2) + (rowNumber * columnHeight)), textPaint );
        canvas.drawText(rs.getString(2),(thirdColStartX + (thirdColWidth / 6)), (firstRowStartY + (columnHeight / 2) + (rowNumber * columnHeight)), textPaint );


    }

    /**
     *Draws main menu button
     * @param canvas The canvas
     */
    private void drawMainMenuButton(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        TextPaint txtPaint = new TextPaint();
        txtPaint.setColor(Color.BLACK);
        txtPaint.setTextSize(30);
        //draw main menu button
        canvas.drawRect(menuBtnStartX, menuBtnStartY, menuBtnStartX + buttonWidth, menuBtnStartY + buttonHeight, paint);
        canvas.drawText(getResources().getString(R.string.main_menu), (menuBtnStartX +((buttonWidth / 10) * 3)), menuBtnStartY + ((buttonHeight / 3) * 2), txtPaint);
    }
}
