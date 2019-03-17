package fr.itspower.kolize;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

public class ItemBuilder implements Listener {
     
    private ItemStack                              is;
    public ItemBuilder(final Material mat, int i, short s) {
        is = new ItemStack(mat, i, s);
    }
     
    public ItemBuilder(final ItemStack is) {
        this.is = is;
    }
     
    public ItemBuilder amount(final int amount) {
        is.setAmount(amount);
        return this;
    }
     
    public ItemBuilder name(final String name) {
        final ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        is.setItemMeta(meta);
        return this;
    }
     
    public ItemBuilder lore(final String name) {
        final ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        }
        lore.add(name);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return this;
    }
    
    public ItemBuilder lore(final ArrayList<String> lines) {
        final ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        }
        for(String s : lines) {
        	lore.add(s);
    	}
        meta.setLore(lore);
        is.setItemMeta(meta);
        return this;
    }
     
    public ItemBuilder durability(final int durability) {
        is.setDurability((short) durability);
        return this;
    }
     
    public ItemBuilder setSkull(final String head) {
         SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
         skullMeta.setOwner(head);
         is.setItemMeta(skullMeta);
        return this;
    }
     
    public ItemBuilder setNmsStackInt(final String key, Integer value) {
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(is);
        nms.getTag().setInt(key, value);
        this.is = CraftItemStack.asBukkitCopy(nms);
        return this;
    }
     
    public ItemBuilder setNmsStackString(final String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(is);
        nms.getTag().setString(key, value);
        this.is = CraftItemStack.asBukkitCopy(nms);
        return this;
    }
     
    @SuppressWarnings("deprecation")
    public ItemBuilder data(final int data) {
        is.setData(new MaterialData(is.getType(), (byte) data));
        return this;
    }
     
    public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }
     
    public ItemBuilder enchantment(final Enchantment enchantment) {
        is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }
     
    public ItemBuilder type(final Material material) {
        is.setType(material);
        return this;
    }
     
    public ItemBuilder clearLore() {
        final ItemMeta meta = is.getItemMeta();
        meta.setLore(new ArrayList<String>());
        is.setItemMeta(meta);
        return this;
    }
 
    public ItemBuilder hideA() {
        final ItemMeta iM = is.getItemMeta();
        iM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        is.setItemMeta(iM);
        return this;
    }

    public ItemBuilder hideU() {
        final ItemMeta iM = is.getItemMeta();
        iM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        is.setItemMeta(iM);
        return this;
    }
    
    public ItemBuilder hideE() {
        final ItemMeta iM = is.getItemMeta();
        iM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        is.setItemMeta(iM);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (final Enchantment e : is.getEnchantments().keySet()) {
            is.removeEnchantment(e);
        }
        return this;
    }
     
    public ItemBuilder color(Color color) {
        if (is.getType() == Material.LEATHER_BOOTS || is.getType() == Material.LEATHER_CHESTPLATE || is.getType() == Material.LEATHER_HELMET
                || is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
            meta.setColor(color);
            is.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }
     /*
    public ItemBuilder effect(PotionEffectType type, int duration, int amplifier, boolean ambient) {
        effect(new PotionEffect(type, duration, amplifier, ambient));
        return this;
    }
     
    public ItemBuilder effect(PotionEffect effect) {
        if (!listener) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            listener = true;
        }
        String name = is.getItemMeta().getDisplayName();
        while (effects.containsKey(name)) {
            name = name + "#";
        }
        effects.put(name, effect);
        return this;
    }
     
    public ItemBuilder effect(PotionEffectType type, int duration, int amplifier) {
        effect(new PotionEffect(type, duration == -1 ? 1000000 : duration, amplifier));
        return this;
    }
     
    public ItemBuilder effect(PotionEffectType type, int duration) {
        effect(new PotionEffect(type, duration == -1 ? 1000000 : duration, 1));
        return this;
    }*/
     
    public ItemStack build() {
        return is;
    }
    /*
    public ItemBuilder addPlaceability() {
    	net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(is);
        
        NBTTagList placeable = (NBTTagList) stack.getTag().get("PlaceAll");
        if (placeable == null) {
            placeable = new NBTTagList();
        }
        placeable.add(new NBTTagString("1"));
   
        return CraftItemStack.asCraftMirror(stack);
    }*/
    
     /*
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        if (e.getItem().hasItemMeta()) {
            @SuppressWarnings("unchecked") HashMap<String, PotionEffect> copy = (HashMap<String, PotionEffect>) effects.clone();
            String name = e.getItem().getItemMeta().getDisplayName();
            while (copy.containsKey(name)) {
                e.getPlayer().addPotionEffect(copy.get(name), true);
                copy.remove(name);
                name += "#";
            }
        }
    }*/
     
    @EventHandler
    public void onItemApply(InventoryClickEvent e) {
    }

	public ItemBuilder unbreakable() {
        final ItemMeta iM = is.getItemMeta();
        iM.spigot().setUnbreakable(true);
        is.setItemMeta(iM);
        return this;
	}
     
}