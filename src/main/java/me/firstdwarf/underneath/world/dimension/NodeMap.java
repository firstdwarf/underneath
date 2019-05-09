package me.firstdwarf.underneath.world.dimension;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class NodeMap {
	public ConcurrentHashMap<ChunkPos, int[]> nodeMap;
	public ArrayList<Tunnel> tunnels;
	public NodeMap(World world)	{
		ChunkPos spawnChunk = new ChunkPos(0, 0);
		BlockPos spawnPos = new BlockPos(8, 245, 8);
		int nodeIndex = 0;
		this.nodeMap.put(spawnChunk, new int[] {spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), nodeIndex});
		
	}
	
	public void addNode(Tunnel tunnel)	{
		
	}
}
