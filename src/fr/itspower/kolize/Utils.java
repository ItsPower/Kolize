package fr.itspower.kolize;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {
    
	public static String locToString(Location loc){
        return loc.getWorld().getName() + "!" + loc.getX() + "!" + loc.getY() + "!" + loc.getZ() + "!" + loc.getYaw() + "!" + loc.getPitch();
    }
	
    public static Location stringToLoc(String str) {
    	if(str == null) return Bukkit.getWorld("world").getSpawnLocation();
        return new Location(Bukkit.getWorld(str.split("!")[0]), Double.parseDouble(str.split("!")[1]), Double.parseDouble(str.split("!")[2]), Double.parseDouble(str.split("!")[3]), Float.parseFloat(str.split("!")[4]), Float.parseFloat(str.split("!")[5]));
    }
    
	public static void resetPlayer(Player p) {
		System.out.println("reseting player: "+p.getName());
		p.setMaxHealth(20);
    	p.setLevel(0);
    	p.setExp(0.0f);
    	p.setTotalExperience(0);
    	p.getInventory().clear();
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setWalkSpeed(0.2f);
		p.setGameMode(GameMode.ADVENTURE);
		p.getInventory().setHeldItemSlot(0);
    }

	public static ItemStack a1 =  new ItemBuilder(Material.IRON_HELMET, 1, (short)0).unbreakable().hideU().build();
	public static ItemStack a2 =  new ItemBuilder(Material.IRON_CHESTPLATE, 1, (short)0).unbreakable().hideU().build();
	public static ItemStack a3 =  new ItemBuilder(Material.IRON_LEGGINGS, 1, (short)0).unbreakable().hideU().build();
	public static ItemStack a4 =  new ItemBuilder(Material.IRON_BOOTS, 1, (short)0).unbreakable().hideU().build();
	public static ItemStack epee =  new ItemBuilder(Material.STONE_SWORD, 1, (short)0).unbreakable().hideU().build();
	public static ItemStack peche =  new ItemBuilder(Material.FISHING_ROD, 1, (short)0).unbreakable().hideU().build();
	public static ItemStack sable =  new ItemBuilder(Material.SAND, 32, (short)0).build();
	public static ItemStack gapple =  new ItemBuilder(Material.GOLDEN_APPLE, 1, (short)0).build();
	public static ItemStack arc =  new ItemBuilder(Material.BOW, 1, (short)0).unbreakable().hideU().build();
	public static ItemStack fleche =  new ItemBuilder(Material.ARROW, 5, (short)0).build();
	public static ItemStack bouffe =  new ItemBuilder(Material.COOKED_BEEF, 32, (short)0).build();

	public static ItemStack poison2 = createPotion(PotionEffectType.POISON, 1, 1, 20*20, true, (byte)8196);
	public static ItemStack speed2 = createPotion(PotionEffectType.SPEED, 1, 1, 20*20, true, (byte)8194);
	
    public static void giveStuff(Player p) {
		p.getInventory().setHelmet(a1);
		p.getInventory().setChestplate(a2);
		p.getInventory().setLeggings(a3);
		p.getInventory().setBoots(a4);
		p.getInventory().setItem(0, epee);
		p.getInventory().setItem(1, peche);
		p.getInventory().setItem(2, arc);
		p.getInventory().setItem(3, gapple);
		p.getInventory().setItem(4, sable);
		p.getInventory().setItem(7, bouffe);
		p.getInventory().setItem(8, fleche);
	}
    
    public static void spawnChest(Location tmp) {
    	Location l = new Location(tmp.getWorld(), 
    			tmp.getBlockX()+.5,
				tmp.getBlockY()+15,
				tmp.getBlockZ()+.5);
        ArmorStand a = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        
        a.setHelmet(new ItemStack(Material.CHEST));
        a.setVisible(false);
        a.setGravity(false);
        
        Location tg = new Location(l.getWorld(), 
        		l.getBlockX()+1,
        		l.getBlockY()+1,
        		l.getBlockZ());
        ArmorStand b = (ArmorStand) l.getWorld().spawnEntity(tg, EntityType.ARMOR_STAND);

        b.setVisible(false);
        b.setGravity(false);
        List<Entity> chickens = new ArrayList<Entity>();
        
        for (int i = 0; i < 10; i++) {
			Chicken chicken = (Chicken) l.getWorld().spawnEntity(l.clone().add(new Random().nextDouble()*2-1, 4, new Random().nextDouble()*2-1), EntityType.CHICKEN);
			chickens.add(chicken);
			chicken.setLeashHolder(b);
		}
        chickens.add(a);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				l.add(0, -0.15, 0);
				tg.add(0, -0.15, 0);
				
				if(l.getBlock().getType().isSolid()) {
					Bukkit.broadcastMessage("place chest");
					for(Entity tg : chickens) {
						tg.remove();
					}
					l.add(0, 1, 0);
					l.getBlock().setType(Material.CHEST);
					a.remove();
					b.remove();
					
					Chest c = (Chest) l.getBlock().getState();
					Bukkit.broadcastMessage("place items");
					c.getBlockInventory().addItem(gapple);
					c.getBlockInventory().addItem(sable);
					c.getBlockInventory().addItem(speed2);
					c.getBlockInventory().addItem(poison2);
					
					cancel();
					return;
				}
				a.teleport(l);
				b.teleport(tg);
			}
		}.runTaskTimer(Kolize.getK(), 0, 1);
    }
    
    private static ItemStack createPotion(PotionEffectType type, int ammount, int amplifier, int duration, boolean override, byte bits) {
    	ItemStack is = new ItemStack(Material.POTION, ammount, bits);
    	 
    	ItemMeta im = is.getItemMeta();
    	PotionMeta pm = (PotionMeta) im;
    	 
    	pm.setMainEffect(type);
    	pm.addCustomEffect(new PotionEffect(type, duration, amplifier), override);
    	is.setItemMeta(pm);
    	return is;
    }
}