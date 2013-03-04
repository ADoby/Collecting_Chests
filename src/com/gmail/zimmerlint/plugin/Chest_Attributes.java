package com.gmail.zimmerlint.plugin;

import java.util.List;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class Chest_Attributes {

	Chest chest;
	ItemFilter filter;
	
	public Chest_Attributes(){
		filter = new ItemFilter();
	}
	
	public Chest_Attributes(ItemStack item, boolean ignoreDamage){
		filter = new ItemFilter();
		addItemFilter(item, ignoreDamage);
	}
	
	public boolean addItemFilter(ItemStack item, boolean ignoreDamage){
		return filter.addItem(item, ignoreDamage);
	}
	
	public boolean removeItemFilter(ItemStack item){
		return filter.removeItem(item);
	}

	
	public boolean wantsItem(ItemStack item){
		return filter.isAllowed(item);
	}
	
	public Chest getChest(){
		return chest;
	}
	
	public List<String> getWantedItems(){
		return filter.getStringList();
	}
	
	public List<String> getConfigList(){
		return filter.getConfigList();
	}
	
	public boolean hasFilters(){
		return filter.hasFilters();
	}
	
}
