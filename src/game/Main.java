package game;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;
import gameserver.Common;

public class Main extends Application {

	public static final int size = 30;
	public static final int scene_height = size * 20 + 50;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right, hero_left, hero_up, hero_down;

	public static Player me;
	public static List<Player> players = new ArrayList<Player>();
	

	public static SignInDialog signInDialog;

	private Label[][] fields;
	private TextArea scoreList;
	private String userName;
	
	private static Socket connectionSocket;
	private static DataOutputStream outToServer;
	private static BufferedReader inFromServer;
	private static ClientThread clientThread;
	

	// -------------------------------------------
	// | Maze: (0,0) | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1) | scorelist |
	// | | (1,1) |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();

			GridPane boardGrid = new GridPane();

			image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

			hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
			hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
			hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
			hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

			fields = new Label[20][20];
			for (int j = 0; j < 20; j++) {
				for (int i = 0; i < 20; i++) {
					switch (Generel.board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default:
						throw new Exception("Illegal field value: " + Generel.board[j].charAt(i));
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);

			grid.add(mazeLabel, 0, 0);
			grid.add(scoreLabel, 1, 0);
			grid.add(boardGrid, 0, 1);
			grid.add(scoreList, 1, 1);

			Scene scene = new Scene(grid, scene_width, scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:
					playerMoved(0, -1, "up");
					break;
				case DOWN:
					playerMoved(0, +1, "down");
					break;
				case LEFT:
					playerMoved(-1, 0, "left");
					break;
				case RIGHT:
					playerMoved(+1, 0, "right");
					break;
				default:
					break;
				}
			});
			
			pair pa = getRandomFreePosition();
			Player harry = new Player("Harry",pa.getX(),pa.getY(),"up");
			players.add(harry);
			fields[pa.getX()][pa.getY()].setGraphic(new ImageView(hero_up));
		
			// Input field: username made by Christian Eld.
			String playerName = JOptionPane.showInputDialog("enter a player name: ");
			InputStream convert = new ByteArrayInputStream(playerName.getBytes());
			BufferedReader username = new BufferedReader(new InputStreamReader(convert));
			
			String user = new String();
			for (String line; (line = username.readLine()) != null; user += line);
			
			pair p = getRandomFreePosition();
			me = new Player(user, p.getX(), p.getY(), "up");
			players.add(me);
			fields[p.getX()][p.getY()].setGraphic(new ImageView(hero_up));
			connectToServer();
		
			scoreList.setText(getScoreList());
			System.out.println("bliver kaldt");
			this.outToServer.writeBytes(me.toString() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connectToServer() {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		try {
			clientThread = new ClientThread(this, connectionSocket, inFromServer, outToServer);
			clientThread.start();

		} catch (Exception e) {
			e.getMessage();
		}
	}

	public pair getRandomFreePosition()
	// finds a random new position which is not wall
	// and not occupied by other players
	{
		int x = 1;
		int y = 1;
		boolean found = false;
		while (!found) {
			Random r = new Random();
			x = Math.abs(r.nextInt() % 18) + 1;
			y = Math.abs(r.nextInt() % 18) + 1;
			if (Generel.board[y].charAt(x) == ' ') {
				found = true;
				for (Player p : players) {
					if (p.xpos == x && p.ypos == y)
						found = false;
				}

			}
		}
		pair p = new pair(x, y);
		return p;
	}

	public void movePlayerOnScreen(int oldx, int oldy, int newx, int newy, String direction) {
		Platform.runLater(() -> {
			fields[oldx][oldy].setGraphic(new ImageView(image_floor));
		});
		Platform.runLater(() -> {
			if (direction.equals("right")) {
				fields[newx][newy].setGraphic(new ImageView(hero_right));
			}
			;
			if (direction.equals("left")) {
				fields[newx][newy].setGraphic(new ImageView(hero_left));
			}
			;
			if (direction.equals("up")) {
				fields[newx][newy].setGraphic(new ImageView(hero_up));
			}
			;
			if (direction.equals("down")) {
				fields[newx][newy].setGraphic(new ImageView(hero_down));
			}
			;
		});

	}

	public void updatePlayer(int delta_x, int delta_y, String direction) {
		me.direction = direction;
		int x = me.getXpos(), y = me.getYpos();

		if (Generel.board[y + delta_y].charAt(x + delta_x) == 'w') {
			me.addPoints(-1);
		} else {
			// prepared for collision detection
			// not quite relevant in single plaver version
			Player p = getPlayerAt(x + delta_x, y + delta_y);
			if (p != null) {
				me.addPoints(10);
				// update the other player
				p.addPoints(-10);
				pair pa = getRandomFreePosition();
				p.xpos = pa.getX();
				p.ypos = pa.getY();
				movePlayerOnScreen(x + delta_x, y + delta_y, pa.getX(), pa.getY(), p.direction);
				try {
					//System.out.println(p.toString());
					this.outToServer.writeBytes(p.toString() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				me.addPoints(1);
			movePlayerOnScreen(x, y, x + delta_x, y + delta_y, direction);
			me.setXpos(x + delta_x);
			me.setYpos(y + delta_y);
			try {
				//System.out.println(me.toString());
				this.outToServer.writeBytes(me.toString() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void updateScoreTable() {
		Platform.runLater(() -> {
			scoreList.setText(getScoreList());
		});
	}

	public void playerMoved(int delta_x, int delta_y, String direction) {
		updatePlayer(delta_x, delta_y, direction);
		updateScoreTable();
	}
	

	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			//System.out.println(p.toString());
			b.append(p +"\r\n");
		}
		return b.toString();
	}

	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			connectionSocket = new Socket("192.168.87.164",6900);
			outToServer = new DataOutputStream(connectionSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		launch(args);
	}

}
