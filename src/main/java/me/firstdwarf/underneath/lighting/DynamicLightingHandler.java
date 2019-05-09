package me.firstdwarf.underneath.lighting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.save.SaveData;
import me.firstdwarf.underneath.world.ChunkGeneratorUnderneath;
import me.firstdwarf.underneath.world.CustomTeleporter;
import me.firstdwarf.underneath.world.dimension.CustomDimension;
import me.firstdwarf.underneath.world.node.Spawn;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DynamicLightingHandler {
	//TODO: Basically, remove this or severely neuter it...
	
	private static Map<UUID, List<BlockPos>> locations = new HashMap<>();
	private static ConcurrentHashMap<UUID, BlockPos> lightLocation = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<UUID, BlockPos> lights = new ConcurrentHashMap<>();
	
	private static int method = 2;
		
	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent e)	{
		
		EntityPlayer p = e.player;
		World w = p.world;
		
//		if (w.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType) && p.isEntityInsideOpaqueBlock())	{
//			
//			//Prepare to launch full server command for clarity
//			MinecraftServer s = FMLCommonHandler.instance().getMinecraftServerInstance();
//			
//			//Get stored spawn location for this dimension
//			SaveData data = SaveData.getData(w);
//			BlockPos pos = data.spawn;
//			
//			//Just in case the spawn has no location in mind
//			if (pos == null)	{
//				pos = w.getSpawnPoint();
//			}
//			
//			//Execute tp command
//			s.getCommandManager().executeCommand(s,
//					"/tp " + p.getName() + " " + pos.getX() + " " + (pos.getY() + 1) + " " + pos.getZ());
//		}
		
		//Temporary int to test different techniques
		if (method == 0)	{
			BlockPos pos = p.getPosition();
			//System.out.println(pos.toString());
			if (w.isRemote)	{
				pos = new BlockPos(Math.floor(pos.getX()), Math.floor(pos.getY()), Math.floor(pos.getZ()));
				BlockPos oldPos = lights.get(p.getPersistentID());
				if (oldPos != null)	{
					if (!oldPos.equals(pos))	{
						System.out.println("Swapping lighting for new pos " + pos.toString());
						w.setLightFor(EnumSkyBlock.BLOCK, pos, 6);
						w.setLightFor(EnumSkyBlock.BLOCK, oldPos, 0);
//						w.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), 6, 6, 6);
//						w.checkLightFor(EnumSkyBlock.BLOCK, pos);
//						w.checkLightFor(EnumSkyBlock.BLOCK, oldPos);
						w.checkLightFor(EnumSkyBlock.BLOCK, pos.down());
						w.checkLightFor(EnumSkyBlock.BLOCK, oldPos.down());
//						w.checkLightFor(EnumSkyBlock.BLOCK, pos.down());
//						w.checkLightFor(EnumSkyBlock.BLOCK, pos.east());
//						w.checkLightFor(EnumSkyBlock.BLOCK, pos.west());
//						w.checkLightFor(EnumSkyBlock.BLOCK, pos.north());
//						w.checkLightFor(EnumSkyBlock.BLOCK, pos.south());
						lights.put(p.getPersistentID(), pos);
					}
				}
				else	{
					lights.put(p.getPersistentID(), pos);
					w.setLightFor(EnumSkyBlock.BLOCK, pos, 6);
					w.checkLightFor(EnumSkyBlock.BLOCK, pos);
//					w.checkLightFor(EnumSkyBlock.BLOCK, pos.up());
//					w.checkLightFor(EnumSkyBlock.BLOCK, pos.down());
//					w.checkLightFor(EnumSkyBlock.BLOCK, pos.east());
//					w.checkLightFor(EnumSkyBlock.BLOCK, pos.west());
//					w.checkLightFor(EnumSkyBlock.BLOCK, pos.north());
//					w.checkLightFor(EnumSkyBlock.BLOCK, pos.south());
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		// Currently this works with any Player holding a torch
		// can restrict this to just a player later on
		EntityLivingBase entity = event.getEntityLiving();
		
		if (method == 1)	{
			if (entity instanceof EntityPlayer) {
			
				BlockPos lastLocation = lightLocation.get(entity.getPersistentID());
			
				ItemStack item = entity.getHeldItem(EnumHand.MAIN_HAND);
				if (item.getUnlocalizedName().equals("tile.torch")) {
				
					BlockPos pos = new BlockPos(MathHelper.floor(entity.posX),
							MathHelper.floor(entity.posY + 1), MathHelper.floor(entity.posZ));
				
					IBlockState state = entity.world.getBlockState(pos);
				
					if (lastLocation != null)	{
						if (!lastLocation.equals(pos))	{
							System.out.println("Swapping light position");
							System.out.println("Old: " + lastLocation.toString() + "    New: " + pos.toString());
							updateLightingLevel(entity.world, lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), 0);
							updateLightingLevel(entity.world, pos.getX(), pos.getY(), pos.getZ(), 14);
							lightLocation.put(entity.getPersistentID(), pos);
						}
					}
					else	{
						System.out.println("Starting light position");
						updateLightingLevel(entity.world, pos.getX(), pos.getY(), pos.getZ(), 14);
						lightLocation.put(entity.getPersistentID(), pos);
					}
				}
				else	{
					if (lastLocation != null)	{
						System.out.println("Removing light position");
						updateLightingLevel(entity.world, lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), 0);
						lightLocation.remove(entity.getPersistentID());
					}
				}
			}
		}
		
		if (method == 2)	{
			if (entity instanceof EntityPlayer) {
			// Get the item held in the entity's main hand
			ItemStack item = entity.getHeldItem(EnumHand.MAIN_HAND);
			
			// If the item the entity is holding in their hand is a torch
			if (item.getUnlocalizedName().equals("tile.torch")) {
				// Determine player location
				int x = MathHelper.floor(entity.posX);
				int y = MathHelper.floor(entity.posY - entity.getYOffset() + 1);
				int z = MathHelper.floor(entity.posZ);
				
				// Place light
				BlockPos blockPosition = new BlockPos(x, y, z);	
				Block blockAtLocation = entity.world.getBlockState(blockPosition).getBlock();
				
				if (locations.containsKey(entity.getPersistentID())) {
					List<BlockPos> blockPositionsList = locations.get(entity.getPersistentID());

					if (blockPositionsList != null && blockPositionsList.size() > 0) {
						for (BlockPos p : blockPositionsList) {
							updateLightingLevel(entity.world, p.getX(), p.getY(), p.getZ(), 0);
						}
					}
					
					locations.remove(entity.getPersistentID());
				}
				
				if (blockAtLocation == Blocks.AIR) {
					updateLightingLevel(entity.world, x, y, z, 14);
					
					List<BlockPos> newPositionsList = new ArrayList<BlockPos>();
					newPositionsList.add(blockPosition);
					
					locations.put(entity.getPersistentID(), newPositionsList);
				}
			} else if (locations.containsKey(entity.getPersistentID())) {
				List<BlockPos> blockPositionsList = locations.get(entity.getPersistentID());
				
				if (blockPositionsList != null && blockPositionsList.size() > 0) {
					for (BlockPos p : blockPositionsList) {
						updateLightingLevel(entity.world, p.getX(), p.getY(), p.getZ(), 0);
					}
				}
				
				locations.remove(entity.getPersistentID());
			}
		}
		}
	}
	
	private static void updateLightingLevel(World world, int x, int y, int z, int level) {
		BlockPos blockPosition = new BlockPos(x, y, z);
		
		if (world.getLight(blockPosition) != level)	{
			//System.out.println("Firing");
			world.setLightFor(EnumSkyBlock.BLOCK, blockPosition, level);
			world.markBlockRangeForRenderUpdate(x, y, z, 6, 6, 6);
			world.updateBlockTick(blockPosition, world.getBlockState(blockPosition).getBlock(), 1, 0);
			world.checkLightFor(EnumSkyBlock.BLOCK, blockPosition.up());
			world.checkLightFor(EnumSkyBlock.BLOCK, blockPosition.down());
			world.checkLightFor(EnumSkyBlock.BLOCK, blockPosition.north());
			world.checkLightFor(EnumSkyBlock.BLOCK, blockPosition.south());
			world.checkLightFor(EnumSkyBlock.BLOCK, blockPosition.east());
			world.checkLightFor(EnumSkyBlock.BLOCK, blockPosition.west());
		}
	}
}
