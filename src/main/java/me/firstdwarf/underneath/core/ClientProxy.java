package me.firstdwarf.underneath.core;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.NaturalBlock;
import me.firstdwarf.underneath.block.OreBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e)	{
		super.preInit(e);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent e)	{
		for(NaturalBlock block : BlockMain.naturalBlockList)	{
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, 
					new ModelResourceLocation(block.getRegistryName(), "inventory"));
		}
		for(OreBlock block : BlockMain.oreBlockList)	{
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, 
					new ModelResourceLocation(block.getRegistryName(), "inventory"));
		}
	}
}