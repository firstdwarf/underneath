package me.firstdwarf.underneath.world;

import me.firstdwarf.underneath.core.Underneath;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.Level;

public class UnderneathDimensions {

    // Dimension types for new dimensions get created here
    public static DimensionType underneathDimensionType;

    /**
     * Initialize our custom dimensions:
     * This is called from the CommonProxy
     */
    public static void init() {
        registerDimensionTypes();
        registerDimensions();
    }

    private static void registerDimensionTypes() {
        underneathDimensionType = DimensionType.register(Underneath.MODID, "_underneath", DimensionManager.getNextFreeDimId(), WorldProviderUnderneath.class, false);
    }

    private static void registerDimensions() {
        DimensionManager.registerDimension(underneathDimensionType.getId(), underneathDimensionType);

        // TODO: Debug -- remove this later
        Underneath.instance.logger.log(Level.DEBUG, "Created Underneath Dimension with ID: " + underneathDimensionType.getId());
    }
}
