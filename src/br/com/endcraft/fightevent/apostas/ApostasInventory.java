package br.com.endcraft.fightevent.apostas;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.endcraft.fightevent.Fight;
import br.com.endcraft.fightevent.Game;

public class ApostasInventory {
	
	public final static String JOGOS = "Apostas - jogos";
	public final static String JOGADORES = "Apostas - jogadores";
	
	public static Inventory getGames() {
		Inventory inv = Bukkit.createInventory(null, 54, JOGOS);
		for(Game game : Fight.getGames()) {
			if(game.isFinalizado())
				continue;
			StringBuilder sb = new StringBuilder();
			game.getAllPlayers().forEach(s -> {
				sb.append(s);
				sb.append(", ");
			});
			sb.delete(sb.length() > 2 ? sb.length() - 2 : 0, sb.length());
			ItemStack item = new ItemStack(Material.WOOL, 1, (short)4);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§6Jogo ID: §f" + game.getGameId());
			meta.setLore(Arrays.asList("§aJogadores", "§6" + sb.toString()));
			item.setItemMeta(meta);
			inv.addItem(item);
		}
		return inv;
	}
	
	
	public static Inventory getPlayers(Game game) {
		Inventory inv = Bukkit.createInventory(null, 54, "Apostas - jogadores");
		for(String player : game.getAllPlayers()) {
			ItemStack item = new ItemStack(Material.WOOL, 1, (short)5);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(player);
			meta.setLore(Arrays.asList("§cClique aqui para apostar neste jogador", "§bValor da aposta: §f" + game.getCustoPorAposta()));
			item.setItemMeta(meta);
			inv.addItem(item);
		}
		return inv;
	}

}
