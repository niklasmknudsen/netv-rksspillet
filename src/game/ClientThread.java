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
			String firstPlayer = "";
			int xpos = 0;
			int ypos = 0;
			String direction = "";
			
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
						outToServer.writeBytes(main.me.getName() + "," + x + "," + y + "," + direc + "\n");
						
						String receivedData = inFromServer.readLine();
						String[] resultSet = receivedData.split(",");
						System.out.println("receivedData: " + receivedData);
						firstPlayer = resultSet[0];
						xpos = Integer.parseInt(resultSet[1]);
						ypos = Integer.parseInt(resultSet[2]);
						direction = resultSet[3];
						
						
						boolean hasPlayer = false;
						
						for (Player player : Main.players) {
							if (player.name.equals(firstPlayer)) 
							{
								
								System.out.println("updating: " + firstPlayer);
								
								int oldx = player.getXpos();
								int oldy = player.getYpos();
								hasPlayer = true;
								player.xpos = xpos;
								player.ypos = ypos;
								player.direction = direction;
								main.movePlayerOnScreen(oldx, oldy, xpos, ypos, direction);
								main.updateScoreTable();
								
							}
						}
						
						if (!hasPlayer) {
							Player p = new Player(firstPlayer,xpos,ypos,direction); 

							Main.players.add(new Player(firstPlayer,xpos,ypos,direction));
							System.out.println("Added player: " + firstPlayer);
							main.movePlayerOnScreen(xpos, ypos, xpos, ypos, direction);;
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
