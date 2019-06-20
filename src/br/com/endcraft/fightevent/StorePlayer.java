package br.com.endcraft.fightevent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

public class StorePlayer {

	
	public static List<PlayerInfo> stores = new ArrayList<>();
	
	public static void savePlayer(Player player) {
		PlayerInfo pi = new PlayerInfo(player);
		pi.store();
		stores.add(pi);
	}
	
	public static void restorePlayer(Player player) {
		Iterator<PlayerInfo> ap = stores.iterator();
		while(ap.hasNext()) {
			PlayerInfo pi = ap.next();
			if(!pi.getPlayer().getName().equalsIgnoreCase(player.getName())) {
				continue;
			}
			pi.restore();
			ap.remove();
			break;
		}
	}
	
}
