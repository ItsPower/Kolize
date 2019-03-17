package fr.itspower.kolize.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.itspower.kolize.Kolize;
import fr.itspower.kolize.Utils.Utils;
import fr.itspower.kolize.types.Joueur;

public class Commandes implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("Kolize")) {
			
        	if (!s.isOp()) {
        		s.sendMessage(Kolize.PREFIXE + "Vous n'avez pas les permissions requises.");
        		return true;
        	}
        	
        	if (args.length == 0) {
        		s.sendMessage(Kolize.PREFIXE + "§e/k start §f- §7Démarre la partie.");

        		s.sendMessage(Kolize.PREFIXE + "§e/k setLobby §f- §7Définis le lobby.");
        		
        		s.sendMessage(Kolize.PREFIXE + "§e/k addPoint §f- §7Ajouter un point de téléportation.");
        		
        		s.sendMessage(Kolize.PREFIXE + "§e/k removePoint §f- §7Supprime le dernier point.");
        		return true;
        		
        	} else if (args.length == 1) {
        		
        		
        		
        		if (args[0].equalsIgnoreCase("setLobby") && s instanceof Player) {
        			setLobby(((Player) s).getLocation());
					s.sendMessage(Kolize.PREFIXE + "Position du lobby définie.");
        		}

        		if(args[0].equalsIgnoreCase("addPoint")) {
        			FileConfiguration fc = Kolize.getK().getConfig();
        			Location loc = ((Player)s).getLocation();
        			int idx = 0;
        			
        			if(fc.contains("kolize.points")) {
        				Set<String> locs = fc.getConfigurationSection("kolize.points").getKeys(false);
        				idx = locs.size();
        			}
        			
        			addPoint(idx, loc);
        			s.sendMessage(Kolize.PREFIXE + "Point ajouté: "+idx);
        		}
        		
        		if(args[0].equalsIgnoreCase("removePoint")) {
        			FileConfiguration fc = Kolize.getK().getConfig();
        			if(fc.contains("kolize.points")) {
        				Set<String> locs = fc.getConfigurationSection("kolize.points").getKeys(false);
        				if(locs.size() != 0) {
        					removePoint(locs.size()-1);
            				s.sendMessage(Kolize.PREFIXE + "Point "+(locs.size()-1)+" supprimé.");
        				} else {
            				s.sendMessage(Kolize.PREFIXE + "Il n'y a plus de points");
            			}
        			} else {
        				s.sendMessage(Kolize.PREFIXE + "Remove impossible.");
        			}
        		}

        		if (args[0].equalsIgnoreCase("stop")) {
        			Kolize.getK().onEnable();
        		}
        	} else if(args.length == 2) {
        		if (args[0].equalsIgnoreCase("start")) {
        			try {
						int id = Integer.parseInt(args[1]);
						if(id <= 0) {
							s.sendMessage("Veuillez préciser un nombre positif pour id_partie");
							return true;
						}
	        			Kolize.getK().setId(id);
					} catch (NumberFormatException e) {
						s.sendMessage("Veuillez préciser un nombre positif pour id_partie");
						return true;
					}

        			Kolize.getK().setEstEnCours(true);
        			Kolize.getK().startGame();
					s.sendMessage(Kolize.PREFIXE + "Démarrage");
					List<Joueur> players = new ArrayList<Joueur>();
					List<Location> points = new ArrayList<Location>();
					Random r = new Random();
					for(Joueur j : Kolize.getK().getJoueurs()) {
						players.add(j);
					}

					for(Location loc : Kolize.getK().getPoints()) {
						points.add(loc);
        			}
					
					if(points.size() < players.size()){
						s.sendMessage("Pas assez de points par rapport au nombre de joueurs.");
						return true;
					}
					
					new BukkitRunnable() {
						@Override
						public void run() {
							safeMessage(s, Kolize.PREFIXE + "run()");
							if(players.isEmpty()) {
								safeMessage(s, Kolize.PREFIXE + "plus de joueurs à tp");
								cancel();
								return;
							}
							int idx = r.nextInt(points.size());
							
							Joueur pp = players.get(0);
							
							if(pp == null || pp.getPlayer() == null || pp.getPlayer().isDead() || !pp.getPlayer().isOnline())
								return;
							
							safeMessage(s, Kolize.PREFIXE + "joueur téléporté: "+pp.getPlayer().getName());
							
							pp.getPlayer().teleport(points.get(idx));
							Utils.giveStuff(pp.getPlayer());
							
							players.remove(0);
							points.remove(idx);
						}
				    }.runTaskTimer(Kolize.getK(), 0, 20);
        		}
        	}
		}
		return true;
	}
	
	private void removePoint(int i) {
		Kolize.getK().getConfig().set("kolize.points."+i, null);
		Kolize.getK().saveConfig();
	}
	
	private void addPoint(int i, Location loc) {
		Kolize.getK().getConfig().set("kolize.points."+i, Utils.locToString(loc));
		Kolize.getK().saveConfig();
	}

	private void setLobby(Location loc) {
		Kolize.getK().getConfig().set("kolize.lobby", Utils.locToString(loc));
		Kolize.getK().saveConfig();
	}
	
	protected void safeMessage(CommandSender p, String msg) {
		Bukkit.getScheduler().runTask(Kolize.getK(), new Runnable() {
			@Override
			public void run() {
				p.sendMessage(msg);
			}
		});
	}
}
