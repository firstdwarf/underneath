package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.utilities.Functions;
import me.firstdwarf.underneath.world.SaveData;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class Spawn extends Node	{
	
	public Spawn()	{
		super("spawn", 3);
		
		this.entrances.add(new Entrance(EnumFacing.SOUTH, 0, 0, 5));
		this.entrances.add(new Entrance(EnumFacing.NORTH, 0, 0, -5));
		this.entrances.add(new Entrance(EnumFacing.WEST, -5, 0, 0));
		this.entrances.add(new Entrance(EnumFacing.EAST, 5, 0, 0));
		
		this.setStates();
		
		super.flagBounds();
	}

	@Override
	public void setStates() {
		super.addCuboid(new BlockPos(-5, 0, -5), new BlockPos(5, 3, 5), Blocks.AIR.getDefaultState());
		this.stateMap.put(new BlockPos(0, 0, 0), Blocks.TORCH.getDefaultState());
		this.stateMap.put(new BlockPos(0, -1, 0), BlockMain.deepStone.getDefaultState());
	}
	
	@Override
	public int getWeight(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation, ArrayList<EnumFacing> facesLinked) {
		int weight = 0;
		
		//Load in spawn location for this world
		SaveData data = SaveData.getData(world);
		BlockPos spawn = data.spawn;
		
		//Check if the spawn hasn't been set yet
		if (spawn == null)	{
			
			//Make this the spawn chunk and set the world spawn
			weight = -1;
			data.setSpawn(Functions.nodeCoordsToWorldCoords(new BlockPos(0, 0, 0), chunkPos, nodeOrigin, nodeRotation));
			
			//Mark data as changed for save operation
			data.markDirty();
		}
		return weight;
	}
}
