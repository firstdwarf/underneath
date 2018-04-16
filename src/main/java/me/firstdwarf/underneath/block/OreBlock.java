package me.firstdwarf.underneath.block;

import me.firstdwarf.underneath.core.Underneath;
import me.firstdwarf.underneath.utilities.CustomMaterial;
import net.minecraft.block.Block;

public class OreBlock extends Block	{
	
	private String name;

	public OreBlock(String name, float hardness, float resistance, int opacity, String toolType) {
		super(CustomMaterial.ORE);
		this.name = name;
		this.setUnlocalizedName("underneath." + name.toLowerCase());
		this.setRegistryName(name.toLowerCase());
		this.setCreativeTab(Underneath.underneathTab);
		this.setHardness(hardness);
		this.setResistance(resistance);
		this.setLightOpacity(opacity);
		this.setHarvestLevel(toolType, 3);
	}
	
	public String getName()	{
		return name;
	}
}