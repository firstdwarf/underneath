package me.firstdwarf.underneath.world.dimension;

import java.util.LinkedList;
import java.util.Random;

import me.firstdwarf.underneath.world.node.NodeGen;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class Tunnel {
	private LinkedList<ChunkPos> path;
	private World world;
	private Random random;
	private ChunkPos currentPos;
	private EnumFacing facing;
	private int turnPercentage = 25;
	public Tunnel(World world, ChunkPos chunkPos, EnumFacing facing, Random random)	{
		this.world = world;
		this.random = random;
		this.facing = facing;
		this.path.add(chunkPos);
	}
	public void generate()	{
		boolean terminated = false;
		int rotation;
		while (!terminated)	{
			rotation = random.nextInt(100);
			if (rotation < turnPercentage/2)	{
				this.facing.rotateY();
			}
			else if (rotation < turnPercentage)	{
				this.facing.rotateYCCW();
			}
			Vec3i unitStep = this.facing.getDirectionVec();
			this.path.add(new ChunkPos(this.currentPos.x + unitStep.getX(), this.currentPos.z + unitStep.getZ()));
		}
	}
}
