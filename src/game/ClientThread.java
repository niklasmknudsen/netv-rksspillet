package game;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;


public class ClientThread extends Thread {
		private Socket connectionSocket;
		private BufferedReader inFromServer;
		private DataOutputStream outToServer;
		private Main main;
		
		public ClientThread(Main main, Socket connectionSocket, BufferedReader inFromServer, DataOutputStream outToServer) {
			this.main = main;
			this.connectionSocket = connectionSocket;
			this.inFromServer = inFromServer;
			this.outToServer = outToServer;
		}

		@Override
		public void run() {
			String sentence;
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			
			// Players
			String playerName = "";
			int playerPositionX = 0;
			int playerPositionY = 0;
			String playerDirection = "";
			
			try {
				System.out.println(inFromServer.readLine() + " U can move the character by pressing on the arrow keys");
				// common get players
				String response = inFromServer.readLine();
				System.out.println(response);
				String[] serverGeneratedPosition = response.split(","); 
				
				try {
				    System.out.println("server generated position: " + response);
					playerName = serverGeneratedPosition[0];
					playerPositionX = Integer.parseInt(serverGeneratedPosition[1]);
					playerPositionY = Integer.parseInt(serverGeneratedPosition[2]);
					playerDirection = serverGeneratedPosition[3];
					
					boolean gameActivated = false;
					 
					while (true) {
						
						for (Player player: main.players) {
							if (player.getName().equals(playerName)) {
								
								int oldX = player.getXpos();
								int oldY = player.getYpos();
								player.setXpos(playerPositionX);
								player.setYpos(playerPositionY);
								player.setDirection(playerDirection);
								main.movePlayerOnScreen(oldX, oldY, playerPositionX, playerPositionY, playerDirection);
								main.updateScoreTable();
								gameActivated = true;
							}
						}
						
						
						if (!gameActivated) {
							Player newPlayer = new Player(playerName, playerPositionX, playerPositionY, playerDirection);
							System.out.println("new player has spawned " + playerName);
							main.players.add(newPlayer);
							main.movePlayerOnScreen(playerPositionX, playerPositionY, playerPositionX, playerPositionY, playerDirection);
							main.updateScoreTable();
						} 
						
					
					}
					
					} catch (NumberFormatException error) {
						error.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				//this.connectionSocket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

}
