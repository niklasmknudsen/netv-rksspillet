package game;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


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
				/*String response = inFromServer.readLine();
				System.out.println(response);
				String[] serverGeneratedPosition = response.split(","); */
				
				try {
					/* System.out.println("server generated position: " + response);
					String playerName = serverGeneratedPosition[0];
					int playerPositionX = Integer.parseInt(serverGeneratedPosition[1]);
					int playerPositionY = Integer.parseInt(serverGeneratedPosition[2]);
					String playerDirection = serverGeneratedPosition[3];
					
					main.me = new Player();
					main.me.setName(playerName);
					main.me.setXpos(playerPositionX);
					main.me.setYpos(playerPositionY);
					main.me.setDirection(playerDirection);
					main.players.add(main.me); */
					
					
					while (true) {
						int x = main.me.getXpos();
						int y = main.me.getYpos();
						String direc = main.me.getDirection();
						outToServer.writeBytes(main.me.getName() + "," + x + "," + y + "," + direc + "," + "\n");
						
						String receivedData = inFromServer.readLine();
						String[] resultSet = receivedData.split(",");
						System.out.println("receivedData: " + receivedData);
						playerName = resultSet[0];
						playerPositionX = Integer.parseInt(resultSet[1]);
						playerPositionY = Integer.parseInt(resultSet[2]);
						playerDirection = resultSet[3];
						
						
						boolean hasPlayer = false;
						if (main.me.getName().equals(playerName)) {
							main.me.setName(playerName);
							main.me.setXpos(playerPositionX);
							main.me.setYpos(playerPositionY);
							main.me.setDirection(playerDirection);
						}
						
						
						for (Player player : Main.players) {
							
							if (player.getName().equals(playerName)) {
								System.out.println("player " + playerName + " is moving..");
								
								int oldx = player.getXpos();
								int oldy = player.getYpos();
								
								hasPlayer = true;
								player.setXpos(playerPositionX);
								player.setYpos(playerPositionY);
								player.setDirection(playerDirection);
								main.movePlayerOnScreen(oldx, oldy, playerPositionX, playerPositionY, playerDirection);
								main.updateScoreTable();
								
							}
						}
						
						if (!hasPlayer) {
							Player newPlayer = new Player(playerName,playerPositionX,playerPositionY,playerDirection); 
							main.players.add(newPlayer);
							System.out.println("Added player: " + playerName);
							main.movePlayerOnScreen(playerPositionX, playerPositionY, playerPositionX, playerPositionY, playerDirection);
							main.updateScoreTable();
						}
						
					}
					
					} catch (NumberFormatException error) {
						error.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				connectionSocket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

}
