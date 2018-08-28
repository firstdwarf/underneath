package me.firstdwarf.underneath.utilities;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.NaturalBlock;
import me.firstdwarf.underneath.block.OreBlock;
import me.firstdwarf.underneath.world.TunnelGen;
import me.firstdwarf.underneath.world.UnderneathDimensions;
import me.firstdwarf.underneath.world.node.NodeGen;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
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
	@SubscribeEvent
	public static void breakBlocks(BreakSpeed event)	{
		EntityPlayer player = event.getEntityPlayer();
		String toolMain = player.getHeldItemMainhand().getDisplayName();
		String toolOff = player.getHeldItemOffhand().getDisplayName();
		if(event.getState().getMaterial().equals((Material) CustomMaterial.NATURAL))	{
			event.setCanceled(true);
			System.out.println(toolMain + " " + toolOff);
		}
	}
	@SubscribeEvent
	public static void chunkLoad(ChunkDataEvent.Load event)	{
		World world = event.getWorld();
		NBTTagCompound nbt = event.getData();
		ChunkPos pos = event.getChunk().getPos();
		if (!world.isRemote)	{
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				TunnelGen.chunkTunnelEndpoints.put(pos.toString(), nbt.getByteArray(pos.toString() + ".tunnels"));
				NodeGen.chunkNodes.put(pos.toString(), nbt.getInteger(pos.toString() + ".node"));
			}
		}
	}
	@SubscribeEvent
	public static void chunkSave(ChunkDataEvent.Save event)	{
		World world = event.getWorld();
		NBTTagCompound nbt = event.getData();
		ChunkPos pos = event.getChunk().getPos();
		if (!world.isRemote)	{
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				nbt.setByteArray(pos.toString() + ".tunnels", TunnelGen.chunkTunnelEndpoints.get(pos.toString()));
				nbt.setInteger(pos.toString() + ".node", NodeGen.chunkNodes.get(pos.toString()));
				if (!event.getChunk().isLoaded())	{
					TunnelGen.chunkTunnelEndpoints.remove(pos.toString());
					NodeGen.chunkNodes.remove(pos.toString());
				}
			}
		}
	}
}