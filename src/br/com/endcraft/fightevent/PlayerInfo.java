package br.com.endcraft.fightevent;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInfo {
	private ItemStack[] contents;
	private ItemStack[] armor;
	private float exp, expToLevel;
	private int totalExperience;
	private Player player;
	private int health, foodLevel;
	private int level;
	
	
	public PlayerInfo(Player info) {
		this.player = info;
	}
	
	
	public void store() {
		PlayerInventory inv = player.getInventory();
		contents = inv.getContents().clone();
		armor = inv.getArmorContents().clone();
		totalExperience = player.getTotalExperience();
		exp = player.getExp();
		expToLevel = player.getExpToLevel();
		health = player.getHealth();
		foodLevel = player.getFoodLevel();
		level = player.getLevel();
		inv.clear();
		inv.setArmorContents(null);
		player.setTotalExperience(0);
		player.setExp(0);
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setLevel(0);
	}
	
	
	public void restore() {
		PlayerInventory inv = player.getInventory();
		inv.clear();
		inv.setContents(contents);
		inv.setArmorContents(armor);
		player.setTotalExperience(totalExperience);
		player.setExp(exp);
		player.setHealth(health);
		player.setFoodLevel(foodLevel);
		player.setLevel(level);
	}
	
	
	
	public Player getPlayer() {
		return player;
	}
}