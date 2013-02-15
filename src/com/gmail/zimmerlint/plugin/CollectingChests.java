package com.gmail.zimmerlint.plugin;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.plugin.java.JavaPlugin;

public class CollectingChests extends JavaPlugin{
	//public Logger log;

	String version = "";
	Command_Listener CL;
	Plate_Listener PL;
	
	@Override
    public void onEnable(){
        // TODO Insert logic to be performed when the plugin is enabled
		CL = new Command_Listener(this);
		PL = new Plate_Listener(this);
		
		version = this.getDescription().getVersion();
		
		getServer().getPluginManager().registerEvents(CL, this);
		getServer().getPluginManager().registerEvents(PL, this);
		
		getCommand("collectChest").setExecutor(CL);
		
		getServer().getLogger().info("LOADING CONFIG");
		Set<String> sList = this.getConfig().getConfigurationSection("plates").getKeys(false);
		Iterator<String> ita = sList.iterator();
		getServer().getLogger().info("List size: " + String.valueOf(sList.size()));
		while(ita.hasNext()){
			String plateLoc = ita.next();
			World world = getServer().getWorld(this.getConfig().getString("plates."+plateLoc + ".world"));
			String plateCoords[] = plateLoc.split("/");
			Location plateLocation = new Location(world,Double.parseDouble(plateCoords[0]),Double.parseDouble(plateCoords[1]),Double.parseDouble(plateCoords[2]));
			String chestCoords[] = this.getConfig().getString("plates."+plateLoc + ".chest").split("/");
			Location chestLocation = new Location(world,Double.parseDouble(chestCoords[0]),Double.parseDouble(chestCoords[1]),Double.parseDouble(chestCoords[2]));
			PL.addPlateConnection(new Plate_Connection(plateLocation.getBlock(),((Chest)chestLocation.getBlock().getState())));
		}
		//PL.addPlateConnection(new Plate_Connection());
		getServer().getLogger().info("END LOADING CONFIG");
    }
 
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    	this.saveConfig();
    }
    
    public Plate_Listener getPlateListener(){
    	return PL;
    }
    
    public String getVersion(){
    	return version;
    }

}
