package com.gmail.zimmerlint.plugins.DropChest;

import org.bukkit.plugin.java.JavaPlugin;

public class DropChest extends JavaPlugin{
	//public Logger log;

	@Override
    public void onEnable(){
        // TODO Insert logic to be performed when the plugin is enabled
		DropChest_Plate_Listener listener = new DropChest_Plate_Listener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		getCommand("inform").setExecutor(listener);
    }
 
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    	
    }

}
