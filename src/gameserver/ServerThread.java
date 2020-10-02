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
	private String capitalizedSentence;
	private Socket connectionSocket;
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	private ServerSocket welcomeSocket;
	private String sentence;
	private BufferedReader inFromServer;
	// added players arraylist for random pos
	public static List<Player> players = new ArrayList<Player>();

	// Common
	private static Common common;

	public ServerThread(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) {
		this.connectionSocket = connectionSocket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		this.common = new Common();
	}

	@Override
	public void run() { // connection.getInetAddress().getHostName())
		try {
			while (true) {
				String clientPlayer = connectionSocket.getInetAddress().getHostName();
				String clientPlayerID = connectionSocket.getInetAddress().getHostAddress();

				newPlayerName = inFromClient.readLine(); // from client
				sentence = "Welcome to the server " + newPlayerName;

				Player newPlayer = new Player(newPlayerName);
				newPlayer.setXpos(1);
				newPlayer.setYpos(1);
				newPlayer.setDirection("up");

				common.addPlayer(newPlayer);
				outToClient.writeBytes(sentence + '\n');

				sendPlayer(outToClient);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendPlayer(DataOutputStream outstream) {
		String s = "";
		for (Player p : common.getPlayers()) {
			s = s + p.toString();
		}
		s = s + "";
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
				for (Player p : players) {
					if (p.xpos == x && p.ypos == y)
						found = false;
				}

			}
		}
		pair p = new pair(x, y);
		return p;
	}
}
