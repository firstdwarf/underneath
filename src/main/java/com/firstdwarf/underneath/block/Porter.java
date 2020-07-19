package com.firstdwarf.underneath.block;

import com.firstdwarf.underneath.world.DimensionWrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class Porter extends Block {

	public Porter(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
	         return ActionResultType.SUCCESS;
	    } else {
	    	System.out.println("Click!");
	    	if (!worldIn.isRemote) {
	    		player.changeDimension((worldIn.dimension.getType() == DimensionWrapper.underneathTop) ? DimensionType.OVERWORLD : DimensionWrapper.underneathTop);
	  	    }
	        return ActionResultType.SUCCESS;
	    }
	}
}