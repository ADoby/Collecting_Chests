package com.gmail.zimmerlint.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;

public class ItemFilter {

	List<Integer> itemIDs;
	
	public ItemFilter(){
		itemIDs = new ArrayList<Integer>();
	}
	
	public boolean addItem(int itemID){
		if(itemIDs.contains(itemID)){
			itemIDs.add(itemID);
			return true;
		}
		return false;
	}
	
	public boolean addItem(String itemName){
		if(itemIDs.contains(Material.matchMaterial(itemName).getId())){
			itemIDs.add(Material.matchMaterial(itemName).getId());
			return true;
		}
		return false;
	}
	
	public boolean isAllowed(int itemID){
		return itemIDs.contains(itemID);
	}
	
	public boolean isAllowed(String itemName){
		return itemIDs.contains(Material.matchMaterial(itemName).getId());
	}

	public List<String> getStringList() {
		List<String> stringList = new ArrayList<String>();
		Iterator<Integer> ita = itemIDs.iterator();
		while(ita.hasNext()){
			stringList.add(Material.getMaterial(ita.next()).toString());
		}
		return stringList;
	}
	
}
