package gameserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Generel;
import game.Player;
import game.pair;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6900);

		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			ServerThread t1 = new ServerThread(connectionSocket, inFromClient, outToClient);
			t1.start();

		}
	}

}
