package snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Snake_game extends Application {
	static int score = 0;
	static int speed = 12;
	static int width = 25, height = 25;
	static int food_x = 5, food_y = 5;
	static int squre_size = 25;
	static List<squre> snake = new ArrayList<>();
	static Diraction dir = Diraction.left;
	static boolean game = true;
	static Random rand = new Random();
	static char state='A';

	


	public static class squre {
		int curr_x;
		int curr_y;

		public squre(int x, int y) {
			curr_x = x;
			curr_y = y;
		}

		@Override
		public String toString() {
			return "(" + curr_x + "," + curr_y + ")";
		}
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new VBox();
			Canvas canvas = new Canvas(squre_size * width, squre_size * height);
			GraphicsContext gc = canvas.getGraphicsContext2D();
			root.getChildren().addAll(canvas);

			new AnimationTimer() {
				long lastTick = 0;
				@Override
				public void handle(long now) {
					if (now - lastTick > 1000000000 / speed) {
						lastTick = now;
						tick(gc);
					}
				}
			}.start();

			Scene scene = new Scene(root, squre_size * width, squre_size * height);

			// control
			scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
				if (dir != Diraction.down && key.getCode() == KeyCode.UP) {
					dir = Diraction.up;
				}
				if (dir != Diraction.up &&key.getCode() == KeyCode.DOWN ) {
					dir = Diraction.down;
				}
				if (dir != Diraction.right && key.getCode() == KeyCode.LEFT ) {
					dir = Diraction.left;
				}
				if (dir != Diraction.left &&key.getCode() == KeyCode.RIGHT ) {
					dir = Diraction.right;
				}
				if (state=='R' && key.getCode() == KeyCode.SPACE) {
					Restart();
				}
				if (state!='R'&&key.getCode() == KeyCode.P) {
					Pause();
				}

			});

			// add snake parts when start
			for (int i = 0; i < 3; i++) {
				snake.add(new squre((width / 2)+i, height / 2));
			}
			primaryStage.setScene(scene);
			primaryStage.setTitle("Snake Game");
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//////////////////////////////TICK-ANIMATION////////////////////////////////////////////	
	public static void tick(GraphicsContext gc) {
		if (game == false) {
			Message(gc);
			return;
		}
		
		// going from the tail each i square gets the info of the next i-1 square
		for (int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).curr_x = snake.get(i - 1).curr_x;
			snake.get(i).curr_y = snake.get(i - 1).curr_y;
		}


		switch (dir) {// every tic the curr_x/y update depends the direction
		case up:
			snake.get(0).curr_y--;
			break;
		case down:
			snake.get(0).curr_y++;
			break;
		case left:
			snake.get(0).curr_x--;
			break;
		case right:
			snake.get(0).curr_x++;
			break;
		default:
			break;
		}
		
		wallCollusion();
		biteMyself();
		
		// snake ate food and creating the next one
		if (food_x == snake.get(0).curr_x && food_y == snake.get(0).curr_y) {
			snake.add(new squre(-1, -1));// the square will update next tic
			createFood();
		}


		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width * squre_size, height * squre_size);


		gc.setFill(Color.RED);
		gc.fillOval(food_x * squre_size, food_y * squre_size, squre_size, squre_size);

		for (int i = 1; i < snake.size(); i++) {
			gc.setFill(Color.GREENYELLOW);
			gc.fillRect(snake.get(0).curr_x * squre_size, snake.get(0).curr_y * squre_size, squre_size - 1,
					squre_size - 1);
			squre s = snake.get(i);
			gc.setFill(Color.GREEN);
			gc.fillRect(s.curr_x * squre_size, s.curr_y * squre_size, squre_size - 1, squre_size - 1);
		}

		gc.setFill(Color.WHITE);
		gc.setFont(new Font("Arial", 30));
		gc.fillText("Score: " + score, 10, 30);
	}
	
////////////////////////////////////////FUNCTIONS///////////////////////////////////////////////////////////
	
	public static void Message(GraphicsContext gc) { //printing a prompt
		gc.setFill(Color.DODGERBLUE);
		gc.setFont(new Font("Arial", 50));
		if (state == 'R') {
			gc.fillText("Game Over", (width * squre_size) / 2 - 120, (height * squre_size) / 2);
			gc.setFont(new Font("Arial", 25));
			gc.fillText("\npress spacebar for reset", (width * squre_size) / 2 - 125, (height * squre_size) / 2);
		}
		if (state == 'P') {
			gc.fillText("Pause",(width * squre_size) / 2 - 100, (height * squre_size) / 2);
			gc.setFont(new Font("Arial", 25));
			gc.fillText("\npress P to continue",(width * squre_size) / 2 - 125, (height * squre_size) / 2);
		}

	}

	public static void createFood() { //if snake ate food creating another one
		while (true) {
			food_x = rand.nextInt(width-1);
			food_y = rand.nextInt(height-1);
			if (!snake.contains(new squre(food_x, food_y)))
				break;
		}
		if (speed < 20) {
			speed++;
		}
		score++;
	}

	public static void Restart() { //if lost reset the game by entering spacebar
		state = 'A';
		score = 0;
		speed = 10;
		food_x = 5;
		food_y = 5;
		snake.removeAll(snake);
		for (int i = 0; i < 3; i++) {
			snake.add(new squre(width / 2, height / 2));
		}
		dir = Diraction.left;
		game = true;
	}

	public static void Pause() { //press p for pause
		if (game == true) {
			game = false;
			state = 'P';
		} else {
			game = true;
			state = 'A';
		}
	}
	
	public static void wallCollusion(){ //check if head in walls
		squre head=snake.get(0);
		if(head.curr_x>=width||head.curr_x<0||head.curr_y>=height||head.curr_y<0) {
			game=false;
			state='R';
		}
	}
	
	public static void biteMyself() { //check if snake bite himself
		for (int i = 1; i < snake.size(); i++) {
			if (snake.get(0).curr_x == snake.get(i).curr_x && snake.get(0).curr_y == snake.get(i).curr_y) {
				System.out.println("im dead cuz: "+snake.get(0) +" "+i+snake.get(i));
				game = false;
				state='R';
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
