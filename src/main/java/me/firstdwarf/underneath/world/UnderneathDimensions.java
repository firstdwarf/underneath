package me.firstdwarf.underneath.world;

import me.firstdwarf.underneath.Underneath;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class ModUnderneathDimensions {

    public static DimensionType underneathDimensionType;

    public static void init() {
        registerDimensions();
        registerDimensions();
    }

    private static void registerDimensionTypes() {
        underneathDimensionType = DimensionType.register(Underneath.MODID, "_underneath", DimensionManager.getNextFreeDimId(), WorldProviderUnderneath.class, false);
    }

    private static void registerDimensions() {
        DimensionManager.registerDimension(underneathDimensionType.getId(), underneathDimensionType);
    }
}
