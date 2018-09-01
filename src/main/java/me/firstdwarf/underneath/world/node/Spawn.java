package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;

import me.firstdwarf.underneath.utilities.Coords;
import me.firstdwarf.underneath.utilities.Functions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class Spawn implements INodeProvider	{
	//TODO: Entrance map might only works on game restart- as in, deleting without a restart prevents mapping. Check call location?
	//Consider node origin to be (0, 0, 0)- put this near where you want the main entrance)
	ArrayList<Coords> coordinates = new ArrayList<>();
	ArrayList<IBlockState> states = new ArrayList<>();
	ArrayList<Entrance> entrances = new ArrayList<>();
	int xMin = -5;
	int xMax = 5;
	int zMin = -20;
	int zMax = 20;
	//At least one entrance has to face north, up, or down (except a spawn node)
	Entrance e1 = new Entrance(EnumFacing.SOUTH, 0, 0, 20);
	Entrance e2 = new Entrance(EnumFacing.NORTH, 0, 0, -20);
	Entrance e3 = new Entrance(EnumFacing.WEST, -5, 0, 0);
	Entrance e4 = new Entrance(EnumFacing.EAST, 5, 0, 0);
	
	public Spawn()	{
		entrances.add(e1);
		entrances.add(e2);
		entrances.add(e3);
		entrances.add(e4);
		Coords.recordStateCuboid(0, 0, 0, 0, -4, 4, Blocks.BRICK_BLOCK.getDefaultState(), coordinates, states);
	}

	@Override
	public BlockPos[] getBounds()	{
		BlockPos[] b = {new BlockPos(this.xMin, 0, this.zMin), new BlockPos(this.xMax, 0, this.zMax)};
		return b;
	}
	
	@Override
	public void placeStructures(World world, BlockPos origin, int rotation) {
		for (int i = 0; i < coordinates.size(); i++)	{
			Coords.setBlockFromCoords(world, origin, coordinates.get(i), rotation, states.get(i));
		}
		world.setBlockState(origin, Blocks.GLOWSTONE.getDefaultState());
		for (Entrance e : entrances)	{
			IBlockState state;
			state = e.facing == EnumFacing.NORTH ? Blocks.REDSTONE_BLOCK.getDefaultState() : Blocks.LAPIS_BLOCK.getDefaultState();
			Functions.setBlockFromNodeCoordinates(world, origin, e.coords, rotation, state);
		}
	}

	@Override
	public void generateCave(World world, BlockPos origin, int rotation) {
		// TODO: Make some damn caves
	}

	//TODO: Implement checks on available nodes
	@Override
	public int getWeight(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation) {
		BlockPos spawnBlock = world.getSpawnPoint();
		ChunkPos spawnChunk = new ChunkPos(spawnBlock.getX() >> 4, spawnBlock.getZ() >> 4);
		int weight = 0;
		if (chunkPos.x == spawnChunk.x && chunkPos.z == spawnChunk.z)	{
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
