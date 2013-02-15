package com.gmail.zimmerlint.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Chest_Listener implements Listener {

	Plugin owner;
	
	List<Plate_Watcher> chest_watcher = new ArrayList<Plate_Watcher>();
	List<Chest_Object> chests = new ArrayList<Chest_Object>();
	
	public Chest_Listener(Plugin plugin){
		owner = plugin;
	}

	public int getChestCount(){
		return chests.size();
	}
	
}
