package me.firstdwarf.underneath.world.dimension;

import me.firstdwarf.underneath.core.Underneath;
import me.firstdwarf.underneath.utilities.ChunkSaveFile;
import me.firstdwarf.underneath.world.WorldProviderUnderneath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;

public class CustomDimension {

    // Dimension types for new dimensions get created here
    public static DimensionType underneathDimensionType;
    public static ConcurrentHashMap<ChunkPos, ChunkSaveFile> chunkSaves;

    /**
     * Initialize our custom dimensions:
     * This is called from the CommonProxy
     */
    public static void init() {
        registerDimensionTypes();
        registerDimensions();
        chunkSaves = new ConcurrentHashMap<>(0);
    }
    
    //DimensionManager.getNextFreeDimId()
    private static void registerDimensionTypes() {
        underneathDimensionType = DimensionType.register(Underneath.MODID, "_underneath", 7, WorldProviderUnderneath.class, false);
    }

    private static void registerDimensions() {
        DimensionManager.registerDimension(underneathDimensionType.getId(), underneathDimensionType);

        // TODO: Debug -- remove this later
        Underneath.instance.logger.log(Level.DEBUG, "Created Underneath Dimension with ID: " + underneathDimensionType.getId());
    }
}
