package me.firstdwarf.underneath.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;

import me.firstdwarf.underneath.world.node.INodeProvider;
import me.firstdwarf.underneath.world.node.NodeGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChunkGeneratorUnderneath implements IChunkGenerator {

    private World world;
    private Random random;
    private boolean flag = true;

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
        //System.out.println("Generating Chunk (" + x + ", " + z + ")");
        int nodeIndex = NodeGen.selectNodes(world, random, chunkPos);
        INodeProvider node;
        if (nodeIndex != -1)	{
        	node = NodeGen.nodeTypes.get(nodeIndex);
        }
        else	{
        	node = null;
        }
        NodeGen.chunkNodes.put(chunkPos.toString(), nodeIndex);
        chunkPrimer = TunnelGen.generateTunnelEndpoints(random, world, chunkPrimer, x, z, node);
    	chunkPrimer = TunnelGen.generateTunnelLinks(random, world, chunkPrimer, x, z, node);
        for (int i = 0; i <= 15; i++)	{
        	for (int j = 0; j <= 255; j++)	{
        		//chunkPrimer.setBlockState(i, j, 0, Blocks.GLOWSTONE.getDefaultState());
        		//chunkPrimer.setBlockState(i, j, 15, Blocks.GLOWSTONE.getDefaultState());
        	}
        }
        for (int k = 0; k <= 15; k++)	{
        	for (int j = 0; j <= 255; j++)	{
        		//chunkPrimer.setBlockState(0, j, k, Blocks.GLOWSTONE.getDefaultState());
        		//chunkPrimer.setBlockState(15, j, k, Blocks.GLOWSTONE.getDefaultState());
        	}
        }
        Chunk chunk = new Chunk(this.world, chunkPrimer, x, z);
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
    	INodeProvider node;
    	if (flag)	{
    		System.out.println("First Populated Chunk: " + x + ", " + z);
    		flag = false;
    	}
    	ChunkPos chunkPos = new ChunkPos(x, z);
    	int nodeIndex = NodeGen.chunkNodes.get(chunkPos.toString());
    	if (nodeIndex != -1)	{
    		node = NodeGen.nodeTypes.get(nodeIndex);
    	}
    	else	{
    		node = null;
    	}
    	NodeGen.generateNodes(world, random, chunkPos, node);
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
