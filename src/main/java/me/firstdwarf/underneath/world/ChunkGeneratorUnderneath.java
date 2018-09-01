package me.firstdwarf.underneath.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;

import me.firstdwarf.underneath.utilities.Functions;
import me.firstdwarf.underneath.world.node.INodeProvider;
import me.firstdwarf.underneath.world.node.NodeGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChunkGeneratorUnderneath implements IChunkGenerator {
 
    private World world;
    private Random random;

    /**
     * Constructor
     *
     * @param world The world
     * @param seed The world seed
     */
    public ChunkGeneratorUnderneath(World world, long seed) {
        this.world = world;
        this.random = new Random(seed);
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        this.random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        ChunkPos chunkPos = new ChunkPos(x, z);
        
        for (int i = 0; i <= 15; i++)	{
        	for (int j = 0; j <= 255; j++)	{
        		for (int k = 0; k <= 15; k++)	{
        			chunkPrimer.setBlockState(i, j, k, Blocks.STONE.getDefaultState());
        		}
        	}
        }
        
        int nodeRotation = 90*random.nextInt(4);
        BlockPos nodeOrigin = new BlockPos(random.nextInt(12) + 2, random.nextInt(128) + 63, random.nextInt(12) + 2);
        
        ChunkPos spawnPos = new ChunkPos(world.getSpawnPoint().getX() >> 4, world.getSpawnPoint().getZ() >> 4);
        if (spawnPos.x == chunkPos.x && spawnPos.z == chunkPos.z)	{
        	nodeOrigin = Functions.worldCoordsToChunkCoords(world.getSpawnPoint());
        	nodeOrigin = Functions.addCoords(nodeOrigin, new BlockPos(0, -1, 0));
        }
        if (world.isChunkGeneratedAt(x, z - 1))	{
        	nodeRotation = 0;
        	world.getChunkFromChunkCoords(x, z - 1);
        	byte[] tunnelInfo = TunnelGen.chunkTunnelEndpoints.get(new ChunkPos(x, z - 1).toString());
        	nodeOrigin = new BlockPos(nodeOrigin.getX(), tunnelInfo[2] + random.nextInt(5) - 3, nodeOrigin.getZ());
        }
        else if (world.isChunkGeneratedAt(x - 1, z))	{
        	nodeRotation = 90;
        	world.getChunkFromChunkCoords(x - 1, z);
        	byte[] tunnelInfo = TunnelGen.chunkTunnelEndpoints.get(new ChunkPos(x - 1, z).toString());
        	nodeOrigin = new BlockPos(nodeOrigin.getX(), tunnelInfo[4] + random.nextInt(5) - 3, nodeOrigin.getZ());
        }
        else if (world.isChunkGeneratedAt(x, z + 1))	{
        	nodeRotation = 180;
        	world.getChunkFromChunkCoords(x, z + 1);
        	byte[] tunnelInfo = TunnelGen.chunkTunnelEndpoints.get(new ChunkPos(x, z + 1).toString());
        	nodeOrigin = new BlockPos(nodeOrigin.getX(), tunnelInfo[1] + random.nextInt(5) - 3, nodeOrigin.getZ());
        }
        else if (world.isChunkGeneratedAt(x + 1, z))	{
        	nodeRotation = 270;
        	world.getChunkFromChunkCoords(x + 1, z);
        	byte[] tunnelInfo = TunnelGen.chunkTunnelEndpoints.get(new ChunkPos(x + 1, z).toString());
        	nodeOrigin = new BlockPos(nodeOrigin.getX(), tunnelInfo[3] + random.nextInt(5) - 3, nodeOrigin.getZ());
        }
        
        //System.out.println("Generating Chunk (" + x + ", " + z + ")");
        int nodeIndex = NodeGen.selectNodes(world, random, chunkPos, nodeOrigin, nodeRotation);
        INodeProvider node;
        if (NodeGen.chunkNodes.containsKey(chunkPos.toString()))	{
        	//System.out.println("Found a pre-specified chunk");
        	//System.out.println(chunkPos.toString());
        	if (NodeGen.chunkNodes.get(chunkPos.toString()) == -2)	{
        		node = null;
        		nodeIndex = -2;
        	}
        	else	{
        		node = null;
        	}
        }
        else if (nodeIndex != -1)	{
        	//System.out.println("Set up a node");
        	node = NodeGen.nodeTypes.get(nodeIndex);
        }
        else	{
        	node = null;
        }
        
        if (node != null)	{
        	node.flagNeighbors(chunkPos, nodeOrigin, nodeRotation);
        	//System.out.println("Flagging the neighbors of a chunk. Watch for this w/o restart");
        }
        NodeGen.chunkNodes.put(chunkPos.toString(), nodeIndex);
        NodeGen.chunkNodes.put(chunkPos.toString() + ".rotation", nodeRotation);
        NodeGen.chunkNodes.put(chunkPos.toString() + ".origin.x", nodeOrigin.getX());
        NodeGen.chunkNodes.put(chunkPos.toString() + ".origin.y", nodeOrigin.getY());
        NodeGen.chunkNodes.put(chunkPos.toString() + ".origin.z", nodeOrigin.getZ());
        chunkPrimer = TunnelGen.generateTunnelEndpoints(random, world, chunkPrimer, x, z, node, nodeOrigin, nodeRotation, nodeIndex);
    	chunkPrimer = TunnelGen.generateTunnelLinks(random, world, chunkPrimer, x, z, node, nodeOrigin, nodeRotation, nodeIndex);
    	
        Chunk chunk = new Chunk(this.world, chunkPrimer, x, z);
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
    	INodeProvider node;
    	ChunkPos chunkPos = new ChunkPos(x, z);
    	//System.out.println("Populating chunk " + chunkPos.toString());
    	int nodeIndex = NodeGen.chunkNodes.get(chunkPos.toString());
    	int nodeRotation = NodeGen.chunkNodes.get(chunkPos.toString() + ".rotation");
    	BlockPos nodeOrigin = new BlockPos(NodeGen.chunkNodes.get(chunkPos.toString() + ".origin.x"),
    			NodeGen.chunkNodes.get(chunkPos.toString() + ".origin.y"),
    			NodeGen.chunkNodes.get(chunkPos.toString() + ".origin.z"));
    	if (nodeIndex >= 0)	{
    		node = NodeGen.nodeTypes.get(nodeIndex);
    	}
    	else	{
    		node = null;
    	}
    	NodeGen.generateNodes(world, random, chunkPos, node, nodeOrigin, nodeRotation);
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return new ArrayList<Biome.SpawnListEntry>();
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return new BlockPos(0, 0, 0);
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
