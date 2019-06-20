package br.com.endcraft.fightevent.apostas;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.endcraft.fightevent.Fight;
import br.com.endcraft.fightevent.Game;

public class ListemInventory implements Listener {
	
	private Map<String, Integer> handlerClick = new HashMap<>();

	
	@EventHandler
	public void onClickInventory(InventoryClickEvent e) {
		if(!e.getInventory().getName().startsWith("Apostas - ")) {
			return;
		}
		String name = e.getInventory().getName();
		e.setCancelled(true);
		switch(name) {
			case ApostasInventory.JOGOS:
				managerJogosClick(e);
				break;
			case ApostasInventory.JOGADORES:
				managerJogadoresClick(e);
				break;
		}
	}

	private void managerJogadoresClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}
		if(!handlerClick.containsKey(player.getName())) {
			return;
		}
		Game game = Fight.getGameByID(handlerClick.get(player.getName()));
		String aposta = e.getCurrentItem().getItemMeta().getDisplayName().trim();
		if(!game.getAllPlayers().contains(aposta)) {
			player.sendMessage("§cNão é possível mais apostar neste jogador: " + aposta);
			return;
		}
		boolean b = game.receberAposta(player.getName(), aposta);
		if(!b) {
			player.sendMessage("§cSem dinheiro para apostar.");
		} else 
		player.sendMessage("§7[Ultra Fight] §bVocê apostou no jogador §f" + aposta + "!§b Boa sorte, ao final do fight você irá receber o resultado.");
		player.closeInventory();
	}

	private void managerJogosClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if(item == null || item.getType() == Material.AIR) {
			return;
		}
		Player player = (Player) e.getWhoClicked();
		ItemMeta meta = item.getItemMeta();
		int gameId = Integer.parseInt(meta.getDisplayName().substring(13));
		Game game = Fight.getGameByID(gameId);
		if(game.isFinalizado()) {
			player.sendMessage("§cEste jogo já foi finalizado.");
			return;
		}
		if(!game.isStarted()) {
			player.sendMessage("§cEste jogo ainda não foi iniciado.");
			return;
		}
		if(game.getApostadores().containsKey(player.getName())) {
			player.sendMessage("§cVocê não pode apostar novamente, e não é possível remover a aposta.");
			return;
		}
		
		player.openInventory(ApostasInventory.getPlayers(game));
		if(handlerClick.containsKey(player.getName()))
			handlerClick.remove(player.getName());
		handlerClick.put(player.getName(), gameId);
	}
}
