package br.com.endcraft.fightevent;

import org.bukkit.entity.Player;

public class Tools {
	
	public static boolean isClearInventory(Player player) {
		
		/*for(ItemStack i : player.getInventory().getContents()) {
			if(i != null && i.getType() != Material.AIR)
				return false;
		}
		for(ItemStack i : player.getInventory().getArmorContents()) {
			if(i != null && i.getType() != Material.AIR)
				return false;
		}*/
		return true;
	}

}
