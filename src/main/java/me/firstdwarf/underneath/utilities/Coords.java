package me.firstdwarf.underneath.utilities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO: Write a world coordinate retrieval method
public class Coords {
	public int x;
	public int y;
	public int z;
	public Coords(int x, int y, int z)	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(int x, int y, int z)	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	/**
	 * 
	 * @param world
	 * @param origin
	 * @param c
	 * @param rotation
	 * @param state
	 */
	public static void setBlockFromCoords(World world, BlockPos origin, Coords c, int rotation, IBlockState state)	{
		switch (rotation)	{
		case 0:
			world.setBlockState(new BlockPos(c.x + origin.getX(), c.y + origin.getY(), c.z + origin.getZ()), state);
			break;
		case 90:
			world.setBlockState(new BlockPos(c.z + origin.getX(), c.y + origin.getY(), -1*c.x + origin.getZ()), state);
			break;
		case 180:
			world.setBlockState(new BlockPos(-1*c.x + origin.getX(), c.y + origin.getY(), -1*c.z + origin.getZ()), state);
			break;
		case 270:
			world.setBlockState(new BlockPos(-1*c.z + origin.getX(), c.y + origin.getY(), c.x + origin.getZ()), state);
			break;
		}
	}
	
	public static Coords nodeCoordsToChunkCoords(Coords c, Coords nodeOrigin, int nodeRotation)	{
		Coords cOut = new Coords(0, 0, 0);
		switch (nodeRotation)	{
		case 0:
			cOut.set(c.x + nodeOrigin.x, c.y + nodeOrigin.y, c.z + nodeOrigin.z);
			break;
		case 90:
			cOut.set(c.z + nodeOrigin.x, c.y + nodeOrigin.y, -1*c.x + nodeOrigin.z);
			break;
		case 180:
			cOut.set(-1*c.x + nodeOrigin.x, c.y + nodeOrigin.y, -1*c.z + nodeOrigin.z);
			break;
		case 270:
			cOut.set(-1*c.z + nodeOrigin.x, c.y + nodeOrigin.y, c.x + nodeOrigin.z);
			break;
		}
		return cOut;
	}
	public static Coords add(Coords c1, Coords c2)	{
		return new Coords(c1.x + c2.x, c1.y + c2.y, c1.z + c2.z);
	}
}
