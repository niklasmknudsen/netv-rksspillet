package gameserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import game.Player;

public class Server {
	
	private static HashSet<Player> players = new HashSet<Player>();
	static HashSet<ServerThread> clients = new HashSet<>();
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6900);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			ServerThread t1 = new ServerThread(connectionSocket, inFromClient, outToClient, players);
			clients.add(t1);
			t1.start();
			
		}
	}

}
