//Denis Ladychenko


package edu.wscc.dxladychenko.snake_game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;

import edu.wscc.dxladychenko.snake_game.enums.Direction;

public class MainMenuButtonView extends View {
    //constructor
    public MainMenuButtonView(Context context) {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        TextPaint txtPaint = new TextPaint();
        txtPaint.setColor(Color.BLACK);
        txtPaint.setTextSize(50);

        //print buttons
        printPlayButton(canvas, txtPaint);
        printLoadGameButton(canvas, txtPaint);
        printHighScoreButton(canvas, txtPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
               float touchXCoord = event.getX();
               float touchYCoord = event.getY();
                //play button
                if(touchXCoord < 370 && touchXCoord > 115 && touchYCoord < 525 && touchYCoord > 455){
                    Intent myIntent = new Intent(getContext(), GameActivity.class);
                    getContext().startActivity(myIntent);
                }
                //load game button
                else if(touchXCoord < 420 && touchXCoord > 70 && touchYCoord < 610 && touchYCoord > 540) {
                    Intent loadGameIntent = new Intent(getContext(), LoadGameActivity.class);
                    getContext().startActivity(loadGameIntent);
                }
                //high score button
                else if(touchXCoord < 440 && touchXCoord > 60 && touchYCoord < 710 && touchYCoord > 635) {
                    Intent highScoreIntent = new Intent(getContext(), HighScoreActivity.class);
                    getContext().startActivity(highScoreIntent);
                }


        }
        return super.onTouchEvent(event);
    }


    /**
     *Prints load game button
     * @param canvas The canvas
     * @param paint The paint for a text
     */
    private void printLoadGameButton(Canvas canvas, TextPaint paint){
        canvas.drawText(getResources().getText(R.string.load_game_str).toString(), 110, 598, paint);
    }
    /**
     *Prints high score button
     * @param canvas The canvas
     * @param paint The paint for a text
     */
    private void printHighScoreButton(Canvas canvas, TextPaint paint){
        canvas.drawText(getResources().getText(R.string.high_score_str).toString(), 120, 695, paint);
    }
    /**
     *Prints play button
     * @param canvas The canvas
     * @param paint The paint for a text
     */
    private void printPlayButton(Canvas canvas, TextPaint paint){
        canvas.drawText(getResources().getText(R.string.play_str).toString(), 190, 510, paint);
    }
}
