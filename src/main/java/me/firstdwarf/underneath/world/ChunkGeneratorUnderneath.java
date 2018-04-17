package me.firstdwarf.underneath.world;

import net.minecraft.block.state.IBlockState;
import me.firstdwarf.underneath.block.BlockMain;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;

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
    	IBlockState block;
        this.random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        
        for (int i = 0; i <= 15; i++)	{
        	for (int j = 0; j <= 15; j++)	{
        		for (int k = 0; k <= 15; k++)	{
        			if (Math.random() <= 0.5)	{
        				block = BlockMain.exampleBlock.getDefaultState();
        			}
        			else	{
        				block = BlockMain.oreCopper.getDefaultState();
        			}
        			
        			chunkPrimer.setBlockState(i, j, k, block);
        		}
        	}
        }

        chunkPrimer.setBlockState(0, 0, 0, BlockMain.exampleBlock.getDefaultState());

        return new Chunk(this.world, chunkPrimer, x, z);
    }

    @Override
    public void populate(int x, int z) {
    	//check neighbors
    	//decide if node
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
