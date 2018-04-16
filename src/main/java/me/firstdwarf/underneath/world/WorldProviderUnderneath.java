package me.firstdwarf.underneath.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderUnderneath extends WorldProvider {

    @Override
    public DimensionType getDimensionType() {
        return UnderneathDimensions.underneathDimensionType;
    }

    @Override
    public String getSaveFolder() {
        return "DIM-Underneath";
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorUnderneath(this.world, this.world.getSeed());
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public BlockPos getRandomizedSpawnPoint() {
        return this.getSpawnPoint();
    }

    @Override
    public BlockPos getSpawnPoint() {
        return new BlockPos(0, 0, 0); // Make sure we always spawn at X = 0, Y = 0, & Z = 0 for testing
    }

    /**
     * When a player is added to this world provider (goes to the underneath world)
     *
     * @param player The player
     */
    // TODO: REMOVE THIS -- this is for debug only
    @SideOnly(Side.CLIENT)
    @Override
    public void onPlayerAdded(EntityPlayerMP player) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Welcome to the Underneath!"));
    }

}
