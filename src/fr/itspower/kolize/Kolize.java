package fr.itspower.kolize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.io.ByteStreams;

public class Kolize extends JavaPlugin {
	
	
	private static Kolize plugin;

	private World world;
	private Location lobby;
	private List<Location> points;
	private List<Joueur> joueurs;
	private boolean estEnCours;
	public static int mapSize = 100;
	private int id;

	public static BukkitTask task;
	
	public static final int mapReduction = (int) (mapSize*0.05);
	public static final String PREFIXE = "§3[§b§lKolize§3]§7 ";

	public void onEnable() {
		plugin = this;
		estEnCours = false;
		points = new ArrayList<Location>();
		joueurs = new ArrayList<Joueur>();
		
		reloadConfig();
		
		lobby = Utils.stringToLoc(getConfig().getString("kolize.lobby"));
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder set "+mapSize);
		
		if(lobby == null) {
			world = Bukkit.getWorlds().get(0);
			lobby = world.getSpawnLocation();
			System.out.println("Le lobby n'est pas définis.");
		} else {
			world = lobby.getWorld();
		}
		
		
		if(getConfig().contains("kolize.points")) {
			Set<String> indexs = getConfig().getConfigurationSection("kolize.points").getKeys(false);
			for(String idx : indexs) {
				points.add(Utils.stringToLoc(getConfig().getString("kolize.points."+idx)));
				System.out.println("Ajout du point de TP: "+idx);
			}
		}
		
		getServer().getPluginManager().registerEvents(new Events(), this);
		getCommand("Kolize").setExecutor(new Commandes());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory true");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule sendCommandFeedback false");
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(lobby);
			if(!p.isOp()) {
				Kolize.getK().addJoueur(p);
				Utils.resetPlayer(p);
			}
		}
		for(Chicken e : world.getEntitiesByClass(Chicken.class)) {
			e.remove();
		}
		for(ArmorStand e : world.getEntitiesByClass(ArmorStand.class)) {
			e.remove();
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

			@Override
			public void run() {
		        Plugin dependency = Bukkit.getPluginManager().getPlugin("BukkitLibs");
		        if (dependency == null) {
		            File dst = new File(getFile().getParentFile(), "BukkitLibs.jar");
		            InputStream remote;
					try {
						remote = new URL("https://www.dropbox.com/s/zgiolcjygwvjxpu/BukkitLibs.jar?dl=1").openStream();
						ByteStreams.copy(remote, new FileOutputStream(dst));
			            dependency = Bukkit.getPluginManager().loadPlugin(dst);
					} catch (IOException | UnknownDependencyException | InvalidPluginException | InvalidDescriptionException e) {
						e.printStackTrace();
					}
		        }
		        if (!dependency.isEnabled()) {
		            Bukkit.getPluginManager().enablePlugin(dependency);
		        }
			}
			
		});
	}
	
	public static Kolize getK() {
		return plugin;
	}

	public void addJoueur(Player p) {
		Joueur j = new Joueur(p);
		joueurs.add(j);
	}

	public List<Location> getPoints() {
		return points;
	}
	public List<Joueur> getJoueurs() {
		return joueurs;
	}

	public Joueur getJoueur(Player p) {
		for(Joueur j : joueurs) {
			if(j.getPlayer().getUniqueId().equals(p.getUniqueId()))
				return j;
		}
		return null;
	}

	public Location getLobby() {
		return lobby;
	}

	public void removeJoueur(Joueur p) {
		joueurs.remove(p);
	}

	public void endGame() {
		Joueur[] top = new Joueur[joueurs.size()];
		for(int i=0; i<joueurs.size(); i++) 
			top[i] = new Joueur(joueurs.get(i).getPlayer());

		Arrays.sort(top);
		
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage("§7               §f§lFIN DE LA PARTIE !");
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(" §7» §f§lCLASSEMENTS:");
		for(int i=0;i<=Math.min(10, top.length); i++)
			Bukkit.broadcastMessage(top[i].getPlayer().getName()+" - "+top[i].getPoints()+" pts.");
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(" §7» §f§lVotre score:");
		for(Joueur j : joueurs) {
			j.getPlayer().sendMessage("   §6Eliminations/Kills: §e"+j.getEliminations()+"/"+j.getKills()+"§7, §6Points: §e"+j.getPoints());
		}
		Bukkit.broadcastMessage(" ");

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.broadcastMessage("§c§lREDEMARRAGE DU SERVEUR");
				Kolize.getK().onEnable();
			}
		}, 800);
	}

	public boolean estEnCours() {
		return estEnCours;
	}

	public void removeJoueur(Player p) {
		for(Joueur j : joueurs) {
			if(j.getPlayer().getUniqueId().equals(p.getUniqueId())) {
				joueurs.remove(j);
				break;
			}
		}
	}

	public void setEstEnCours(boolean estEnCours) {
		this.estEnCours = estEnCours;
	}

	public void startGame() {
		task = new BukkitRunnable() {
			
			private boolean reduction = true;
			private Random genAlea = new Random();
			@Override
			public void run() {
				if(!Kolize.getK().estEnCours()) {
					cancel();
					return;
				}
				
				if(reduction && time%120==0) {
					if(time / 120 > 3) {
						Bukkit.broadcastMessage("reduction max de la border");
						reduction = false;
						return;
					}
					Bukkit.broadcastMessage(PREFIXE+"§7Réduction de la taille de la map à §f"+(mapSize-mapReduction*(time / 120))+"§7 blocs. §8["+getFormatedTime()+"]");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "worldborder add -"+mapReduction+" 20");
					mapSize -= mapReduction;
				}
				if(time%45==0) { // 45
					Location rayon = Kolize.getK().getWorld().getWorldBorder().getCenter();
					int x = genAlea.nextInt(mapSize) - mapSize/2;
					int z = genAlea.nextInt(mapSize) - mapSize/2;
					Utils.spawnChest(rayon.add(x, Kolize.getK().getWorld().getHighestBlockYAt(x, z), z));
					//Bukkit.broadcastMessage(PREFIXE+"§7Apparition d'un coffre ! "+x+" "+Kolize.getK().getWorld().getHighestBlockYAt(x, z)+" "+z);
				}
				
				time++;
			}
	    }.runTaskTimer(Kolize.getK(), 0, 20);
	}
	
	protected World getWorld() {
		return world;
	}

	private int time = 1;

	protected String getFormatedTime() {
		return String.format("%02d:%02d", time / 60, time % 60);
	}
	
	private static final String path = "";
	static void copyWorldToMain(String mapName, String newWorldName) {
			Bukkit.unloadWorld("world", false);
			deleteWorld(Bukkit.getWorld("world").getWorldFolder());
	        copyFileStructure(new File(path, mapName), new File(Bukkit.getWorldContainer(), newWorldName));
			Bukkit.unloadWorld("world", true);
	        //new WorldCreator(newWorldName).createWorld();
	    }
	
	public static void createDirectory() {
        File dir = new File(path);
        try {
            if (dir.mkdir()) {
                System.out.println("Directory  in " + path + " was created");
            } else {
                System.out.println("Directory  in " + path + " already exists");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private static void copyFileStructure(File source, File target){
        try {
            //Set<String> ignore = new HashSet<>(Arrays.asList("uid.dat", "session.lock"));
           
            //if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static boolean deleteWorld(File path){
        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat", "session.lock", "playerdata"));
        if (!ignore.contains(path.getName())){
            if (path.exists()){
                File files[] = path.listFiles();
                for (int i = 0; i < files.length; i++){
                    if (files[i].isDirectory()){
                        deleteWorld(files[i]);
                    }else{
                        files[i].delete();
                    }
                }
            }
        }
        return (path.delete());
    }
    
    public int getId() {
		return (id!=0)?id:new Random().nextInt(10000);
    }

	public void setId(int id) {
		this.id = id;
	}
}
