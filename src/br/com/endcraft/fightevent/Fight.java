package br.com.endcraft.fightevent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import com.google.common.collect.Lists;

import br.com.endcraft.fightevent.apostas.ListemInventory;
import net.milkbowl.vault.economy.Economy;

public class Fight extends JavaPlugin{

	private static List<Game> games;
	private static List<Arena> arenas;
	private static Fight instance;
	private static List<String> blockCommands;
	private static List<Kit> kits;
	private static Economy economy;
	
	@Override
	public void onEnable() {
		games = new ArrayList<>();
		arenas = new ArrayList<>();
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ListemInventory(), this);
		getCommand("fight").setExecutor(new Comandos());
		this.instance = this;
		if(getDataFolder().exists()) {
			File arenaFile = new File(getDataFolder(), "arenas.dat");
			if(arenaFile.exists()) {
				try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arenaFile))){
					arenas = (List<Arena>) ois.readObject();
				}catch (Exception e) {e.printStackTrace();}
			}
		}
		if(!getConfig().contains("block_commands")) {
			getConfig().set("block_commands", Arrays.asList("pvp", "evento", "gladiador", "killer"));
			saveConfig();
		}
		kits = loadKits();
		blockCommands = getConfig().getStringList("block_commands");
		setupEconomy();
		
	}

	public static Fight getInstance() {
		return instance;
	}
	
	public static Game getGameByPlayer(String player) {
		for(Game g : games) {
			if(g.isFinalizado())
				continue;
			if(g.getPlayers().contains(player)) {
				return g;
			} else if (g.getInGame().contains(player)) {
				return g;
			}
		}
		return null;
	}
	
	
	public static List<Arena> getArenas() {
		return arenas;
	}
	
	public static List<Game> getGames() {
		return games;
	}
	
	public static Arena getArenaByName(String name) {
		for(Arena a : getArenas()) {
			if(a.getName().equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}
	
	public static Game getGameByID(int id) {
		for(Game game : games) {
			if(game.getGameId() == id)
				return game;
		}
		return null;
	}
	
	@Override
	public void onDisable() {
		getGames().forEach(game ->{
			if(!game.isFinalizado())
				game.forceEndGame();
		});
		arenas.forEach(a -> a.size = 0);
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File arenaFile = new File(getDataFolder(), "arenas.dat");
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arenaFile))){
			oos.writeObject(arenas);
		} catch (Exception e) {e.printStackTrace();}
		saveKits();
	}
	
	public static void log(String msg) {
		getInstance().getLogger().log(Level.INFO, msg);
	}
	
	public static boolean isInBlockCommands(String startWith) {
		for(String s : blockCommands) {
			if(s.startsWith(startWith))
				return true;
		}
		return false;
	}
	
	public void saveKits() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		List<String> data = Lists.newArrayList();
		kits.forEach(k -> data.add(Kit.toJson(k).toString()));
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(getDataFolder(), "kits.json")))){
			oos.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<Kit> loadKits(){
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
			return Lists.newArrayList();
		}
		List<Kit> kits = new ArrayList<>();
		File json = new File(getDataFolder(), "kits.json");
		if(!json.exists()) {
			return Lists.newArrayList();
		}
		List<String> data = null;
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(json))){
			data = (List<String>) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		data.forEach(j -> {
			kits.add(Kit.fromJson(new JSONObject(j)));
		});
		return kits;
	}
	

	public static List<Kit> getKits() {
		return kits;
	}

	public static void setKits(List<Kit> kits) {
		Fight.kits = kits;
	}
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public static Economy getEconomy() {
		return economy;
	}
}
