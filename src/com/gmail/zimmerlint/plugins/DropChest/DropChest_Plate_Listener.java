package com.gmail.zimmerlint.plugins.DropChest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;


public class DropChest_Plate_Listener implements Listener, CommandExecutor{
	
	Plugin owner;
	List<BukkitTask> list1 = new ArrayList<BukkitTask>();
	List<PlateNearChest_Watcher> platesToWatch = new ArrayList<PlateNearChest_Watcher>();
	
	List<CommandListener> runningCommands = new ArrayList<CommandListener>();
	
	List<PlateToListen> platesToListen = new ArrayList<PlateToListen>();
	
	public DropChest_Plate_Listener(Plugin plugin){
		owner = plugin;
	}

	@EventHandler
	public void OnBreakBlock(BlockBreakEvent event){
		if(event.getBlock().getTypeId() == 72 || event.getBlock().getTypeId() == 54){
			
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("inform")){ // If the player typed /basic then do the following...
			if (sender instanceof Player) {
				sender.sendMessage("Click Plate or Chest");
				runningCommands.add(new InformPlate_Command((Player)sender));
				return true;
			}
		} //If this has happened the function will return true. 
	        // If this hasn't happened the a value of false will be returned.
		return false; 
	}

	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event){
		List<CommandListener> deleteThem = new ArrayList<CommandListener>();
		Iterator<CommandListener> ita = runningCommands.iterator();
		while(ita.hasNext()){
			CommandListener cl = ita.next();
			if(cl.OnPlayerInteract(event)){
				//War vermutlich ein InformPlate und ist fertig
				if(cl instanceof InformPlate_Command){
					platesToListen.add(new PlateToListen(((InformPlate_Command)cl).getPlate(),((InformPlate_Command)cl).getChest()));
					event.getPlayer().sendMessage("Plate will be Watched");
					deleteThem.add(cl);
				}
			}
		}
		for(int i=0;i<deleteThem.size();i++){
			runningCommands.remove(deleteThem.get(i));
		}
	}
	
	@EventHandler
	public void OnEntityInteract(EntityInteractEvent event){
		if(event.getEntity() instanceof Item && event.getBlock().getTypeId() == 72){
			//Item on Wooden Pressure Plate
			//Same as AC-Chest
			
				Block plate = event.getBlock();
				Location plateLoc = plate.getLocation();
				Location chestLocs[] = {plateLoc.clone().add(1, 0, 0),plateLoc.clone().add(-1, 0, 0),plateLoc.clone().add(0, 0, 1),plateLoc.clone().add(0, 0, -1)};
				
				for(int i = 0; i<chestLocs.length; i++){
					if(chestLocs[i].getBlock().getTypeId() == 54){
						//Chest on X+1/X-1/Z+1/Z-1 Location of Plate
						Chest c = (Chest) chestLocs[i].getBlock().getState();
						
						//Add Entity(As Item) to Chest
						c.getInventory().addItem(((Item)event.getEntity()).getItemStack());
								
						//Delete Item from World
						event.getEntity().remove();
								
						//Start Thread to check if there are more Items dropped on the plate while its pressed
						Bukkit.getServer().getLogger().info("PlateNearChest_Watcher gestartet");
						platesToWatch.add(new PlateNearChest_Watcher(owner,plate));
						break;
					}
				}
				
				Bukkit.getServer().getLogger().info(String.valueOf(platesToListen.size()) + " Anzahl Extra Plates");
				for(int i=0;i<platesToListen.size();i++){
					if(platesToListen.get(i).getPlate().equals(plate)){
						Bukkit.getServer().getLogger().info("Its the Right Plate");
						Chest c = platesToListen.get(i).getChest();
						
						//Add Entity(As Item) to Chest
						c.getInventory().addItem(((Item)event.getEntity()).getItemStack());
								
						//Delete Item from World
						event.getEntity().remove();
								
						//Start Thread to check if there are more Items dropped on the plate while its pressed
						Bukkit.getServer().getLogger().info("PlateNearChest_Watcher gestartet");
						platesToWatch.add(new PlateNearChest_Watcher(owner,plate,c));
					}
				}
				
				/*
				if(event.getBlock().getWorld().getBlockTypeIdAt(event.getBlock().getX()+1,event.getBlock().getY(),event.getBlock().getZ()) == 54){				
					//Get Chest at X+1 Location of Plate
					Chest c = (Chest) event.getBlock().getWorld().getBlockAt(event.getBlock().getX()+1,event.getBlock().getY(),event.getBlock().getZ()).getState();

					//Add item to Chest
					c.getInventory().addItem(((Item)event.getEntity()).getItemStack());
							
					//Delete Item from World
					event.getEntity().remove();
							
					//Start Thread to check if there are more Items dropped on the plate while its pressed
					list1.add(Bukkit.getScheduler().runTask(owner, new PlateNearChest_Watcher(owner, event.getBlock())));
				}*/

		}
	}


}
