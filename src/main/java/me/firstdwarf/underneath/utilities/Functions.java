package me.firstdwarf.underneath.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class Functions {
	//TODO: Make sure step downs have free space
	public static HashMap<BlockPos, Boolean> generateTunnelCell(Random random, HashMap<BlockPos, Boolean> airMap)	{
		int blankPercentage = 0;
		int maxIterations = 3;
		HashMap<BlockPos, Boolean> clone = new HashMap<>();
		clone.putAll(airMap);
		for (BlockPos p : clone.keySet())	{
			for (int y = 1; y <= 2; y++)	{
				airMap.put(new BlockPos(p.getX(), p.getY() + y, p.getZ()), true);
			}
		}
		for (BlockPos p : clone.keySet())	{
			for (int x = -1; x <= 1; x++)	{
				for (int z = -1; z <= 1; z++)	{
					if (airMap.get(new BlockPos(p.getX() + x, p.getY() + 1, p.getZ() + z)) != null
							&& !airMap.get(new BlockPos(p.getX() + x, p.getY() + 1, p.getZ() + z)))	{
						airMap.put(new BlockPos(p.getX(), p.getY() + 3, p.getZ()), true);
					}
				}
			}
		}
		clone.clear();
		clone.putAll(airMap);
		for (BlockPos p : clone.keySet())	{
			for (int x = -2; x <= 2; x++)	{
				for (int y = 0; y <= 1; y++)	{
					for (int z = -2; z <= 2; z++)	{
						boolean b = (random.nextInt(100) + 1) <= blankPercentage;
						airMap.put(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z), b);
					}
				}
			}
		}
		for (BlockPos p : clone.keySet())	{
			airMap.put(p, clone.get(p));
		}
		for (int l = 0; l <= maxIterations; l++)	{
			for (BlockPos p : airMap.keySet())	{
				int neighborAirCount = 0;
				for (int x = -1; x <= 1; x++)	{
					for (int y = -1; y <= 1; y++)	{
						for (int z = -1; z <= 1; z++)	{
							if (airMap.get(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z)) != null
									&& airMap.get(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z)))	{
								neighborAirCount++;
							}
						}
					}
				}
				if (neighborAirCount >= 12)	{
					airMap.put(p, true);
				}
				else	{
					airMap.put(p, false);
				}
			}
		}
		for (BlockPos p : clone.keySet())	{
			airMap.put(p, clone.get(p));
		}
		return airMap;
	}
	public static HashMap<BlockPos, Boolean> generateCaveCell(Random random, HashMap<BlockPos, IBlockState> stateMap, int range)	{
		int blankPercentage = 80;
		int maxIterations = 5;
		HashMap<BlockPos, Boolean> airMap = new HashMap<>();
		for (BlockPos p : stateMap.keySet())	{
			for (int x = -1*range; x <= range; x++)	{
				for (int y = 0; y <= range; y++)	{
					for (int z = -1*range; z <= range; z++)	{
						boolean b = (random.nextInt(100) + 1) <= blankPercentage;
						airMap.put(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z), b);
					}
				}
			}
		}
		for (BlockPos p : stateMap.keySet())	{
			airMap.put(p, true);
		}
		for (int l = 0; l <= maxIterations; l++)	{
			for (BlockPos p : airMap.keySet())	{
				int neighborAirCount = 0;
				for (int x = -1; x <= 1; x++)	{
					for (int y = -1; y <= 1; y++)	{
						for (int z = -1; z <= 1; z++)	{
							if (airMap.get(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z)) != null
									&& airMap.get(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z)))	{
								neighborAirCount++;
							}
						}
					}
				}
				if (neighborAirCount >= 14)	{
					airMap.put(p, true);
				}
				else	{
					airMap.put(p, false);
				}
			}
		}
		return airMap;
	}
	public static HashMap<BlockPos, Boolean> generateCaveCell(Random random, BlockPos max, BlockPos min, int range)	{
		int blankPercentage = 80;
		int maxIterations = 5;
		HashMap<BlockPos, Boolean> airMap = new HashMap<>();
		for (int i = min.getX() - range; i <= max.getX() + range; i++)	{
			for (int j = min.getY(); j <= max.getY() + range; j++)	{
				for (int k = min.getZ() - range; k <= max.getZ() + range; k++)	{
					boolean b = (random.nextInt(100) + 1) <= blankPercentage;
					airMap.put(new BlockPos(i, j, k), b);
				}
			}
		}
		for (int i = min.getX(); i <= max.getX(); i++)	{
			for (int j = min.getY(); j <= max.getY(); j++)	{
				for (int k = min.getZ(); k <= max.getZ(); k++)	{
					airMap.put(new BlockPos(i, j, k), true);
				}
			}
		}
		for (int l = 0; l <= maxIterations; l++)	{
			for (int i = min.getX() - range; i <= max.getX() + range; i++)	{
				for (int j = min.getY(); j <= max.getY() + range; j++)	{
					for (int k = min.getZ() - range; k <= max.getZ() + range; k++)	{
						int neighborAirCount = 0;
						for (int x = -1; x <= 1; x++)	{
							for (int y = -1; y <= 1; y++)	{
								for (int z = -1; z <= 1; z++)	{
									if (airMap.get(new BlockPos(i + x, j + y, k + z)) != null
											&& airMap.get(new BlockPos(i + x, j + y, k + z)))	{
										neighborAirCount++;
									}
								}
							}
						}
						if (neighborAirCount >= 14)	{
							airMap.put(new BlockPos(i, j, k), true);
						}
						else	{
							airMap.put(new BlockPos(i, j, k), false);
						}
					}
				}
			}
		}
		for (int i = min.getX(); i <= max.getX(); i++)	{
			for (int j = min.getY(); j <= max.getY(); j++)	{
				for (int k = min.getZ(); k <= max.getZ(); k++)	{
					airMap.put(new BlockPos(i, j, k), true);
				}
			}
		}
		return airMap;
	}
	public static BlockPos addCoords(BlockPos c1, BlockPos c2)	{
		return new BlockPos(c1.getX() + c2.getX(), c1.getY() + c2.getY(), c1.getZ() + c2.getZ());
	}
	public static BlockPos nodeCoordsToWorldCoords(BlockPos coords, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation)	{
		BlockPos chunkCoords = nodeCoordsToChunkCoords(coords, nodeOrigin, nodeRotation);
		return new BlockPos(chunkPos.getBlock(chunkCoords.getX(), chunkCoords.getY(), chunkCoords.getZ()));
	}
	public static BlockPos worldCoordsToChunkCoords(BlockPos worldCoords)	{
		return new BlockPos(worldCoords.getX() & 0x0f, worldCoords.getY(), worldCoords.getZ() & 0x0f);
	}
	public static void setBlockFromNodeCoordinates(World world, ChunkPos chunkPos,
			BlockPos origin, BlockPos coords, int rotation, IBlockState state)	{
		origin = chunkPos.getBlock(origin.getX(), origin.getY(), origin.getZ());
		switch (rotation)	{
		case 0:
			world.setBlockState(new BlockPos(coords.getX() + origin.getX(), coords.getY() + origin.getY(),
					coords.getZ() + origin.getZ()), state);
			break;
		case 90:
			world.setBlockState(new BlockPos(coords.getZ() + origin.getX(), coords.getY() + origin.getY(),
					-1*coords.getX() + origin.getZ()), state);
			break;
		case 180:
			world.setBlockState(new BlockPos(-1*coords.getX() + origin.getX(), coords.getY() + origin.getY(),
					-1*coords.getZ() + origin.getZ()), state);
			break;
		case 270:
			world.setBlockState(new BlockPos(-1*coords.getZ() + origin.getX(), coords.getY() + origin.getY(),
					coords.getX() + origin.getZ()), state);
			break;
		}
	}
	
	public static BlockPos nodeCoordsToChunkCoords(BlockPos coords, BlockPos nodeOrigin, int nodeRotation)	{
		BlockPos cOut = new BlockPos(0, 0, 0);
		switch (nodeRotation)	{
		case 0:
			cOut = new BlockPos(coords.getX() + nodeOrigin.getX(), coords.getY() + nodeOrigin.getY(),
					coords.getZ() + nodeOrigin.getZ());
			break;
		case 90:
			cOut = new BlockPos(coords.getZ() + nodeOrigin.getX(), coords.getY() + nodeOrigin.getY(),
					-1*coords.getX() + nodeOrigin.getZ());
			break;
		case 180:
			cOut = new BlockPos(-1*coords.getX() + nodeOrigin.getX(), coords.getY() + nodeOrigin.getY(),
					-1*coords.getZ() + nodeOrigin.getZ());
			break;
		case 270:
			cOut = new BlockPos(-1*coords.getZ() + nodeOrigin.getX(), coords.getY() + nodeOrigin.getY(),
					coords.getX() + nodeOrigin.getZ());
			break;
		}
		return cOut;
	}
	public static void recordStateCuboid(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax,
			IBlockState state, ArrayList<Coords> coords, ArrayList<IBlockState> states)	{
		for (int x = xMin; x <= xMax; x++)	{
			for (int y = yMin; y <= yMax; y++)	{
				for (int z = zMin; z <= zMax; z++)	{
					coords.add(new Coords(x, y, z));
					states.add(state);
				}
			}
		}
	}
}
