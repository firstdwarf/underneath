package me.firstdwarf.underneath.world.node;

import me.firstdwarf.underneath.utilities.Coords;
import net.minecraft.util.EnumFacing;

public class Entrance {
	public EnumFacing facing;
	public int x, y, z;
	public Coords coords;
	
	public Entrance(EnumFacing facing, int x, int y, int z)	{
		this.facing = facing;
		this.x = x;
		this.y = y;
		this.z = z;
		this.coords = new Coords(x, y, z);
	}
	
	public Entrance(EnumFacing facing, Coords coords)	{
		this.facing = facing;
		this.x = coords.x;
		this.y = coords.y;
		this.z = coords.z;
		this.coords = coords;
	}
	
	public Entrance rotate(int degree)	{
		int count = degree/90;
		Entrance eOut = new Entrance(this.facing, this.coords);
		for (int i = 1; i <= count; i++)	{
			eOut.facing = eOut.facing.rotateYCCW();
		}
		return eOut;
	}
}
