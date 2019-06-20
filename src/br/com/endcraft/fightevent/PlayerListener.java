package br.com.endcraft.fightevent;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener{

	
	@EventHandler
	public void onPlayerDead(PlayerDeathEvent e) {
		Game game = Fight.getGameByPlayer(e.getEntity().getName());
		if(game == null)
			return;
		String dead = e.getEntity().getName();
		if(game.isPrepare()) {
			game.remove(dead);
			return;
		}
		if(game.isStarted() && game.getInGame().contains(dead)) {
			game.remove(dead);
			game.next();	
		}
		Iterator<ItemStack> stacks = e.getDrops().iterator();
		while(stacks.hasNext()) {
			stacks.next();
			stacks.remove();
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Game game = Fight.getGameByPlayer(e.getPlayer().getName());
		if(game == null)
			return;
		e.getPlayer().closeInventory();
		String player = e.getPlayer().getName();
		if(!game.isStarted() && !game.isPrepare()) {
			game.exitQuery(player);
			Fight.log("Exit on query");
		}
		if(game.isPrepare()) {
			game.remove(player);
			callEvent(new PlayerQuitFromGameEvent(e.getPlayer(), game));
			Fight.log("Exit during prepare");
			return;
		}
		if(game.isStarted() && game.getInGame().contains(player)) {
			Fight.log("Exit on started and in Game");
			game.remove(player);
			callEvent(new PlayerQuitFromGameEvent(e.getPlayer(), game));
			game.next();
			return;
		}
		if(game.isStarted() && !game.getInGame().contains(player)) {
			Fight.log("Exit on started and is not in game");
			game.remove(player);
			callEvent(new PlayerQuitFromGameEvent(e.getPlayer(), game));
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		Game game = Fight.getGameByPlayer(e.getPlayer().getName());
		if(game == null) {
			return;
		}
		if(game.isStarted() || game.isPrepare()) {
			if(e.getCause() == TeleportCause.COMMAND) {
				e.setCancelled(true);
				Fight.log("Teleporte cancelado devido a comando: " + e.getPlayer().getName());
				e.getPlayer().sendMessage("§7[Ultra Fight] §cVocê não pode sair do evento.");
				return;
			}
			if(e.isCancelled()) {
				e.setCancelled(false);
				Fight.log(e.getPlayer().getName() + " forçado teleporte evento: " + e.getCause().name());
			}
		}
	}
	
	@EventHandler
	public void onCommandExecute(PlayerCommandPreprocessEvent e) {
		if(Fight.isInBlockCommands(e.getMessage().replaceAll("/", ""))) {
			Game game = Fight.getGameByPlayer(e.getPlayer().getName());
			if(game == null)
				return;
			e.getPlayer().sendMessage("§cVocê não pode jogar 2 jogos ao mesmo tempo.");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		StorePlayer.restorePlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitFromGameEvent e) {
		Fight.log("Called event.");
		StorePlayer.restorePlayer(e.getPlayer());
	}

	public static void callEvent(PlayerQuitFromGameEvent playerQuitFromGameEvent) {
		Bukkit.getPluginManager().callEvent(playerQuitFromGameEvent);
	}
}
