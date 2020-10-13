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
	static HashSet<ServerThread> playerClients = new HashSet<>();
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6900);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			ServerThread playerClient = new ServerThread(connectionSocket, inFromClient, outToClient, players);
			playerClients.add(playerClient);
			playerClient.start();
			
		}
	}

}
