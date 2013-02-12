package com.gmail.zimmerlint.plugin;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;


public class Plate_Listener implements Listener{
	
	Plugin owner;
	List<Plate_Watcher> plate_watcher = new ArrayList<Plate_Watcher>();

	List<Plate_Connection> plate_connections = new ArrayList<Plate_Connection>();
	
	public Plate_Listener(Plugin plugin){
		owner = plugin;
	}

	@EventHandler
	public void OnBreakBlock(BlockBreakEvent event){
		// TODO delete Connections etc.
		if(event.getBlock().getTypeId() == 72){
			deletePlateConnection(event.getPlayer(),event.getBlock());
		}
	}

	public void addPlateConnection(Plate_Connection pc){
		// TODO create config/look if theres such a connection
		plate_connections.add(pc);
		
		//Save Connection
		String plateString = "plates." + String.valueOf(pc.getPlate().getX()) + "/" + String.valueOf(pc.getPlate().getY()) + "/" + String.valueOf(pc.getPlate().getZ()) ;
		String chestLocation = String.valueOf(pc.getChest().getX()) + "/" + String.valueOf(pc.getChest().getY()) + "/" + String.valueOf(pc.getChest().getZ());
		owner.getConfig().set(plateString+ ".world", pc.getPlate().getWorld().getName());
		owner.getConfig().set(plateString+ ".chest", chestLocation);
	}
	
	@EventHandler
	public void OnEntityInteract(EntityInteractEvent event){

		if(event.getEntity() instanceof Item && event.getBlock().getTypeId() == 72){
			//Item on Wooden Pressure Plate
			//Same as AC-Chest
			
			Block plate = event.getBlock();
			Location plateLoc = plate.getLocation();
			Location chestLocs[] = {plateLoc.clone().add(1, 0, 0),plateLoc.clone().add(-1, 0, 0),plateLoc.clone().add(0, 0, 1),plateLoc.clone().add(0, 0, -1)};
				
			boolean itemGone = false;
			
			for(int i = 0; i<chestLocs.length; i++){
				if(chestLocs[i].getBlock().getTypeId() == 54){
					//Chest on X+1/X-1/Z+1/Z-1 Location of Plate
					Inventory inv = ((Chest)chestLocs[i].getBlock().getState()).getInventory();
					
					if(inv.firstEmpty() != -1){
						
						//Add Entity(As Item) to Chest
						inv.addItem(((Item)event.getEntity()).getItemStack());
									
						//Delete Item from World
						event.getEntity().remove();
						
						//Start Thread to check if there are more Items dropped on the plate while its pressed
						plate_watcher.add(new Plate_Watcher(owner,plate));
						
						itemGone = true;
						break;
					}
				}
			}
				
			if(!itemGone){
				//Item isn't gone yet, try other connections
				for(int i=0;i<plate_connections.size();i++){
					if(plate_connections.get(i).getPlate().equals(plate)){
						Inventory inv = plate_connections.get(i).getChest().getInventory();
						
						if(inv.firstEmpty() != -1){
							//Add Entity(As Item) to Chest
							inv.addItem(((Item)event.getEntity()).getItemStack());
							
							//Delete Item from World
							event.getEntity().remove();
							
							//Start Thread to check if there are more Items dropped on the plate while its pressed
							plate_watcher.add(new Plate_Watcher(owner,plate,plate_connections.get(i).getChest()));
							break;
						}
					}
				}
			}
			

		}else if(event.getBlock().getTypeId() == 72){
			//Its no Item
			plate_watcher.add(new Plate_Watcher(owner,event.getBlock()));
		}
	}

	public boolean deletePlateConnection(Player player, Block plate) {
		for(int i=0;i<plate_connections.size();i++){
			if(plate_connections.get(i).getPlate().equals(plate)){
				Plate_Connection pc = plate_connections.get(i);
				//Plate is in list
				plate_connections.remove(i);
				
				String plateLoc = String.valueOf(pc.getPlate().getX()) + "/" + String.valueOf(pc.getPlate().getY()) + "/" + String.valueOf(pc.getPlate().getZ()) ;
				
				owner.getConfig().getConfigurationSection("plates").set(plateLoc, null);
				
				owner.saveConfig();
				
				if(player != null){
					player.sendMessage("§eCollecting Chests:");
					player.sendMessage("  §cConnection removed");
				}
				return true;
			}
		}
		return false;
	}


}
