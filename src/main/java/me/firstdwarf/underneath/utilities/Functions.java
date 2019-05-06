package me.firstdwarf.underneath.utilities;

import java.util.HashMap;
import java.util.Random;

import me.firstdwarf.underneath.core.Config;
import me.firstdwarf.underneath.world.SaveData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Functions {
	
	//TODO: Keep an eye out for step downs without free space
	public static HashMap<BlockPos, Boolean> generateTunnelCell(Random random, HashMap<BlockPos, Boolean> airMap)	{
		int blankPercentage = Config.tunnelAirWeight;
		int maxIterations = Config.tunnelCellStageCount;
		int range = Config.tunnelRange;
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
			for (int x = -1*range; x <= range; x++)	{
				for (int y = 0; y <= 1; y++)	{
					for (int z = -1*range; z <= range; z++)	{
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
				if (neighborAirCount >= Config.tunnelCellAirRule)	{
					airMap.put(p, true);
				}
				else	{
					airMap.put(p, false);
				}
			}
		}
		for (BlockPos p : clone.keySet())	{
			airMap.put(p, (clone.get(p) | airMap.get(p)));
		}
		return airMap;
	}
	
	//TODO: Consider allowing finer control of cave generation- perhaps on a per-node basis
	public static HashMap<BlockPos, Boolean> generateCaveCell(Random random, HashMap<BlockPos, IBlockState> blockMap,
			HashMap<BlockPos, Boolean> airMap, int range, boolean level)	{
		int ymin = level ? 0 : -1*range;
		int blankPercentage = Config.caveAirWeight;
		int maxIterations = Config.caveCellStageCount;
		HashMap<BlockPos, Boolean> tempMap = new HashMap<>();
		for (BlockPos p : airMap.keySet())	{
			for (int x = -1*range; x <= range; x++)	{
				for (int y = ymin; y <= range; y++)	{
					for (int z = -1*range; z <= range; z++)	{
						boolean b = (random.nextInt(100) + 1) <= blankPercentage;
						b &= !blockMap.containsKey(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z));
						tempMap.put(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z), b);
					}
				}
			}
		}
		for (BlockPos p : airMap.keySet())	{
			tempMap.put(p, true);
		}
		for (int l = 0; l <= maxIterations; l++)	{
			for (BlockPos p : tempMap.keySet())	{
				int neighborAirCount = 0;
				for (int x = -1; x <= 1; x++)	{
					for (int y = -1; y <= 1; y++)	{
						for (int z = -1; z <= 1; z++)	{
							if (tempMap.containsKey(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z))
									&& tempMap.get(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z)))	{
								neighborAirCount++;
							}
						}
					}
				}
				if (airMap.containsKey(new BlockPos(p.getX(), p.getY(), p.getZ())) || neighborAirCount >= Config.caveCellAirRule
						&& !blockMap.containsKey(new BlockPos(p.getX(), p.getY(), p.getZ())))	{
					tempMap.put(p, true);
				}
				else	{
					tempMap.put(p, false);
				}
			}
		}
		return tempMap;
	}
	
	public static HashMap<BlockPos, Boolean> generateFluidCave(Random random, HashMap<BlockPos, IBlockState> blockMap,
			HashMap<BlockPos, Boolean> fluidMap, int range, boolean level)	{
		int ymax = level ? 0 : range;
		int blankPercentage = Config.fluidWeight;
		int maxIterations = Config.fluidCellStageCount;
		HashMap<BlockPos, Boolean> tempMap = new HashMap<>();
		for (BlockPos p : fluidMap.keySet())	{
			for (int x = -1*range; x <= range; x++)	{
				for (int y = -1*range; y <= ymax; y++)	{
					for (int z = -1*range; z <= range; z++)	{
						boolean b = (random.nextInt(100) + 1) <= blankPercentage;
						b &= !blockMap.containsKey(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z));
						tempMap.put(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z), b);
					}
				}
			}
		}
		for (BlockPos p : fluidMap.keySet())	{
			tempMap.put(p, true);
		}
		for (int l = 0; l <= maxIterations; l++)	{
			for (BlockPos p : tempMap.keySet())	{
				int neighborFluidCount = 0;
				for (int x = -1; x <= 1; x++)	{
					for (int y = -1; y <= 1; y++)	{
						for (int z = -1; z <= 1; z++)	{
							if (tempMap.containsKey(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z))
									&& tempMap.get(new BlockPos(p.getX() + x, p.getY() + y, p.getZ() + z)))	{
								neighborFluidCount++;
							}
						}
					}
				}
				if (fluidMap.containsKey(new BlockPos(p.getX(), p.getY(), p.getZ())) || neighborFluidCount >= Config.fluidCellRule
						&& !blockMap.containsKey(new BlockPos(p.getX(), p.getY(), p.getZ())))	{
					tempMap.put(p, true);
				}
				else	{
					tempMap.put(p, false);
				}
			}
		}
		return tempMap;
	}

	public static void placeBlockSafely(World world, BlockPos pos, IBlockState state)	{
		
		ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
//		if (!(world.isBlockLoaded(pos) && world.getBlockState(pos).equals(state)))	{
			if (world.isChunkGeneratedAt(chunkPos.x, chunkPos.z))	{
//				System.out.println("Chunk is " + chunkPos.toString());
//				System.out.println("Position is " + pos.toString());
				world.setBlockState(pos, state);
				System.out.println("Set block in chunk " + chunkPos.toString());
			}
			else	{
				System.out.println("Saving additional data for chunk " + chunkPos.toString());
				ChunkSaveFile save = ChunkSaveFile.getSave(world, chunkPos, true);
				save.addToMap(pos, state);
			}
//		}
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
	public static BlockPos chunkCoordsToWorldCoords(BlockPos chunkCoords, ChunkPos chunkPos)	{
		return new BlockPos(chunkPos.getXStart() + chunkCoords.getX(), chunkCoords.getY(), chunkPos.getZStart() + chunkCoords.getZ());
	}
	public static void setBlockFromNodeCoordinates(World world, ChunkPos chunkPos,
			BlockPos origin, BlockPos coords, int rotation, IBlockState state)	{
		setBlockFromNodeCoordinates(world, chunkPos, origin, coords, rotation, state, 3);
	}
	
	public static void setBlockFromNodeCoordinates(World world, ChunkPos chunkPos,
			BlockPos origin, BlockPos coords, int rotation, IBlockState state, int flag)	{
		origin = chunkPos.getBlock(origin.getX(), origin.getY(), origin.getZ());
		switch (rotation)	{
		case 0:
			Functions.placeBlockSafely(world, coords, state);
			world.setBlockState(new BlockPos(coords.getX() + origin.getX(), coords.getY() + origin.getY(),
					coords.getZ() + origin.getZ()), state, flag);
			break;
		case 90:
			world.setBlockState(new BlockPos(coords.getZ() + origin.getX(), coords.getY() + origin.getY(),
					-1*coords.getX() + origin.getZ()), state, flag);
			break;
		case 180:
			world.setBlockState(new BlockPos(-1*coords.getX() + origin.getX(), coords.getY() + origin.getY(),
					-1*coords.getZ() + origin.getZ()), state, flag);
			break;
		case 270:
			world.setBlockState(new BlockPos(-1*coords.getZ() + origin.getX(), coords.getY() + origin.getY(),
					coords.getX() + origin.getZ()), state, flag);
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
}
