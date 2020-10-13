package gameserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Generel;
import game.Player;
import game.pair;

public class ServerThread extends Thread {
	private String newPlayerName;
	private Socket connectionSocket;
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	private ServerSocket welcomeSocket;
	private String sentence;
	
	// Common
	private static Common common;

	public ServerThread(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient, Common common) {
		this.connectionSocket = connectionSocket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		this.common = common;
	}

	@Override
	public void run() { // connection.getInetAddress().getHostName())
		try {
			newPlayerName = inFromClient.readLine(); // from client
			sentence = "Welcome to the server " + newPlayerName;
			outToClient.writeBytes(sentence + '\n');
			

			pair p = getRandomFreePosition();
			Player newPlayer = new Player();
			newPlayer.setName(newPlayerName);
			newPlayer.setXpos(p.getX());
			newPlayer.setYpos(p.getY());
			newPlayer.setDirection("up");
			Common.addPlayer(newPlayer);

			
			while (true) {				
				String clientPlayer = connectionSocket.getInetAddress().getHostName();
				String clientPlayerID = connectionSocket.getInetAddress().getHostAddress();


				sendPlayer(outToClient);
				System.out.println("clients connected = " + Server.clients.size());
				for (ServerThread st: Server.clients) {
					if (newPlayer.getName().length() != 0) {
						st.updatePlayers(inFromClient.readLine());
					}
					else {
						System.out.println("player hasen't been created yet");
					}
				}

				sendPlayer(outToClient);
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void updatePlayers(String newPositions) {
		try {
			String[] receivedPosition = newPositions.split(",");
			String firstPlayer = receivedPosition[0];
			System.out.println("new positions: " + newPositions);
			String secondPlayer = "";
			String thirdPlayer = "";
			
			if (receivedPosition.length > 4 && receivedPosition[4] != null) {
				secondPlayer = receivedPosition[4];
				checkifExists(secondPlayer);
				Player newPlayer = new Player();
				newPlayer.setName(secondPlayer);
				newPlayer.setXpos(Integer.parseInt(receivedPosition[5]));
				newPlayer.setYpos(Integer.parseInt(receivedPosition[6]));
				newPlayer.setDirection(receivedPosition[7]);
				Common.addPlayer(newPlayer);
			}
			if (receivedPosition.length > 7 && receivedPosition[8] != null) {
				thirdPlayer = receivedPosition[8];
				checkifExists(thirdPlayer);
				Player newPlayer = new Player();
				newPlayer.setName(secondPlayer);
				newPlayer.setXpos(Integer.parseInt(receivedPosition[9]));
				newPlayer.setYpos(Integer.parseInt(receivedPosition[10]));
				newPlayer.setDirection(receivedPosition[11]);
				Common.addPlayer(newPlayer); 
			}

			for (int i = 0; i < Common.getPlayers().size(); i++) {
				if (Common.getPlayers().get(i).getName().equals(firstPlayer)) {
					int newX = Integer.parseInt(receivedPosition[1]);
					int newY = Integer.parseInt(receivedPosition[2]);
					String playerDirection = receivedPosition[3];
					Player foundPlayer = Common.getPlayers().get(i);
					System.out.println("first person: " + foundPlayer);
					foundPlayer.setXpos(newX);
					foundPlayer.setYpos(newY);
					foundPlayer.setDirection(playerDirection);
				}
				if (Common.getPlayers().get(i).getName().equals(secondPlayer)) {
					int newX = Integer.parseInt(receivedPosition[5]);
					int newY = Integer.parseInt(receivedPosition[6]);
					String playerDirection = receivedPosition[7];
					Player foundPlayer = Common.getPlayers().get(i);
					System.out.println("second person: " + foundPlayer);
					foundPlayer.setXpos(newX);
					foundPlayer.setYpos(newY);
					foundPlayer.setDirection(playerDirection);
				}
				if (Common.getPlayers().get(i).getName().equals(thirdPlayer)) {
					int newX = Integer.parseInt(receivedPosition[9]);
					int newY = Integer.parseInt(receivedPosition[10]);
					String playerDirection = receivedPosition[11];
					Player foundPlayer = Common.getPlayers().get(i);
					System.out.println("third person: " + foundPlayer);
					foundPlayer.setXpos(newX);
					foundPlayer.setYpos(newY);
					foundPlayer.setDirection(playerDirection);
				}
				
			}	
		} catch (NumberFormatException error) {
			error.printStackTrace();
		}
	}

	public static void sendPlayer(DataOutputStream outstream) {
		String s = "";
		for (Player p : Common.getPlayers()) {
			s = s + p.toString();
		}
		System.out.println(s);
		try {
			outstream.writeBytes(s + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void checkifExists(String playerName) {
		for (Player pl : Common.getPlayers()) {
			if (pl.getName().equals(playerName)) {
				Common.removePlayer(pl);
			}
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
				for (Player p : Common.getPlayers()) {
					if (p.xpos == x && p.ypos == y)
						found = false;
				}

			}
		}
		pair p = new pair(x, y);
		return p;
	}
}
