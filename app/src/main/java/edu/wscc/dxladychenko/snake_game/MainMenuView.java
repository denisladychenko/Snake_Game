//Denis Ladychenko


package edu.wscc.dxladychenko.snake_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class MainMenuView extends View {
    public MainMenuView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        //prints snake image as a background
        Bitmap snakeImg = BitmapFactory.decodeResource(getResources(), R.drawable.snake_img);
        canvas.drawBitmap(snakeImg, -100 , -180, paint);
    }
}
