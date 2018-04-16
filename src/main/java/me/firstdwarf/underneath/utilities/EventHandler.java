package me.firstdwarf.underneath.utilities;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.NaturalBlock;
import me.firstdwarf.underneath.block.OreBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)	{
		BlockMain.createBlocks();
		for(NaturalBlock block : BlockMain.naturalBlockList)	{
			event.getRegistry().register(block);
		}
		for(OreBlock block : BlockMain.oreBlockList)	{
			event.getRegistry().register(block);
		}
	}
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)	{
		for(NaturalBlock block : BlockMain.naturalBlockList)	{
			event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}
		for(OreBlock block : BlockMain.oreBlockList)	{
			event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}
	}
	@SubscribeEvent
	public static void breakBlocks(BreakSpeed event)	{
		EntityPlayer player = event.getEntityPlayer();
		String toolMain = player.getHeldItemMainhand().getDisplayName();
		String toolOff = player.getHeldItemOffhand().getDisplayName();
		if(event.getState().getMaterial().equals((Material) CustomMaterial.NATURAL))	{
			System.out.println(toolMain + " " + toolOff);
		}
	}
}