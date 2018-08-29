package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.utilities.Coords;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class NodeGen {
	//TODO: Allow multi-chunk nodes
	public static ArrayList<INodeProvider> nodeTypes = new ArrayList<>(0);
	public static ConcurrentHashMap<String, Integer> chunkNodes = new ConcurrentHashMap<>();
	public static void register()	{
		Spawn spawn = new Spawn();
		nodeTypes.add(spawn);
	}
	
	public static void generateNodes(World world, Random random, ChunkPos chunkPos, INodeProvider node,
			Coords nodeOrigin, int nodeRotation)	{
		if (node != null)	{
			node.placeStructures(world, chunkPos.getBlock(nodeOrigin.x, nodeOrigin.y, nodeOrigin.z), nodeRotation);
			node.generateCave(world, chunkPos.getBlock(nodeOrigin.x, nodeOrigin.y, nodeOrigin.z), nodeRotation);
		}
	}
	
	public static int selectNodes(World world, Random random, ChunkPos chunkPos)	{
		int blankWeight = 10;
		int totalWeight = blankWeight;
		int nodeWeight = 0;
		int choiceIndex = -1;
		int i = 1;
		
		int[] numberLine = new int[nodeTypes.size() + 1];
		numberLine[0] = blankWeight;
		
		for (INodeProvider node : nodeTypes)	{
			nodeWeight = node.getWeight(world, chunkPos);
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
