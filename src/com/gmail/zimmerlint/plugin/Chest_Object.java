package com.gmail.zimmerlint.plugin;

import java.util.List;

import org.bukkit.block.Chest;

public class Chest_Object {

	Chest chest;
	ItemFilter filter;
	
	public Chest_Object(){
		filter = new ItemFilter();
	}
	
	public boolean addItemFilter(int itemID){
		return filter.addItem(itemID);
	}
	
	public boolean wantsItem(int itemID){
		return filter.isAllowed(itemID);
	}
	
	public Chest getChest(){
		return chest;
	}
	
	public List<String> getWantedItems(){
		return filter.getStringList();
	}
	
}
