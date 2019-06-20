package br.com.endcraft.fightevent;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.endcraft.fightevent.apostas.ApostasInventory;

public class Comandos implements CommandExecutor{

	private List<String> inCommand = new ArrayList<>();
	private List<String> confirma = new ArrayList<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length >= 2 && sender.isOp() && args[0].equalsIgnoreCase("savekit")) {
			StringBuilder sb = new StringBuilder();
			for(int x = 1; x < args.length;x++) {
				sb.append(args[x]);
				sb.append(" ");
			}
			Kit kit = new Kit();
			Player player = (Player)sender;
			kit.setArmor(player.getInventory().getArmorContents());
			kit.setContents(player.getInventory().getContents());
			kit.setName(sb.toString());
			Fight.getKits().add(0, kit);
			player.sendMessage("§7[Ultra PVP] §a KIT salvo");
			return true;
		}
		if(args.length >= 1 && sender.isOp()) {
			if(args[0].equalsIgnoreCase("help")) {
				StringBuilder sb = new StringBuilder();
				sb.append("§a------------- §dAJUDA §a-------------\n")
				  .append("§b").append(label).append(" arena create <arenaName>\n")
				  .append("§b").append(label).append(" arena pos1,pos2,saida,espera <arenaName>\n")
				  .append("§b").append(label).append(" arena list\n")
				  .append("§b").append(label).append(" arena delete <name>\n");
				sender.sendMessage(sb.toString());
				  return true; 
			}
			if(args[0].equalsIgnoreCase("arena")) {
				Player player = (Player)sender;
				if(args[1].equalsIgnoreCase("list")) {
					StringBuilder sb = new StringBuilder();
					sb.append("§a------------- §dArenas §a-------------\n").
						append("§7Marcação: §apos1 - pos2 - espera - saida\n")
						.append("§7* = okay, 0 = não marcado\n");
					Fight.getArenas().forEach(arena -> {
						sb.append("§c").append(arena.getName())
						.append(" - ")
						.append(arena.getPos1() != null ? "* " : "0 ")
						.append(arena.getPos2() != null ? "* " : "0 ")
						.append(arena.getWait() != null ? "* " : "0 ")
						.append(arena.getExit() != null ? "*" : "0")
						.append("\n");
					});
					player.sendMessage(sb.toString());
					return true;
				}
				Arena arena = Fight.getArenaByName(args[2]);
				if(args[1].equalsIgnoreCase("create")) {
					if(arena!=null) {
						sender.sendMessage("§7[Ultra Fight] §cJá existe uma arena com esse nome");
						return true;
					}
					arena = new Arena();
					arena.setName(args[2]);
					Fight.getArenas().add(arena);
					player.sendMessage("§6[Ultra Fight] §aNova arena criada.");
					return true;
				}
				if(arena == null) {
					sender.sendMessage("§7[Ultra Fight] §cArena não encontrada.");
					return true;
				}
				if(args[1].equalsIgnoreCase("delete")) {
					Fight.getArenas().remove(arena);
					player.sendMessage("§6[Ultra Fight] §aArena removida com sucesso!");
					return true;
				}
				if(args[1].equalsIgnoreCase("pos1")) {
					arena.setPos1(player.getLocation());
					player.sendMessage("§6[Ultra Fight] §aPosição 1 marcada.");
					return true;
				}
				if(args[1].equalsIgnoreCase("pos2")) {
					arena.setPos2(player.getLocation());
					player.sendMessage("§6[Ultra Fight] §aPosição 2 marcada.");
					return true;
				}
				if(args[1].equalsIgnoreCase("saida")) {
					arena.setExit(player.getLocation());
					player.sendMessage("§6[Ultra Fight] §aPosição de saida marcada.");
					return true;
				}
				if(args[1].equalsIgnoreCase("espera")) {
					arena.setWait(player.getLocation());
					player.sendMessage("§6[Ultra Fight] §aPosição de espera marcada.");
					return true;
				}
				return true;
			}
		}
		Player player = (Player)sender;
		if(args.length == 1 && args[0].equalsIgnoreCase("sair")) {
			Game game = Fight.getGameByPlayer(player.getName());
			if(game == null)
				return true;
			game.exitQuery(player.getName());
			player.sendMessage("§7[Ultra Fight] §aVocê saiu! não deixe de jogar /fight");
			return true;
		}
		if(args.length >= 1 && args[0].equalsIgnoreCase("apostar")) {
			player.openInventory(ApostasInventory.getGames());
			return true;
		}
		if(inCommand.contains(player.getName())) {
			Fight.log("inCommand contains");
			return true;
		}
		if(Fight.getGameByPlayer(player.getName()) != null) {
			Fight.log("getGameByPlayer != null");
			return true;
		}
		if(!Tools.isClearInventory(player)) {
			player.sendMessage("§7[Ultra Fight] §aLimpe o inventário para poder entrar");
			return true;
		}
		if(!confirma.contains(player.getName())) {
			confirma.add(player.getName());
			player.sendMessage("§4§m---------------------------");
			player.sendMessage("§c        ! -- Aviso -- !      ");
			player.sendMessage("§cRecomendamos que você guarde ");
			player.sendMessage("§cSeus itens, o sistema está sendo");
			player.sendMessage("§cdesenvolvido e pode conter bugs");
			player.sendMessage("§cDigite novamente o comando.");
			player.sendMessage("§4§m---------------------------");
			return true;
		}
		confirma.remove(player.getName());
		entryInGame(player);
		return false;
	}

	private void entryInGame(Player player) {
		if(!inCommand.contains(player.getName()))
			inCommand.add(player.getName());
		for(Game game : Fight.getGames()) {
			if(game.isFinalizado())
				continue;
			if(!game.isPrepare() && !game.isStarted()) {
				inCommand.remove(player.getName());
				game.entryQuery(player);
				return;
			}
		}
		createAGame(player);
	}

	private void createAGame(Player player) {
		for(Arena arenas : Fight.getArenas()) {
			if(!Fight.getArenas().isEmpty() && arenas.size == 0 && !arenas.isNull()) {
				Game game = new Game(arenas);
				Fight.getGames().add(game);
				entryInGame(player);
				return;
			}
		}
		
		player.sendMessage("§7[Ultra PVP] §aAguardando uma arena livre...");
		new BukkitRunnable() {
			@Override
			public void run() {
				entryInGame(player);
			}
		}.runTaskLater(Fight.getInstance(), 20 * 5);
	}
}
