package collectingchests;

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
import org.bukkit.inventory.ItemStack;

public class Command_Listener implements Listener, CommandExecutor{

	CollectingChests owner;
	List<String> primaryCommands = new ArrayList<String>();
	List<String> secondaryCommands = new ArrayList<String>();
	
	List<String> createCommands = new ArrayList<String>();
	List<String> deleteCommands = new ArrayList<String>();
	
	
	List<PlayerListener> runningCommands = new ArrayList<PlayerListener>();
	
	public Command_Listener(CollectingChests plugin){
		owner = plugin;
		primaryCommands.add("plate");
		primaryCommands.add("area");
		primaryCommands.add("chest");
		primaryCommands.add("end");
		primaryCommands.add("filter");
		createCommands.add("create");
		createCommands.add("add");
		deleteCommands.add("delete");
		deleteCommands.add("remove");
		
		secondaryCommands.addAll(createCommands);
		secondaryCommands.addAll(deleteCommands);
	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event){
		List<PlayerListener> deleteThem = new ArrayList<PlayerListener>();
		Iterator<PlayerListener> ita = runningCommands.iterator();
		while(ita.hasNext()){
			PlayerListener commandL = ita.next();
			int returns = 0;
			
			if(commandL.getPlayer().equals(event.getPlayer())){
				returns = commandL.onPlayerInteract(event);
			}
			
			if(returns == 1){ //0 not finished, 1 finished, 2 error
				//Plate Commands
				if(commandL instanceof PlateCreate_CommandListener){
					//Its a "/collectChest plate create" Listener
					Block plate = ((PlateCreate_CommandListener)commandL).getPlate();
					Chest chest = ((PlateCreate_CommandListener)commandL).getChest();
					owner.getPlateListener().addPlateConnection(new Plate_Connection(plate,chest));
					event.getPlayer().sendMessage("  §cConnection created");
					
					deleteThem.add(commandL);
				}
				if(commandL instanceof PlateDelete_CommandListener){
					//Its a "/collectChest plate delete" Listener
					Block plate = ((PlateDelete_CommandListener)commandL).getPlate();
					if(!owner.getPlateListener().deletePlateConnection(event.getPlayer(),plate)){
						event.getPlayer().sendMessage("§eCollecting Chests:");
						event.getPlayer().sendMessage("  §cThere was no connection");
					}
					deleteThem.add(commandL);
				}
				
				//Filter Commands
				if(commandL instanceof FilterCreate_CommandListener){
					//Its a "/collectChest plate create" Listener
					Chest chest = ((FilterCreate_CommandListener)commandL).getChest();
					ItemStack item = ((FilterCreate_CommandListener)commandL).getItem();
					boolean ignoreDamage = ((FilterCreate_CommandListener)commandL).getIgnoreDamage();
					
					owner.getChestListener().addFilter(chest, item, ignoreDamage);
					
					event.getPlayer().sendMessage("  §aFilter created");
					
					deleteThem.add(commandL);
				}
				if(commandL instanceof FilterDelete_CommandListener){
					//Its a "/collectChest plate delete" Listener
					Chest chest = ((FilterDelete_CommandListener)commandL).getChest();
					ItemStack item = ((FilterDelete_CommandListener)commandL).getItem();

					if(!owner.getChestListener().deleteFilter(chest, item)){
						//Er hat ne Kiste angeklickt, aber die hat den Filter garnicht
						event.getPlayer().sendMessage("§eCollecting Chests:");
						event.getPlayer().sendMessage("  §cThere was no such Filter");
					}else{
						event.getPlayer().sendMessage("§eCollecting Chests:");
						event.getPlayer().sendMessage("  §aFilter deleted");
					}
					deleteThem.add(commandL);
				}
			}else if(returns == 2){
				//Error, end Command
				deleteThem.add(commandL);
			}
		}
		for(int i=0;i<deleteThem.size();i++){
			runningCommands.remove(deleteThem.get(i));
		}
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	private void abortCommands(Player player){
		List<PlayerListener> deleteThem = new ArrayList<PlayerListener>();
		Iterator<PlayerListener> ita = runningCommands.iterator();
		while(ita.hasNext()){
			PlayerListener commandL = ita.next();
			if(commandL.getPlayer() == player){
				//Delete it
				deleteThem.add(commandL);
			}

		}
		for(int i=0;i<deleteThem.size();i++){
			runningCommands.remove(deleteThem.get(i));
		}
	}
	
	private void sendHelpToSender(CommandSender sender){
		

		
		sender.sendMessage("§aCollecting Chests v" + owner.getVersion() + " help");
    	sender.sendMessage("§e/collectChests <primary> <secondary> <arg>");
    	sender.sendMessage("§6/collectChest plate create");
    	sender.sendMessage("  §aConnects a plate with a chest");
    	sender.sendMessage("§6/collectChest plate delete");
    	sender.sendMessage("  §aDeletes the connection between a plate and a chest");
    		
    	sender.sendMessage("§6/collectChest filter add §b<ItemID/ItemName>");
    	sender.sendMessage("  §aAdds a filter for one Item for a chest");
    	sender.sendMessage("§6/collectChest filter remove §b<ItemID/ItemName>");
    	sender.sendMessage("  §aRemoves a filter for one Item for a chest");
    	sender.sendMessage("§6/collectChest filter list");
    	sender.sendMessage("  §aLists all filters for a chest");
    	
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
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("collectChest") || cmd.getName().equalsIgnoreCase("cc")){
			if (args.length > 3) {
				//Maximum 3 Arguments
				sender.sendMessage("§4Too many arguments! §2See /collectChest for help");
				return false;
			} 
		    if(args.length == 0){
		    	//Help
		    	sendHelpToSender(sender);
		    	return true;
		    }

		    if(!primaryCommands.contains(args[0].toLowerCase())){
		    	//Theres no such primary command
		    	sender.sendMessage("§4Theres no such primary command! §2See /collectChest for help");
		    	sender.sendMessage("§4Try 'plate' or 'area' or 'chest' or 'end' or 'filter'");
		    	return false;
		    }
		    if(args.length>1 && !secondaryCommands.contains(args[1].toLowerCase())){
		    	//Theres no such secondary command
		    	sender.sendMessage("§4Theres no such secondary command! §2See /collectChest for help");
		    	sender.sendMessage("§4Try 'create' or 'delete'");
		    	return false;
		    }
			if (sender instanceof Player) {
				Player player = (Player)sender;
				if(args[0].equalsIgnoreCase("end")){
					abortCommands(player);
					player.sendMessage("§eCollecting Chests:");
					player.sendMessage("  §cCommands aborted");
					return true;
				}else if(args[0].equalsIgnoreCase("plate")){
					if(createCommands.contains(args[1].toLowerCase())){
						player.sendMessage("§eCollecting Chests:");
						player.sendMessage("  §bNow click or step on a plate §aor click on a chest");
						startNewCommand(new PlateCreate_CommandListener(player));
						return true;
					}else if(deleteCommands.contains(args[1].toLowerCase())){
						player.sendMessage("§eCollecting Chests:");
						player.sendMessage("  §aNow click or step on the plate");
						startNewCommand(new PlateDelete_CommandListener(player));
						return true;
					}
				}else if(args[0].equalsIgnoreCase("filter")){
					if(createCommands.contains(args[1].toLowerCase())){
						player.sendMessage("§eCollecting Chests:");
						player.sendMessage("  §aNow click on a chest with the item");
						startNewCommand(new FilterCreate_CommandListener(player));
						return true;
					}else if(deleteCommands.contains(args[1].toLowerCase())){
						player.sendMessage("§eCollecting Chests:");
						player.sendMessage("  §aNow click on a chest with the item");
						startNewCommand(new FilterDelete_CommandListener(player));
						return true;
					}
				}
			}
		}
		
		return false;
	}

	
	private void startNewCommand(PlayerListener playerL){
		abortCommands(playerL.getPlayer());
		runningCommands.add(playerL);
	}
}
