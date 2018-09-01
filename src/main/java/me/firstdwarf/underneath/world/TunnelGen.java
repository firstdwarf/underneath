package me.firstdwarf.underneath.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.utilities.Functions;
import me.firstdwarf.underneath.world.node.Entrance;
import me.firstdwarf.underneath.world.node.INodeProvider;
import me.firstdwarf.underneath.world.node.NodeGen;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

public class TunnelGen {
	
	//TODO: Remove y-conversion method
	//TODO: Use neighbor boolean array
	//TODO: Force tunnels to continue through empty chunks
	public static ConcurrentHashMap<String, byte[]> chunkTunnelEndpoints = new ConcurrentHashMap<>();
	
	public static int convertY(byte b)	{
		return (int) b & 0xff;
	}
	
	public static boolean checkPath(World world, ChunkPos chunkPos)	{
		return true;
	}
	
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
		byte[] dataArray = new byte[9];
		byte[] loadedTags;
		Chunk chunk = null;
		
		//Face order: north, south, west, east (same as y-coordinate storage order)
		EnumFacing[] faceIndex = {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
		boolean[] isNeighborLoaded = {world.isChunkGeneratedAt(x1, z1 - 1), world.isChunkGeneratedAt(x1, z1 + 1),
			world.isChunkGeneratedAt(x1 - 1, z1), world.isChunkGeneratedAt(x1 + 1, z1)};
		Chunk[] neighborChunks = {null, null, null, null};
		byte[] yData = {0x00, 0x00, 0x00, 0x00};
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
		random.nextBytes(dataArray);
		
		dataArray[0] = 0;
		
		//Clamp y-coords for crossing tunnels
		
		//Load neighbor information
		if (nodeIndex == -1)	{
			//System.out.println("Found a -1!!!");
			ArrayList<EnumFacing> availableFaces = new ArrayList<>();
			for (EnumFacing e : faceIndex)	{
				availableFaces.add(e);
			}
			int yAverage = 0;
			int tunnelCount = 0;
			for (int i = 0; i < 4; i++)	{
				//System.out.println("Checking neighboring chunk " + i);
				Chunk c = neighborChunks[i];
				if (c != null)	{
					loadedTags = chunkTunnelEndpoints.get(c.getPos().toString());
					switch (i)	{
					case 0:
						availableFaces.remove(faceIndex[i]);
						if ((loadedTags[0] & 0x04) != 0)	{
							yData[i] = loadedTags[2];
							tunnelCount++;
						}
						break;
					case 1:
						availableFaces.remove(faceIndex[i]);
						if ((loadedTags[0] & 0x08) != 0)	{
							yData[i] = loadedTags[1];
							tunnelCount++;
						}
						break;
					case 2:
						availableFaces.remove(faceIndex[i]);
						if ((loadedTags[0] & 0x01) != 0)	{
							yData[i] = loadedTags[4];
							tunnelCount++;
						}
						break;
					case 3:
						availableFaces.remove(faceIndex[i]);
						if ((loadedTags[0] & 0x02) != 0)	{
							yData[i] = loadedTags[3];
							tunnelCount++;
						}
						break;
					}
				}
				yAverage += (yData[i] & 0xff);
			}
			//System.out.println("Tunnel count is " + tunnelCount);
			if (tunnelCount != 0)	{
				yAverage /= tunnelCount;
			}
			//Force another face
			if (tunnelCount >= 1)	{
				while (!availableFaces.isEmpty())	{
					int max = availableFaces.size();
					int r = random.nextInt(max);
					EnumFacing chosenFace = availableFaces.get(r);
					availableFaces.remove(r);
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
					//Tweak chances to spawn additional tunnels
					//Number of times to run through the loop
					int t = 2;
					//Odds of not removing a face is (1/p)
					int p = 4;
					for (int i = 0; i < t; i++)	{
						if (random.nextInt(p) != 0 && !availableFaces.isEmpty())	{
							availableFaces.remove(0);
						}
					}
				}
				//System.out.println("Generation flag after force: " + dataArray[0]);
			}
		}
		
		//Force tunnel endings on faces with entrances, and clamp y-coords
		if (node != null)	{
			boolean[] freeFaces = {true, true, true, true};
			for (Entrance e : node.getEntrances())	{
				e = e.rotateFacing(nodeRotation);
				BlockPos coords = Functions.nodeCoordsToChunkCoords(e.coords, nodeOrigin, nodeRotation);
				if (coords.getX() >= 0 && coords.getX() < 16 && coords.getZ() >= 0 && coords.getZ() < 16)	{
					//System.out.println(coords.getX() + " " + coords.getY() + " " + coords.getZ());
					//System.out.println("Chunk: (" + x1 + ", " + z1 + ")    Node Entrance Direction: " + e.facing.getName());
					boolean flag = false;
					int i;
					switch (e.facing)	{
					case NORTH:
						dataArray[0] |= 0x08;
						dataArray[1] = (byte) (coords.getY() + random.nextInt(5) - 2);
						freeFaces[3] ^= true;
						break;
					case SOUTH:
						dataArray[0] |= 0x04;
						dataArray[2] = (byte) (coords.getY() + random.nextInt(5) - 2);
						freeFaces[2] ^= true;
						break;
					case WEST:
						dataArray[0] |= 0x02;
						dataArray[3] = (byte) (coords.getY() + random.nextInt(5) - 2);
						freeFaces[1] ^= true;
						break;
					case EAST:
						dataArray[0] |= 0x01;
						dataArray[4] = (byte) (coords.getY() + random.nextInt(5) - 2);
						freeFaces[0] ^= true;
						break;
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
		}
		else if (nodeIndex == -2)	{
			//Pull existing entrance data from map
			//System.out.println("Forcing!!!");
			ChunkPos chunkPos = new ChunkPos(x1, z1);
			ArrayList<Entrance> entrances = NodeGen.chunkEntrances.get(chunkPos.toString());
			//System.out.println(entrances != null);
			if (entrances != null)	{
				for (Entrance e : entrances)	{
					e = e.setCoords(Functions.worldCoordsToChunkCoords(e.coords));
					boolean[] freeFaces = {true, true, true, true};
					//System.out.println(e.coords.getX() + " " + e.coords.getY() + " " + e.coords.getZ());
					//System.out.println("Chunk: (" + x1 + ", " + z1 + ")    Node Entrance Direction: " + e.facing.getName());
					boolean flag = false;
					int i;
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
		}
		int x, y, z;
		//xy plane (0)-- north
		if(!world.isChunkGeneratedAt(x1, z1 - 1))	{
			if(!((dataArray[0] & 0x08) == 0))	{
				x = dataArray[5] & 0x0f;
				y = convertY(dataArray[1]);
				z = 0;
				//System.out.println(x + " " + y + " " + z);
				//chunkPrimer.setBlockState(x, y, z, Blocks.FLOWING_WATER.getDefaultState());
			}
		}
		else	{
			chunk = world.getChunkFromChunkCoords(x1, z1 - 1);
			loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
			dataArray[5] = loadedTags[6];
			dataArray[1] = loadedTags[2];
			if(!((loadedTags[0] & 0x04) == 0))	{
				dataArray[0] |= 0x08;
				x = loadedTags[6] & 0x0f;
				y = convertY(loadedTags[2]);
				z = 0;
				//System.out.println(x + " " + y + " " + z);
				//chunkPrimer.setBlockState(x, y, z, BlockMain.oreCopper.getDefaultState());
			}
			else	{
				dataArray[0] &= 0xf7;
			}
		}
		//xy plane (15)-- south
		if(!world.isChunkGeneratedAt(x1, z1 + 1))	{
			if(!((dataArray[0] & 0x04) == 0))	{
				x = dataArray[6] & 0x0f;
				y = convertY(dataArray[2]);
				z = 15;
				//System.out.println(x + " " + y + " " + z);
				//chunkPrimer.setBlockState(x, y, z, Blocks.FLOWING_WATER.getDefaultState());
			}
		}
		else	{
			chunk = world.getChunkFromChunkCoords(x1, z1 + 1);
			loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
			dataArray[6] = loadedTags[5];
			dataArray[2] = loadedTags[1];
			if(!((loadedTags[0] & 0x08) == 0))	{
				dataArray[0] |= 0x04;
				x = loadedTags[5] & 0x0f;
				y = convertY(loadedTags[1]);
				z = 15;
				//System.out.println(x + " " + y + " " + z);
				//chunkPrimer.setBlockState(x, y, z, BlockMain.oreCopper.getDefaultState());
			}
			else	{
				dataArray[0] &= 0xfb;
			}
		}
		//zy plane (0)-- west
		if(!world.isChunkGeneratedAt(x1 - 1, z1))	{
			if(!((dataArray[0] & 0x02) == 0))	{
				x = 0;
				y = convertY(dataArray[3]);
				z = dataArray[7] & 0x0f;
				//chunkPrimer.setBlockState(x, y, z, Blocks.FLOWING_WATER.getDefaultState());
			}
		}
		else	{
			chunk = world.getChunkFromChunkCoords(x1 - 1, z1);
			loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
			dataArray[7] = loadedTags[8];
			dataArray[3] = loadedTags[4];
			if(!((loadedTags[0] & 0x01) == 0))	{
				dataArray[0] |= 0x02;
				x = 0;
				y = convertY(loadedTags[4]);
				z = loadedTags[8] & 0x0f;
				//System.out.println(x + " " + y + " " + z);
				//chunkPrimer.setBlockState(x, y, z, BlockMain.oreCopper.getDefaultState());
			}
			else	{
				dataArray[0] &= 0xfd;
			}
		}
		//zy plane (15)-- east
		if(!world.isChunkGeneratedAt(x1 + 1, z1))	{
			if(!((dataArray[0] & 0x01) == 0))	{
				x = 15;
				y = convertY(dataArray[4]);
				z = dataArray[8] & 0x0f;
				//System.out.println(x + " " + y + " " + z);
				//chunkPrimer.setBlockState(x, y, z, Blocks.FLOWING_WATER.getDefaultState());
			}
		}
		else	{
			chunk = world.getChunkFromChunkCoords(x1 + 1, z1); 
			loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
			dataArray[8] = loadedTags[7];
			dataArray[4] = loadedTags[3];
			if(!((loadedTags[0] & 0x02) == 0))	{
				dataArray[0] |= 0x01;
				x = 15;
				y = convertY(loadedTags[3]);
				z = loadedTags[7] & 0x0f;
				//System.out.println(x + " " + y + " " + z);
				//chunkPrimer.setBlockState(x, y, z, BlockMain.oreCopper.getDefaultState());
			}
			else	{
				dataArray[0] &= 0xfe;
			}
		}
		//System.out.println(dataArray[0] + " " + dataArray[1] + " " + dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " " + 
		//		dataArray[5] + " " + dataArray[6] + " " + dataArray[7] + " " + dataArray[8]);
		chunkTunnelEndpoints.put(new ChunkPos(x1, z1).toString(), dataArray);
		return chunkPrimer;
	}
	
	public static ChunkPrimer generateTunnelLinks(Random random, World world, ChunkPrimer chunkPrimer,
			int x, int z, INodeProvider node, BlockPos nodeOrigin, int nodeRotation, int nodeIndex)	{
		ChunkPos chunkPos = new ChunkPos(x, z);
		byte[] loadedTags = chunkTunnelEndpoints.get(chunkPos.toString());
		EnumFacing[] directionReference = {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
		BlockPos[] faceTargets = {null, null, null, null};
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
		if (node != null)	{
			for (Entrance e : node.getEntrances()) {
				e = e.setCoords(Functions.nodeCoordsToChunkCoords(e.coords, nodeOrigin, nodeRotation));
				if (e.coords.getX() >= 0 && e.coords.getX() < 16 && e.coords.getZ() >= 0 && e.coords.getZ() < 16)	{
					e = e.rotateFacing(nodeRotation);
					for (int k = 0; k < 4; k++)	{
						if (directionReference[k] == e.facing)	{
							if (faceTargets[k] != null)	{
								chunkPrimer = connectEndpoints(random, chunkPrimer, e.coords, faceTargets[k]);
							}
						}
					}
				}
			}
		}
		else if (nodeIndex == -2)	{
			//System.out.println("Checking spillover chunk");
			ArrayList<Entrance> entrances = NodeGen.chunkEntrances.get(chunkPos.toString());
			if (entrances != null)	{
				for (Entrance e : entrances) {
					//e = e.setCoords(Functions.nodeCoordsToChunkCoords(e.coords, nodeOrigin, nodeRotation));
					for (int k = 0; k < 4; k++)	{
						if (directionReference[k] == e.facing)	{
							if (faceTargets[k] != null)	{
								System.out.println("Linking spillover chunk");
								chunkPrimer = connectEndpoints(random, chunkPrimer, e.coords, faceTargets[k]);
							}
						}
					}
				}
				NodeGen.chunkEntrances.remove(chunkPos.toString());
			}
		}
		else if (nodeIndex == -1)	{
			int totalFaces = 0;
			BlockPos averagePos = new BlockPos(0, 0, 0);
			for (BlockPos p : faceTargets)	{
				if (p != null)	{
					totalFaces++;
					averagePos = Functions.addCoords(averagePos, p);
				}
			}
			if (totalFaces != 0)	{
				//System.out.println("Total faces: " + totalFaces);
				//System.out.println("Generation flags: " + loadedTags[0]);
				averagePos = new BlockPos(averagePos.getX()/totalFaces, averagePos.getY()/totalFaces, averagePos.getZ()/totalFaces);
				//System.out.println("Average position: " + averagePos.toString());
			}
			//System.out.println("Average position for some chunk or other: " + averagePos.toString());
			for (BlockPos p : faceTargets)	{
				if (p != null)	{
					//System.out.println("Connecting targets: " + p.toString() + " and " + averagePos.toString());
					chunkPrimer = connectEndpoints(random, chunkPrimer, p, averagePos);
				}
			}
		}
		return chunkPrimer;
	}
	
	public static ChunkPrimer connectEndpoints(Random random, ChunkPrimer chunkPrimer, BlockPos p1, BlockPos p2)	{
		HashMap<BlockPos, Boolean> airMap = new HashMap<>();
		//System.out.println("Beginning connection");
		//System.out.println("(" + p1.getX() + ", " + p1.getY() + ", " + p1.getZ() + ")");
		//System.out.println(p1.getX() + "    " + p2.getX());
		int xDelta = p1.getX() - p2.getX();
		int yDelta = p1.getY() - p2.getY();
		int zDelta = p1.getZ() - p2.getZ();
		//System.out.println("(" + xDelta + ", " + yDelta + ", " + zDelta + ")");
		int selection = -1;
		int totalWeight = 0;
		int r;
		//Adjust this to change the likelihood of repeating a direction
		float p = 0.5f;
		int[] numberLine = new int[3];
		airMap.put(p1, false);
		chunkPrimer.setBlockState(p1.getX(), p1.getY(), p1.getZ(), Blocks.LAPIS_BLOCK.getDefaultState());
		while (Math.abs(xDelta) != 0 || Math.abs(yDelta) != 0 || Math.abs(zDelta) != 0)	{
			totalWeight = 0;
			switch (selection)	{
			case -1:
				numberLine[0] = Math.abs(xDelta);
				totalWeight += numberLine[0];
				totalWeight += Math.abs(yDelta);
				numberLine[1] = totalWeight;
				totalWeight += Math.abs(zDelta);
				numberLine[2] = totalWeight;
				break;
			case 0:
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
				totalWeight += (int) Math.ceil(p*Math.abs(zDelta));
				numberLine[2] = totalWeight;
				break;
			}
			r = random.nextInt(totalWeight + 1);
			for (int j = 0; j < 3; j++)	{
				if (r <= numberLine[j])	{
					selection = j;
					break;
				}
			}
			switch (selection)	{
			case 0:
				if (xDelta > 0)	{
					p1 = p1.west();
				}
				else if (xDelta < 0)	{
					p1 = p1.east();
				}
				break;
			case 1:
				if (yDelta > 0)	{
					p1 = p1.down();
				}
				else if (yDelta < 0)	{
					p1 = p1.up();
				}
				break;
			case 2:
				if (zDelta > 0)	{
					p1 = p1.north();
				}
				else if (zDelta < 0)	{
					p1 = p1.south();
				}
				break;
			}
			//System.out.println("Random: " + r + "    Line: [" + numberLine[0] + " " + numberLine[1] + " " + numberLine[2] + "]");
			//System.out.println("(" + xDelta + ", " + yDelta + ", " + zDelta + ")");
			//System.out.println("(" + p1.getX() + ", " + p1.getY() + ", " + p1.getZ() + ")");
			//System.out.println("Next!");
			airMap.put(p1, false);
			chunkPrimer.setBlockState(p1.getX(), p1.getY(), p1.getZ(), Blocks.LAPIS_BLOCK.getDefaultState());
			xDelta = p1.getX() - p2.getX();
			yDelta = p1.getY() - p2.getY();
			zDelta = p1.getZ() - p2.getZ();
		}
		airMap = Functions.generateTunnelCell(random, airMap);
		for (BlockPos pos : airMap.keySet())	{
			if (pos.getX() >= 0 && pos.getX() < 16 && pos.getY() >= 0 && pos.getY() < 256 && pos.getZ() >= 0 && pos.getZ() < 16)	{
				if (airMap.get(pos))	{
					chunkPrimer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), Blocks.AIR.getDefaultState());
				}
				else	{
					//chunkPrimer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), Blocks.ICE.getDefaultState());
				}
			}
		}
		return chunkPrimer;
	}
}