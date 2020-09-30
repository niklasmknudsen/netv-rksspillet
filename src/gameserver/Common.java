package gameserver;

import java.util.ArrayList;

import game.Player;

public class Common {
	private ArrayList<Player> players;
	private String tekst;

	public Common() {
		this.players = new ArrayList();
	}
	
	public ArrayList<Player> getPlayers() {
		return new ArrayList<>(players);
	}

	public void addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
		}
	}
	
	public void removePlayer(Player player) {
		if (players.contains(player)) {
			players.add(player);
		}
	}
	
}
