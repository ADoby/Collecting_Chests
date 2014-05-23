package com.gmail.zimmerlint.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class Chest_Listener implements Listener {

	CollectingChests owner;
	
	List<Plate_Watcher> chest_watcher = new ArrayList<Plate_Watcher>();
	Map<Chest,Chest_Attributes> chests = new HashMap<Chest,Chest_Attributes>();
	
	
	public Chest_Listener(CollectingChests plugin){
		owner = plugin;
	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event){
		// TODO delete Connections etc.
		if(event.getBlock().getTypeId() == 54){
			if(deleteFilters((Chest)event.getBlock().getState()) && event.getPlayer() != null){
				event.getPlayer().sendMessage("§eCollecting Chests:");
				event.getPlayer().sendMessage("  §cFilters deleted");
			}
		}
	}
	
	public int getChestCount(){
		return chests.size();
	}
	
	public boolean hasChestAttribute(Chest chest){
		return chests.containsKey(chest);
	}
	
	public Chest_Attributes getChestAttributes(Chest chest){
		return chests.get(chest);
	}

	public void addFilter(Chest chest, ItemStack item, boolean ignoreDamage){
		if(chests.containsKey(chest)){
			chests.get(chest).addItemFilter(item,ignoreDamage);
		}else{
			chests.put(chest, new Chest_Attributes(item,ignoreDamage));
		}
		
		//Save Connection, overides existence connection
		String chestString = "chests." + String.valueOf(chest.getX()) + "/" + String.valueOf(chest.getY()) + "/" + String.valueOf(chest.getZ()) ;
		
		owner.getFiltersConfig().getConfig().set(chestString+ ".world", chest.getWorld().getName());
		//Save Filter list (Filter is deleted)
		
		//error ?
		List<String> configList = chests.get(chest).getConfigList();
		
		Bukkit.getServer().getLogger().info("Collecting Chests Filter Count: " + String.valueOf(configList.size()));
		if(chests.get(chest).getConfigList().size() > 0){
			Bukkit.getServer().getLogger().info("Collecting Chests Filter 1: " + configList.get(0));
		}
		owner.getFiltersConfig().getConfig().set(chestString + ".filters", configList);
		owner.getFiltersConfig().saveConfig();
	}
	
	public boolean deleteFilter(Chest chest, ItemStack item){
		if(chests.containsKey(chest)){
			chests.get(chest).removeItemFilter(item);
			String chestLoc = String.valueOf(chest.getX()) + "/" + String.valueOf(chest.getY()) + "/" + String.valueOf(chest.getZ()) ;
			if(!chests.get(chest).hasFilters()){
				chests.remove(chest);
				
				//Delete Chest from Config
				owner.getFiltersConfig().getConfig().getConfigurationSection("chests").set(chestLoc, null);
				owner.getFiltersConfig().saveConfig();
			}else{
				//Save Filter list (Filter is deleted)
				owner.getFiltersConfig().getConfig().set("chests."+chestLoc + ".filters", chests.get(chest).getConfigList());
				owner.getFiltersConfig().saveConfig();
			}
			return true;
		}
		return false;
	}
	
	public boolean deleteFilters(Chest chest){
		if(chests.containsKey(chest)){

			chests.remove(chest);
				
			//Delete Chest from Config
			String chestLoc = String.valueOf(chest.getX()) + "/" + String.valueOf(chest.getY()) + "/" + String.valueOf(chest.getZ()) ;
			owner.getFiltersConfig().getConfig().getConfigurationSection("chests").set(chestLoc, null);
			owner.getFiltersConfig().saveConfig();
			
			return true;
		}
		return false;
	}
	
}
