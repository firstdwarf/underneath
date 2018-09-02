package me.firstdwarf.underneath.utilities;

import akka.actor.FSM.Event;
import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.NaturalBlock;
import me.firstdwarf.underneath.block.OreBlock;
import me.firstdwarf.underneath.world.ChunkGeneratorUnderneath;
import me.firstdwarf.underneath.world.TunnelGen;
import me.firstdwarf.underneath.world.UnderneathDimensions;
import me.firstdwarf.underneath.world.node.NodeGen;
import me.firstdwarf.underneath.world.node.Spawn;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

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
	public static void playerJoin(PlayerLoggedInEvent e)	{
		DimensionType d = e.player.world.provider.getDimensionType();
		BlockPos p = ChunkGeneratorUnderneath.spawns.get(d);
		if (d == UnderneathDimensions.underneathDimensionType && !e.player.world.isChunkGeneratedAt(p.getX() >> 4, p.getZ() >> 4))	{
			ChunkGeneratorUnderneath.spawns.remove(d);
		}
	}
	@SubscribeEvent
	public static void livingHurt(LivingHurtEvent e)	{
		System.out.println("Fired");
		if (e.getEntity() instanceof EntityPlayerMP)	{
			EntityPlayerMP p = (EntityPlayerMP) e.getEntity();
			World world = p.world;
			if (e.getSource() == DamageSource.IN_WALL)	{
				if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
					int x = MathHelper.floor(p.posX);
					int y = MathHelper.floor(p.posY - p.getYOffset());
					int z = MathHelper.floor(p.posZ);
					boolean inDanger = true;
					for (int i = -1; i <= 1; i++)	{
						for (int j = -1; j <= 2; j++)	{
							for (int k = -1; k <= 1; k++)	{
								IBlockState state = world.getBlockState(new BlockPos(x + i, y + j, z + k));
								if (state == null || state == Blocks.AIR.getDefaultState())	{
									inDanger = false;
								}
							}
						}
					}
					MinecraftServer s = FMLCommonHandler.instance().getMinecraftServerInstance();
					if (inDanger)	{
						if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
							BlockPos pos = ChunkGeneratorUnderneath.spawns.get(UnderneathDimensions.underneathDimensionType);
							s.getCommandManager().executeCommand(s,
									"/tp " + p.getName() + " " + pos.getX() + " " + (pos.getY() + 1) + " " + pos.getZ());
							e.setCanceled(true);
						}
					}
				}
			}
		}
	}
	@SubscribeEvent
	public static void chunkLoad(ChunkDataEvent.Load event)	{
		World world = event.getWorld();
		NBTTagCompound nbt = event.getData();
		ChunkPos pos = event.getChunk().getPos();
		int[] origin = new int[3];
		if (!world.isRemote)	{
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				TunnelGen.chunkTunnelEndpoints.put(pos.toString(), nbt.getByteArray(pos.toString() + ".tunnels"));
				NodeGen.chunkNodes.put(pos.toString(), nbt.getInteger(pos.toString() + ".node"));
				NodeGen.chunkNodes.put(pos.toString() + ".rotation", nbt.getInteger(pos.toString() + ".rotation"));
				origin = nbt.getIntArray(pos.toString() + ".origin");
				NodeGen.chunkNodes.put(pos.toString() + ".origin.x", origin[0]);
				NodeGen.chunkNodes.put(pos.toString() + ".origin.y", origin[1]);
				NodeGen.chunkNodes.put(pos.toString() + ".origin.z", origin[2]);
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
				int[] origin = {NodeGen.chunkNodes.get(pos.toString() + ".origin.x"),
						NodeGen.chunkNodes.get(pos.toString() + ".origin.y"), NodeGen.chunkNodes.get(pos.toString() + ".origin.z")};
				nbt.setByteArray(pos.toString() + ".tunnels", TunnelGen.chunkTunnelEndpoints.get(pos.toString()));
				nbt.setInteger(pos.toString() + ".node", NodeGen.chunkNodes.get(pos.toString()));
				nbt.setInteger(pos.toString() + ".rotation", NodeGen.chunkNodes.get(pos.toString() + ".rotation"));
				nbt.setIntArray(pos.toString() + ".origin", origin);
				if (!event.getChunk().isLoaded())	{
					TunnelGen.chunkTunnelEndpoints.remove(pos.toString());
					NodeGen.chunkNodes.remove(pos.toString());
					NodeGen.chunkNodes.remove(pos.toString() + ".rotation");
					NodeGen.chunkNodes.remove(pos.toString() + ".origin.x");
					NodeGen.chunkNodes.remove(pos.toString() + ".origin.y");
					NodeGen.chunkNodes.remove(pos.toString() + ".origin.z");
				}
			}
		}
	}
	@SubscribeEvent
	public static void worldUnload(WorldEvent.Unload event)	{
		World world = event.getWorld();
		if (!world.isRemote)	{
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				NodeGen.chunkNodes.clear();
				TunnelGen.chunkTunnelEndpoints.clear();
			}
		}
	}
}