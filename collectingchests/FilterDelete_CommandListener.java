package collectingchests;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FilterDelete_CommandListener extends PlayerListener {

	Chest chest;
	ItemStack item;
	
	public FilterDelete_CommandListener(Player player) {
		super(player);
	}

	@Override
	public int onPlayerInteract(PlayerInteractEvent event) {
		//0 not finished, 1 finished, 2 error
		event.setCancelled(true);
		if(event.getClickedBlock().getTypeId() == 54) {
			chest = (Chest)event.getClickedBlock().getState();
			item = event.getPlayer().getItemInHand();
			if(item.getType() != null){
				//finished
				return 1; 
			}else{
				//No Item in Hand
				player.sendMessage("§eCollecting Chests:");
				player.sendMessage("  §cYou need an item in your hand");
				return 2;
			}


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
	
}
