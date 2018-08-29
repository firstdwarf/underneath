package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import me.firstdwarf.underneath.utilities.Coords;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class Spawn implements INodeProvider	{
	//TODO: Make spawn appear in spawn chunk, not (0, 0)
	//Consider node origin to be (0, 0, 0)- this will be the ideological center of the node
	ArrayList<Entrance> entrances = new ArrayList<>();
	Entrance e1 = new Entrance(EnumFacing.SOUTH, 0, 0, 5);
	Entrance e2 = new Entrance(EnumFacing.NORTH, 0, 0, -5);
	
	public Spawn()	{
		entrances.add(e1);
		entrances.add(e2);
	}

	@Override
	public void placeStructures(World world, BlockPos origin, int rotation) {
		world.setBlockState(origin, Blocks.GLOWSTONE.getDefaultState());
		for (Entrance e : entrances)	{
			IBlockState state;
			state = e.facing == EnumFacing.NORTH ? Blocks.REDSTONE_BLOCK.getDefaultState() : Blocks.LAPIS_BLOCK.getDefaultState();
			Coords.setBlockFromCoords(world, origin, e.coords, rotation, state);
		}
	}

	@Override
	public void generateCave(World world, BlockPos origin, int rotation) {
		// TODO: Make some damn caves
	}

	@Override
	public int getWeight(World world, ChunkPos chunkPos) {
		int weight = 0;
		if (chunkPos.x == 0 && chunkPos.z == 0)	{
			weight = -1;
		}
		return weight;
	}

	@Override
	public ArrayList<Entrance> getEntrances() {
		return entrances;
	}
	
	@Override
	public String getName()	{
		return "spawn";
	}
}
