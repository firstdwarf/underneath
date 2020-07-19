package com.firstdwarf.underneath.world;

import com.firstdwarf.underneath.Underneath;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;

public class DimensionWrapper {
	
	public static DimensionType underneathTop;
	private static ModDimension dim;
	
	private static UnderneathDimension getDim(World world, DimensionType type)	{
		return new UnderneathDimension(world, type, 0);
	}
	
	public static void makeTemplate(RegistryEvent.Register<ModDimension> e)	{
		dim = ModDimension.withFactory(DimensionWrapper::getDim);
		dim.setRegistryName("underneath:underneath_top");
		e.getRegistry().register(dim);
	}
	
	
	public static void makeDimensionTypes() {
		Underneath.UnderneathLogger.info("hi there bb");
		ResourceLocation top = new ResourceLocation("underneath:top");
		underneathTop = DimensionManager.registerOrGetDimension(top, dim, new PacketBuffer(Unpooled.buffer()), false);
		if(underneathTop == null)	{
			Underneath.UnderneathLogger.info("Dimension type is null");
		}
	}
}
