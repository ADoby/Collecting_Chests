package com.gmail.zimmerlint.plugins.DropChest;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.material.PressurePlate;
import org.bukkit.plugin.Plugin;

public class PlateNearChest_Watcher implements Runnable{

	private Plugin owner;
	private Block plate;
	private World world;
	private Chest chest = null;
	private Location[] chestLocations;
	
	public PlateNearChest_Watcher(Plugin plugin, Block plate){
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
	
	public PlateNearChest_Watcher(Plugin plugin, Block plate, Chest chest){
		owner = plugin;
		this.plate = plate;
		this.world = plate.getWorld();
		this.chest = chest;
		chestLocations = new Location[4];
		chestLocations[0] = new Location(world,plate.getX()+1,plate.getY(),plate.getZ());
		chestLocations[1] = new Location(world,plate.getX()-1,plate.getY(),plate.getZ());
		chestLocations[2] = new Location(world,plate.getX(),plate.getY(),plate.getZ()+1);
		chestLocations[3] = new Location(world,plate.getX(),plate.getY(),plate.getZ()-1);
		Bukkit.getServer().getScheduler().runTaskLater(owner, this, 20L);
	}
	
	private void addItemToChest(Item item){
		if(chest!=null){
			//Add Entity(As Item) to Chest
			chest.getInventory().addItem(item.getItemStack());
					
			//Delete Item from World
			item.remove();
		}
		for(int i=0;i<chestLocations.length;i++){
			if(chestLocations[i].getBlock().getTypeId() == 54){
				//Chest on X+1/X-1/Z+1/Z-1 Location of Plate
				Chest c = (Chest) chestLocations[i].getBlock().getState();
				
				//Add Entity(As Item) to Chest
				c.getInventory().addItem(item.getItemStack());
						
				//Delete Item from World
				item.remove();
			}
		}
	}
	
	
	@Override
	public void run() {
		if(((PressurePlate)plate.getState().getData()).isPressed()){
			//As long as plate is pressed, try to get new Items above it
			Bukkit.getServer().getLogger().info("PlateNearChest_Watcher Suche Items");
			
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
			
			//Old Version
			/*//Spawn Random Entity to find Nearby Entities
			Entity e = plate.getWorld().spawnEntity(plate.getLocation().add(0.5,0.5,0.5), EntityType.ARROW);
			
			List<Entity> es = e.getNearbyEntities(1, 1, 1);
			e.remove();
			
			for (Iterator<Entity> ita = es.iterator(); ita.hasNext();){
				Entity itaE = ita.next();
				if(itaE instanceof Item){
					addItemToChest((Item)itaE);
				}
			}*/
			
			Bukkit.getServer().getScheduler().runTaskLater(owner, this, 20L);
		}else{
			Bukkit.getServer().getLogger().info("PlateNearChest_Watcher Beendet");
		}
	}
	
	public Plugin getOwner(){
		return owner;
	}
	
}
