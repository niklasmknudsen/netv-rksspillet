package gameserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
	private String clientSentence;
	private String capitalizedSentence;
	private Socket connectionSocket;
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	private ServerSocket welcomeSocket;
	private String sentence;
	private BufferedReader inFromServer;
	
	public ServerThread(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) {
		//this.welcomeSocket = welcomeSocket;
		this.connectionSocket = connectionSocket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(60000);
				clientSentence = inFromClient.readLine();
				
				System.out.println("from client" + clientSentence);
				if (clientSentence.contains(":exit")) {
					connectionSocket.close();
				} else if (clientSentence != null) {
					sentence = clientSentence.toUpperCase();
					System.out.println("Till client: " + sentence);
					//capitalizedSentence = clientSentence.toUpperCase() + '\n';
					outToClient.writeBytes(sentence + '\n');	
				}
	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
