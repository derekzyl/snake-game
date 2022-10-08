package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App extends Application {
    static String path = "application/png.png";
    static Image image = new Image(path);

    static int speed = 1;
    static int width = 20;
    static int height = 20;
    static int foodColor = 0;
    static int foodY = 0;
    static int foodX = 0;
    static int cornerSize = 25;


    static int snakeLength = 1;
    static List<Corner> snake = new ArrayList<>();
    static boolean gameOver = false;
    static boolean gameStarted = false;
    static boolean gamePaused = false;
    static boolean gameWon = false;
    static boolean gameLost = false;
    static Random random = new Random();
    static Direction direction = Direction.RIGHT;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public static class Corner {
        int x;
        int y;
        int color;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;

        }
    }

    public void start(Stage primaryStage)  {

        generateFood();
        Pane root = new Pane();
        Canvas canvas = new Canvas(width * cornerSize, height * cornerSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, width * cornerSize, height * cornerSize);


        AnimationTimer anime = new AnimationTimer() {
            long lastTick = 0;

            @Override
            public void handle(long lng) {

                if (lastTick == 0) {
                    lastTick = lng;
                    tick(gc);
                    return;
                }
                if (lng - lastTick > 1000000000 / speed) {
                    lastTick = lng;
                    tick(gc);
                }

            }
        };
        anime.start();
        gameStarted = true;
        // controls
        scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (KeyCode.SPACE.equals(key.getCode())) {
                if (gameStarted) {
                    if (gamePaused) {
                        anime.start();
                        gamePaused = false;
                    } else {
                        anime.stop();
                        gamePaused = true;
                    }
                } else {
                    gameStarted = true;
                    anime.start();
                }

                gamePaused = !gamePaused;
            }
            if (KeyCode.R.equals(key.getCode())) {
                anime.start();
                gameOver = false;
                gameStarted = false;
                gamePaused = false;
                gameWon = false;
                gameLost = false;
                speed = 1;
                snake.clear();
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                generateFood();
            }
            if (key.getCode() == KeyCode.UP || key.getCode() == KeyCode.W) {
                direction = Direction.UP;
            }
            if (key.getCode() == KeyCode.DOWN || key.getCode() == KeyCode.X) {
                direction = Direction.DOWN;
            }
            if (key.getCode() == KeyCode.LEFT || key.getCode() == KeyCode.A) {
                direction = Direction.LEFT;
            }
            if (key.getCode() == KeyCode.RIGHT || key.getCode() == KeyCode.D) {
                direction = Direction.RIGHT;
            }
            if (key.getCode() == KeyCode.P || key.getCode() == KeyCode.SPACE) {
                gamePaused = !gamePaused;
            }
        });

        // snake game start
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));

        // scene.getStylesheets().add(App.class.getResource("application.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake Game");

        primaryStage.show();

    }

    public static void tick(GraphicsContext gc) {
        if (gameOver) {


            gc.setFill(Color.RED);
            gc.clearRect(0, 0, width * cornerSize, height * cornerSize);

            gc.fillText("Game Over", width * cornerSize / 2 - 50, height * cornerSize / 1);
            gc.fillText("Score: " + (speed - 2), width * cornerSize / 2 - 50, height * cornerSize / 2 + 50);
            gc.fillText("Press R to play again", width * cornerSize / 2 - 50, height * cornerSize / 2 + 100);

            // scene = new Scene(button, width * cornerSize, height * cornerSize);
            // primaryStage.setScene(scene);
            // 100 250
            // gc.fill(button, width * cornerSize / 2 - 50, height * cornerSize / 2); // 100
            // 250

            ;
            return;
        }

      if(!gameOver) {
            for (int i = snake.size() - 1; i > 0; i--) {
                snake.get(i).x = snake.get(i - 1).x;
                snake.get(i).y = snake.get(i - 1).y;
            }

            /**
             * this to call in for direction
             *
             */
            switch (direction) {
                case UP:
                    snake.get(0).y--;
                    if (snake.get(0).y < 0) {
                        gameOver = false;
                        snake.set(0, new Corner(snake.get(0).x, height - 1));

                    }
                    break;
                case DOWN:
                    snake.get(0).y++;
                    if (snake.get(0).y > height - 1) {
                        gameOver = false;
                        snake.set(0, new Corner(snake.get(0).x, 0));
//                        snake.get(0).y--;
                    }
                    break;

                case LEFT:
                    snake.get(0).x--;
                    if (snake.get(0).x < 0) {
                        gameOver = false;
                        snake.set(0, new Corner(width - 1, snake.get(0).y));

                    }
                    break;

                case RIGHT:
                    snake.get(0).x++;
                    if (snake.get(0).x > width - 1) {
                        gameOver = false;
                        snake.set(0, new Corner(0, snake.get(0).y));

                    }
                    break;

            }

            // snake taking meals and growing
            if (snake.get(0).x == foodX && snake.get(0).y == foodY) {
                snake.add(new Corner(-1, -1));

                generateFood();
            }

            // snake eating itself
            for (int i = 1; i < snake.size(); i++) {
                if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                    gameOver = true;
                }
            }
            // background color
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width * cornerSize, height * cornerSize);

            // score
            gc.setFill(Color.WHITE);
            gc.fillText("Score: " + (speed - 2), width * cornerSize / 2 - 50, height * cornerSize / 2); // change all these
            // to 10,30

            gc.fillText("Press SPACE or P key to  pause  or start the game \n" +
                            "W  or  UP key for up direction \n" +
                            "S  or  LEFT key for right direction \n" +
                            "A  or  RIGHT key for right direction \n" +
                            "X  or  DOWN key for down direction \n", width * cornerSize / 2 - 50,
                    height * cornerSize / 1.8);

            // FOOD COLOR
            Color color = Color.BLUE;
            switch (foodColor) {
                case 0:
                    color = Color.RED;
                    break;
                case 1:
                    color = Color.GREEN;
                    break;
                case 2:
                    color = Color.BLUE;
                    break;
                case 3:
                    color = Color.YELLOW;
                    break;
                case 4:
                    color = Color.PURPLE;
                    break;
                case 5:
                    color = Color.ORANGE;
                    break;
                case 6:
                    color = Color.CYAN;
                    break;
                case 7:
                    color = Color.MAGENTA;
                    break;
                case 8:
                    color = Color.LIGHTBLUE;
                    break;
                case 9:
                    color = Color.GREEN;
                    break;
                case 10:
                    color = Color.RED;
                    break;
                case 11:
                    color = Color.YELLOW;
                    break;
                case 12:
                    color = Color.PURPLE;
                    break;
                case 13:
                    color = Color.ORANGE;
                    break;
                case 14:
                    color = Color.CYAN;
                    break;
                case 15:
                    color = Color.MAGENTA;
                    break;
                case 16:
                    color = Color.DARKBLUE;
                    break;
                case 17:
                    color = Color.DARKGREEN;
                    break;
                case 18:
                    color = Color.DARKRED;
                    break;
                case 19:
                    color = Color.YELLOW;
                    break;
                case 20:
                    color = Color.PURPLE;
                    break;
                case 21:
                    color = Color.DARKORANGE;
                    break;
                case 22:
                    color = Color.DARKCYAN;
                    break;
                case 23:
                    color = Color.DARKMAGENTA;
                    break;
                case 24:
                    color = Color.GRAY;
                    break;
            }

            // food color position
            gc.setFill(color);
            gc.fillOval(foodX * cornerSize, foodY * cornerSize, cornerSize, cornerSize);
//        gc.drawImage(image, cornerSize, cornerSize);


            // snake setup
            for (Corner corner : snake) {

                gc.setFill(Color.WHITE);
                gc.fillOval(corner.x * cornerSize, corner.y * cornerSize, cornerSize - 1, cornerSize - 1);
            }
        }

    }

    // food
    public static void generateFood() {
        start: while (true) {
            foodX = random.nextInt(width);
            foodY = random.nextInt(height);
            for (Corner corner : snake) {
                if (corner.x == foodX && corner.y == foodY) {
                    continue start;
                }
            }

            foodColor = random.nextInt(23);
            speed++;

            if (snake.stream().filter(c -> c.x == foodX && c.y == foodY).count() == 0) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
