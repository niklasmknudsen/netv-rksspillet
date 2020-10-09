package gameserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import game.Player;

public class ServerThread extends Thread {
	private String newPlayerName;
	private String capitalizedSentence;
	private Socket connectionSocket;
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	private ServerSocket welcomeSocket;
	private String sentence;
	private BufferedReader inFromServer;
	
	// Common
	private static Common common = new Common();
	
	public ServerThread(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) {
		this.connectionSocket = connectionSocket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
	}
	
	@Override
	public void run() { // connection.getInetAddress().getHostName())
		try {
			while (true) {
				String clientPlayer = connectionSocket.getInetAddress().getHostName();
				String clientPlayerID = connectionSocket.getInetAddress().getHostAddress();
				
				newPlayerName = inFromClient.readLine(); // from client
				sentence = "Welcome to the server " + newPlayerName;
				
				Player newPlayer = new Player();
				newPlayer.setName(newPlayerName);
				newPlayer.setXpos(1);
				newPlayer.setYpos(1);
				newPlayer.setDirection("up");
				
				Common.addPlayer(newPlayer);
				outToClient.writeBytes(sentence + '\n');
				outToClient.writeBytes(Common.getPlayers().size() + "\n");
				sendPlayer(outToClient);
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
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
}
