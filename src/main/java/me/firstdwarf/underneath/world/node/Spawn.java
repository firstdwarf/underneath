package me.firstdwarf.underneath.world.node;

import java.util.ArrayList;
import java.util.HashMap;

import me.firstdwarf.underneath.utilities.Functions;
import me.firstdwarf.underneath.world.UnderneathDimensions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class Spawn implements INodeProvider	{
	//TODO: Move entrance registration to allow restarting
	//Consider node origin to be (0, 0, 0)- put this near where you want the main entrance)
	ArrayList<BlockPos> coordinates = new ArrayList<>();
	ArrayList<IBlockState> states = new ArrayList<>();
	ArrayList<Entrance> entrances = new ArrayList<>();
	HashMap<BlockPos, IBlockState>	stateMap = new HashMap<>();
	public static HashMap<DimensionType, BlockPos> spawns = new HashMap<>();
	boolean flag = true;
	int xMin = -5;
	int xMax = 5;
	int zMin = -5;
	int zMax = 5;
	int yMin = 0;
	int yMax = 3;
	//At least one entrance has to face north, up, or down (except a spawn node)
	Entrance e1 = new Entrance(EnumFacing.SOUTH, 0, 0, 5);
	Entrance e2 = new Entrance(EnumFacing.NORTH, 0, 0, -5);
	Entrance e3 = new Entrance(EnumFacing.WEST, -5, 0, 0);
	Entrance e4 = new Entrance(EnumFacing.EAST, 5, 0, 0);
	
	public Spawn()	{
		entrances.add(e1);
		entrances.add(e2);
		entrances.add(e3);
		entrances.add(e4);
		for (int i = xMin; i <= xMax; i++)	{
			for (int j = yMin; j <= yMax; j++)	{
				for (int k = zMin; k <= zMax; k++)	{
					stateMap.put(new BlockPos(i, j, k), Blocks.AIR.getDefaultState());
				}
			}
		}
	}

	@Override
	public HashMap<BlockPos, IBlockState> getStateMap()	{
		return this.stateMap;
	}
	
	@Override
	public BlockPos[] getBounds()	{
		BlockPos[] b = {new BlockPos(this.xMin, this.yMin, this.zMin), new BlockPos(this.xMax, this.yMax, this.zMax)};
		return b;
	}
	
	@Override
	public void placeStructures(World world, ChunkPos chunkPos, BlockPos origin, int rotation) {
		Functions.setBlockFromNodeCoordinates(world, chunkPos, origin,
				new BlockPos(0, 0, 0), rotation, Blocks.GLOWSTONE.getDefaultState());
		world.setBlockState(origin, Blocks.GLOWSTONE.getDefaultState());
		for (Entrance e : entrances)	{
			IBlockState state;
			state = e.facing == EnumFacing.NORTH ? Blocks.REDSTONE_BLOCK.getDefaultState() : Blocks.LAPIS_BLOCK.getDefaultState();
			Functions.setBlockFromNodeCoordinates(world, chunkPos, origin, e.coords, rotation, state);
		}
	}

	//TODO: Implement checks on available nodes
	@Override
	public int getWeight(World world, ChunkPos chunkPos, BlockPos nodeOrigin, int nodeRotation, boolean isSpawn) {
		int weight = 0;
		if (isSpawn)	{
			if (world.provider.getDimensionType().equals(UnderneathDimensions.underneathDimensionType))	{
				spawns.put(UnderneathDimensions.underneathDimensionType,
						chunkPos.getBlock(nodeOrigin.getX(), nodeOrigin.getY(), nodeOrigin.getZ()));
			}
			System.out.println("Placing spawn node at " + chunkPos.toString() + "    " + nodeOrigin.toString());
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
