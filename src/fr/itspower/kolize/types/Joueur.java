package fr.itspower.kolize.types;

import org.bukkit.entity.Player;

public class Joueur implements Comparable<Joueur> {
	
	private Player p;
	private int kills;
	private int eliminations;
	private Etat etat;
	//private int place;
	
	private byte vies = 2;
	
	public Joueur(Player p) {
		this.p =            p;
		this.etat =         Etat.LOBBY;
		this.kills =        0;
		this.eliminations = 0;
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

	// ordre croissant
	@Override
	public int compareTo(Joueur comparaison) {
		int compareQuantity = comparaison.getPoints(); 
		return compareQuantity - getPoints();
	}

	public int getEliminations() {
		return eliminations;
	}

	public int getKills() {
		return kills;
	}

	public Etat getEtat() {
		return etat;
	}

	public void setEtat(Etat etat) {
		this.etat = etat;
	}
}
