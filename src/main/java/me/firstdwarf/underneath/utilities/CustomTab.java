package me.firstdwarf.underneath.utilities;

import me.firstdwarf.underneath.block.BlockMain;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CustomTab extends CreativeTabs	{

	public CustomTab(String name) {
		super(name);
	}
	
	@Override
	public ItemStack getTabIconItem()	{
		return new ItemStack(Item.getItemFromBlock(BlockMain.exampleBlock));
	}
}