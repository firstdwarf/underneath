package me.firstdwarf.underneath.world;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.utilities.Coords;
import me.firstdwarf.underneath.world.node.Entrance;
import me.firstdwarf.underneath.world.node.INodeProvider;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;

public class TunnelGen {
	
	//TODO: Remove y-conversion method
	//TODO: Use neighbor boolean array
	//TODO: Hook up the non-entrance faces
	//TODO: Add checks for relative coordinates of entrance when forcing faces
	public static ConcurrentHashMap<String, byte[]> chunkTunnelEndpoints = new ConcurrentHashMap<>();
	
	public static int convertY(byte b)	{
		return (int) b & 0xff;
	}
	
	public static boolean checkPath(World world, ChunkPos chunkPos)	{
		return true;
	}
	
	public static ChunkPrimer generateTunnelEndpoints(Random random, World world, ChunkPrimer chunkPrimer,
			int x1, int z1, INodeProvider node)	{
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
		boolean[] neighbors = {world.isChunkGeneratedAt(x1, z1 - 1), world.isChunkGeneratedAt(x1, z1 + 1),
			world.isChunkGeneratedAt(x1 - 1, z1), world.isChunkGeneratedAt(x1 + 1, z1),};
		random.nextBytes(dataArray);
		//Randomly remove tunnel endings; can adjust loop length to tweak
		for (int j = 0; j < 100; j++)	{
			if (random.nextBoolean())	{
				dataArray[0] &= ~2^(random.nextInt(4));
			}
		}
		dataArray[0] = 0;
		
		//Clamp y-coords for crossing tunnels. Done before entrance clamping so it will be overridden for entrances
		for (int j = 0; j < 4; j++)	{
			int yIndex = 0;
			if (neighbors[j])	{
				switch (j)	{
				case 0:
					chunk = world.getChunkFromChunkCoords(x1, z1 - 1);
					yIndex = 1;
					break;
				case 1:
					chunk = world.getChunkFromChunkCoords(x1, z1 + 1);
					yIndex = 2;
					break;
				case 2:
					chunk = world.getChunkFromChunkCoords(x1 - 1, z1);
					yIndex = 3;
					break;
				case 3:
					chunk = world.getChunkFromChunkCoords(x1 + 1, z1);
					yIndex = 4;
					break;
				}
				loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
				dataArray[1] = (byte)(((int) loadedTags[yIndex] & 0xff) + random.nextInt(21) - 10);
				dataArray[2] = (byte)(((int) loadedTags[yIndex] & 0xff) + random.nextInt(21) - 10);
				dataArray[3] = (byte)(((int) loadedTags[yIndex] & 0xff) + random.nextInt(21) - 10);
				dataArray[4] = (byte)(((int) loadedTags[yIndex] & 0xff) + random.nextInt(21) - 10);
				break;
			}
		}
		
		//Force tunnel endings on faces with entrances and clamp y-coords
		if (node != null)	{
			boolean[] freeFaces = {true, true, true, true};
			for (Entrance e : node.getEntrances())	{
				dataArray[0] &= 0x0f;
				System.out.println(dataArray[3] & 0xff);
				//System.out.println("Chunk: (" + x1 + ", " + z1 + ")    Node Entrance Direction: " + e.facing.getName());
				boolean flag = false;
				int i;
				switch (e.facing)	{
				case NORTH:
					dataArray[0] |= 0x08;
					dataArray[1] = (byte) (e.y + random.nextInt(9) - 4);
					freeFaces[3] ^= true;
					break;
				case SOUTH:
					dataArray[0] |= 0x04;
					dataArray[2] = (byte) (e.y + random.nextInt(9) - 4);
					freeFaces[2] ^= true;
					break;
				case WEST:
					dataArray[0] |= 0x02;
					dataArray[3] = (byte) (e.y + random.nextInt(9) - 4);
					freeFaces[1] ^= true;
					break;
				case EAST:
					dataArray[0] |= 0x01;
					dataArray[4] = (byte) (e.y + random.nextInt(9) - 4);
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
		
		int x, y, z;
		
		//xy plane (0)-- north
		if(!world.isChunkGeneratedAt(x1, z1 - 1))	{
			if(!((dataArray[0] & 0x08) == 0))	{
				x = dataArray[5] & 0x0f;
				y = convertY(dataArray[1]);
				z = 0;
				//System.out.println(x + " " + y + " " + z);
				chunkPrimer.setBlockState(x, y, z, BlockMain.exampleBlock.getDefaultState());
			}
		}
		else	{
			chunk = world.getChunkFromChunkCoords(x1, z1 - 1);
			loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
			dataArray[5] = loadedTags[6];
			dataArray[1] = loadedTags[2];
			if(!((loadedTags[0] & 0x04) == 0))	{
				x = loadedTags[6] & 0x0f;
				y = convertY(loadedTags[2]);
				z = 0;
				//System.out.println(x + " " + y + " " + z);
				chunkPrimer.setBlockState(x, y, z, BlockMain.oreCopper.getDefaultState());
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
				chunkPrimer.setBlockState(x, y, z, BlockMain.exampleBlock.getDefaultState());
			}
		}
		else	{
			chunk = world.getChunkFromChunkCoords(x1, z1 + 1);
			loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
			dataArray[6] = loadedTags[5];
			dataArray[2] = loadedTags[1];
			if(!((loadedTags[0] & 0x08) == 0))	{
				x = loadedTags[5] & 0x0f;
				y = convertY(loadedTags[1]);
				z = 15;
				//System.out.println(x + " " + y + " " + z);
				chunkPrimer.setBlockState(x, y, z, BlockMain.oreCopper.getDefaultState());
			}
			else	{
				dataArray[0] &= 0xfb;
			}
		}
		//zy plane (0)-- west
		if(!world.isChunkGeneratedAt(x1 - 1, z1))	{
			if(!((dataArray[0] & 0x02) == 0))	{
				if (x1 == 0 && z1 == 0)	{
				}
				x = 0;
				y = convertY(dataArray[3]);
				z = dataArray[7] & 0x0f;
				if (x1 == 0 && z1 == 0)	{
					//System.out.println(x + " " + y + " " + z);
				}
				chunkPrimer.setBlockState(x, y, z, BlockMain.exampleBlock.getDefaultState());
			}
		}
		else	{
			chunk = world.getChunkFromChunkCoords(x1 - 1, z1);
			loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
			dataArray[7] = loadedTags[8];
			dataArray[3] = loadedTags[4];
			if(!((loadedTags[0] & 0x01) == 0))	{
				x = 0;
				y = convertY(loadedTags[4]);
				z = loadedTags[8] & 0x0f;
				//System.out.println(x + " " + y + " " + z);
				chunkPrimer.setBlockState(x, y, z, BlockMain.oreCopper.getDefaultState());
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
				chunkPrimer.setBlockState(x, y, z, BlockMain.exampleBlock.getDefaultState());
			}
		}
		else	{
			chunk = world.getChunkFromChunkCoords(x1 + 1, z1); 
			loadedTags = chunkTunnelEndpoints.get(chunk.getPos().toString());
			dataArray[8] = loadedTags[7];
			dataArray[4] = loadedTags[3];
			if(!((loadedTags[0] & 0x02) == 0))	{
				x = 15;
				y = convertY(loadedTags[3]);
				z = loadedTags[7] & 0x0f;
				//System.out.println(x + " " + y + " " + z);
				chunkPrimer.setBlockState(x, y, z, BlockMain.oreCopper.getDefaultState());
			}
			else	{
				dataArray[0] &= 0xfe;
			}
		}
		//System.out.println(dataArray[0] + " " + dataArray[1] + " " + dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " " + 
		//		dataArray[5] + " " + dataArray[6] + " " + dataArray[7] + " " + dataArray[8]);
		chunkTunnelEndpoints.put("[" + x1 + ", " + z1 + "]", dataArray);
		return chunkPrimer;
	}
	
	public static ChunkPrimer generateTunnelLinks(Random random, World world, ChunkPrimer chunkPrimer,
			int x, int z, INodeProvider node)	{
		ChunkPos chunkPos = new ChunkPos(x, z);
		byte[] loadedTags = chunkTunnelEndpoints.get(chunkPos.toString());
		EnumFacing[] directionReference = {EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
		Coords[] faceTargets = {null, null, null, null};
		for (int j = 0; j < 4; j++)	{
			if (!((loadedTags[0] & 2^j) == 0))	{
				
			}
		}
		if (!((loadedTags[0] & 2^3) == 0))	{
			faceTargets[0] = new Coords(loadedTags[5] & 0x0f, (int) loadedTags[1] & 0xff, 0);
		}
		if (!((loadedTags[0] & 2^2) == 0))	{
			faceTargets[1] = new Coords(loadedTags[6] & 0x0f, (int) loadedTags[2] & 0xff, 15);
		}
		if (!((loadedTags[0] & 2^1) == 0))	{
			faceTargets[2] = new Coords(0, (int) loadedTags[3] & 0xff, loadedTags[7] & 0x0f);
		}
		if (!((loadedTags[0] & 2^0) == 0))	{
			faceTargets[3] = new Coords(15, (int) loadedTags[4] & 0xff, loadedTags[8] & 0x0f);
		}
		if (node != null)	{
			for (Entrance e : node.getEntrances()) {
				for (int k = 0; k < 4; k++)	{
					if (directionReference[k] == e.facing)	{
						if (faceTargets[k] != null)	{
							chunkPrimer = connectEndpoints(random, chunkPrimer, e.coords, faceTargets[k]);
							faceTargets[k] = null;
						}
					}
				}
			}
		}
		for (Coords p : faceTargets)	{
			if (p != null)	{
				
			}
		}
		return chunkPrimer;
	}
	
	public static ChunkPrimer connectEndpoints(Random random, ChunkPrimer chunkPrimer, Coords c1, Coords c2)	{
		System.out.println(c1.x + "    " + c2.x);
		int xDelta = c1.x - c2.x;
		int yDelta = c1.y - c2.y;
		int zDelta = c1.z - c2.z;
		System.out.println("(" + xDelta + ", " + yDelta + ", " + zDelta + ")");
		int selection = -1;
		int totalWeight = 0;
		int r;
		int[] numberLine = new int[3];
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
				numberLine[0] = 2*Math.abs(xDelta);
				totalWeight += numberLine[0];
				totalWeight += Math.abs(yDelta);
				numberLine[1] = totalWeight;
				totalWeight += Math.abs(zDelta);
				numberLine[2] = totalWeight;
				break;
			case 1:
				numberLine[0] = Math.abs(xDelta);
				totalWeight += numberLine[0];
				totalWeight += 2*Math.abs(yDelta);
				numberLine[1] = totalWeight;
				totalWeight += Math.abs(zDelta);
				numberLine[2] = totalWeight;
				break;
			case 2:
				numberLine[0] = Math.abs(xDelta);
				totalWeight += numberLine[0];
				totalWeight += Math.abs(yDelta);
				numberLine[1] = totalWeight;
				totalWeight += 2*Math.abs(zDelta);
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
					c1.set(c1.x - 1, c1.y, c1.z);
				}
				else if (xDelta < 0)	{
					c1.set(c1.x + 1, c1.y, c1.z);
				}
				break;
			case 1:
				if (yDelta > 0)	{
					c1.set(c1.x, c1.y - 1, c1.z);
				}
				else if (yDelta < 0)	{
					c1.set(c1.x, c1.y + 1, c1.z);
				}
				break;
			case 2:
				if (zDelta > 0)	{
					c1.set(c1.x, c1.y, c1.z - 1);
				}
				else if (zDelta < 0)	{
					c1.set(c1.x, c1.y, c1.z + 1);
				}
				break;
			}
			chunkPrimer.setBlockState(c1.x, c1.y, c1.z, Blocks.BEDROCK.getDefaultState());
			xDelta = c1.x - c2.x;
			yDelta = c1.y - c2.y;
			zDelta = c1.z - c2.z;
			System.out.println("Random: " + r + "    Line: [" + numberLine[0] + " " + numberLine[1] + " " + numberLine[2] + "]");
			System.out.println("(" + xDelta + ", " + yDelta + ", " + zDelta + ")");
		}
		return chunkPrimer;
	}
}