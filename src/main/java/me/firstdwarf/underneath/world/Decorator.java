package me.firstdwarf.underneath.world;

import java.util.Random;

import me.firstdwarf.underneath.block.BlockMain;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class Decorator {
	
	public WorldGenerator exampleGen;
	public BlockPos pos;
	
	public void generateVeins(World world, Random random, ChunkPos chunkPos)	{
		this.exampleGen = new WorldGenNatural(BlockMain.exampleBlock.getDefaultState(), 10);
		this.pos = new BlockPos(chunkPos.x*16, 0, chunkPos.z*16);
		this.genStandardOre1(world, random, 100, exampleGen, 1, 254);
	}
	
	protected void genStandardOre1(World worldIn, Random random, int blockCount, WorldGenerator generator, int minHeight, int maxHeight)
    {
        if (maxHeight < minHeight)
        {
            int i = minHeight;
            minHeight = maxHeight;
            maxHeight = i;
        }
        else if (maxHeight == minHeight)
        {
            if (minHeight < 255)
            {
                ++maxHeight;
            }
            else
            {
                --minHeight;
            }
        }

        for (int j = 0; j < blockCount; ++j)
        {
            BlockPos blockpos = this.pos.add(random.nextInt(16), random.nextInt(maxHeight - minHeight) + minHeight, random.nextInt(16));
            generator.generate(worldIn, random, blockpos);
        }
    }
}
