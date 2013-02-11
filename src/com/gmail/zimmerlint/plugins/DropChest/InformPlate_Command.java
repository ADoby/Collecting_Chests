package com.gmail.zimmerlint.plugins.DropChest;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class InformPlate_Command extends CommandListener{

	private Block plate = null;
	private Chest chest = null;
	
	public InformPlate_Command(Player player){
		super(player);
	}	
	
	public Block getPlate(){
		return plate;
	}
	
	public Chest getChest(){
		return chest;
	}

	public boolean OnPlayerInteract(PlayerInteractEvent event){
		if(plate==null && event.getClickedBlock().getTypeId() == 72 && event.getPlayer() == player) {
			plate = event.getClickedBlock();
			event.setCancelled(true);
			
			if(chest==null){
				player.sendMessage("Plate Informed, Infor Chest Next");
			}else{
				return true;
			}
		}else if(chest==null && event.getClickedBlock().getTypeId() == 54 && event.getPlayer() == player) {
			chest = (Chest)event.getClickedBlock().getState();
			event.setCancelled(true);
			
			if(plate==null){
				player.sendMessage("Chest Informed, Inform Plate Next");
			}else{
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
}
