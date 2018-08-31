package me.firstdwarf.underneath.world.node;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Entrance {
	public EnumFacing facing;
	public int x, y, z;
	public BlockPos coords;
	
	public Entrance(EnumFacing facing, int x, int y, int z)	{
		this.facing = facing;
		this.x = x;
		this.y = y;
		this.z = z;
		this.coords = new BlockPos(x, y, z);
	}
	
	public Entrance(EnumFacing facing, BlockPos coords)	{
		this.facing = facing;
		this.x = coords.getX();
		this.y = coords.getY();
		this.z = coords.getZ();
		this.coords = coords;
	}
	
	public Entrance rotateFacing(int degree)	{
		int count = degree/90;
		Entrance eOut = new Entrance(this.facing, this.coords);
		for (int i = 1; i <= count; i++)	{
			eOut.facing = eOut.facing.rotateYCCW();
		}
		return eOut;
	}
	public Entrance setCoords(BlockPos blockPos)	{
		return new Entrance(this.facing, blockPos);
	}
	public Entrance translate(BlockPos blockPos, int degree)	{
		int count = degree/90;
		Entrance eOut = new Entrance(this.facing, blockPos);
		for (int i = 1; i <= count; i++)	{
			eOut.facing = eOut.facing.rotateYCCW();
		}
		return eOut;
	}
}
