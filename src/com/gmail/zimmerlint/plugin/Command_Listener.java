package com.gmail.zimmerlint.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class Command_Listener implements Listener, CommandExecutor{

	CollectingChests owner;
	List<String> primaryCommands = new ArrayList<String>();
	List<String> secondaryCommands = new ArrayList<String>();
	
	List<PlayerListener> runningCommands = new ArrayList<PlayerListener>();
	
	public Command_Listener(CollectingChests plugin){
		owner = plugin;
		primaryCommands.add("plate");
		primaryCommands.add("area");
		primaryCommands.add("chest");
		primaryCommands.add("end");
		secondaryCommands.add("create");
		secondaryCommands.add("delete");
	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event){
		List<PlayerListener> deleteThem = new ArrayList<PlayerListener>();
		Iterator<PlayerListener> ita = runningCommands.iterator();
		while(ita.hasNext()){
			PlayerListener cl = ita.next();
			int returns = cl.OnPlayerInteract(event);
			
			if(returns == 1){ //0 not finished, 1 finished, 2 error
				//Finished, end Command
				if(cl instanceof PlateCreate_CommandListener){
					//Its a "/collectChest plate create" Listener
					Block plate = ((PlateCreate_CommandListener)cl).getPlate();
					Chest chest = ((PlateCreate_CommandListener)cl).getChest();
					owner.getPlateListener().addPlateConnection(new Plate_Connection(plate,chest));
					event.getPlayer().sendMessage("§aConnection created");
					
					deleteThem.add(cl);
				}
				if(cl instanceof PlateDelete_CommandListener){
					//Its a "/collectChest plate delete" Listener
					Block plate = ((PlateDelete_CommandListener)cl).getPlate();
					if(!owner.getPlateListener().deletePlateConnection(event.getPlayer(),plate)){
						event.getPlayer().sendMessage("§eCollecting Chests:");
						event.getPlayer().sendMessage("  §cThere was no connection");
					}
					deleteThem.add(cl);
				}
			}else if(returns == 2){
				//Error, end Command
				deleteThem.add(cl);
			}
		}
		for(int i=0;i<deleteThem.size();i++){
			runningCommands.remove(deleteThem.get(i));
		}
	}
	
	private void abortCommands(Player player){
		List<PlayerListener> deleteThem = new ArrayList<PlayerListener>();
		Iterator<PlayerListener> ita = runningCommands.iterator();
		while(ita.hasNext()){
			PlayerListener cl = ita.next();
			if(cl.getPlayer() == player){
				//Delete it
				deleteThem.add(cl);
			}

		}
		for(int i=0;i<deleteThem.size();i++){
			runningCommands.remove(deleteThem.get(i));
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		if(cmd.getName().equalsIgnoreCase("collectChest") || cmd.getName().equalsIgnoreCase("cc")){
			if (args.length > 3) {
				//Maximum 3 Arguments
				sender.sendMessage("§4Too many arguments! §2See /collectChest for help");
				return false;
			} 
		    if (args.length < 1) {
		    	//Minimum 2 Arguments
		    	if(args.length == 0){
		    		//Help
		    		sender.sendMessage("§aCollecting Chests v" + owner.getVersion() + " help");
		    		sender.sendMessage("§e/collectChests <primary> <secondary> <arg>");
		    		sender.sendMessage("§6/collectChest plate create");
		    		sender.sendMessage("  §aConnects a plate with a chest");
		    		sender.sendMessage("§6/collectChest plate delete");
		    		sender.sendMessage("  §aDeletes the connection between a plate and a chest");
		    		
		    		sender.sendMessage("§6/collectChest area create");
		    		sender.sendMessage("  §aCreates and connects an area with a chest");
		    		sender.sendMessage("§6/collectChest area delete");
		    		sender.sendMessage("  §aDeletes an area around a block and his connection");
		    		
		    		sender.sendMessage("§6/collectChest chest create §b<distance>");
		    		sender.sendMessage("  §aCreates an automatic collecting chest");
		    		sender.sendMessage("§6/collectChest chest delete");
		    		sender.sendMessage("  §aStops a chest from automatically collecting");
		    		sender.sendMessage("§6/collectChest end");
		    		sender.sendMessage("  §aStops current command");
		    	}else{
		    		sender.sendMessage("§6Not enough arguments! §2See /collectChest for help");
		    	}
		        return false;
		    }
		    if(!primaryCommands.contains(args[0].toLowerCase())){
		    	//Theres no such primary command
		    	sender.sendMessage("§4Theres no such primary command! §2See /collectChest for help");
		    	sender.sendMessage("§4Try 'plate' or 'area' or 'chest' or 'end'");
		    	return false;
		    }
		    if(args.length>1 && !secondaryCommands.contains(args[1].toLowerCase())){
		    	//Theres no such secondary command
		    	sender.sendMessage("§4Theres no such secondary command! §2See /collectChest for help");
		    	sender.sendMessage("§4Try 'create' or 'delete'");
		    	return false;
		    }
			if (sender instanceof Player) {
				if(args[0].equalsIgnoreCase("end")){
					abortCommands((Player)sender);
					sender.sendMessage("§eCollecting Chests:");
					sender.sendMessage("  §cCommands aborted");
				}
				if(args[0].equalsIgnoreCase("plate")){
					boolean workingCommand = false;
					if(args[1].equalsIgnoreCase("create")){
						//He wants to create a plate connection
						workingCommand = true;
					}else if(args[1].equalsIgnoreCase("delete")){
						//He wants to delete a plate connection
						workingCommand = true;
					}
					
					if(workingCommand){
						startNewCommand((Player)sender,args[0] + " " + args[1]);
						return true;
					}
				}
			}
		}
		
		return false;
	}

	
	private void startNewCommand(Player player, String command){
		abortCommands(player);
		if(command.equalsIgnoreCase("plate create")){
			player.sendMessage("§eCollecting Chests:");
			player.sendMessage("  §bNow click or step on a plate §aor click on a chest");
			runningCommands.add(new PlateCreate_CommandListener(player));
		}
		if(command.equalsIgnoreCase("plate delete")){
			player.sendMessage("§eCollecting Chests:");
			player.sendMessage("  §aNow click or step on the plate");
			runningCommands.add(new PlateDelete_CommandListener(player));
		}
	}
}
