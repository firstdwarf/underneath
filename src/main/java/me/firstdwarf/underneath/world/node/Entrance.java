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
}
