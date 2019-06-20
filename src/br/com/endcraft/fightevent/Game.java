package br.com.endcraft.fightevent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import br.com.endcraft.fightevent.apostas.Apostar;

public class Game extends Apostar{

	private List<String> players;
	private List<String> inGame;
	private boolean prepare = false;
	private boolean started = false;
	public static final int MAX_PLAYERS = 5;
	private static final int delay = 30;
	private static final int IN_FIGHT = 2;
	private BukkitTask waitTask = null;
	private int countDown = 0; 
	private Random random = new Random();
	private final Arena arena;
	private boolean finalizado = false;
	private static Game instance;
	private final int gameId;
	
	public Game(Arena arena) {
		players = new ArrayList<>();
		inGame = new ArrayList<>();
		this.arena = arena;
		this.arena.size++;
		instance = this;
		gameId = random.nextInt(999);
		super.setCustoPorAposta(Math.pow(Bukkit.getOnlinePlayers().length, 3));
		waitTask = new BukkitRunnable() {
			@Override
			public void run() {
				sendMessageForAll("§7[Ultra Fight] §aAguardando mais jogadores...");
			}
		}.runTaskTimer(Fight.getInstance(), 0, 20 * 20);
		StringBuilder sb = new StringBuilder();
		sb	.append("§a    <§m--------------------------§a>\n")
			.append("\n")
			.append("§aEvento Fight iniciado para §6").append(MAX_PLAYERS).append("§a jogadores\n")
			.append("Participe §6/fight\n")
			.append("§aOu aposte nos jogadores §6/fight apostar\n")
			.append("\n")
			.append("§a    <§m--------------------------§a>");

		Bukkit.broadcastMessage(sb.toString());
	}

	public void entryQuery(Player player) {
		players.add(player.getName());
		player.sendMessage("§7[Ultra Fight] §aVocê entrou!, Aguarde.");
		if(players.size() == MAX_PLAYERS) {
			prepareGame();
		}
	}
	
	private void prepareGame() {
		Iterator<String> manager = players.iterator();
		while(manager.hasNext()) {
			Player p = Bukkit.getPlayer(manager.next());
			if(p == null) {
				manager.remove();
				continue;
			}
			if(!Tools.isClearInventory(p)) {
				p.sendMessage("§7[Ultra PVP] §aVocê precisa limpar o inventário!");
				manager.remove();
			}
		}
		if(players.size() != MAX_PLAYERS) {
			return;
		}
		
		waitTask.cancel();
		prepare = true;
		sendMessageForAll("§7[Ultra Fight] §aIniciando Jogo em "+ delay +" Segundos!");
		players.forEach(p -> {
			Player player = Bukkit.getPlayer(p);
			player.teleport(Arena.toLocation(arena.getWait()));
			StorePlayer.savePlayer(player);
		});
		playSoundForAll(Sound.AMBIENCE_THUNDER, true, 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				countDown++;
				if(countDown >= delay - 5) {
					sendMessageForAll("§7[Ultra Fight] §aIniciando jogo em " + ((delay - countDown) + 1));
					playSound(Sound.ARROW_HIT);
				}
				if(countDown == delay) {
					startGame();
					this.cancel();
				}
			}
		}.runTaskTimer(Fight.getInstance(), 0, 20);
	}

	private void startGame() {
		started = true;
		prepare = false;
		next();
	}

	protected void playSound(Sound arrowHit) {
		players.forEach(p -> {
			Player player = Bukkit.getPlayer(p);
			player.playSound(player.getLocation(), arrowHit, 1F, 1F);
		});
	}


	public void next() {
		Iterator<String> i = inGame.iterator();
		while(i.hasNext()) {
			String p = i.next();
			Player t = Bukkit.getPlayer(p);
			t.getInventory().clear();
			t.getInventory().setArmorContents(null);
			t.teleport(Arena.toLocation(arena.getWait()));
			players.add(p);
			i.remove();
		}

		if(players.size() == 1) {
			endGame();
			return;
		}
		
		StringBuilder vsMensagem = new StringBuilder();
		
		vsMensagem.append("§7[Ultra Fight] §c");
		
		for(int x = 0; x < IN_FIGHT; x++) {
			String player = players.get(random.nextInt(players.size()));
			players.remove(player);
			inGame.add(player);
			Fight.log("TO add: " + player);
			Player p1 = Bukkit.getPlayer(player);
			healPlayer(p1);
			if(Fight.getKits().isEmpty()) {
				Kit.addKit(p1);
			} else {
				Fight.getKits().get(0).put(p1);
			}
			p1.teleport(Arena.toLocation(getArena().getPos(x)));
			vsMensagem.append((x == IN_FIGHT-1) ? "§a vs §c" + player : player);
		}
		
		sendMessageForAll(vsMensagem.toString());
		sendMessageForAll("§7[Ultra Fight] §bAposte agora no fight §6/Fight apostar");
		
		playSoundForAll(Sound.ZOMBIE_METAL, true, 1.5F);
	}
	
	private void healPlayer(Player p1) {
		p1.setHealth(20);
		p1.setFoodLevel(20);
		Iterator<PotionEffect> iterator = p1.getActivePotionEffects().iterator();
		while(iterator.hasNext()) {
			PotionEffect next = iterator.next();
			p1.removePotionEffect(next.getType());
		}
	}

	public void forceEndGame() {
		Iterator<String> inside = inGame.iterator();
		while(inside.hasNext()) {
			String p = inside.next();
			Player t = Bukkit.getPlayer(p);
			t.getInventory().clear();
			t.getInventory().setArmorContents(null);
			t.teleport(Arena.toLocation(arena.getWait()));
			players.add(p);
			inside.remove();
		}
		Iterator<String> i = players.iterator();
		while(i.hasNext()) {
			Player p = Bukkit.getPlayer(i.next());
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.teleport(Arena.toLocation(arena.getExit()));
			StorePlayer.restorePlayer(p);
		}
		Bukkit.broadcastMessage("§7[Ultra Fight] §bFight finalizado sem ganhadores.");
		this.arena.size--;
		finalizado = true;
	}

	private void endGame() {
		Player vencedor = Bukkit.getPlayer(players.get(0));
		vencedor.teleport(Arena.toLocation(arena.getExit()));
		StringBuilder sb = new StringBuilder();
		sb.append("§b§m-------------------------------\n\n");
		sb.append("§bFight encerrado!\n");
		sb.append("§b§l"); sb.append(vencedor.getName());
		sb.append(" §bGanhou o jogo!\n");
		sb.append("§bJogue novamente! /fight\n\n");
		sb.append("§b§m-------------------------------");
		Bukkit.broadcastMessage(sb.toString());
		super.fecharApostas(vencedor.getName(), getGameId());
		playSoundForAll(Sound.ENDERDRAGON_GROWL, false, 1);
		Bukkit.getPluginManager().callEvent(new PlayerQuitFromGameEvent(vencedor, this));
		this.arena.size--;
		finalizado = true;
	}

	public void remove(String kill) {
		Bukkit.getPlayer(kill).teleport(Arena.toLocation(arena.getExit()));
		if(inGame.contains(kill)) {
			System.out.println("remove() inGame = " + kill);
			inGame.remove(kill);
		}
		if(players.contains(kill)) {
			System.out.println("remove() players = " + kill);
			players.remove(kill);
		}
	}
	
	public List<String> getPlayers() {
		return players;
	}
	
	public List<String> getInGame() {
		return inGame;
	}
	
	public boolean isFinalizado() {
		return finalizado;
	}
	
	public boolean isPrepare() {
		return prepare;
	}
	public boolean isStarted() {
		return started;
	}
	
	
	private void sendMessageForAll(String string) {
		players.forEach(p -> {
			Player player = Bukkit.getPlayer(p);
			player.sendMessage(string);
		});
		inGame.forEach(p -> {
			Player player = Bukkit.getPlayer(p);
			player.sendMessage(string);
		});
	}

	private void playSoundForAll(Sound sound, boolean juntInGame, float pitch) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(juntInGame) {
				if(players.contains(p.getName())) {
					p.playSound(p.getLocation(), sound, 1F, pitch);
				}
			} else {
				p.playSound(p.getLocation(), sound, 1F, pitch);
			}
		}
	}
	
	public Arena getArena() {
		return arena;
	}

	public void exitQuery(String player) {
		players.remove(player);
	}
	
	public int getGameId() {
		return gameId;
	}
	
	public List<String> getAllPlayers(){
		List<String> temp = new ArrayList<>();
		temp.addAll(inGame);
		temp.addAll(players);
		return temp;
	}
	
}
