package me.firstdwarf.underneath.core;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.OreBlock;
import me.firstdwarf.underneath.utilities.EventHandler;
import me.firstdwarf.underneath.world.UnderneathDimensions;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber
public class CommonProxy {

	public void init(FMLInitializationEvent e) {

	}

	public void preInit(FMLPreInitializationEvent e) {
        UnderneathDimensions.init();

        MinecraftForge.EVENT_BUS.register(EventHandler.class);
		for(OreBlock block : BlockMain.oreBlockList)	{
			OreDictionary.registerOre(block.getName(), block);
		}
	}

	public void postInit(FMLPostInitializationEvent e) {

	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {

	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		
	}

}