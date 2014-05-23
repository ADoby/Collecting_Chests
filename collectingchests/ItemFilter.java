package collectingchests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ItemFilter {
	
	Map<Integer,Filter_Attributes> filters = new HashMap<Integer,Filter_Attributes>();
	
	public boolean addItem(ItemStack item, boolean ignoreDamage){
		if(!filters.containsKey(item.getData().getItemTypeId())){
			filters.put(item.getData().getItemTypeId(), new Filter_Attributes(item.getData(),ignoreDamage));
			return true;
		}
		return false;
	}
	
	public boolean removeItem(ItemStack item){
		if(filters.containsKey(item.getData().getItemTypeId())){
			filters.remove(item.getData().getItemTypeId());
			return true;
		}
		return false;
	}
	
	public boolean isAllowed(ItemStack item){
		if(filters.containsKey(item.getData().getItemTypeId())){
			if(filters.get(item.getData().getItemTypeId()).ignoreDamage){
				return true;
			}
			if(filters.get(item.getData().getItemTypeId()).mData.equals(item.getData())){
				return true;
			}
		}
		return false;
	}

	public List<String> getStringList() {
		//TODO create new List Method
		List<String> stringList = new ArrayList<String>();
		stringList.add("TODO Sory");
		for(int i=0;i<filters.size();i++){
			MaterialData mData = filters.get(i).mData;
			String ignoreDamage = String.valueOf(filters.get(i).ignoreDamage);
			stringList.add(mData.toString() + ":" + ignoreDamage);
		}
		return stringList;
	}
	
	public List<String> getConfigList(){
		List<String> stringList = new ArrayList<String>();
		
		Iterator<Entry<Integer, Filter_Attributes>> it = filters.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, Filter_Attributes> entry = it.next();
			Filter_Attributes fA = entry.getValue();
			MaterialData mData = fA.mData;
			String ignoreDamage = String.valueOf(fA.ignoreDamage);
			Bukkit.getServer().getLogger().info(String.valueOf(mData.getItemTypeId()) + ":" + mData.getData() + ":" + ignoreDamage);
			stringList.add(String.valueOf(mData.getItemTypeId()) + ":" + mData.getData() + ":" + ignoreDamage);
		}
		for(int i=0;i<filters.size();i++){
			
		}
		return stringList;
	}
	
	public boolean hasFilters(){
		return (filters.size()>0);
	}
	
}
