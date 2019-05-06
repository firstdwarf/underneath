package me.firstdwarf.underneath.core;

import me.firstdwarf.underneath.command.TeleportWorldCommand;
import me.firstdwarf.underneath.utilities.CustomTab;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

@Mod(modid = Underneath.MODID, name = Underneath.NAME, version = Underneath.VERSION, useMetadata = true)
public class Underneath {
	
	public static final String MODID = "underneath";
	public static final String NAME = "underneath";
	public static final String VERSION = "0.1";
	
	public static ArrayList<String> objResourceLocations = new ArrayList<>(0);
	
	@SidedProxy(clientSide = "me.firstdwarf.underneath.core.ClientProxy", 
			serverSide = "me.firstdwarf.underneath.core.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance
	public static Underneath instance;
	
	public static Logger logger;

	@Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent e) {
	    e.registerServerCommand(new TeleportWorldCommand());
    }

	public static final CustomTab underneathTab = new CustomTab("underneathBlocks");
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)	{
		logger = e.getModLog();
		proxy.preInit(e);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)	{
        proxy.init(e);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)	{
		proxy.postInit(e);
	}
}