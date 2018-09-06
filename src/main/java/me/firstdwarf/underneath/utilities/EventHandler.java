package me.firstdwarf.underneath.utilities;

import java.util.ArrayList;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.block.NaturalBlock;
import me.firstdwarf.underneath.block.OreBlock;
import me.firstdwarf.underneath.world.UnderneathDimensions;
import me.firstdwarf.underneath.world.SaveData;
import me.firstdwarf.underneath.world.node.Entrance;
import me.firstdwarf.underneath.world.node.NodeGen;
import me.firstdwarf.underneath.world.node.TunnelGen;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
	
	//This event is fired when an entity is hurt
	@SubscribeEvent
	public static void livingHurt(LivingHurtEvent e)	{
		
		//Fired if a player is hurt
		if (e.getEntity() instanceof EntityPlayerMP)	{
			EntityPlayerMP p = (EntityPlayerMP) e.getEntity();
			World world = p.world;
			
			//Fired if the player is in a wall
			if (e.getSource() == DamageSource.IN_WALL)	{
				
				//Fired if it's our dimension
				if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
					
					//Get the player's position
					int x = MathHelper.floor(p.posX);
					int y = MathHelper.floor(p.posY - p.getYOffset());
					int z = MathHelper.floor(p.posZ);
					
					//Marks if the player needs saving
					boolean inDanger = true;
					
					//Check all the blocks around the player for air
					for (int i = -1; i <= 1; i++)	{
						for (int j = -1; j <= 2; j++)	{
							for (int k = -1; k <= 1; k++)	{
								IBlockState state = world.getBlockState(new BlockPos(x + i, y + j, z + k));
								if (state == null || state == Blocks.AIR.getDefaultState())	{
									//Mark the player safe if there is air
									inDanger = false;
								}
							}
						}
					}
					
					//Save the player if necessary
					if (inDanger)	{
						
						//Prepare to launch full server command for clarity
						MinecraftServer s = FMLCommonHandler.instance().getMinecraftServerInstance();
						
						//Get stored spawn location for this dimension
						SaveData data = SaveData.getData(world);
						BlockPos pos = data.spawn;
						
						//Just in case the spawn has no location in mind
						if (pos == null)	{
							pos = world.getSpawnPoint();
						}
						
						//Execute tp command
						s.getCommandManager().executeCommand(s,
								"/tp " + p.getName() + " " + pos.getX() + " " + (pos.getY() + 1) + " " + pos.getZ());
						
						//Prevent the player from being hurt by the suffocation
						e.setCanceled(true);
					}
				}
			}
		}
	}
	
	//This event is fired when a chunk loads
	@SubscribeEvent
	public static void chunkLoad(ChunkDataEvent.Load event)	{
		World world = event.getWorld();
		
		//The data loading in
		NBTTagCompound nbt = event.getData();
		ChunkPos pos = event.getChunk().getPos();
		
		//Prepare an int array to store the node origin
		int[] origin = new int[3];
		
		//Check if the event is fired on server side
		if (!world.isRemote)	{
			
			//Check if the chunk is from one of our dimensions
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				
				//System.out.println("Loading data for chunk " + pos.toString());
				
				//Load chunk tunnel endpoint and node data into their respective ConcurrentHashMaps from nbt data
				TunnelGen.chunkTunnelEndpoints.put(pos.toString(), nbt.getByteArray(pos.toString() + ".tunnels"));
				NodeGen.chunkNodes.put(pos.toString(), nbt.getInteger(pos.toString() + ".node"));
				NodeGen.chunkNodes.put(pos.toString() + ".rotation", nbt.getInteger(pos.toString() + ".rotation"));
				
				//Load and fill origin array from nbt before placing its elements in their ConcurrentHashMap
				origin = nbt.getIntArray(pos.toString() + ".origin");
				NodeGen.chunkNodes.put(pos.toString() + ".origin.x", origin[0]);
				NodeGen.chunkNodes.put(pos.toString() + ".origin.y", origin[1]);
				NodeGen.chunkNodes.put(pos.toString() + ".origin.z", origin[2]);
				
				//Check if there was an entrance ArrayList stored
				int size = nbt.getInteger(pos.toString() + ".eSize");
				if (size > 0)	{
					
					//Prepare ArrayList of entrances for retrieval
					ArrayList<Entrance> entrances = new ArrayList<>();
					int[] coords;
					EnumFacing facing;
					
					//Count up stored index
					for (int i = 0; i < size; i++)	{
						
						//Retrieve facing and coordinate information before adding to list
						 facing = EnumFacing.byName(nbt.getString(pos.toString() + ".eFacing." + String.valueOf(i)));
						 coords = nbt.getIntArray(pos.toString() + ".ePos." + String.valueOf(i));
						 entrances.add(new Entrance(facing, coords[0], coords[1], coords[2]));
					}
					
					//Store new ArrayList in ConcurrentHashMap
					NodeGen.chunkEntrances.put(pos.toString(), entrances);
				}
			}
		}
	}
	
	//This event is fired when a chunk saves- NOT when a world saves (so not on shutdown or save and quit)
	@SubscribeEvent
	public static void chunkSave(ChunkDataEvent.Save event)	{
		
		World world = event.getWorld();
		
		//The data to be saved
		NBTTagCompound nbt = event.getData();
		ChunkPos pos = event.getChunk().getPos();
		
		//Check if the event is fired on the server side
		if (!world.isRemote)	{
			
			//Check if the chunk is from one of our dimensions
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				
				//System.out.println("Saving data for chunk " + pos.toString());
				
				//Retrieve the position of the node origin from a ConcurrentHashMap
				int[] origin = {NodeGen.chunkNodes.get(pos.toString() + ".origin.x"),
						NodeGen.chunkNodes.get(pos.toString() + ".origin.y"), NodeGen.chunkNodes.get(pos.toString() + ".origin.z")};
				
				//Retrieve this chunk's tunnel endpoint data from a ConcurrentHashMap and store it in nbt
				nbt.setByteArray(pos.toString() + ".tunnels", TunnelGen.chunkTunnelEndpoints.get(pos.toString()));
				
				//Retrieve this chunk's node data from a ConcurrentHashMap and store it in nbt
				nbt.setInteger(pos.toString() + ".node", NodeGen.chunkNodes.get(pos.toString()));
				nbt.setInteger(pos.toString() + ".rotation", NodeGen.chunkNodes.get(pos.toString() + ".rotation"));
				
				//Store the node origin in nbt
				nbt.setIntArray(pos.toString() + ".origin", origin);
				
				//Get entrances ArrayList for current chunk
				ArrayList<Entrance>	entrances = NodeGen.chunkEntrances.get(pos.toString());
				if (entrances != null)	{
					
					//Store each entrance's direction and coordinates separately
					for (int i = 0; i < entrances.size(); i++)	{
						Entrance e = entrances.get(i);
						int[] coords = {e.x, e.y, e.z};
						nbt.setString(pos.toString() + ".eFacing." + String.valueOf(i), e.facing.toString());
						nbt.setIntArray(pos.toString() + ".ePos." + String.valueOf(i), coords);
					}
					
					//Store size information for indexing during retrieval
					nbt.setInteger(pos.toString() + ".eSize", entrances.size());
				}
				
				//Make sure the chunk is unloaded
				if (!event.getChunk().isLoaded())	{
					
					//System.out.println("Unloading data for chunk " + pos.toString());
					
					//Remove this chunk's entries from all ConcurrentHashMaps
					TunnelGen.chunkTunnelEndpoints.remove(pos.toString());
					NodeGen.chunkNodes.remove(pos.toString());
					NodeGen.chunkNodes.remove(pos.toString() + ".rotation");
					NodeGen.chunkNodes.remove(pos.toString() + ".origin.x");
					NodeGen.chunkNodes.remove(pos.toString() + ".origin.y");
					NodeGen.chunkNodes.remove(pos.toString() + ".origin.z");
					NodeGen.chunkEntrances.remove(pos.toString());
				}
			}
		}
	}
	
	//This event is fired when the world is saved
	@SubscribeEvent
	public static void worldSave(WorldEvent.Save event)	{
		World world = event.getWorld();
		
		//Check if the event is fired on the server side
		if (!world.isRemote)	{
			
			//Check if the world is one of our dimensions
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				
			}
		}
	}
	
	//This event is fired when the world unloads
	@SubscribeEvent
	public static void worldUnload(WorldEvent.Unload event)	{
		World world = event.getWorld();
		
		//Check if the event is fired on the server sides
		if (!world.isRemote)	{
			
			//Check if the world is one of our dimensions
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				
				//Clear the ConcurrentHashMaps storing chunk node and tunnel data. This is done in case the chunks don't unload fully
				TunnelGen.chunkTunnelEndpoints.clear();
				NodeGen.chunkNodes.clear();
				NodeGen.chunkEntrances.clear();
			}
		}
	}
	
	@SubscribeEvent
	public static void worldLoad(WorldEvent.Load event)	{
		World world = event.getWorld();
		
		//Check if the event is fired on the server side
		if (!world.isRemote)	{
					
			//Check if the world is one of our dimensions
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				
			}
		}
	}
}