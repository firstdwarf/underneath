package com.firstdwarf.underneath.block;

import com.firstdwarf.underneath.world.DimensionWrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class Porter extends Block {

	public Porter(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
	         return ActionResultType.SUCCESS;
	    } else if (!worldIn.isRemote) {
	    	ServerPlayerEntity sp = (ServerPlayerEntity) player;
	    	if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(sp, DimensionWrapper.underneathTop)) return ActionResultType.SUCCESS;
	    	
	    	ServerWorld target = (worldIn.dimension.getType() != DimensionWrapper.underneathTop) ? sp.server.getWorld(DimensionWrapper.underneathTop) : sp.server.getWorld(DimensionType.OVERWORLD);
	    	//BlockPos spawn = target.dimension.findSpawn((int) sp.getPosX(), (int) sp.getPosZ(), false);
	    	BlockPos spawn = new BlockPos(100, 200, 100);
	    	sp.teleport(target, spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5, sp.rotationYaw, sp.rotationPitch);
	    	
	    	net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(sp, target.dimension.getType(), target.dimension.getType());
	        return ActionResultType.SUCCESS;
	    }
		return ActionResultType.SUCCESS;
	}
}