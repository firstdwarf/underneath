package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import me.firstdwarf.underneath.core.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class Shaft extends Node	{
	
	public Shaft()	{
		super("shaft", 3);
		
		this.entrances.add(new Entrance(EnumFacing.SOUTH, 0, -23, 0));
		
		this.setStates();
		
		super.flagBounds();
	}

	@Override
	public void setStates() {
		super.addCuboid(-1, 0, -1, 1, 3, 1, this.AIR);
		super.addCuboid(0, -20, 0, 0, 0, 0, this.AIR);
		super.addCuboid(-1, -23, -1, 1, -20, 1, this.AIR);
	}

	@Override
	public int getWeight(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation, ArrayList<EnumFacing> facesLinked) {
		int weight = 0;
		if (nodeOrigin.getY() >= 35 && super.checkSpace(world, chunkPos, nodeOrigin, nodeRotation))	{
			weight = Config.shaftWeight;
		}
		return weight;
	}
}
