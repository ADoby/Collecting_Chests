package collectingchests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class PlayerListener {
	
	Player player;
	
	public PlayerListener(Player player){
		this.player = player;
	}
	
	@EventHandler
	abstract public int onPlayerInteract(PlayerInteractEvent event);
	
	public Player getPlayer() {
		return player;
	}

}
