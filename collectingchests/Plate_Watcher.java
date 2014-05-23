package collectingchests;

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
	
	private void addItem(Chest chest, Item item){
		//Add Entity(As Item) to Chest
		chest.getInventory().addItem(item.getItemStack());
			
		//Delete Item from World
		item.remove();
	}
	
	private void addItemToChest(Item item){
		if(chestList.size()!=0){
			for(int i=0;i<chestList.size();i++){
				if(chestList.get(i).getInventory().firstEmpty() != -1){
					//Chest NOT Full (1 slot free)

					if(owner.getChestListener().hasChestAttribute(chestList.get(i))){
						//Chest has got Filters
						if(owner.getChestListener().getChestAttributes(chestList.get(i)).wantsItem(item.getItemStack())){
							//Chest wants Item
							addItem(chestList.get(i),item);
							return;
						}
					}else{
						addItem(chestList.get(i),item);
						return;
					}
					
					

				}
			}			
		}
		for(int i=0;i<chestLocations.length;i++){
			if(chestLocations[i].getBlock().getTypeId() == 54){
				//Chest on X+1/X-1/Z+1/Z-1 Location of Plate
				Chest chest = (Chest) chestLocations[i].getBlock().getState();
				
				if(chest.getInventory().firstEmpty() != -1){
					if(owner.getChestListener().hasChestAttribute(chest)){
						//Chest has got Filters
						if(owner.getChestListener().getChestAttributes(chest).wantsItem(item.getItemStack())){
							//Chest wants Item
							addItem(chest,item);
							return;
						}
					}else{
						addItem(chest,item);
						return;
					}
				}
			}
		}
	}
	
	
	@Override
	public void run() {
		if(plate.getTypeId() == 72 && ((PressurePlate)plate.getState().getData()).isPressed()){
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
