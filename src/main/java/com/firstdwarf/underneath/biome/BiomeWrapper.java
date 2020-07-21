package com.firstdwarf.underneath.biome;

import com.firstdwarf.underneath.Underneath;
import com.firstdwarf.underneath.block.BlockWrapper;
import com.firstdwarf.underneath.world.ExampleBiome;
//import net.minecraft.world.biome.BiomeManager; //I think we want the other version for this
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type; //for taking in type for registerBiome
import net.minecraftforge.common.BiomeManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeWrapper {
    public static final DeferredRegister<Biome> BIOMES = new DeferredRegister<>(ForgeRegistries.BIOMES, "underneath");

    public static final RegistryObject<Biome> testBiome = BIOMES.register("test_biome", //This sets registry name
            ()-> new ExampleBiome(new Biome.Builder()
                    .precipitation(Biome.RainType.RAIN)
                    .scale(4f)
                    .temperature(0.5f)
                    .waterColor(16724639).waterFogColor(16762304)
                    .surfaceBuilder(SurfaceBuilder.DEFAULT,
                            new SurfaceBuilderConfig(
                                    Blocks.BEACON.getDefaultState(), //Surface block info
                                    Blocks.DIRT.getDefaultState(), //just under the surface block info
                                    Blocks.YELLOW_WOOL.getDefaultState())) //under the water block info
                    .category(Biome.Category.PLAINS)
                    .downfall(1f) //how often it rains (ex. .5 would fall half the time)
                    .depth(0.12f) //how low is your biome
                    .parent(null)
            ));
    //NOTE: for finding specific colors https://www.mathsisfun.com/hexadecimal-decimal-colors.html

    public static void registerBiomes() {
        registerBiome(testBiome.get(), Type.PLAINS, Type.OVERWORLD); //This determines how the biome spawns

    }

    public static void registerBiome (Biome biome, Type... types) {
        BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(biome, 10000));
        BiomeDictionary.addTypes(biome, types);
        BiomeManager.addSpawnBiome(biome);
    }
}
