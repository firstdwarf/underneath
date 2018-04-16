package me.firstdwarf.underneath;

import me.firstdwarf.underneath.command.TeleportWorldCommand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import me.firstdwarf.underneath.utilities.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Underneath.MODID, name = Underneath.NAME, version = Underneath.VERSION, useMetadata = true)
public class Underneath {

	public static final String MODID = "underneath";
	public static final String NAME = "underneath";
	public static final String VERSION = "0.1";
	
	@SidedProxy(clientSide = "me.firstdwarf.underneath.utilities.ClientProxy", 
			serverSide = "me.firstdwarf.underneath.utilities.ServerProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance
	public static Underneath instance;
	
	public Logger logger;

	@Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent e) {
	    e.registerServerCommand(new TeleportWorldCommand());
    }
	
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