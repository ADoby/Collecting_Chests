package collectingchests;

import org.bukkit.material.MaterialData;

public class Filter_Attributes {

	MaterialData mData;
	boolean ignoreDamage;
	
	public Filter_Attributes(MaterialData mData, boolean ignoreDamage){
		this.mData = mData;
		this.ignoreDamage = ignoreDamage;
	}
	
}
