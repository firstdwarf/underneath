package com.firstdwarf.underneath;

import com.firstdwarf.underneath.biome.BiomeWrapper;
import com.firstdwarf.underneath.world.UnderneathDimension;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.firstdwarf.underneath.block.BlockWrapper;
import com.firstdwarf.underneath.item.ItemWrapper;
import com.firstdwarf.underneath.world.DimensionWrapper;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("underneath")
//@Mod.EventBusSubscriber(modid = Underneath.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Underneath
{
    public static final String MOD_ID = "underneath";

    // Directly reference a log4j logger.
    public static final Logger UnderneathLogger = LogManager.getLogger();

    public Underneath() {
//        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BiomeWrapper.BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        UnderneathLogger.info("HELLO FROM PREINIT");
        UnderneathLogger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        UnderneathLogger.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { UnderneathLogger.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        UnderneathLogger.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        UnderneathLogger.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onRegisterBiomes(final RegistryEvent.Register<Biome> event) {
            UnderneathLogger.info("HELLO from biome registry");
            BiomeWrapper.registerBiomes();
        }

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> e) {
            UnderneathLogger.info("HELLO from Register Block");
            BlockWrapper.makeBlock(e);
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e) {
            BlockWrapper.makeBlockItem(e);
            ItemWrapper.makeItem(e);
        }
        
        @SubscribeEvent
        public static void onDimensionFactoryRegistry(final RegistryEvent.Register<ModDimension> e) {
        	UnderneathLogger.info("NOOT NOOT 2");
            DimensionWrapper.makeTemplate(e);
        }
    }
}
