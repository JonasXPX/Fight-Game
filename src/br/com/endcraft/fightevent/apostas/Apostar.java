package br.com.endcraft.fightevent.apostas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import br.com.endcraft.fightevent.Fight;
import net.milkbowl.vault.economy.EconomyResponse;

public class Apostar {

	private Map<String, String> apostadores;
	private double custoPorAposta;
	
	public Apostar() {
		apostadores = new HashMap<>();
	}
	
	public boolean receberAposta(String de, String para) {
		apostadores.put(de, para);
		if(Fight.getEconomy().getBalance(Bukkit.getOfflinePlayer(de)) < getCustoPorAposta()) {
			return false;
		}
		Fight.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(de), custoPorAposta);
		return true;
	}
	
	public void fecharApostas(String ganhador, int gameID) {
		List<String> ganhadores = Lists.newArrayList();
		apostadores.keySet().forEach(key -> {
			if(key.equals(ganhador))
				ganhadores.add(key);
			else {
				Player player = Bukkit.getPlayer(key);
				if(player != null) {
					player.sendMessage("§7[Ultra Fight] §cVocê perdeu a aposta no jogo: §4" + gameID);
				}
			}
		});
		if(ganhadores.isEmpty())
			return;
		
		double ganhosPorPlayers = apostadores.size() * custoPorAposta / ganhadores.size();
		
		ganhadores.forEach(p -> {
			OfflinePlayer player = Bukkit.getOfflinePlayer(p);
			EconomyResponse depositPlayer = Fight.getEconomy().depositPlayer(player, ganhosPorPlayers);
			if(player.isOnline() && depositPlayer.transactionSuccess()) {
				player.getPlayer().sendMessage("§7[Ultra Fight] §bVocê ganhou na aposta, total de §f" + ganhosPorPlayers + 
						(ganhadores.size() > 1 ? "§b dividido entre §f" + ganhadores.size() + " jogadores." : " §bVocê ganhou sozinho!"));
			}
		});
	}
	
	public void cancelarApostas() {
		for(String key : apostadores.keySet()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(key);
			Fight.getEconomy().depositPlayer(player, getCustoPorAposta());
			if(player.isOnline()) {
				player.getPlayer().sendMessage("§7[Ultra Fight] §cAs apostas foram canceladas, seu dinheiro foi devolvido.");
			}
		}
	}
	
	public Map<String, String> getApostadores() {
		return apostadores;
	}
	
	public double getCustoPorAposta() {
		return custoPorAposta;
	}
	
	public void setCustoPorAposta(double custoPorAposta) {
		this.custoPorAposta = custoPorAposta;
	}
	
}
