package me.firstdwarf.underneath.utilities;

import java.util.ArrayList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class Functions {
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
	public static void setBlockFromNodeCoordinates(World world, BlockPos origin, BlockPos coords, int rotation, IBlockState state)	{
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
			cOut = cOut.add(coords.getX() + nodeOrigin.getX(), coords.getY() + nodeOrigin.getY(),
					coords.getZ() + nodeOrigin.getZ());
			break;
		case 90:
			cOut = cOut.add(coords.getZ() + nodeOrigin.getX(), coords.getY() + nodeOrigin.getY(),
					-1*coords.getX() + nodeOrigin.getZ());
			break;
		case 180:
			cOut = cOut.add(-1*coords.getX() + nodeOrigin.getX(), coords.getY() + nodeOrigin.getY(),
					-1*coords.getZ() + nodeOrigin.getZ());
			break;
		case 270:
			cOut = cOut.add(-1*coords.getZ() + nodeOrigin.getX(), coords.getY() + nodeOrigin.getY(),
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
