package me.firstdwarf.underneath.utilities;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class CustomMaterial extends Material	{

	public static final CustomMaterial NATURAL = 
			(CustomMaterial) ((CustomMaterial) (new CustomMaterial(MapColor.OBSIDIAN)).setRequiresTool()).setImmovableMobility();
	public static final CustomMaterial ORE = 
			(CustomMaterial) ((CustomMaterial) (new CustomMaterial(MapColor.ICE)).setRequiresTool()).setImmovableMobility();
	
	public CustomMaterial(MapColor color) {
		super(color);
	}

}