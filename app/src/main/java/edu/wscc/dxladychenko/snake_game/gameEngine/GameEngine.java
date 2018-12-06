//Denis Ladychenko

package edu.wscc.dxladychenko.snake_game.gameEngine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.text.InputType;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.wscc.dxladychenko.snake_game.R;
import edu.wscc.dxladychenko.snake_game.SnakeCanvas;
import edu.wscc.dxladychenko.snake_game.classes.Coordinates;
import edu.wscc.dxladychenko.snake_game.enums.Direction;
import edu.wscc.dxladychenko.snake_game.enums.GameState;
import edu.wscc.dxladychenko.snake_game.enums.TileType;

public class GameEngine implements Serializable {

    private int points;               //game points
    private transient SnakeCanvas view;        //snake field view, not serializable
    private TileType[][] gameMap;
    private Coordinates food;
    private List<Coordinates> snakeField;
    private List<Coordinates> walls;
    private List<Coordinates> snake;
    private GameState gameState;
    private Direction currentDirection;



    //constructor
    public GameEngine(SnakeCanvas view){
        points = 0;
        this.view = view;
        snakeField = new ArrayList<>();
        walls = new ArrayList<>();
        snake = new ArrayList<>();
        gameState = GameState.Ready;
        currentDirection = Direction.Right;

    }
    /**
     * Initializes game elements
     */
    public void initGame(){
        addField();
        addWalls();
        addSnake();
        addFood();
        setGameMap();
    }

    /**
     * Updates snake coordinates
     * @param  currentDirection The direction of the snakes head
     */
    public void update(Direction currentDirection){
        switch(currentDirection){
            case Up: updateView(0, -1);
                break;
            case Down: updateView(0, 1);
                break;
            case Left: updateView(-1, 0);
                break;
            case Right: updateView(1, 0);
                break;

        }
        if(wallHit() || selfHit()){
            gameState = GameState.Lost;
        }
        if(foodEaten()){
            snake.add(food);
            addFood();
            //thread to count points and play sound
            new Thread(new Runnable() {
                @Override
                public void run() {
                    points += 10;
                    makeSound(view.getContext());

                }
            }).start();

        }
    }
    /**
     * Plays sound
     * @param  context Game context
     */
    public void makeSound(Context context){
        MediaPlayer ring= MediaPlayer.create(context,R.raw.game_sound);
        ring.start();
    }
    /**
     * Tests if the snake ran into itself
     * @return true If snake ran into itself
     */
    public boolean selfHit(){
        for(int i = 1; i < snake.size(); i++ ){
            if(snake.get(0).equals(snake.get(i))){
                return true;
            }
        }
        return false;
    }


    /**
     * Tests if snake hit the wall
     * @return true If snake ran into the wall
     */
   public boolean wallHit(){
        for(Coordinates wall: walls){
            if(snake.get(0).equals(wall)){
                return true;
            }
        }
        return false;
   }
    /**
     * Tests if snake had eaten the food
     * @return true If snake had eaten the food
     */
    public boolean foodEaten(){
        for(Coordinates s: snake){
            if(snake.get(0).equals(food)){
                return true;
            }
        }
        return false;
    }
    /**
     * Updates snake's coordinates
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void updateView(int x, int y){
        for(int i = snake.size() - 1; i > 0; i--){
            snake.get(i).setX(snake.get(i - 1).getX());
            snake.get(i).setY(snake.get(i - 1).getY());
        }
        //snake's head
        snake.get(0).setX(snake.get(0).getX() + x);
        snake.get(0).setY(snake.get(0).getY() + y);
    }
    /**
     * Creates 2 dim array of tiles used in a game map
     */
    public void setGameMap(){
        gameMap = new TileType[view.getNumTilesVertical()][view.getNumTilesHorizon()];
        for(Coordinates field: snakeField){
            gameMap[field.getY()][field.getX()] = TileType.Nothing;
        }
        for(Coordinates field: walls){
            gameMap[field.getY()][field.getX()] = TileType.Wall;
        }
        for(Coordinates field: snake){
            gameMap[field.getY()][field.getX()] = TileType.SnakeTail;
        }
            gameMap[food.getY()][food.getX()] = TileType.Food;
    }
    /**
     * Gets the map of tiles
     * @return gameMap The map
     */
    public TileType[][] getGameMap() {
        return gameMap;
    }

    /**
     * Creates 2 dim array of tiles used in a game map
     */
    public void addField(){

        for(int y = 0; y <  view.getNumTilesVertical(); y++){
            for(int x = 0; x < view.getNumTilesHorizon(); x++){
                snakeField.add(new Coordinates(x, y));
            }
        }
    }
    /**
     * Adds wall tiles to the map
     */
    public void addWalls(){
        //add upper and lower walls
        for(int x = 0; x <  view.getNumTilesHorizon(); x++){
                walls.add(new Coordinates(x, 0));
                walls.add(new Coordinates(x, view.getNumTilesVertical() - 1));
        }
        //add left and right walls
        for(int y = 1; y <  view.getNumTilesVertical() - 1; y++){
            walls.add(new Coordinates(0, y));
            walls.add(new Coordinates(view.getNumTilesHorizon() - 1, y));
        }
    }
    /**
     * Adds snake tiles to the map
     */
    public void addSnake(){
        snake.clear();
        snake.add(new Coordinates( 22, 15));
        snake.add(new Coordinates( 21, 15));
        snake.add(new Coordinates( 20, 15));
        snake.add(new Coordinates( 19, 15));
        snake.add(new Coordinates( 18, 15));
    }
    /**
     * Adds food tile to the map
     */
    public void addFood(){
        Random rand = new Random();
        int x, y;

        //subtract 4 and add 2 so that there is a margin between wall and a food
        x = rand.nextInt(view.getNumTilesHorizon() - 4) + 2;
        y = rand.nextInt(view.getNumTilesVertical() - 4) + 2;

        //if coordinates belong to the snake than don't place food there
        while (onSnake(new Coordinates(x, y))){
            x = rand.nextInt(view.getNumTilesHorizon() - 4) + 2;
            y = rand.nextInt(view.getNumTilesVertical() - 4) + 2;
        }
        food = new Coordinates(x, y);
    }
    /**
     * Tests coordinates to see if they belong to the snake
     * @param coord The coordinates to test
     * @return true If coordinates belong to the snake
     */
   public boolean onSnake(Coordinates coord){
        for(Coordinates s: snake){
            if(s.equals(coord)){
                return true;
            }
        }
        return false;
   }
    /**
     * Gets current snake direction
     * @return currentDirection The current direction
     */
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    /**
     * Sets current snake direction
     * @param currentDirection The current direction
     */
    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }
    /**
     * Gets game state
     * @return gameState The game state
     */
    public GameState getGameState() {
        return gameState;
    }
    /**
     * Sets game state
     * @param  gameState The game state
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    /**
     * Gets earned points
     * @return points The points
     */
    public int getPoints() {
        return points;
    }
    /**
     * Sets the view
     * @param  view The game view
     */
    public void setCanvas(SnakeCanvas view){
        this.view = view;
    }

}
