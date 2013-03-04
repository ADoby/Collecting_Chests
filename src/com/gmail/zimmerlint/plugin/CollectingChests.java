package com.gmail.zimmerlint.plugin;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CollectingChests extends JavaPlugin{
	//public Logger log;

	String version = "";
	Command_Listener CommandL;
	Plate_Listener PlateL;
	Chest_Listener ChestL;
	
	ConfigAccessor plateConfig = null;
	ConfigAccessor chestConfig = null;
	ConfigAccessor filterConfig = null;
	
	@Override
    public void onEnable(){
        // TODO Insert logic to be performed when the plugin is enabled
		CommandL = new Command_Listener(this);
		PlateL = new Plate_Listener(this);
		ChestL = new Chest_Listener(this);
		
		version = this.getDescription().getVersion();
		
		getServer().getPluginManager().registerEvents(CommandL, this);
		getServer().getPluginManager().registerEvents(PlateL, this);
		getServer().getPluginManager().registerEvents(ChestL, this);
		
		getCommand("collectChest").setExecutor(CommandL);
		
		log("LOADING CONFIG");
		loadConfig();
		log("CONFIG LOADED");
		log("LOADING PLATES");
		loadPlates();
		log("PLATES LOADED");
		log("LOADING CHESTS");
		loadChests();
		log("CHESTS LOADED");
		log("LOADING FILTERS");
		loadFilters();
		log("FILTERS LOADED");
    }
 
	private void log(String msg){
		getServer().getLogger().info("[Collecting Chests] "+msg);
	}
	
	private void loadConfig(){
		plateConfig = new ConfigAccessor(this, "plates.yml");
		chestConfig = new ConfigAccessor(this,"chests.yml");
		filterConfig = new ConfigAccessor(this,"filters.yml");
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
					if(plateLocation.getBlock().getTypeId() != 72 || chestLocation.getBlock().getTypeId() != 54){
						//Plate ist keine Plate mehr, oder Chest keine Chest
						log("Delete Connection: Plate or Chest changed their BlockType");
					}else{
						//Else its correct, listen on it
						PlateL.addPlateConnection(new Plate_Connection(plateLocation.getBlock(),((Chest)chestLocation.getBlock().getState())));
					}
				}
			}
			log("Connection count: " + String.valueOf(PlateL.getConnectionCount()));
		}
	}
	
	private void loadChests(){
		
	}
	
	private void loadFilters(){
		ConfigurationSection CS = filterConfig.getConfig().getConfigurationSection("chests");
		if(CS != null){
			Set<String> chestList = CS.getKeys(false);
			Iterator<String> ita = chestList.iterator();
			while(ita.hasNext()){
				String chestLoc = ita.next();
				World world = getServer().getWorld(filterConfig.getConfig().getString("chests." + chestLoc + ".world"));
				if(world!=null) {
					
					
					String chestCoords[] = chestLoc.split("/");
					Location ChestLocation = new Location(world,Double.parseDouble(chestCoords[0]),Double.parseDouble(chestCoords[1]),Double.parseDouble(chestCoords[2]));
					
				
					
					if(ChestLocation.getBlock().getTypeId() == 54){
						ChestL.chests.put((Chest)ChestLocation.getBlock().getState(), new Chest_Attributes());
						//ChestL.addFilter((Chest)ChestLocation.getBlock().getState(), new Chest_Attributes(item,ignoreDamage));
					}
					
					//TODO Add all Filters
					List<String> stringList = filterConfig.getConfig().getStringList("chests." + chestLoc + ".filters");
					Chest_Attributes cA = ChestL.chests.get((Chest)ChestLocation.getBlock().getState());
					Iterator<String> Listita = stringList.iterator();
					while(Listita.hasNext()){
						String itemStrings[] = Listita.next().split(":");
						if(isInteger(itemStrings[0]) && isInteger(itemStrings[1])){
							cA.addItemFilter(new ItemStack(Integer.parseInt(itemStrings[0]),1,(short)Integer.parseInt(itemStrings[1])), Boolean.parseBoolean(itemStrings[2]));
						}
						
					}
					
					//String chestCoords[] = plateConfig.getConfig().getString("chests."+chestLoc + ".chest").split("/");
					//Location chestLocation = new Location(world,Double.parseDouble(chestCoords[0]),Double.parseDouble(chestCoords[1]),Double.parseDouble(chestCoords[2]));
					//PL.addPlateConnection(new Plate_Connection(plateLocation.getBlock(),((Chest)chestLocation.getBlock().getState())));
				}
			}

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
	
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    	this.saveConfig();
    	plateConfig.saveConfig();
    	chestConfig.saveConfig();
    	filterConfig.saveConfig();
    }
    
    public Plate_Listener getPlateListener(){
    	return PlateL;
    }
    
    public Chest_Listener getChestListener(){
    	return ChestL;
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

	public ConfigAccessor getFiltersConfig() {
		return filterConfig;
	}
}
