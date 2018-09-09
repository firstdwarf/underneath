package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.core.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class NodeGen {
	
	//TODO: Allow forced node choices- store returning values in ArrayList and repeat number line
	//ArrayList and ConcurrentHashMaps to store all node types, chunk node indices, and node entrances in a chunk
	public static ArrayList<Node> nodeTypes = new ArrayList<>(0);
	public static ConcurrentHashMap<String, Integer> chunkNodes = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, ArrayList<Entrance>> chunkEntrances = new ConcurrentHashMap<>();
	
	//Called in the common proxy during initialization
	public static void register()	{
		
		//Constructs nodes to store them in the node type ArrayList
		nodeTypes.add(new Spawn());
		nodeTypes.add(new Shaft());
	}
	
	/**
	 * This function simply calls the generation methods for whatever node is chosen
	 * @param world is the world object containing the chunk to generate the node in
	 * @param random is the random number generator used in computation
	 * @param chunkPos is the coordinates of the chunk to be generated
	 * @param node is the type of node contained in this chunk
	 * @param nodeOrigin is the position of the center of the node in chunk coordinates
	 * @param nodeRotation is the amount the node is rotated in degrees
	 */
	public static void generateNodes(World world, Random random, ChunkPos chunkPos, Node node,
			BlockPos nodeOrigin, int nodeRotation)	{
		
		//The node to be generated can and will be null
		if (node != null)	{
			
			//Call any generation methods located in INodeProvider
			node.generateCave(world, random, chunkPos, nodeOrigin, nodeRotation);
			node.placeStructures(world, chunkPos, nodeOrigin, nodeRotation);
		}
	}
	
	//TODO: Make sure this takes neighbors into account in each node's getWeight method
	/**
	 * This selects a node for this chunk, returning its node index
	 * @param world is the world object containing the chunk to generate the node in
	 * @param random is the random number generator used in computation
	 * @param chunkPos is the coordinates of the chunk to be generated
	 * @param nodeOrigin is the position of the center of the node in chunk coordinates
	 * @param nodeRotation is the amount the node is rotated in degrees
	 * @return either the node index of the selected node, -1, or -2
	 */
	public static int selectNodes(World world, Random random, ChunkPos chunkPos,
			BlockPos nodeOrigin, int nodeRotation, ArrayList<EnumFacing> facesLinked)	{
		
		//Weight representing the chances to have no node
		int blankWeight = Config.blankWeight;
		
		//A running total used to create intervals in a number line
		int totalWeight = blankWeight;
		
		//Initialize the choiceIndex and the index to access in the number line in a loop through the node types
		int choiceIndex = -1;
		int i = 1;
		
		/*
		 * The algorithm uses a number line to make a weighted choice between random options.
		 * Think of the line as a set of intervals, where the first interval is (0, numberLine[0]).
		 * The second interval is (numberLine[0], numberLine[1]), and the other intervals are calculated similarly
		 */
		int[] numberLine = new int[nodeTypes.size() + 1];
		numberLine[0] = blankWeight;
		
		//Loop through all node types
		for (Node node : nodeTypes)	{
			
			//Calculate a weighted likelihood of the node to be selected based on world conditions
			int nodeWeight = node.getWeight(world, chunkPos, nodeOrigin, nodeRotation, facesLinked);
			
			//If nodeWeight is returned as -1, this node must be the choice
			if (nodeWeight == -1)	{
				choiceIndex = i - 1;
				break;
			}
			
			/*
			 * Set the upper bound of the intervals; each interval represents the weighted likelihood of a node type being selected.
			 * This means that the most likely node to be chosen has the widest interval.
			 * All node weights should be considered by the programmer with respect to each other.
			 * Adding new nodes with comparable weights will reduce the relative chances of other nodes to be selected
			 */
			totalWeight += nodeWeight;
			numberLine[i] = totalWeight;
			i++;
		}
		
		//Make node spawning selection by iterating up number line if the choice wasn't forced
		if (choiceIndex == -1)	{
			
			//Choose a random number somewhere on the number line. The interval the number is in dictates the selection
			int r = random.nextInt(totalWeight + 1);
			
			//Reset the index of the node that will be chosen
			i = 0;
			
			//Check each interval from the bottom up
			for (int n : numberLine)	{
				
				//The first time the number is below the upper bound of the interval, it must be in that interval and not prior ones
				if (r <= n)	{
					
					//Set the choiceIndex. If the first interval (no node) was chosen, the index is -1
					choiceIndex = i - 1;
					break;
				}
				else	{
					
					//Increment the index of the node that will be chosen
					i++;
				}
			}
		}
		
		//Return the index of the node that has been chosen
		return choiceIndex;
	}
}
