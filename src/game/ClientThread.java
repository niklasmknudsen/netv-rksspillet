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
			String secondPlayer = "";
			String thirdPlayer = "";
			
			try {
				String newPlayerName = inFromUser.readLine();
				// Setting up standard players
				outToServer.writeBytes(newPlayerName + "\n");
				System.out.println(inFromServer.readLine() + " U can move the character by pressing on the arrow keys");
			
				
				// common get players
				String response = inFromServer.readLine();
				System.out.println(response);
				String[] commands = response.split(",");
				
				try {
					System.out.println("commands: " + response);
					String playerName = commands[0];
					int playerPositionX = Integer.parseInt(commands[1]);
					int playerPositionY = Integer.parseInt(commands[2]);
					String playerDirection = commands[3];

					main.me = new Player();
					main.me.setName(playerName);
					main.me.setXpos(playerPositionX);
					main.me.setYpos(playerPositionY);
					main.me.setDirection(playerDirection);
					main.players.add(main.me);
					
					
					
					while (true) {
						main.updateScoreTable();
						int x = main.me.getXpos();
						int y = main.me.getYpos();
						String direc = main.me.getDirection();
						System.out.println("direction: " + direc);
						outToServer.writeBytes(main.me.getName() + "," + x + "," + y + "," + direc + "\n");
						
						System.out.println(main.players.size());
						String receivedData = inFromServer.readLine();
						String[] resultSet = receivedData.split(",");
						System.out.println("receivedData: " + receivedData);
						firstPlayer = resultSet[0];
						
						if (resultSet.length > 4 && resultSet[4] != null) {
							secondPlayer = resultSet[4];
							boolean check = checkifExists(secondPlayer);
							System.out.println(check);
							Player newPlayer = new Player();
							newPlayer.setName(secondPlayer);
							newPlayer.setXpos(Integer.parseInt(resultSet[5]));
							newPlayer.setYpos(Integer.parseInt(resultSet[6]));
							newPlayer.setDirection(resultSet[7]);
							main.players.add(newPlayer);
							//updatePlayer(Integer.parseInt(resultSet[5]), Integer.parseInt(resultSet[6]), resultSet[7]);
							//updateScoreTable();
							
						}
						if (resultSet.length > 8 && resultSet[8] != null) {
							thirdPlayer = resultSet[8];
							boolean check = checkifExists(thirdPlayer);
							System.out.println(check);
							Player newPlayer = new Player();
							newPlayer.setName(thirdPlayer);
							newPlayer.setXpos(Integer.parseInt(resultSet[9]));
							newPlayer.setYpos(Integer.parseInt(resultSet[10]));
							newPlayer.setDirection(resultSet[11]);
							main.players.add(newPlayer);
							//updatePlayer(Integer.parseInt(resultSet[5]), Integer.parseInt(resultSet[6]), resultSet[7]);
							//updateScoreTable();
						}
	
						for (Player player: main.players) {
							System.out.println("players online: " + main.players.size());
							if (player.getName().equals(firstPlayer)) {
								System.out.println("player: " + firstPlayer + " is moving.....");
								int oldX = player.getXpos();
								int oldY = player.getYpos();
								
								int newX = Integer.parseInt(resultSet[1]);
								int newY = Integer.parseInt(resultSet[2]);
								
								main.movePlayerOnScreen(oldX, oldY, newX, newY, resultSet[3]);
							} 
							if (player.getName().equals(secondPlayer)) {
								System.out.println("player: " + secondPlayer + " is moving.....");
								int oldX = player.getXpos();
								int oldY = player.getYpos();
								
								int newX = Integer.parseInt(resultSet[5]);
								int newY = Integer.parseInt(resultSet[6]);
								
								main.movePlayerOnScreen(oldX, oldY, newX, newY, resultSet[7]);
							}  
							if (player.getName().equals(thirdPlayer)) {
								System.out.println("player: " + thirdPlayer + " is moving.....");
								int oldX = player.getXpos();
								int oldY = player.getYpos();
								
								int newX = Integer.parseInt(resultSet[9]);
								int newY = Integer.parseInt(resultSet[10]);
								
								main.movePlayerOnScreen(oldX, oldY, newX, newY, resultSet[11]);
							}
							main.updateScoreTable();
						}
						//Thread.sleep(2000);
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
		
		
	public boolean checkifExists(String playerName) {
		for (Player pl : main.players) {
			if (pl.getName().equals(playerName)) {
				System.out.println("check if " + pl.getName() + " exists");
				main.players.remove(pl);
				return true;
			}
		}
		return false;
	}

	public static boolean isNumeric(String str) { 
		  try {  
		    Integer.parseInt(str);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
		}

}
