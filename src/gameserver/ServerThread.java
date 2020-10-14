package gameserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
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
	
	// HashSet with Players
	private static HashSet<Player> players;

	// last visited by player with attribute values
	private int xpos = 0;
	private int ypos = 0;
	private String direction = "";
	private String newPlayerJoined = "";
	private String playerName = "";
	
	public ServerThread(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient, HashSet<Player> players) {
		this.connectionSocket = connectionSocket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		this.players = players;
	}

	@Override
	public void run() { // connection.getInetAddress().getHostName())
		try {
			newPlayerJoined = inFromClient.readLine(); // from client
			Player newPlayer = null;
			String[] newPlayerDetails = newPlayerJoined.split(",");
			sentence = "Welcome to the server " + newPlayerDetails[0];
			outToClient.writeBytes(sentence + '\n');
			try {
				newPlayer = new Player(newPlayerDetails[0], Integer.parseInt(newPlayerDetails[1]), Integer.parseInt(newPlayerDetails[2]), newPlayerDetails[3]);
			} catch (NumberFormatException e) {
				e.getMessage();
			}
			
			if (!players.contains(newPlayer)) {
				players.add(newPlayer);
				System.out.println("added new player: " + newPlayerDetails[0]);
				this.sendPlayer(outToClient);
			}
			
			while (true) {				
				String clientPlayer = connectionSocket.getInetAddress().getHostName();
				String clientPlayerID = connectionSocket.getInetAddress().getHostAddress();
			
				String s = inFromClient.readLine();
				try {
					String [] playerDetails = s.split(",");
					playerName = playerDetails[0];
					xpos = Integer.parseInt(playerDetails[1]);
					ypos = Integer.parseInt(playerDetails[2]);
					direction = playerDetails[3];
					
					
					String response = playerName + " " + xpos + " " + ypos + " " + direction;
					System.out.println(response);
				} catch (NumberFormatException err) {
					err.getMessage();
				}


				for (ServerThread st: Server.playerClients) {
					st.sendPlayer(outToClient);
				}
				this.connectionSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public synchronized void update(String s) {
		try {
			this.outToClient.writeBytes(s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void sendPlayer(DataOutputStream outstream) {
		String s = "";
		for (Player p : players) {
			s = s + p.toString();
		}
		System.out.println(s);
		try {
			
			outstream.writeBytes(s + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public StringBuffer GetUpdate() {
		
		StringBuffer a = new StringBuffer();
		a.append(String.valueOf(xpos) + ",");
		a.append(String.valueOf(ypos) + ",");
		a.append(direction + ",");
		a.append(playerName);
	
		return a;
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
