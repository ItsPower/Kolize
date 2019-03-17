package fr.itspower.kolize;

import org.bukkit.entity.Player;

public class Joueur implements Comparable<Joueur> {
	
	private Player p;
	private int kills;
	private int eliminations;
	//private int place;
	
	private byte vies = 2;
	
	public Joueur(Player p) {
		this.p = p;
	}

	public Player getPlayer() {
		return p;
	}

	public void addKill() {
		kills++;
	}
	public void addElimination() {
		eliminations++;
	}
	
	public int getPoints() {
		return kills + eliminations * 2;
	}

	public byte getVies() {
		return vies;
	}
	public void reduireVie() {
		vies--;
	}

	@Override
	public int compareTo(Joueur comparaison) {
		int compareQuantity = comparaison.getPoints(); 
		// ordre croissant
		return compareQuantity - getPoints();
	}

	public int getEliminations() {
		return eliminations;
	}

	public int getKills() {
		return kills;
	}
}
