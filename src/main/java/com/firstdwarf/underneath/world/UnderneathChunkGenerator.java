package com.firstdwarf.underneath.world;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;

public class UnderneathChunkGenerator extends ChunkGenerator<GenerationSettings> {

	public UnderneathChunkGenerator(IWorld worldIn, BiomeProvider biomeProviderIn,
			GenerationSettings generationSettingsIn) {
		super(worldIn, biomeProviderIn, generationSettingsIn);
	}

	@Override
	public void generateBiomes(IChunk chunkIn) {
		ChunkPos chunkpos = chunkIn.getPos();
	    ((ChunkPrimer)chunkIn).func_225548_a_(new BiomeContainer(chunkpos, this.biomeProvider));
	}

	@Override
	protected Biome getBiome(BiomeManager biomeManagerIn, BlockPos posIn) {
		return biomeManagerIn.getBiome(posIn);
	}
	
	@Override
	public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
	}

	@Override
	public int getGroundHeight() {
		return 0;
	}
	
	@Override
	public void makeBase(IWorld worldIn, IChunk chunkIn) {
		for(int x = 1; x <= 16; x++)	{
			for(int z = 1; z <= 16; z++)	{
				chunkIn.setBlockState(new BlockPos(x, 1, z), Blocks.BEDROCK.getDefaultState(), false);
			}
		}
	}

	@Override
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Type heightmapType) {
		return 0;
	}

	@Override
	public void decorate(WorldGenRegion region) {
		
	}
	
}
