package collectingchests;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FilterCreate_CommandListener extends PlayerListener {
	
	Chest chest;
	ItemStack item = null;
	boolean ignoreDamage = false;
	
	
	public FilterCreate_CommandListener(Player player) {
		super(player);
	}
	
	@Override
	public int onPlayerInteract(PlayerInteractEvent event) {
		//0 not finished, 1 finished, 2 error
		event.setCancelled(true);
		if(item == null && event.getClickedBlock().getTypeId() == 54) {
			chest = (Chest)event.getClickedBlock().getState();
			item = event.getPlayer().getItemInHand();
			if(item.getType() != null){
				//Item Saved Now ask for Ignoring Damage
				player.sendMessage("§eCollecting Chests:");
				player.sendMessage("  §aDo you want do ignore ItemDamage (Colored Wool or weapons) click on the chest again, if NOT click somewhere else");
				return 0; 
			}else{
				//No Item in Hand
				player.sendMessage("§eCollecting Chests:");
				player.sendMessage("  §cYou need an item in your hand");
				return 2;
			}
		}else if(item != null && event.getClickedBlock().getTypeId() == 54){
			ignoreDamage = true;
			return 1;
		}else if(item != null){
			ignoreDamage = false;
			return 1;
		}else{
			//error/wrong block
			player.sendMessage("§eCollecting Chests:");
			player.sendMessage("  §cThats no chest");
			return 2;
		}
	}

	public Chest getChest() {
		return chest;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public boolean getIgnoreDamage(){
		return ignoreDamage;
	}

}
