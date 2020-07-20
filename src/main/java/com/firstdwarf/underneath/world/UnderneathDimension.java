package com.firstdwarf.underneath.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProviderSettings;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class UnderneathDimension extends Dimension {

	public UnderneathDimension(World world, DimensionType type, float p_i225788_3_) {
		super(world, type, p_i225788_3_);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		//Delicious pasta
		WorldType worldtype = this.world.getWorldInfo().getGenerator();
		ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> overworldGenerator = ChunkGeneratorType.SURFACE;
		BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> biomeprovidertype = BiomeProviderType.FIXED;
		BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> biomeprovidertype1 = BiomeProviderType.VANILLA_LAYERED;
		BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> biomeprovidertype2 = BiomeProviderType.CHECKERBOARD;
		
		
		BiomeProvider biomeprovider = null;
	    JsonElement jsonelement = Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.world.getWorldInfo().getGeneratorOptions());
	    JsonObject jsonobject = jsonelement.getAsJsonObject();
	    JsonObject jsonobject1 = jsonobject.getAsJsonObject("biome_source");
	    if (jsonobject1 != null && jsonobject1.has("type") && jsonobject1.has("options")) {
	    	BiomeProviderType<?, ?> biomeprovidertype3 = Registry.BIOME_SOURCE_TYPE.getOrDefault(new ResourceLocation(jsonobject1.getAsJsonPrimitive("type").getAsString()));
	    	JsonObject jsonobject2 = jsonobject1.getAsJsonObject("options");
	    	Biome[] abiome = new Biome[]{Biomes.OCEAN};
	    	if (jsonobject2.has("biomes")) {
	    		JsonArray jsonarray = jsonobject2.getAsJsonArray("biomes");
	    		abiome = jsonarray.size() > 0 ? new Biome[jsonarray.size()] : new Biome[]{Biomes.OCEAN};
	
	    		for(int i = 0; i < jsonarray.size(); ++i) {
	    			abiome[i] = Registry.BIOME.getValue(new ResourceLocation(jsonarray.get(i).getAsString())).orElse(Biomes.OCEAN);
	    		}
	    	}
	
	    	if (BiomeProviderType.FIXED == biomeprovidertype3) {
	    		SingleBiomeProviderSettings singlebiomeprovidersettings2 = biomeprovidertype.createSettings(this.world.getWorldInfo()).setBiome(abiome[0]);
	    		biomeprovider = biomeprovidertype.create(singlebiomeprovidersettings2);
	    	}
	
	    	if (BiomeProviderType.CHECKERBOARD == biomeprovidertype3) {
	    		int j = jsonobject2.has("size") ? jsonobject2.getAsJsonPrimitive("size").getAsInt() : 2;
	    		CheckerboardBiomeProviderSettings checkerboardbiomeprovidersettings = biomeprovidertype2.createSettings(this.world.getWorldInfo()).setBiomes(abiome).setSize(j);
	    		biomeprovider = biomeprovidertype2.create(checkerboardbiomeprovidersettings);
	    	}
	
	    	if (BiomeProviderType.VANILLA_LAYERED == biomeprovidertype3) {
	    		OverworldBiomeProviderSettings overworldbiomeprovidersettings1 = biomeprovidertype1.createSettings(this.world.getWorldInfo());
	    		biomeprovider = biomeprovidertype1.create(overworldbiomeprovidersettings1);
	    	}
	    }
	
	    if (biomeprovider == null) {
	    	biomeprovider = biomeprovidertype.create(biomeprovidertype.createSettings(this.world.getWorldInfo()).setBiome(Biomes.OCEAN));
	    }
	
		BlockState stoneState = Blocks.STONE.getDefaultState();
		BlockState waterState = Blocks.WATER.getDefaultState();
		JsonObject jsonobject3 = jsonobject.getAsJsonObject("chunk_generator");
		if (jsonobject3 != null && jsonobject3.has("options")) {
			JsonObject jsonobject4 = jsonobject3.getAsJsonObject("options");
		    if (jsonobject4.has("default_block")) {
		    	String s = jsonobject4.getAsJsonPrimitive("default_block").getAsString();
		    	stoneState = Registry.BLOCK.getOrDefault(new ResourceLocation(s)).getDefaultState();
		    }
		
		    if (jsonobject4.has("default_fluid")) {
		    	String s1 = jsonobject4.getAsJsonPrimitive("default_fluid").getAsString();
		    	waterState = Registry.BLOCK.getOrDefault(new ResourceLocation(s1)).getDefaultState();
		    }
		}
	
		OverworldGenSettings overworldgensettings1 = overworldGenerator.createSettings();
		overworldgensettings1.setDefaultBlock(stoneState);
		overworldgensettings1.setDefaultFluid(waterState);
		return overworldGenerator.create(this.world, biomeprovider, overworldgensettings1);
	}

	@Override
	public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid) {
		//Pasta
		for(int i = chunkPosIn.getXStart(); i <= chunkPosIn.getXEnd(); ++i) {
	         for(int j = chunkPosIn.getZStart(); j <= chunkPosIn.getZEnd(); ++j) {
	            BlockPos blockpos = this.findSpawn(i, j, checkValid);
	            if (blockpos != null) {
	               return blockpos;
	            }
	         }
	      }

	      return null;
	}

	@Override
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid) {
		//Pasta
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(posX, 0, posZ);
	      Biome biome = this.world.getBiome(blockpos$mutable);
	      BlockState blockstate = biome.getSurfaceBuilderConfig().getTop();
	      if (checkValid && !blockstate.getBlock().isIn(BlockTags.VALID_SPAWN)) {
	         return null;
	      } else {
	         Chunk chunk = this.world.getChunk(posX >> 4, posZ >> 4);
	         int i = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, posX & 15, posZ & 15);
	         if (i < 0) {
	            return null;
	         } else if (chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, posX & 15, posZ & 15) > chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR, posX & 15, posZ & 15)) {
	            return null;
	         } else {
	            for(int j = i + 1; j >= 0; --j) {
	               blockpos$mutable.setPos(posX, j, posZ);
	               BlockState blockstate1 = this.world.getBlockState(blockpos$mutable);
	               if (!blockstate1.getFluidState().isEmpty()) {
	                  break;
	               }

	               if (blockstate1.equals(blockstate)) {
	                  return blockpos$mutable.up().toImmutable();
	               }
	            }

	            return null;
	         }
	      }
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return 0;
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks) {
		//Pasta
		float f = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
	      f = MathHelper.clamp(f, 0.0F, 1.0F);
	      float f1 = 0.7529412F;
	      float f2 = 0.84705883F;
	      float f3 = 1.0F;
	      f1 = f1 * (f * 0.94F + 0.06F);
	      f2 = f2 * (f * 0.94F + 0.06F);
	      f3 = f3 * (f * 0.91F + 0.09F);
	      //return new Vec3d((double)f1, (double)f2, (double)f3);
	      return new Vec3d(1, 1, 1);
	}

	@Override
	public boolean canRespawnHere() {
		return true;
	}

	@Override
	public boolean doesXZShowFog(int x, int z) {
		return true;
	}

}
