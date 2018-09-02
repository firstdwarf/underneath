package me.firstdwarf.underneath.lighting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.firstdwarf.underneath.world.CustomTeleporter;
import me.firstdwarf.underneath.world.UnderneathDimensions;
import me.firstdwarf.underneath.world.node.Spawn;
import net.minecraft.block.Block;
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

public class DynamicLightingHandler {
	
	private static Map<UUID, List<BlockPos>> locations = new HashMap<>();
		
	@SubscribeEvent
	public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		// Currently this works with any Player holding a torch
		// can restrict this to just a player later on
		EntityLivingBase entity = event.getEntityLiving();
		
		if (entity instanceof EntityPlayer) {
			// Get the item held in the entity's main hand
			ItemStack item = entity.getHeldItem(EnumHand.MAIN_HAND);
			
			int x1 = MathHelper.floor(entity.posX);
			int y1 = MathHelper.floor(entity.posY - entity.getYOffset());
			int z1 = MathHelper.floor(entity.posZ);
			World world = entity.getEntityWorld();
			boolean inDanger = true;
			for (int i = -1; i <= 1; i++)	{
				for (int j = -1; j <= 2; j++)	{
					for (int k = -1; k <= 1; k++)	{
						if (world.getBlockState(new BlockPos(x1 + i, y1 + j, z1 + k)) == Blocks.AIR.getDefaultState())	{
							inDanger = false;
						}
					}
				}
			}
			MinecraftServer s = FMLCommonHandler.instance().getMinecraftServerInstance();
			if (inDanger)	{
				if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
					BlockPos p = Spawn.spawns.get(UnderneathDimensions.underneathDimensionType);
					s.getCommandManager().executeCommand(s,
							"/tp " + entity.getName() + " " + p.getX() + " " + (p.getY() + 1) + " " + p.getZ());
				}
			}
			
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
	
	private static void updateLightingLevel(World world, int x, int y, int z, int level) {
		BlockPos blockPosition = new BlockPos(x, y, z);
		
		if (world.getLight(blockPosition) != level) {
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
