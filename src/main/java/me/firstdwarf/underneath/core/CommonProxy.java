package me.firstdwarf.underneath.core;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.OreBlock;
import me.firstdwarf.underneath.utilities.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber
public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e)	{
		MinecraftForge.EVENT_BUS.register(EventHandler.class);
		for(OreBlock block : BlockMain.oreBlockList)	{
			OreDictionary.registerOre(block.getName(), block);
		}
	}
	public void init(FMLInitializationEvent e)	{
		
	}
	public void postInit(FMLPostInitializationEvent e)	{
		
	}
}