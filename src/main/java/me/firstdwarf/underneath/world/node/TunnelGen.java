package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.core.Config;
import me.firstdwarf.underneath.utilities.Functions;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

public class TunnelGen {
	
	//TODO: Use neighbor boolean array
	public static ConcurrentHashMap<String, byte[]> chunkTunnelEndpoints = new ConcurrentHashMap<>();
	
	public static boolean checkPath(World world, ChunkPos chunkPos)	{
		return true;
	}
	
	/**
	 * This method produces and stores a packed byte array representing the tunnel locations in a chunk.
	 * This is called during world generation, NOT world population.
	 * @param random is the random number generator used in computation
	 * @param world is the world object containing the chunk to fill with tunnels
	 * @param chunkPrimer is the current state of the generating chunk. This is adjusted and returned
	 * @param x1 is the x coordinate of the chunk to generate
	 * @param z1 is the z coordinate of the chunk to generate
	 * @param node is the type of node contained in this chunk. Used to match node entrances to faces
	 * @param nodeOrigin is the relative position of the node within the chunk. Used to match node entrances to faces
	 * @param nodeRotation is the rotation of the node. Used to match node entrances to faces
	 * @param nodeIndex is the index of the node type selected for the chunk. Passed separately for use in non-node forcing cases
	 * @return
	 */
	public static ChunkPrimer generateTunnelEndpoints(Random random, World world, ChunkPrimer chunkPrimer,
			int x1, int z1, INodeProvider node, BlockPos nodeOrigin, int nodeRotation, int nodeIndex)	{
		
		/**
		 * Byte array formatting for tunnel endpoint information. Each byte represents the following:
		 * byte[i]	|	purpose				msb		6		5		4		3		2		1		lsb
		 * ----------------------------------------------------------------------------------------------
		 * byte[0]	|	generation flag		(		arbitrary		)		north	south	west	east
		 * byte[1]	|	north y coord		(						binary up to 256				)
		 * byte[2]	|	south y coord		(						binary up to 256				)
		 * byte[3]	|	west y coord		(						binary up to 256				)
		 * byte[4]	|	east y coord		(						binary up to 256				)
		 * byte[5]	|	north x coord		(		arbitrary		)		(		binary up to 16	)
		 * byte[6]	|	south x coord		(		arbitrary		)		(		binary up to 16	)
		 * byte[7]	|	west z coord		(		arbitrary		)		(		binary up to 16	)
		 * byte[8]	|	east z coord		(		arbitrary		)		(		binary up to 16	)
		 */
		
		//Note that extracting an unsigned int from a byte is accomplished by bitwise ANDing with 0xff
		byte[] dataArray = new byte[9];
		
		//Target to load the tunnel information of neighboring chunks into
		byte[] loadedTags;
		
		//Boolean array holding whether or not a chunk is generated in the cardinal directions
		boolean[] isNeighborLoaded = {world.isChunkGeneratedAt(x1, z1 - 1), world.isChunkGeneratedAt(x1, z1 + 1),
			world.isChunkGeneratedAt(x1 - 1, z1), world.isChunkGeneratedAt(x1 + 1, z1)};
		
		//Chunk array holding the neighboring chunks
		Chunk[] neighborChunks = {null, null, null, null};
		
		//Reference for which face a chunk is in- neighborChunks[i] is at face faceIndex[i]. Used for looping and common logic
		EnumFacing[] faceIndex = {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
		
		//Place chunk objects into neighborChunk array
		if (isNeighborLoaded[0])	{
			neighborChunks[0] = world.getChunkFromChunkCoords(x1, z1 - 1);
		}
		if (isNeighborLoaded[1])	{
			neighborChunks[1] = world.getChunkFromChunkCoords(x1, z1 + 1);
		}
		if (isNeighborLoaded[2])	{
			neighborChunks[2] = world.getChunkFromChunkCoords(x1 - 1, z1);
		}
		if (isNeighborLoaded[3])	{
			neighborChunks[3] = world.getChunkFromChunkCoords(x1 + 1, z1);
		}
		
		//Fill tunnel information for this chunk with completely randomized data
		random.nextBytes(dataArray);
		
		//By default, the chunk should have no tunnel endpoints
		dataArray[0] = 0;
		
		//Force any empty chunk (flagged by a node index of -1) with tunnels entering it to continue the tunnels
		if (nodeIndex == -1)	{
			
			//ArrayList of faces that a tunnel could be added to
			ArrayList<EnumFacing> availableFaces = new ArrayList<>();
			
			//Begin with the ArrayList of faces full
			for (EnumFacing e : faceIndex)	{
				availableFaces.add(e);
			}
			
			//An average of adjacent tunnel endpoints from neighboring chunks used to generate additional endpoints nearby
			int yAverage = 0;
			
			//The total number of connections coming in to this chunk
			int tunnelCount = 0;
			for (int i = 0; i < 4; i++)	{
				
				//Count up the tunnels and add up the y-coordinates to calculate yAverage
				if (neighborChunks[i] != null)	{
					
					//Load up neighbor tunnel information
					loadedTags = chunkTunnelEndpoints.get(neighborChunks[i].getPos().toString());
					
					/*
					 * Check a different face from the tags for each side.
					 * For each face, mark face as unavailable and extract endpoint y-coordinate if a tunnel is found
					 */
					switch (i)	{
					case 0:
						availableFaces.remove(faceIndex[i]);
						if ((loadedTags[0] & 0x04) != 0)	{
							yAverage += loadedTags[2] & 0xff;
							tunnelCount++;
						}
						break;
					case 1:
						availableFaces.remove(faceIndex[i]);
						if ((loadedTags[0] & 0x08) != 0)	{
							yAverage += loadedTags[1] & 0xff;
							tunnelCount++;
						}
						break;
					case 2:
						availableFaces.remove(faceIndex[i]);
						if ((loadedTags[0] & 0x01) != 0)	{
							yAverage += loadedTags[4] & 0xff;
							tunnelCount++;
						}
						break;
					case 3:
						availableFaces.remove(faceIndex[i]);
						if ((loadedTags[0] & 0x02) != 0)	{
							yAverage += loadedTags[3] & 0xff;
							tunnelCount++;
						}
						break;
					}
				}
			}
			
			//Calculate the average y-coordinate of all located tunnels without dividing by zero
			if (tunnelCount != 0)	{
				yAverage /= tunnelCount;
			}
			
			//Force a chunk with tunnels coming in to produce at least one new endpoint if it has an ungenerated neighbor to enter
			if (tunnelCount >= 1)	{
				
				//This will place entrances on available faces while removing taken faces
				while (!availableFaces.isEmpty())	{
					
					//Random number to choose a face from the ArrayList of available faces
					int max = availableFaces.size();
					int r = random.nextInt(max);
					EnumFacing chosenFace = availableFaces.get(r);
					
					//Remove selection for next passes
					availableFaces.remove(r);
					
					//Set the chunk tunnel generation flag and select a reasonable y-coordinate for the chosen face
					switch (chosenFace)	{
					case NORTH:
						dataArray[0] |= 0x08;
						dataArray[1] = (byte) (yAverage + random.nextInt(5) - 2);
						break;
					case SOUTH:
						dataArray[0] |= 0x04;
						dataArray[2] = (byte) (yAverage + random.nextInt(5) - 2);
						break;
					case WEST:
						dataArray[0] |= 0x02;
						dataArray[3] = (byte) (yAverage + random.nextInt(5) - 2);
						break;
					case EAST:
						dataArray[0] |= 0x01;
						dataArray[4] = (byte) (yAverage + random.nextInt(5) - 2);
						break;
					default:
						break;
					}
					
					/*
					 * Tweak chances to spawn additional tunnels by removing available faces from the ArrayList.
					 * int t represents the number of times to attempt to remove a face
					 * int p represents the odds of the face staying in the pool (1/p)
					 */
					int t = Config.tunnelBranchRemovalCount;
					int p = Config.tunnelBranchRemovalOdds;
					for (int i = 0; i < t; i++)	{
						if (random.nextInt(100) + 1 <= p && !availableFaces.isEmpty())	{
							availableFaces.remove(0);
						}
					}
				}
			}
		}
		
		//TODO: Prioritize a matching face for up and down entrances
		//For chunks with nodes and chunks flagged by other nodes, obtain a list of node entrances that need associated endpoints
		ArrayList<Entrance> entrances = new ArrayList<>();
		
		//Retrieve node entrances if this chunk has a node
		if (node != null)	{
			
			//Node entrances are returned unrotated in node coordinates
			for (Entrance e : node.getEntrances())	{
				e = e.rotateFacing(nodeRotation);
				e = e.setCoords(Functions.nodeCoordsToChunkCoords(e.coords, nodeOrigin, nodeRotation));
				
				//Make sure this entrance is actually in the chunk
				if (e.coords.getX() >= 0 && e.coords.getX() < 16 && e.coords.getZ() >= 0 && e.coords.getZ() < 16)	{
					entrances.add(e);
				}
			}
		}
		
		//Retrieve node entrances if this chunk has been flagged as containing part of another chunk's node
		else if (nodeIndex == -2)	{
			
			//Retrieve the entrance ArrayList from a ConcurrentHashMap of chunks that had entrances in them from other nodes
			ArrayList<Entrance> flaggedEntrances = NodeGen.chunkEntrances.get(new ChunkPos(x1, z1).toString());
			
			//The map will be null if no entrances happen to be in this chunk. It can and will happen
			if (flaggedEntrances != null)	{
				for (Entrance e : flaggedEntrances)	{
					
					//Entrances are retrieved in world coordinates, already rotated, that must be converted to chunk coordinates
					//TODO: Swap this around. Should be extremely simple
					e = e.setCoords(Functions.worldCoordsToChunkCoords(e.coords));
					entrances.add(e);
				}
			}
		}
		
		//Place an endpoint on faces at each entrance in the ArrayList
		if (entrances != null)	{
			for (Entrance e : entrances)	{
				
				//Boolean array storing which faces are available for up and down entrances
				boolean[] freeFaces = {true, true, true, true};
				boolean flag = false;
				int i;
				
				//Set chunk tunnel information for each entrance direction
				switch (e.facing)	{
				case NORTH:
					dataArray[0] |= 0x08;
					dataArray[1] = (byte) (e.coords.getY() + random.nextInt(5) - 2);
					freeFaces[3] ^= true;
					break;
				case SOUTH:
					dataArray[0] |= 0x04;
					dataArray[2] = (byte) (e.coords.getY() + random.nextInt(5) - 2);
					freeFaces[2] ^= true;
					break;
				case WEST:
					dataArray[0] |= 0x02;
					dataArray[3] = (byte) (e.coords.getY() + random.nextInt(5) - 2);
					freeFaces[1] ^= true;
					break;
				case EAST:
					dataArray[0] |= 0x01;
					dataArray[4] = (byte) (e.coords.getY() + random.nextInt(5) - 2);
					freeFaces[0] ^= true;
					break;
					
				//Select random face to attach to
				case UP:
					i = 0;
					while (!flag)	{
						i = random.nextInt(4);
						flag = freeFaces[i];
					}
					dataArray[0] |= 2^i;
					break;
				case DOWN:
					i = 0;
					while (!flag)	{
						i = random.nextInt(4);
						flag = freeFaces[i];
					}
					dataArray[0] |= 2^i;
					break;
				}
			}
		}
		
		//Copy endpoint position from neighbors and flag endpoints
		if (isNeighborLoaded[0])	{
			
			//Load tunnel information from neighboring chunk
			loadedTags = chunkTunnelEndpoints.get(neighborChunks[0].getPos().toString());
			
			//Copy and set data
			dataArray[5] = loadedTags[6];
			dataArray[1] = loadedTags[2];
			if((loadedTags[0] & 0x04) != 0)	{
				dataArray[0] |= 0x08;
			}
		}
		
		//Copy endpoint position from neighbors and flag endpoints
		if (isNeighborLoaded[1])	{
			
			//Load tunnel information from neighboring chunk
			loadedTags = chunkTunnelEndpoints.get(neighborChunks[1].getPos().toString());
			
			//Copy and set data
			dataArray[6] = loadedTags[5];
			dataArray[2] = loadedTags[1];
			if((loadedTags[0] & 0x08) != 0)	{
				dataArray[0] |= 0x04;
			}
		}
		
		//Copy endpoint position from neighbors and flag endpoints
		if (isNeighborLoaded[2])	{
			
			//Load tunnel information from neighboring chunk
			loadedTags = chunkTunnelEndpoints.get(neighborChunks[2].getPos().toString());
			
			//Copy and set data
			dataArray[7] = loadedTags[8];
			dataArray[3] = loadedTags[4];
			if((loadedTags[0] & 0x01) != 0)	{
				dataArray[0] |= 0x02;
			}
		}
		
		//Copy endpoint position from neighbors and flag endpoints
		if (isNeighborLoaded[3])	{
			
			//Load tunnel information from neighboring chunk
			loadedTags = chunkTunnelEndpoints.get(neighborChunks[3].getPos().toString());
			
			//Copy and set data
			dataArray[8] = loadedTags[7];
			dataArray[4] = loadedTags[3];
			if((loadedTags[0] & 0x02) != 0)	{
				dataArray[0] |= 0x01;
			}
		}
		
		//Store endpoint information in a ConcurrentHashMap
		chunkTunnelEndpoints.put(new ChunkPos(x1, z1).toString(), dataArray);
		
		//Return chunkPrimer. This is currently not ever edited, but it could be without changing additional structure
		return chunkPrimer;
	}
	
	/**
	 * This method generates tunnels between endpoints and node entrances in a chunk
	 * @param random is the random number generator used in computation
	 * @param world is the world object containing the chunk to fill with tunnels
	 * @param chunkPrimer is the current state of the generating chunk. This is adjusted and returned
	 * @param x is the x coordinate of the chunk to generate
	 * @param z is the z coordinate of the chunk to generate
	 * @param node is the type of node contained in this chunk. Used to match node entrances to faces
	 * @param nodeOrigin is the relative position of the node within the chunk. Used to match node entrances to faces
	 * @param nodeRotation is the rotation of the node. Used to match node entrances to faces
	 * @param nodeIndex is the index of the node type selected for the chunk. Passed separately for use in non-node forcing cases
	 * @return
	 */
	public static ChunkPrimer generateTunnelLinks(Random random, World world, ChunkPrimer chunkPrimer,
			int x, int z, INodeProvider node, BlockPos nodeOrigin, int nodeRotation, int nodeIndex)	{
		
		//Object holding the chunk coordinates of the chunk to generate
		ChunkPos chunkPos = new ChunkPos(x, z);
		
		//Byte array containing the tunnel information for this chunk loaded from a ConcurrentHashMap
		byte[] loadedTags = chunkTunnelEndpoints.get(chunkPos.toString());
		
		//Reference arrays to store endpoint block positions and the face of the endpoint at equivalent indices
		EnumFacing[] directionReference = {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
		BlockPos[] faceTargets = {null, null, null, null};
		
		/*
		 * Set endpoint position from the chunk's tunnel information if it should be generated.
		 * Note the use of byte & 0x0f to extract an unsigned int representation of the least significant nyble of each byte
		 */
		if ((loadedTags[0] & 0x08) != 0)	{
			faceTargets[0] = new BlockPos(loadedTags[5] & 0x0f, (int) loadedTags[1] & 0xff, 0);
		}
		if ((loadedTags[0] & 0x04) != 0)	{
			faceTargets[1] = new BlockPos(loadedTags[6] & 0x0f, (int) loadedTags[2] & 0xff, 15);
		}
		if ((loadedTags[0] & 0x02) != 0)	{
			faceTargets[2] = new BlockPos(0, (int) loadedTags[3] & 0xff, loadedTags[7] & 0x0f);
		}
		if ((loadedTags[0] & 0x01) != 0)	{
			faceTargets[3] = new BlockPos(15, (int) loadedTags[4] & 0xff, loadedTags[8] & 0x0f);
		}
		
		//TODO: Think about these later
		//Link node entrances to tunnel endpoints
		if (node != null)	{
			for (Entrance e : node.getEntrances()) {
				
				//Node entrances come unrotated and in node coordinates; this moves the entrance
				e = e.setCoords(Functions.nodeCoordsToChunkCoords(e.coords, nodeOrigin, nodeRotation));
				
				//Make sure the entrance is in this chunk. It is very possible it won't be
				if (e.coords.getX() >= 0 && e.coords.getX() < 16 && e.coords.getZ() >= 0 && e.coords.getZ() < 16)	{
					
					//This rotates the entrance
					e = e.rotateFacing(nodeRotation);
					
					//Index through the directions and endpoints until they match this entrance
					for (int k = 0; k < 4; k++)	{
						if (directionReference[k] == e.facing)	{
							if (faceTargets[k] != null)	{
								
								//Call a function that draws a tunnel between two positions
								chunkPrimer = connectEndpoints(random, chunkPrimer, e.coords, faceTargets[k]);
							}
						}
					}
				}
			}
		}
		
		//Link entrances flagged by nearby nodes to tunnel endpoints
		else if (nodeIndex == -2)	{
			
			//Retrieve an ArrayList of entrances in this chunk from a ConcurrentHashMap
			ArrayList<Entrance> entrances = NodeGen.chunkEntrances.get(chunkPos.toString());
			
			//This list can be null if no entrances are in this chunk. This happens frequently
			if (entrances != null)	{
				for (Entrance e : entrances) {
					
					//Index through the directions and endpoints until they match this entrance
					for (int k = 0; k < 4; k++)	{
						if (directionReference[k] == e.facing)	{
							if (faceTargets[k] != null)	{
								
								//Call a function that draws a tunnel between two positions
								chunkPrimer = connectEndpoints(random, chunkPrimer, e.coords, faceTargets[k]);
							}
						}
					}
				}
				
				//The ArrayList of entrances can now be removed safely- the chunk has been generated
				NodeGen.chunkEntrances.remove(chunkPos.toString());
			}
		}
		
		//Link tunnel endpoints together if the chunk has no node or entrances
		else if (nodeIndex == -1)	{
			
			//Calculate the average position of all endpoints in this chunk
			int totalFaces = 0;
			BlockPos averagePos = new BlockPos(0, 0, 0);
			for (BlockPos p : faceTargets)	{
				
				//Endpoints can and will be null
				if (p != null)	{
					
					//Add to variables for averaging
					totalFaces++;
					averagePos = Functions.addCoords(averagePos, p);
				}
			}
			
			//Calculate average position without dividing by zero
			if (totalFaces != 0)	{
				averagePos = new BlockPos(averagePos.getX()/totalFaces, averagePos.getY()/totalFaces, averagePos.getZ()/totalFaces);
			}
			for (BlockPos p : faceTargets)	{
				
				//Endpoints can and will be null
				if (p != null)	{
					
					//Call a function that draws a tunnel between two positions- this connects each endpoint to the average position
					chunkPrimer = connectEndpoints(random, chunkPrimer, p, averagePos);
				}
			}
		}
		
		//Return the edited chunkPrimer
		return chunkPrimer;
	}
	
	/**
	 * This function draws a full tunnel between two points
	 * @param random is the random number generator used in computation
	 * @param chunkPrimer is the current state of the generating chunk. This is edited and returned
	 * @param p1 is the first point to connect. The function draws from this point
	 * @param p2 is the second point to connect. The function draws to this point
	 * @return
	 */
	public static ChunkPrimer connectEndpoints(Random random, ChunkPrimer chunkPrimer, BlockPos p1, BlockPos p2)	{
		
		/*
		 * The algorithm begins by placing each block position along a generated pathway into a HashMap.
		 * Each entry of the map has the block position as a key and stores a boolean value.
		 * This map and the boolean values it contains are used later.
		 */
		HashMap<BlockPos, Boolean> airMap = new HashMap<>();
		
		//Calculate the difference between the two points
		int xDelta = p1.getX() - p2.getX();
		int yDelta = p1.getY() - p2.getY();
		int zDelta = p1.getZ() - p2.getZ();
		
		//This integer records the last direction the algorithm stepped
		int selection = -1;
		
		//This is a running sum used in creating a number line
		int totalWeight = 0;
		int r;
		
		/*
		 * Adjust this to change the likelihood of the algorithm stepping in the same direction twice in a row.
		 * It's multiplicative- anything greater than 1 increases the odds and anything less than 1 decreases them.
		 */
		float p = 0.5f;
		
		/*
		 * The algorithm uses a number line to make a weighted choice between random options.
		 * Think of the line as a set of intervals, where the first interval is (0, numberLine[0]).
		 * The second interval is (numberLine[0], numberLine[1]), and the other intervals are calculated similarly
		 */
		int[] numberLine = new int[3];
		
		//Records the starting position of the algorithm in the HashMap of block positions
		airMap.put(p1, false);
		
		//Currently sets the starting position of the path to lapis for testing purposes only. This line can simply be removed
		//chunkPrimer.setBlockState(p1.getX(), p1.getY(), p1.getZ(), Blocks.LAPIS_BLOCK.getDefaultState());
		
		//Continue the path generation algorithm, moving the starting point, until the starting point has reached the end
		while (Math.abs(xDelta) != 0 || Math.abs(yDelta) != 0 || Math.abs(zDelta) != 0)	{
			
			//Reset running sum
			totalWeight = 0;
			
			//Adjust odds based on the last step the algorithm took
			switch (selection)	{
			
			//If selection is -1, this is the first run of the algorithm and no adjustments should be made
			case -1:
				
				/*
				 * Set the upper bound of the intervals; each interval represents a coordinate difference in x, y, or z.
				 * This means that the largest interval is the width of the largest difference in coordinates.
				 * The most likely step to be taken is in the direction of the largest coordinate difference
				 */
				numberLine[0] = Math.abs(xDelta);
				totalWeight += numberLine[0];
				totalWeight += Math.abs(yDelta);
				numberLine[1] = totalWeight;
				totalWeight += Math.abs(zDelta);
				numberLine[2] = totalWeight;
				break;
			case 0:
				
				//If a prior step was made, the interval width for the direction of the last step is multiplied by the float p
				numberLine[0] = (int) Math.ceil(p*Math.abs(xDelta));
				totalWeight += numberLine[0];
				totalWeight += Math.abs(yDelta);
				numberLine[1] = totalWeight;
				totalWeight += Math.abs(zDelta);
				numberLine[2] = totalWeight;
				break;
			case 1:
				numberLine[0] = Math.abs(xDelta);
				totalWeight += numberLine[0];
				
				//If a prior step was made, the interval width for the direction of the last step is multiplied by the float p
				totalWeight += (int) Math.ceil(p*Math.abs(yDelta));
				numberLine[1] = totalWeight;
				totalWeight += Math.abs(zDelta);
				numberLine[2] = totalWeight;
				break;
			case 2:
				numberLine[0] = Math.abs(xDelta);
				totalWeight += numberLine[0];
				totalWeight += Math.abs(yDelta);
				numberLine[1] = totalWeight;
				
				//If a prior step was made, the interval width for the direction of the last step is multiplied by the float p
				totalWeight += (int) Math.ceil(p*Math.abs(zDelta));
				numberLine[2] = totalWeight;
				break;
			}
			
			//Choose a random number somewhere on the number line. The interval the number is in dictates the selection
			r = random.nextInt(totalWeight + 1);
			
			//Check each interval from the bottom up
			for (int j = 0; j < 3; j++)	{
				
				//The first time the number is below the upper bound of the interval, it must be in that interval and not prior ones
				if (r <= numberLine[j])	{
					selection = j;
					break;
				}
			}
			
			//Step in a direction based on which choice was made
			switch (selection)	{
			case 0:
				
				//Switch the direction the block position moves based on which way it has to move to reach the target
				if (xDelta > 0)	{
					p1 = p1.west();
				}
				else if (xDelta < 0)	{
					p1 = p1.east();
				}
				break;
			case 1:
				
				//Switch the direction the block position moves based on which way it has to move to reach the target
				if (yDelta > 0)	{
					p1 = p1.down();
				}
				else if (yDelta < 0)	{
					p1 = p1.up();
				}
				break;
			case 2:
				
				//Switch the direction the block position moves based on which way it has to move to reach the target
				if (zDelta > 0)	{
					p1 = p1.north();
				}
				else if (zDelta < 0)	{
					p1 = p1.south();
				}
				break;
			}
			
			//Records the current position of the algorithm in the HashMap of block positions
			airMap.put(p1, false);
			
			//Currently sets the block at the algorithm's position to lapis. This line can simply be removed
			//chunkPrimer.setBlockState(p1.getX(), p1.getY(), p1.getZ(), Blocks.LAPIS_BLOCK.getDefaultState());
			
			//Find the difference between the algorithm's current position and the target position
			xDelta = p1.getX() - p2.getX();
			yDelta = p1.getY() - p2.getY();
			zDelta = p1.getZ() - p2.getZ();
		}
		
		//This returns a HashMap of boolean values and block positions. If the boolean is true, that position should be set to air
		airMap = Functions.generateTunnelCell(random, airMap);
		for (BlockPos pos : airMap.keySet())	{
			
			//The HashMap can and will contain a few positions outside the chunkPrimer
			if (pos.getX() >= 0 && pos.getX() < 16 && pos.getY() >= 0 && pos.getY() < 256 && pos.getZ() >= 0 && pos.getZ() < 16)	{
				
				//Hollows out this position in the chunkPrimer if it's part of the tunnel
				if (airMap.get(pos))	{
					chunkPrimer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), Blocks.AIR.getDefaultState());
				}
			}
		}
		
		//Returns the edited chunkPrimer
		return chunkPrimer;
	}
}