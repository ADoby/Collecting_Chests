package com.gmail.zimmerlint.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.material.PressurePlate;

public class Plate_Watcher implements Runnable{

	private CollectingChests owner;
	private Block plate;
	private World world;
	private List<Chest> chestList = new ArrayList<Chest>();
	private Location[] chestLocations;
	
	public Plate_Watcher(CollectingChests plugin, Block plate){
		owner = plugin;
		this.plate = plate;
		this.world = plate.getWorld();
		chestLocations = new Location[4];
		chestLocations[0] = new Location(world,plate.getX()+1,plate.getY(),plate.getZ());
		chestLocations[1] = new Location(world,plate.getX()-1,plate.getY(),plate.getZ());
		chestLocations[2] = new Location(world,plate.getX(),plate.getY(),plate.getZ()+1);
		chestLocations[3] = new Location(world,plate.getX(),plate.getY(),plate.getZ()-1);
		Bukkit.getServer().getScheduler().runTaskLater(owner, this, 20L);
	}
	
	public Plate_Watcher(CollectingChests plugin, Block plate, Chest chest){
		owner = plugin;
		this.plate = plate;
		this.world = plate.getWorld();
		chestList.add(chest);
		chestLocations = new Location[4];
		chestLocations[0] = new Location(world,plate.getX()+1,plate.getY(),plate.getZ());
		chestLocations[1] = new Location(world,plate.getX()-1,plate.getY(),plate.getZ());
		chestLocations[2] = new Location(world,plate.getX(),plate.getY(),plate.getZ()+1);
		chestLocations[3] = new Location(world,plate.getX(),plate.getY(),plate.getZ()-1);
		Bukkit.getServer().getScheduler().runTaskLater(owner, this, 20L);
	}
	
	public Plate_Watcher(CollectingChests plugin, Block plate, List<Chest> chestList){
		owner = plugin;
		this.plate = plate;
		this.world = plate.getWorld();
		this.chestList = chestList;
		chestLocations = new Location[4];
		chestLocations[0] = new Location(world,plate.getX()+1,plate.getY(),plate.getZ());
		chestLocations[1] = new Location(world,plate.getX()-1,plate.getY(),plate.getZ());
		chestLocations[2] = new Location(world,plate.getX(),plate.getY(),plate.getZ()+1);
		chestLocations[3] = new Location(world,plate.getX(),plate.getY(),plate.getZ()-1);
		Bukkit.getServer().getScheduler().runTaskLater(owner, this, 20L);
	}
	
	private void addItemToChest(Item item){
		if(chestList.size()!=0){
			for(int i=0;i<chestList.size();i++){
				if(chestList.get(i).getInventory().firstEmpty() != -1){
					//Chest NOT Full (1 slot free)

					//Add Entity(As Item) to Chest
					chestList.get(i).getInventory().addItem(item.getItemStack());
						
					//Delete Item from World
					item.remove();
					return;
				}
			}			
		}
		for(int i=0;i<chestLocations.length;i++){
			if(chestLocations[i].getBlock().getTypeId() == 54){
				//Chest on X+1/X-1/Z+1/Z-1 Location of Plate
				Chest c = (Chest) chestLocations[i].getBlock().getState();
				
				if(c.getInventory().firstEmpty() != -1){
					//Chest NOT Full (1 slot free)
					
					//Add Entity(As Item) to Chest
					c.getInventory().addItem(item.getItemStack());
							
					//Delete Item from World
					item.remove();
					return;
				}
			}
		}
	}
	
	
	@Override
	public void run() {
		if(((PressurePlate)plate.getState().getData()).isPressed()){
			//As long as plate is pressed, try to get new Items above it
			
			Chunk c = plate.getChunk();
			
			Entity[] es = c.getEntities();
			
			for(int i=0;i<es.length;i++){
				if(es[i] instanceof Item){
					double diffZ = (es[i].getLocation().getZ()-plate.getZ());
					if(diffZ<=1.0 && diffZ>=0){
						double diffX = (es[i].getLocation().getX()-plate.getX());
						double diffY = (es[i].getLocation().getY()-plate.getY());
						if(diffX<=1.0 && diffX>=0 && diffY<=1.0 && diffY>=0){
							addItemToChest((Item)es[i]);
						}
					}
				}
			}
			Bukkit.getServer().getScheduler().runTaskLater(owner, this, 20L);
		}
	}
	
	public CollectingChests getOwner(){
		return owner;
	}
	
}
