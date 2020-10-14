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
			String resp = inFromClient.readLine();
			String[] response = resp.split(",");
			playerName = response[0];
			try {
				xpos = Integer.parseInt(response[1]);
				ypos = Integer.parseInt(response[2]);
			} catch(NumberFormatException error) {
				error.getMessage();
			}		
			
			direction = response[3];
			
			
			String welcomePlayer = inFromClient.readLine();
			String[] playerInfo = welcomePlayer.split(",");
			sentence = "Welcome to the server " + playerInfo[0];
			System.out.println("sentence: " + sentence);
			outToClient.writeBytes(sentence + '\n');
			
			
			
			for (ServerThread pc : Server.playerClients) {
				String currentStats = pc.GetUpdate().toString();
				System.out.println(currentStats + " the currentStats");
				if (!pc.equals(this)) {
					this.outToClient.writeBytes(currentStats + "\n");
				}
			}
			
			
			while (true) {
				String receivedData = inFromClient.readLine();
				
				String[] playerDescription = receivedData.split(",");
				playerName = playerDescription[0];
				try {
					xpos = Integer.parseInt(response[1]);
					ypos = Integer.parseInt(response[2]);
				} catch(NumberFormatException error) {
					error.getMessage();
				}	
				direction = playerDescription[3];
				System.out.println(receivedData);
				for (ServerThread pc : Server.playerClients) {
					if (!pc.equals(this)) {
						pc.update(receivedData);
					}
				}
				
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
		a.append(playerName + ",");
		a.append(String.valueOf(xpos) + ",");
		a.append(String.valueOf(ypos) + ",");
		a.append(direction);
	
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
