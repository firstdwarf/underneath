package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import me.firstdwarf.underneath.core.Config;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class PoolCave extends Node {

	public PoolCave() {
		super("PoolCave", 3, true);
		this.setStates();
		super.flagBounds();
	}

	//TODO: Make sure you can check for total tunnel count- this is a dead end node
	@Override
	public int getWeight(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation,
			ArrayList<EnumFacing> facesLinked) {
		int weight = 0;
		if (nodeOrigin.getY() >= 100 && super.checkSpace(world, chunkPos, nodeOrigin, nodeRotation, false))	{
			weight = Config.poolCaveWeight;
		}
		return weight;
	}

	@Override
	public void setStates() {
		super.addCuboid(-3, 0, 0, 3, 3, 6, this.OPEN_AIR);
		super.addCuboid(-1, -1, 4, 1, -1, 4, Blocks.WATER.getDefaultState());
		super.addCuboid(0, -1, 3, 0, -1, 5, Blocks.WATER.getDefaultState());
		super.addWaterCuboid(0, -20, 3, 0, -2, 5);
	}

}
