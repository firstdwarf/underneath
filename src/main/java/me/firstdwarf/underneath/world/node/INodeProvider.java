package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public interface INodeProvider {
	public void placeStructures(World world, ChunkPos chunkPos);
	public void generateCave(World world, ChunkPos chunkPos);
	public int getWeight(World world, ChunkPos chunkPos);
	/**
	 * Number of tunnels, entrance coordinates, entrance direction
	 */
	public ArrayList<Entrance> getEntrances();
	public String getName();
}
