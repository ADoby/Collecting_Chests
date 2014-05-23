package collectingchests;

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


public class Plate_Listener implements Listener{
	
	CollectingChests owner;
	
	List<Plate_Watcher> plate_watcher = new ArrayList<Plate_Watcher>();
	List<Plate_Connection> plate_connections = new ArrayList<Plate_Connection>();
	
	public Plate_Listener(CollectingChests plugin){
		owner = plugin;
	}

	public int getConnectionCount(){
		return plate_connections.size();
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event){
		// TODO delete Connections etc.
		if(event.getBlock().getTypeId() == 72 || event.getBlock().getTypeId() == 54){
			deletePlateConnection(event.getPlayer(),event.getBlock());
		}
	}

	public void addPlateConnection(Plate_Connection pc){
		// TODO create config/look if theres such a connection
		plate_connections.add(pc);
		
		//Save Connection, overides existence connection
		String plateString = "plates." + String.valueOf(pc.getPlate().getX()) + "/" + String.valueOf(pc.getPlate().getY()) + "/" + String.valueOf(pc.getPlate().getZ()) ;
		String chestLocation = String.valueOf(pc.getChest().getX()) + "/" + String.valueOf(pc.getChest().getY()) + "/" + String.valueOf(pc.getChest().getZ());
		owner.getPlateConfig().getConfig().set(plateString+ ".world", pc.getPlate().getWorld().getName());
		owner.getPlateConfig().getConfig().set(plateString+ ".chest", chestLocation);
		owner.getPlateConfig().saveConfig();
	}
	
	private void addItem(Chest chest, Item item){
		//Add Entity(As Item) to Chest
		chest.getInventory().addItem(item.getItemStack());
			
		//Delete Item from World
		item.remove();
	}
	
	@EventHandler
	public void onEntityInteract(EntityInteractEvent event){

		if(event.getEntity() instanceof Item && event.getBlock().getTypeId() == 72){
			//Item on Wooden Pressure Plate
			
			Block plate = event.getBlock();
			boolean itemGone = false;
			
				
				for(int i=0;i<plate_connections.size();i++){
					if(plate_connections.get(i).getPlate().equals(plate)){
						Inventory inv = plate_connections.get(i).getChest().getInventory();
						
						if(inv.firstEmpty() != -1){
							
							if(owner.getChestListener().hasChestAttribute(plate_connections.get(i).getChest())){
								//Chest has got Filters
								if(owner.getChestListener().getChestAttributes(plate_connections.get(i).getChest()).wantsItem(((Item)event.getEntity()).getItemStack())){
									//Chest wants Item
									addItem(plate_connections.get(i).getChest(),(Item)event.getEntity());
									itemGone = true;
									break;
								}
							}else{
								addItem(plate_connections.get(i).getChest(),(Item)event.getEntity());
								itemGone = true;
								break;
							}

						}
					}
				}
			
			
			if(!itemGone){
				
				//Item isn't gone yet, try near chests
				
				Location plateLoc = plate.getLocation();
				Location chestLocs[] = {plateLoc.clone().add(1, 0, 0),plateLoc.clone().add(-1, 0, 0),plateLoc.clone().add(0, 0, 1),plateLoc.clone().add(0, 0, -1)};
				
				for(int i = 0; i<chestLocs.length; i++){
					if(chestLocs[i].getBlock().getTypeId() == 54){
						//Chest on X+1/X-1/Z+1/Z-1 Location of Plate
						Chest chest = (Chest)chestLocs[i].getBlock().getState();
						Inventory inv = chest.getInventory();
						
						if(inv.firstEmpty() != -1){
							
							
							if(owner.getChestListener().hasChestAttribute(chest)){
								//Chest has got Filters
								if(owner.getChestListener().getChestAttributes(chest).wantsItem(((Item)event.getEntity()).getItemStack())){
									//Chest wants Item
									addItem(chest,(Item)event.getEntity());
									break;
								}
							}else{
								addItem(chest,(Item)event.getEntity());
								break;
							}							
						}
					}
				}

			}
			
			//Start Thread to check if there are more Items dropped on the plate while its pressed
			plate_watcher.add(new Plate_Watcher(owner,plate,getChests(plate)));

		}else if(event.getBlock().getTypeId() == 72){
			//Its no Item
			Block plate = event.getBlock();
			plate_watcher.add(new Plate_Watcher(owner,plate,getChests(plate)));
		}
	}

	public List<Chest> getChests(Block plate){
		List<Chest> chestList = new ArrayList<Chest>();
		
		for(int i=0;i<plate_connections.size();i++){
			if(plate_connections.get(i).getPlate().equals(plate)){
				chestList.add(plate_connections.get(i).getChest());
			}
		}
		return chestList;
	}
	
	public boolean deletePlateConnection(Player player, Block plate) {
		for(int i=0;i<plate_connections.size();i++){
			if(plate_connections.get(i).getPlate().equals(plate) || plate_connections.get(i).getChest().getBlock().equals(plate)){
				Plate_Connection pc = plate_connections.get(i);
				//Plate is in list
				plate_connections.remove(i);
				
				String plateLoc = String.valueOf(pc.getPlate().getX()) + "/" + String.valueOf(pc.getPlate().getY()) + "/" + String.valueOf(pc.getPlate().getZ()) ;
				
				owner.getPlateConfig().getConfig().getConfigurationSection("plates").set(plateLoc, null);
				
				owner.getPlateConfig().saveConfig();
				
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
