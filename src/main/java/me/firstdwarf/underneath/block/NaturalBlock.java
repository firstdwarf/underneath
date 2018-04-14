package me.firstdwarf.underneath.block;

import me.firstdwarf.underneath.core.Underneath;
import me.firstdwarf.underneath.utilities.CustomMaterial;
import net.minecraft.block.Block;

public class NaturalBlock extends Block	{
	
	public NaturalBlock(String name, float hardness, float resistance, int opacity, String toolType) {
		super(CustomMaterial.NATURAL);
		this.setUnlocalizedName("underneath." + name.toLowerCase());
		this.setRegistryName(name.toLowerCase());
		this.setCreativeTab(Underneath.underneathTab);
		this.setHardness(hardness);
		this.setResistance(resistance);
		this.setLightOpacity(opacity);
		this.setHarvestLevel(toolType, 3);
	}
}