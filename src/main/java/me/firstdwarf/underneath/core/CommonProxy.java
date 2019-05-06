package me.firstdwarf.underneath.core;

import java.io.File;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.OreBlock;
import me.firstdwarf.underneath.lighting.DynamicLightingHandler;
import me.firstdwarf.underneath.tile.TileMagmaticStone;
import me.firstdwarf.underneath.utilities.EventHandler;
import me.firstdwarf.underneath.world.dimension.CustomDimension;
import me.firstdwarf.underneath.world.node.NodeGen;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber
public class CommonProxy {

	public static Configuration config;
	
	public void init(FMLInitializationEvent e) {
		NodeGen.register();
	}

	public void preInit(FMLPreInitializationEvent e) {
		
		File f = e.getModConfigurationDirectory();
		config = new Configuration(new File(f.getPath(), "underneath.cfg"));
		Config.loadConfig();
		
        CustomDimension.init();

        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(DynamicLightingHandler.class);
		for (OreBlock block : BlockMain.oreBlockList)	{
			OreDictionary.registerOre(block.getName(), block);
		}
		
		GameRegistry.registerTileEntity(TileMagmaticStone.class, "underneath:tilemagmaticstone");
		
		OBJLoader.INSTANCE.addDomain(Underneath.MODID);
	}

	public void postInit(FMLPostInitializationEvent e) {

		if (config.hasChanged())	{
			config.save();
		}
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {

	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		
	}

}