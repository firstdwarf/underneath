package me.firstdwarf.underneath.world.node;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Entrance {
	
	//An object that stores a direction and a block position for use with nodes implementing INodeProvider
	public EnumFacing facing;
	public int x, y, z;
	public BlockPos coords;
	
	/**
	 * Constructor for an Entrance object
	 * @param facing is the direction the entrance faces in node coordinates
	 * @param x is the x-coordinate of the entrance position in node coordinates
	 * @param y is the y-coordinate of the entrance position in node coordinates
	 * @param z is the z-coordinate of the entrance position in node coordinates
	 */
	public Entrance(EnumFacing facing, int x, int y, int z)	{
		this.facing = facing;
		this.x = x;
		this.y = y;
		this.z = z;
		this.coords = new BlockPos(x, y, z);
	}
	
	/**
	 * Constructor for an Entrance object
	 * @param facing is the direction the entrance faces in node coordinates
	 * @param coords is the block position of this entrance in node coordinates
	 */
	public Entrance(EnumFacing facing, BlockPos coords)	{
		this.facing = facing;
		this.x = coords.getX();
		this.y = coords.getY();
		this.z = coords.getZ();
		this.coords = coords;
	}
	
	/**
	 * This function returns an entrance at the same position facing in a different direction
	 * @param degree is the amount to rotate the entrance counterclockwise (0, 90, 180, or 270)
	 * @return
	 */
	public Entrance rotateFacing(int degree)	{
		
		//The number of times to rotate the entrance
		int count = degree/90;
		
		//Entrance to return
		Entrance eOut = new Entrance(this.facing, this.coords);
		for (int i = 1; i <= count; i++)	{
			
			//This uses the counterclockwise rotation method from EnumFacing
			eOut.facing = eOut.facing.rotateYCCW();
		}
		return eOut;
	}
	
	//Convenience method that returns a new entrance with a different position facing the same way as the original
	public Entrance setCoords(BlockPos blockPos)	{
		return new Entrance(this.facing, blockPos);
	}
	
	/**
	 * This function returns an entrance at a different position from and facing in a different direction than the original
	 * @param blockPos is the position the entrance should be at
	 * @param degree is the amount to rotate the entrance counterclockwise (0, 90, 180, or 270)
	 * @return
	 */
	public Entrance translate(BlockPos blockPos, int degree)	{
		
		//The number of times to rotate the entrance
		int count = degree/90;
		
		//Entrance to return
		Entrance eOut = new Entrance(this.facing, blockPos);
		for (int i = 1; i <= count; i++)	{
			
			//This uses the counterclockwise rotation method from EnumFacing
			eOut.facing = eOut.facing.rotateYCCW();
		}
		return eOut;
	}
}
