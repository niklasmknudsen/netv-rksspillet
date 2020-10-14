package gameserver;

import java.util.ArrayList;

import game.Player;

public class Common {
	private static ArrayList<Player> players = new ArrayList<Player>();
	private String tekst;

	public Common() {
		
	}
	
	public static ArrayList<Player> getPlayers() {
		return new ArrayList<>(players);
	}

	public static void addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
		}
	}
	
	public static void removePlayer(Player player) {
		if (players.contains(player)) {
			players.remove(player);
		}
	}
	
	public static int size() {
		return players.size();
	}
	
	public static Player findPlayer(String playerName) {
		Player requestedPlayer = null;
		
		for (int i = 0; i < players.size(); i++) {
			System.out.println(players.get(i).getName());
			if (players.get(i).getName().equals(playerName)) {
				requestedPlayer = players.get(i);
			}
		}
		
		return requestedPlayer;
	}
}
