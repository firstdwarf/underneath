package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.firstdwarf.underneath.utilities.Functions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public interface INodeProvider {
	//TODO: Add crossroads nodes
	//TODO: Potentially move other methods here
	public void placeStructures(World world, ChunkPos chunkPos, BlockPos origin, int rotation);
	public int getWeight(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation, boolean isSpawn);
	/**
	 * Number of tunnels, entrance coordinates, entrance direction
	 */
	public ArrayList<Entrance> getEntrances();
	public BlockPos[] getBounds();
	public String getName();
	public HashMap<BlockPos, IBlockState> getStateMap();
	
	public default void generateCave(World world, Random random, ChunkPos chunkPos, BlockPos origin, int rotation)	{
		System.out.println("Making the cave!");
		IBlockState air = Blocks.AIR.getDefaultState();
		IBlockState bedrock = Blocks.BEDROCK.getDefaultState();
		IBlockState glass = Blocks.STAINED_GLASS.getDefaultState();
		IBlockState stone = Blocks.STONE.getDefaultState();
		HashMap<BlockPos, IBlockState> stateMap = this.getStateMap();
		BlockPos min = this.getBounds()[0];
		BlockPos max = this.getBounds()[1];
		int range = 3;
		HashMap<BlockPos, Boolean> airMap = Functions.generateCaveCell(random, stateMap, range);
		for (BlockPos target : airMap.keySet())	{
			if (airMap.get(target) &&
					world.getBlockState(Functions.nodeCoordsToWorldCoords(target, chunkPos, origin, rotation)) != air)	{
				Functions.setBlockFromNodeCoordinates(world, chunkPos, origin, target, rotation, air);
			}
			else if (world.getBlockState(Functions.nodeCoordsToWorldCoords(target, chunkPos, origin, rotation)) != air)	{
				Functions.setBlockFromNodeCoordinates(world, chunkPos, origin, target, rotation, stone);
			}
		}
	}
	
	public default void flagNeighbors(ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation)	{
		BlockPos chunkMax = Functions.nodeCoordsToChunkCoords(this.getBounds()[1], nodeOrigin, nodeRotation);
		BlockPos chunkMin = Functions.nodeCoordsToChunkCoords(this.getBounds()[0], nodeOrigin, nodeRotation);
		int maxXReach, minXReach, maxZReach, minZReach;
		maxXReach = (int) ((chunkMax.getX() >= 0) ? Math.floor((chunkMax.getX())/16.0) : Math.floor((chunkMax.getX()/16.0)));
		minXReach = (int) ((chunkMin.getX() >= 0) ? Math.floor((chunkMin.getX())/16.0) : Math.floor((chunkMin.getX()/16.0)));
		maxZReach = (int) ((chunkMax.getZ() >= 0) ? Math.floor((chunkMax.getZ())/16.0) : Math.floor((chunkMax.getZ()/16.0)));
		minZReach = (int) ((chunkMin.getZ() >= 0) ? Math.floor((chunkMin.getZ())/16.0) : Math.floor((chunkMin.getZ()/16.0)));
		//System.out.println("Values of max and min: " + chunkMax.toString() + "    " + chunkMin.toString());
		//System.out.println("xMax: " + maxXReach +  " xMin: " + minXReach + " zMax: " + maxZReach + " zMin: " + minZReach);
		ArrayList<ChunkPos> flaggedChunks = new ArrayList<>();
		//Flag chunks to the chunkMax corner
		if (maxXReach >= 0)	{
			if (maxZReach >= 0) {
				for (int i = 0; i <= maxXReach; i++)	{
					for (int j = 0; j <= maxZReach; j++)	{
						if (i != 0 || j != 0)	{
							flaggedChunks.add(new ChunkPos(chunkPos.x + i, chunkPos.z + j));
						}
					}
				}
			}
			else if (maxZReach < 0)	{
				for (int i = 0; i <= maxXReach; i++)	{
					for (int j = 0; j >= maxZReach; j--)	{
						if (i != 0 || j != 0)	{
							flaggedChunks.add(new ChunkPos(chunkPos.x + i, chunkPos.z + j));
						}
					}
				}
			}
		}
		else if (maxXReach < 0)	{
			if (maxZReach >= 0) {
				for (int i = 0; i >= maxXReach; i--)	{
					for (int j = 0; j <= maxZReach; j++)	{
						if (i != 0 || j != 0)	{
							flaggedChunks.add(new ChunkPos(chunkPos.x + i, chunkPos.z + j));
						}
					}
				}
			}
			else if (maxZReach < 0)	{
				for (int i = 0; i >= maxXReach; i--)	{
					for (int j = 0; j >= maxZReach; j--)	{
						if (i != 0 || j != 0)	{
							flaggedChunks.add(new ChunkPos(chunkPos.x + i, chunkPos.z + j));
						}
					}
				}
			}
		}
		
		//Flag chunks to the chunkMin corner
		if (minXReach >= 0)	{
			if (minZReach >= 0) {
				for (int i = 0; i <= minXReach; i++)	{
					for (int j = 0; j <= minZReach; j++)	{
						if (i != 0 || j != 0)	{
							flaggedChunks.add(new ChunkPos(chunkPos.x + i, chunkPos.z + j));
						}
					}
				}
			}
			else if (minZReach < 0)	{
				for (int i = 0; i <= minXReach; i++)	{
					for (int j = 0; j >= minZReach; j--)	{
						if (i != 0 || j != 0)	{
							flaggedChunks.add(new ChunkPos(chunkPos.x + i, chunkPos.z + j));
						}
					}
				}
			}
		}
		else if (minXReach < 0)	{
			if (minZReach >= 0) {
				for (int i = 0; i >= minXReach; i--)	{
					for (int j = 0; j <= minZReach; j++)	{
						if (i != 0 || j != 0)	{
							flaggedChunks.add(new ChunkPos(chunkPos.x + i, chunkPos.z + j));
						}
					}
				}
			}
			else if (minZReach < 0)	{
				for (int i = 0; i >= minXReach; i--)	{
					for (int j = 0; j >= minZReach; j--)	{
						if (i != 0 || j != 0)	{
							flaggedChunks.add(new ChunkPos(chunkPos.x + i, chunkPos.z + j));
						}
					}
				}
			}
		}
		if (!flaggedChunks.isEmpty())	{
			for(ChunkPos c : flaggedChunks)	{
				if (!NodeGen.chunkNodes.containsKey(c.toString()))	{
					//System.out.println("Flagged chunk: " + c.toString());
					NodeGen.chunkNodes.put(c.toString(), -2);
				}
			}
		}
		//Store present entrances
		for(Entrance e : this.getEntrances())	{
			//System.out.println("Preparing to flag entrance");
			e = e.rotateFacing(nodeRotation);
			ArrayList<Entrance> chunkEntrances = new ArrayList<>();
			BlockPos eWorldCoords = Functions.nodeCoordsToWorldCoords(e.coords, chunkPos, nodeOrigin, nodeRotation);
			//System.out.println("Entrance coordinates: " + eWorldCoords.toString());
			ChunkPos eChunk = new ChunkPos(eWorldCoords.getX() >> 4, eWorldCoords.getZ() >> 4);
			e = e.setCoords(Functions.worldCoordsToChunkCoords(eWorldCoords));
			//System.out.println("Running entrances for: " + eChunk.toString());
			if (NodeGen.chunkEntrances.containsKey(eChunk.toString()))	{
				chunkEntrances = NodeGen.chunkEntrances.get(eChunk.toString());
				chunkEntrances.add(e);
				NodeGen.chunkEntrances.put(eChunk.toString(), chunkEntrances);
			}
			else	{
				//System.out.println("Stored an entrance for " + eChunk.toString());
				chunkEntrances.add(e);
				NodeGen.chunkEntrances.put(eChunk.toString(), chunkEntrances);
			}
		}
	}
}
