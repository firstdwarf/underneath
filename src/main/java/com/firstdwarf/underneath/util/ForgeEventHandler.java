package com.firstdwarf.underneath.util;

import com.firstdwarf.underneath.Underneath;
import com.firstdwarf.underneath.world.DimensionWrapper;

import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus=EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
	 @SubscribeEvent
     public static void onDimensionRegistry(final RegisterDimensionsEvent e) {
		 Underneath.UnderneathLogger.info("NOOT NOOT");
		 DimensionWrapper.makeDimensionTypes();
     }
}
