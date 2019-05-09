package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import me.firstdwarf.underneath.block.BlockMain;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class AncientForge extends Node	{

	public AncientForge() {
		super("AncientForge", 2, true);
		this.setStates();
		super.flagBounds();
	}

	@Override
	public int getWeight(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation,
			ArrayList<EnumFacing> facesLinked) {
		int weight = 0;
		if (nodeOrigin.getY() >= 100 && super.checkSpace(world, chunkPos, nodeOrigin, nodeRotation, false))	{
			weight = 2;
		}
		return weight;
	}

	@Override
	public void setStates() {
		super.addCylinder(0, 0, 4, 3, 3, this.OPEN_AIR);
		this.blockMap.put(new BlockPos(0, -1, 4), BlockMain.magmaticStone.getDefaultState());
	}
}