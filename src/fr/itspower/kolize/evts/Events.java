package fr.itspower.kolize.evts;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.itspower.kolize.Kolize;
import fr.itspower.kolize.Utils.ItemBuilder;
import fr.itspower.kolize.Utils.Utils;
import fr.itspower.kolize.types.Etat;
import fr.itspower.kolize.types.Joueur;

public class Events implements Listener {
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.setJoinMessage("§7[§a+§7] §f"+e.getPlayer().getName()+"§7 s'est connecté.");
		Player p = e.getPlayer();
		if(!p.isOp()) {
			if(Kolize.getK().estEnCours()) {
				Kolize.getK().removeJoueur(p);
			}
			e.getPlayer().teleport(Kolize.getK().getLobby());
			Utils.resetPlayer(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.setQuitMessage("§7[§c-§7] §f"+e.getPlayer().getName()+"§7 s'est déconnecté.");
		Kolize.getK().removeJoueur(e.getPlayer());
	}

	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		e.setDeathMessage("");
		if(e.getEntity().getKiller() instanceof Player) {
			
			Joueur attacker = Kolize.getK().getJoueur(e.getEntity().getKiller());
			Joueur victim = Kolize.getK().getJoueur(e.getEntity());
			
			if(attacker != null && victim != null) {
				
				if(victim.getVies() <= 0) {
					attacker.addElimination();
					victim.setEtat(Etat.ELIMINE);
					Bukkit.broadcastMessage(Kolize.PREFIXE+victim.getPlayer().getName()+"§7 est éliminé !");
					
				} else {
					attacker.addKill();
					victim.reduireVie();
				}
				
				Bukkit.broadcastMessage(Kolize.PREFIXE+attacker.getPlayer().getName()+" §7a tué §f"+victim.getPlayer().getName()+"§7.");
				
				attacker.getPlayer().getInventory().addItem(new ItemBuilder(Material.SAND, 16, (short)0).build());
				attacker.getPlayer().setHealth(Math.min(attacker.getPlayer().getMaxHealth(), attacker.getPlayer().getHealth()+6));
				attacker.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 20*20));
			}
		}
		
		//TODO tous les joueurs doivent etre elim
		if(Kolize.getK().getJoueurs().size() == 1) {
			Kolize.getK().endGame();
			return;
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if(!Kolize.getK().estEnCours() || Kolize.getK().getJoueur(e.getPlayer()) == null) {
			e.setRespawnLocation(Kolize.getK().getLobby());
			Utils.resetPlayer(e.getPlayer());
		} else {
			Random r = new Random();
			e.setRespawnLocation(
					Kolize.getK().getPoints().get(
							r.nextInt(
									Kolize.getK().getPoints().size())));
		}
		
		Joueur victim = Kolize.getK().getJoueur(e.getPlayer());
		if(victim == null)
			return;
		Utils.giveStuff(e.getPlayer());
		e.getPlayer().sendMessage(Kolize.PREFIXE+((victim.getVies()==0)?"§7Vous n'avez plus de vie, la prochaine mort sera la derniere !":"§7Il vous reste §f"+victim.getVies()+"§7 vie."));
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(!e.getPlayer().isOp())
			e.setCancelled(true);
	}
}
