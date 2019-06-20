package br.com.endcraft.fightevent;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Arena implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1414750563420707426L;
	private String pos1, pos2, wait, exit;
	private String name;
	public static final int MAX_GAME_IN_ARENA = 1;
	public int size = 0;
	
	public Arena() {
	}

	
	public String getPos1() {
		return pos1;
	}
	
	public String getPos2() {
		return pos2;
	}
	
	public String getPos(int index) {
		switch (index) {
		case 0:
			return getPos1();
		case 1:
			return getPos2();
		default:
			return getPos1();
		}
	}
	
	public String getExit() {
		return exit;
	}
	
	public String getWait() {
		return wait;
	}
	
	public boolean isNull() {
		if(getPos1() != null && getPos2() != null && getWait() != null && getExit() != null)
			return false;
		return true;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setPos1(Location pos1) {
		this.pos1 = pos1.getWorld().getName() + ";" + pos1.getBlockX() + ";" + pos1.getBlockY() + ";" + pos1.getBlockZ() + ";" + pos1.getPitch() + ";" + pos1.getYaw();
	}


	public void setPos2(Location pos1) {
		this.pos2 = pos1.getWorld().getName() + ";" + pos1.getBlockX() + ";" + pos1.getBlockY() + ";" + pos1.getBlockZ() + ";" + pos1.getPitch() + ";" + pos1.getYaw();
	}


	public void setWait(Location pos1) {
		this.wait = pos1.getWorld().getName() + ";" + pos1.getBlockX() + ";" + pos1.getBlockY() + ";" + pos1.getBlockZ() + ";" + pos1.getPitch() + ";" + pos1.getYaw();
	}


	public void setExit(Location pos1) {
		this.exit = pos1.getWorld().getName() + ";" + pos1.getBlockX() + ";" + pos1.getBlockY() + ";" + pos1.getBlockZ() + ";" + pos1.getPitch() + ";" + pos1.getYaw();
	}
	
	
	public static Location toLocation(String loc) {
		String[] sp = loc.split(";");
		return new Location(Bukkit.getWorld(sp[0]), Double.parseDouble(sp[1]),
				Double.parseDouble(sp[2]), Double.parseDouble(sp[3]), Float.parseFloat(sp[5]), Float.parseFloat(sp[4]));
	}
	
	
	
	
}
