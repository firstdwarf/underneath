package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public interface INodeProvider {
	//TODO: Add crossroads nodes
	public void placeStructures(World world, BlockPos origin, int rotation);
	public void generateCave(World world, BlockPos origin, int rotation);
	public int getWeight(World world, ChunkPos chunkPos);
	/**
	 * Number of tunnels, entrance coordinates, entrance direction
	 */
	public ArrayList<Entrance> getEntrances();
	public String getName();
}
