package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class NodeGen {
	//TODO: Allow forced node choices
	public static ArrayList<INodeProvider> nodeTypes = new ArrayList<>(0);
	public static ConcurrentHashMap<String, Integer> chunkNodes = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, ArrayList<Entrance>> chunkEntrances = new ConcurrentHashMap<>();
	public static void register()	{
		Spawn spawn = new Spawn();
		nodeTypes.add(spawn);
	}
	
	public static void generateNodes(World world, Random random, ChunkPos chunkPos, INodeProvider node,
			BlockPos nodeOrigin, int nodeRotation)	{
		if (node != null)	{
			node.generateCave(world, random, chunkPos, nodeOrigin, nodeRotation);
			node.placeStructures(world, chunkPos, nodeOrigin, nodeRotation);
		}
	}
	
	public static int selectNodes(World world, Random random, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation)	{
		int blankWeight = 10;
		int totalWeight = blankWeight;
		int nodeWeight = 0;
		int choiceIndex = -1;
		int i = 1;
		
		int[] numberLine = new int[nodeTypes.size() + 1];
		numberLine[0] = blankWeight;
		
		for (INodeProvider node : nodeTypes)	{
			nodeWeight = node.getWeight(world, chunkPos, nodeOrigin, nodeRotation);
			if (nodeWeight == -1)	{
				choiceIndex = i - 1;
				break;
			}
			totalWeight += nodeWeight;
			numberLine[i] = totalWeight;
			i++;
		}
		
		//Make node spawning selection by iterating up number line if the choice wasn't forced
		if (choiceIndex == -1)	{
			int r = random.nextInt(totalWeight + 1);
			i = 0;
			for (int n : numberLine)	{
				if (r <= n)	{
					choiceIndex = i - 1;
					break;
				}
				else	{
					i++;
				}
			}
		}
		
		return choiceIndex;
	}
}
