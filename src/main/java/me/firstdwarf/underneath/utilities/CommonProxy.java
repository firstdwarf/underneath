package me.firstdwarf.underneath.utilities;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.NaturalBlock;
import me.firstdwarf.underneath.block.OreBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber
public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e)	{
		for(OreBlock block : BlockMain.oreBlockList)	{
			OreDictionary.registerOre(block.getName(), block);
		}
	}
	public void init(FMLInitializationEvent e)	{
		
	}
	public void postInit(FMLPostInitializationEvent e)	{
		
	}
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
}