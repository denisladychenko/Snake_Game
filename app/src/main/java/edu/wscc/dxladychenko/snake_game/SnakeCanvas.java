//Denis Ladychenko


package edu.wscc.dxladychenko.snake_game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.io.Serializable;

import edu.wscc.dxladychenko.snake_game.enums.TileType;

public class SnakeCanvas extends View implements Serializable {



    private int tileSize;
    private static final int NUM_TILES_HORIZON = 40;
    private static final int NUM_TILES_VERTICAL = 30;

    private int startLeftCoord,
            startTopCoord;

    private TileType snakeViewMap[][];               //2 dim array of tiles



    private Paint fieldPaint,
        wallPaint;
    private Rect field;

    //constructor
    public SnakeCanvas(Context context) {
        super(context);
        wallPaint = new Paint();
        fieldPaint = new Paint();
        field = new Rect();

    }

    /**
     *Gets number of horizon tiles
     * @return NUM_TILE_HORIZON The number of tiles in a row
     */
    public static int getNumTilesHorizon() {
        return NUM_TILES_HORIZON;
    }
    /**Gets number of vertical tiles
     * @return NUM_TILE_VERTICAL The number of tiles in a column
     */
    public static int getNumTilesVertical() {
        return NUM_TILES_VERTICAL;
    }


    /**Sets the tile size
     * @param tileSize The tile size
     */
    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    /**Sets the snake map
     * @param map The view map
     */
    public void setSnakeViewMap(TileType[][] map){
        snakeViewMap = map;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        wallPaint.setColor(Color.BLACK);

        startLeftCoord = ((canvas.getWidth() - (NUM_TILES_HORIZON * tileSize)) / 2);
        startTopCoord = (canvas.getWidth() / 10) * 2;



        for(int i = 0; i < NUM_TILES_VERTICAL; i++){
            for(int k = 0; k < NUM_TILES_HORIZON; k++) {
                if (snakeViewMap != null) {
                    switch (snakeViewMap[i][k]) {
                        case Nothing:
                            fieldPaint.setColor(Color.WHITE);
                            break;
                        case Wall:
                            fieldPaint.setColor(Color.BLACK);
                            break;
                        case SnakeHead:
                            fieldPaint.setColor(Color.BLACK);
                            break;
                        case SnakeTail:
                            fieldPaint.setColor(Color.BLACK);
                            break;
                        case Food:
                            fieldPaint.setColor(Color.BLACK);
                            break;
                    }
                    canvas.drawRect(startLeftCoord + (k * tileSize), startTopCoord + (i * tileSize),
                            startLeftCoord + (k * tileSize) + tileSize, startTopCoord + (i * tileSize) + tileSize,
                            fieldPaint);

                }
            }
        }

    }


}
