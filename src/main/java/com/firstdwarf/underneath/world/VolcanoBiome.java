package com.firstdwarf.underneath.world;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.common.BiomeDictionary;
//import sun.jvm.hotspot.memory.Generation;

public class VolcanoBiome extends Biome {
    public VolcanoBiome(Builder biomeBuilder) {
        super(biomeBuilder);

        addCarver(GenerationStage.Carving.AIR,
                Biome.createCarver(
                        WorldCarver.CAVE,
                        new ProbabilityConfig((0.14285715f)
                        )
                ));
        DefaultBiomeFeatures.addOres(this);
    }
}
