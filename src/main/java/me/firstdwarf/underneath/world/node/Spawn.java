package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class Spawn implements INodeProvider	{
	//TODO: Make spawn appear in spawn chunk, not (0, 0)
	//TODO: Move y-coord of entrance to node generation, or make it relative
	ArrayList<Entrance> entrances = new ArrayList<>();
	Entrance e1 = new Entrance(EnumFacing.SOUTH, 8, 20, 14);
	Entrance e2 = new Entrance(EnumFacing.NORTH, 8, 20, 2);
	
	public Spawn()	{
		entrances.add(e1);
		entrances.add(e2);
	}

	@Override
	public void placeStructures(World world, ChunkPos chunkPos) {
		world.setBlockState(chunkPos.getBlock(8, 20, 8), Blocks.GLOWSTONE.getDefaultState());
		for (Entrance e : entrances)	{
			world.setBlockState(chunkPos.getBlock(e.x, e.y, e.z), Blocks.REDSTONE_BLOCK.getDefaultState());
		}
	}

	@Override
	public void generateCave(World world, ChunkPos chunkPos) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getWeight(World world, ChunkPos chunkPos) {
		int weight = 0;
		if (chunkPos.x == 0 && chunkPos.z == 0)	{
			weight = -1;
		}
		return weight;
	}

	@Override
	public ArrayList<Entrance> getEntrances() {
		return entrances;
	}
	
	@Override
	public String getName()	{
		return "spawn";
	}
}
