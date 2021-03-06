package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.save.SaveData;
import me.firstdwarf.underneath.utilities.Functions;
import me.firstdwarf.underneath.world.TunnelGen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public abstract class Node	{
	
	//Useful IBlockState constant
	protected final IBlockState OPEN_AIR = Blocks.AIR.getDefaultState();
	
	//A list of entrances for the node
	protected ArrayList<Entrance> entrances = new ArrayList<>();
	
	//A hashmap of the BlockPos and IBlockState for every block in the node. This should include air you want to guarantee
	protected HashMap<BlockPos, IBlockState> blockMap = new HashMap<>(0);
	protected HashMap<BlockPos, Boolean> airMap = new HashMap<>(0);
	protected HashMap<BlockPos, Boolean> waterMap = new HashMap<>(0);
	private String name;
	
	//The minimum and maximum block positions of the node. This is used to check how much space is required
	private BlockPos[] bounds = {new BlockPos(0, 0, 0), new BlockPos(0, 0, 0)};
	
	//The range around node blocks to generate a cave in. Currently includes the floor, but that should be fixed shortly
	private int range;
	
	//Whether or not the floor of the node should be level
	private boolean level;
	
	/**
	 * Required constructor for all nodes. Add any extra entrances, call setStates, and flagBounds after this in your own constructor
	 * @param name is the internal name for the node
	 * @param range is the range around the node used in cave generation
	 * @param level is whether or not the floor should be level
	 */
	public Node(String name, int range, boolean level)	{
		
		//Add an entrance at (0, 0, 0) for every single non-spawn node. This means nodes should not extend in the -z direction
		if (!name.equals("spawn"))	{
			this.entrances.add(new Entrance(EnumFacing.NORTH, new BlockPos(0, 0, 0)));
		}
		this.name = name;
		this.range = range;
		this.level = level;
	}
	
	public String getName()	{
		return this.name;
	}
	
	//TODO: Implement checks on available nodes
	//TODO: Remove defunct facesLinked parameter
	/**
	 * This method should return either a version of the config-specified generation weight for this node or zero
	 * @param world is the world object containing this node
	 * @param chunkPos is the chunk the node would be in
	 * @param nodeOrigin is the start of the node
	 * @param nodeRotation is the direction of the node
	 * @param facesLinked is a parameter that will be edited out soon
	 * @return
	 */
	public abstract int getWeight(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation,
			ArrayList<EnumFacing> facesLinked);
	
	/**
	 * This method should populate the stateMap for this node. Call this from the constructor
	 */
	public abstract void setStates();
	
	//Getter for the node bounds
	public BlockPos[] getBounds()	{
		return this.bounds;
	}
	
	/**
	 * This method is used to set the bounds of the node from the state map. Call this from the constructor AFTER setStates
	 */
	public void flagBounds()	{
		BlockPos min = null;
		BlockPos max = null;
		ArrayList<BlockPos> temp = new ArrayList<>();
		for (BlockPos p : this.blockMap.keySet())	{
			temp.add(p);
		}
		for (BlockPos p : this.waterMap.keySet())	{
			temp.add(p);
		}
		for (BlockPos p : this.airMap.keySet())	{
			temp.add(p);
		}
		for (BlockPos p : temp)	{
			
			//Set beginning max and min
			if (min == null)	{
				min = p;
			}
			if (max == null)	{
				max = p;
			}
			
			//Shift max and min as needed
			min = (p.getX() < min.getX()) ? new BlockPos(p.getX(), min.getY(), min.getZ()) : min;
			min = (p.getY() < min.getY()) ? new BlockPos(min.getX(), p.getY(), min.getZ()) : min;
			min = (p.getZ() < min.getZ()) ? new BlockPos(min.getX(), min.getY(), p.getZ()) : min;
			max = (p.getX() > max.getX()) ? new BlockPos(p.getX(), max.getY(), max.getZ()) : max;
			max = (p.getY() > max.getY()) ? new BlockPos(max.getX(), p.getY(), max.getZ()) : max;
			max = (p.getZ() > max.getZ()) ? new BlockPos(max.getX(), max.getY(), p.getZ()) : max;
		}
		
		this.bounds[0] = min;
		this.bounds[1] = max;
	}
	
	public ArrayList<Entrance> getEntrances()	{
		return this.entrances;
	}
	
	//Method to iterate through the stateMap for a node and set every state from it
	public void placeStructures(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation)	{
		BlockPos p;
		for (BlockPos pos : this.blockMap.keySet())	{
			//Functions.setBlockFromNodeCoordinates(world, chunkPos, nodeOrigin, pos, nodeRotation, this.blockMap.get(pos));
		}
	}
	
	//TODO: Rework state system to use less maps or implement auto-generating cells
	/**
	 * This function adds a cuboid of identical IBlockStates between minimum and maximum block positions to the stateMap.
	 * It is extremely useful, if a little difficult to use. Take care when placing cuboids and entrances
	 * @param min
	 * @param max
	 * @param state
	 */
	public void addCuboid(BlockPos min, BlockPos max, IBlockState state)	{
		this.addCuboid(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), state);
	}
	
	public void addWaterCuboid(BlockPos min, BlockPos max)	{
		this.addWaterCuboid(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}
	
	public void addCylinder(BlockPos center, int radius, int height, IBlockState state)	{
		this.addCylinder(center.getX(), center.getY(), center.getZ(), radius, height, state);
	}
	
	public void addCylinder(int x, int y, int z, int radius, int height, IBlockState state)	{
		int kBound;
		for (int i = -1*radius; i <= radius; i++)	{
			for (int j = 0; j < height; j++)	{
				kBound = (int) Math.floor(Math.sqrt(radius*radius - i*i));
				for (int k = -1*kBound; k <= kBound; k++)	{
					if (!state.equals(this.OPEN_AIR))	{
						this.blockMap.put(new BlockPos(x + i, y + j, z + k), state);
					}
					else	{
						this.airMap.put(new BlockPos(x + i, y + j, z + k), true);
					}
				}
			}
		}
	}
	
	public void addSphere(BlockPos center, int radius, IBlockState state)	{
		this.addSphere(center.getX(), center.getY(), center.getZ(), radius, state);
	}
	
	public void addSphere(int x, int y, int z, int radius, IBlockState state)	{
		int jBound, kBound;
		for (int i = -1*radius; i <= radius; i++)	{
			jBound = (int) Math.floor(Math.sqrt(radius*radius - i*i));
			for (int j = -1*jBound; j <= jBound; j++)	{
				kBound = (int) Math.floor(Math.sqrt(radius*radius - i*i - j*j));
				for (int k = -1*kBound; k <= kBound; k++)	{
					if (!state.equals(this.OPEN_AIR))	{
						this.blockMap.put(new BlockPos(x + i, y + j, z + k), state);
					}
					else	{
						this.airMap.put(new BlockPos(x + i, y + j, z + k), true);
					}
				}
			}
		}
	}
	
	/**
	 * This function adds a cuboid of identical IBlockStates between minimum and maximum coordinates to the stateMap
	 * It is extremely useful, if a little difficult to use. Take care when placing cuboids and entrances
	 * @param xMin
	 * @param yMin
	 * @param zMin
	 * @param xMax
	 * @param yMax
	 * @param zMax
	 * @param state
	 */
	public void addCuboid(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, IBlockState state)	{
		for (int i = xMin; i <= xMax; i++)	{
			for (int j = yMin; j <= yMax; j++)	{
				for (int k = zMin; k <= zMax; k++)	{
					if (state.equals(this.OPEN_AIR))	{
						this.airMap.put(new BlockPos(i, j, k), true);
					}
					else	{
						this.blockMap.put(new BlockPos(i, j, k), state);
					}
				}
			}
		}
	}
	
	public void addWaterCuboid(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax)	{
		for (int i = xMin; i <= xMax; i++)	{
			for (int j = yMin; j <= yMax; j++)	{
				for (int k = zMin; k <= zMax; k++)	{
					this.waterMap.put(new BlockPos(i, j, k), true);
				}
			}
		}
	}
	
	public HashMap<BlockPos, IBlockState>	generateStateMap(Random random, BlockPos origin, int rotation)	{
		HashMap<BlockPos, IBlockState> stateMap = new HashMap<>(0);
		
		HashMap<BlockPos, Boolean> tempAirMap = Functions.generateCaveCell(random, this.blockMap, this.airMap, this.range, this.level);
		HashMap<BlockPos, Boolean> tempWaterMap = Functions.generateFluidCave(random, this.blockMap, this.waterMap,
				this.range, this.level);
		
		
		for (BlockPos pos : tempAirMap.keySet())	{
			if (tempAirMap.get(pos))	{
				stateMap.put(pos, this.OPEN_AIR);
			}
		}
		for (BlockPos pos : tempWaterMap.keySet())	{
			if (tempWaterMap.get(pos))	{
				stateMap.put(pos, Blocks.WATER.getDefaultState());
			}
		}
		for (BlockPos pos : this.blockMap.keySet())	{
			stateMap.put(pos, blockMap.get(pos));
		}
		return stateMap;
	}
	
	//This method is called externally to place a cave around a node using the stateMap
	public void generateCave(World world, Random random, ChunkPos chunkPos, BlockPos origin, int rotation)	{
		HashMap<BlockPos, Boolean> airMap = Functions.generateCaveCell(random, this.blockMap, this.airMap, this.range, this.level);
		HashMap<BlockPos, Boolean> waterMap = Functions.generateFluidCave(random, this.blockMap, this.waterMap,
				this.range, this.level);
		
		for (BlockPos target : airMap.keySet())	{
			if (airMap.get(target))	{
			}
		}
		
		for (BlockPos target : waterMap.keySet())	{
			if (waterMap.get(target))	{
				Functions.setBlockFromNodeCoordinates(world, chunkPos, origin, target, rotation, Blocks.WATER.getDefaultState());
			}
		}
		
//		if (markDirty)	{
//			data.markDirty();
//		}
	}
	
	/**
	 * This function flags any nearby chunks whose space would be needed to fit this node
	 * @param chunkPos is the location of the chunk to be filled
	 * @param nodeOrigin is the start of the node
	 * @param nodeRotation is the direction of the node
	 * @return an ArrayList of chunks needed for extra space
	 */
	public ArrayList<ChunkPos> checkNeighbors(ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation)	{
		ArrayList<ChunkPos> flaggedChunks = new ArrayList<>();
		if (this.bounds[0] == null || this.bounds[1] == null)	{
			return flaggedChunks;
		}
		BlockPos chunkMax = Functions.nodeCoordsToChunkCoords(this.bounds[1], nodeOrigin, nodeRotation);
		BlockPos chunkMin = Functions.nodeCoordsToChunkCoords(this.bounds[0], nodeOrigin, nodeRotation);
		int maxXReach, minXReach, maxZReach, minZReach;
		maxXReach = (int) ((chunkMax.getX() >= 0) ? Math.floor((chunkMax.getX())/16.0) : Math.floor((chunkMax.getX()/16.0)));
		minXReach = (int) ((chunkMin.getX() >= 0) ? Math.floor((chunkMin.getX())/16.0) : Math.floor((chunkMin.getX()/16.0)));
		maxZReach = (int) ((chunkMax.getZ() >= 0) ? Math.floor((chunkMax.getZ())/16.0) : Math.floor((chunkMax.getZ()/16.0)));
		minZReach = (int) ((chunkMin.getZ() >= 0) ? Math.floor((chunkMin.getZ())/16.0) : Math.floor((chunkMin.getZ()/16.0)));
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
		return flaggedChunks;
	}
	
	//Returns whether a node can fit at the given location, taking into account required entrance position. Call this from getWeight
	//TODO: Make this a lot smarter... in particular, allow overriding -1 while considering having a tunnel leading in
	public boolean checkSpace(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation, boolean newExits)	{
		boolean canSpawn = true;
		ArrayList<ChunkPos> flaggedChunks = this.checkNeighbors(chunkPos, nodeOrigin, nodeRotation);
		for (ChunkPos p : flaggedChunks)	{
			canSpawn &= (NodeGen.chunkNodes.get(p.toString()) == null || NodeGen.chunkNodes.get(p.toString()) == -1);
			//canSpawn &= !world.isChunkGeneratedAt(p.x, p.z);
		}
		for (Entrance e : this.entrances)	{
			e = e.rotateFacing(nodeRotation);
			e = e.setCoords(Functions.nodeCoordsToWorldCoords(e.coords, chunkPos, nodeOrigin, nodeRotation));
			ChunkPos eChunk = new ChunkPos(e.x >> 4, e.z >> 4);
			switch (e.facing)	{
			case NORTH:
				eChunk = new ChunkPos(eChunk.x, eChunk.z - 1);
				if (world.isChunkGeneratedAt(eChunk.x, eChunk.z))	{
					if (this.entrances.indexOf(e) != 0 && newExits)	{
						canSpawn = false;
					}
					world.getChunkFromChunkCoords(eChunk.x, eChunk.z);
					byte[] loadedTags = TunnelGen.chunkTunnelEndpoints.get(eChunk.toString());
					canSpawn &= (loadedTags[0] & 0x04) != 0;
				}
				break;
			case SOUTH:
				eChunk = new ChunkPos(eChunk.x, eChunk.z + 1);
				if (world.isChunkGeneratedAt(eChunk.x, eChunk.z))	{
					if (this.entrances.indexOf(e) != 0 && newExits)	{
						canSpawn = false;
					}
					world.getChunkFromChunkCoords(eChunk.x, eChunk.z);
					byte[] loadedTags = TunnelGen.chunkTunnelEndpoints.get(eChunk.toString());
					canSpawn &= (loadedTags[0] & 0x08) != 0;
				}
				break;
			case WEST:
				eChunk = new ChunkPos(eChunk.x - 1, eChunk.z);
				if (world.isChunkGeneratedAt(eChunk.x, eChunk.z))	{
					if (this.entrances.indexOf(e) != 0 && newExits)	{
						canSpawn = false;
					}
					world.getChunkFromChunkCoords(eChunk.x, eChunk.z);
					byte[] loadedTags = TunnelGen.chunkTunnelEndpoints.get(eChunk.toString());
					canSpawn &= (loadedTags[0] & 0x01) != 0;
				}
				break;
			case EAST:
				eChunk = new ChunkPos(eChunk.x + 1, eChunk.z);
				if (world.isChunkGeneratedAt(eChunk.x, eChunk.z))	{
					if (this.entrances.indexOf(e) != 0 && newExits)	{
						canSpawn = false;
					}
					world.getChunkFromChunkCoords(eChunk.x, eChunk.z);
					byte[] loadedTags = TunnelGen.chunkTunnelEndpoints.get(eChunk.toString());
					canSpawn &= (loadedTags[0] & 0x02) != 0;
				}
				break;
			default:
				break;
			}
		}
		return canSpawn;
	}
	
	//This method will set the nodeIndex of flagged chunks to -2 and store any entrances placed in said chunks
	public void flagNeighbors(ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation)	{
		ArrayList<ChunkPos> flaggedChunks = this.checkNeighbors(chunkPos, nodeOrigin, nodeRotation);
		if (!flaggedChunks.isEmpty())	{
			for(ChunkPos c : flaggedChunks)	{
				if (!NodeGen.chunkNodes.containsKey(c.toString()))	{
					NodeGen.chunkNodes.put(c.toString(), -2);
				}
			}
		}
		//Store present entrances
		for(Entrance e : this.entrances)	{
			e = e.rotateFacing(nodeRotation);
			BlockPos eWorldCoords = Functions.nodeCoordsToWorldCoords(e.coords, chunkPos, nodeOrigin, nodeRotation);
			ChunkPos eChunk = new ChunkPos(eWorldCoords.getX() >> 4, eWorldCoords.getZ() >> 4);
			e = e.setCoords(Functions.worldCoordsToChunkCoords(eWorldCoords));
			if (NodeGen.chunkEntrances.containsKey(eChunk.toString()))	{
				NodeGen.chunkEntrances.get(eChunk.toString()).add(e);
			}
			else	{
				ArrayList<Entrance> chunkEntrances = new ArrayList<>(1);
				chunkEntrances.add(e);
				NodeGen.chunkEntrances.put(eChunk.toString(), chunkEntrances);
			}
		}
	}
}
