package com.firstdwarf.underneath.world;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class ExampleBiome extends Biome {
    public ExampleBiome(Builder biomeBuilder) {
        super(biomeBuilder);
        addSpawn(EntityClassification.AMBIENT, new SpawnListEntry(EntityType.OCELOT, 20, 2, 10));


        //Add other methods for biome here
        //DefaultBiomeFeatures contains some common functions that may be useful
        //Things like default plants may not work if there isn't the appropriate soil

        //addCarver may be of particular interest, as it handles caves
        addCarver(GenerationStage.Carving.AIR,
                Biome.createCarver(
                        WorldCarver.CAVE, //Choose the type of cave here
                        new ProbabilityConfig(0.14285715F) //This value is extremely sensitive
                        //You can check standard probabilites in  DefaultBiomeFeatures.class
                ));
        DefaultBiomeFeatures.addOres(this);
        DefaultBiomeFeatures.addExtraEmeraldOre(this);

        //Set Registry Name
    }
}
