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
				
				for (ServerThread st: Server.clients) {
					st.updatePlayers(inFromClient.readLine());
				}

				sendPlayer(outToClient);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updatePlayers(String newPositions) {
		try {
			String[] receivedPosition = newPositions.split(",");
			String playerToUpdate = receivedPosition[0];
			int newX = Integer.parseInt(receivedPosition[1]);
			int newY = Integer.parseInt(receivedPosition[2]);
			String playerDirection = receivedPosition[3];
			
			
			for (int i = 0; i < Common.getPlayers().size(); i++) {
				if (Common.getPlayers().get(i).getName().equals(playerToUpdate)) {
					Player foundPlayer = Common.getPlayers().get(i);
					foundPlayer.setXpos(newX);
					foundPlayer.setYpos(newY);
					foundPlayer.setDirection(playerDirection);
				}
				throw new IllegalArgumentException("player doesn't exists");
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
