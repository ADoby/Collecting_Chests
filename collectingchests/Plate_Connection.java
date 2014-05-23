package collectingchests;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;

public class Plate_Connection {

	private Block plate = null;
	private Chest chest = null;
	
	public Plate_Connection(Block plate, Chest chest){
		this.plate = plate;
		this.chest = chest;
	}
	
	public Block getPlate(){
		return plate;
	}
	
	public Chest getChest(){
		return chest;
	}
	
}
