package br.com.endcraft.fightevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerQuitFromGameEvent extends Event {

	public static final HandlerList handler = new HandlerList();
	private final Player player;
	private final Game game;
	
	public PlayerQuitFromGameEvent(Player player, Game game) {
		Fight.log("Created event " + this.getEventName());
		this.player = player;
		this.game = game;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Game getGame() {
		return game;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handler;
	}
	
	public static HandlerList getHandlerList() {
		return handler;
	}
	
	

}
