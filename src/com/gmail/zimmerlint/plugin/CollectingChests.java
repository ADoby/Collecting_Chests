package com.gmail.zimmerlint.plugin;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class CollectingChests extends JavaPlugin{
	//public Logger log;

	String version = "";
	Command_Listener CL;
	Plate_Listener PL;
	
	ConfigAccessor plateConfig = null;
	ConfigAccessor chestConfig = null;
	
	
	@Override
    public void onEnable(){
        // TODO Insert logic to be performed when the plugin is enabled
		CL = new Command_Listener(this);
		PL = new Plate_Listener(this);
		
		version = this.getDescription().getVersion();
		
		getServer().getPluginManager().registerEvents(CL, this);
		getServer().getPluginManager().registerEvents(PL, this);
		
		getCommand("collectChest").setExecutor(CL);
		
		log("LOADING CONFIG");
		loadConfig();
		log("CONFIG LOADED");
		log("LOADING PLATES");
		loadPlates();
		log("PLATES LOADED");
		log("LOADING CHESTS");
		loadChests();
		log("CHESTS LOADED");
    }
 
	private void log(String msg){
		getServer().getLogger().info("[Collecting Chests] "+msg);
	}
	
	private void loadConfig(){
		plateConfig = new ConfigAccessor(this, "plates.yml");
		chestConfig = new ConfigAccessor(this,"chests.yml");

	}
	
	private void loadPlates(){
		ConfigurationSection CS = plateConfig.getConfig().getConfigurationSection("plates");
		if(CS != null){
			Set<String> plateList = CS.getKeys(false);
			Iterator<String> ita = plateList.iterator();
			while(ita.hasNext()){
				String plateLoc = ita.next();
				World world = getServer().getWorld(plateConfig.getConfig().getString("plates." + plateLoc + ".world"));
				if(world!=null) {
					String plateCoords[] = plateLoc.split("/");
					Location plateLocation = new Location(world,Double.parseDouble(plateCoords[0]),Double.parseDouble(plateCoords[1]),Double.parseDouble(plateCoords[2]));
					String chestCoords[] = plateConfig.getConfig().getString("plates."+plateLoc + ".chest").split("/");
					Location chestLocation = new Location(world,Double.parseDouble(chestCoords[0]),Double.parseDouble(chestCoords[1]),Double.parseDouble(chestCoords[2]));
					log("PlateID: " + String.valueOf(plateLocation.getBlock().getTypeId()));
					log("ChestID: " + String.valueOf(chestLocation.getBlock().getTypeId()));
					PL.addPlateConnection(new Plate_Connection(plateLocation.getBlock(),((Chest)chestLocation.getBlock().getState())));
				}
			}
			log("Connection count: " + String.valueOf(PL.getConnectionCount()));
		}
	}
	
	private void loadChests(){
		ConfigurationSection CS = chestConfig.getConfig().getConfigurationSection("chests");
		if(CS != null){
			Set<String> chestList = CS.getKeys(false);
			Iterator<String> ita = chestList.iterator();
			while(ita.hasNext()){
				String chestLoc = ita.next();
				World world = getServer().getWorld(plateConfig.getConfig().getString("chests." + chestLoc + ".world"));
				if(world!=null) {
					//String plateCoords[] = chestLoc.split("/");
					//Location ChestLocation = new Location(world,Double.parseDouble(plateCoords[0]),Double.parseDouble(plateCoords[1]),Double.parseDouble(plateCoords[2]));
					
					//String chestCoords[] = plateConfig.getConfig().getString("chests."+chestLoc + ".chest").split("/");
					//Location chestLocation = new Location(world,Double.parseDouble(chestCoords[0]),Double.parseDouble(chestCoords[1]),Double.parseDouble(chestCoords[2]));
					//PL.addPlateConnection(new Plate_Connection(plateLocation.getBlock(),((Chest)chestLocation.getBlock().getState())));
				}
			}

		}		
	}
	
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    	this.saveConfig();
    	plateConfig.saveConfig();
    	chestConfig.saveConfig();
    }
    
    public Plate_Listener getPlateListener(){
    	return PL;
    }
    
    public String getVersion(){
    	return version;
    }

	public ConfigAccessor getPlateConfig() {
		return plateConfig;
	}
    
	public ConfigAccessor getChestsConfig() {
		return chestConfig;
	}

}
