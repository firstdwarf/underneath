package me.firstdwarf.underneath.block;

import me.firstdwarf.underneath.Underneath;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class NaturalBlock extends Block	{
	public NaturalBlock(String name) {
		super(Material.ROCK);
		this.setUnlocalizedName("underneath." + name.toLowerCase());
		this.setRegistryName(name.toLowerCase());
		this.setCreativeTab(Underneath.underneathTab);
	}
}