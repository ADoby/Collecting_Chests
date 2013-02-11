package com.gmail.zimmerlint.plugins.DropChest;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;

public class PlateToListen {

	private Block plate = null;
	private Chest chest = null;
	
	public PlateToListen(Block plate, Chest chest){
		this.plate = plate;
		this.chest = chest;
	}
	
	public Block getPlate(){
		return plate;
	}
	
	public Chest getChest(){
		return chest;
	}
	
}
