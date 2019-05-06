package me.firstdwarf.underneath.world;

import me.firstdwarf.underneath.utilities.Functions;
import me.firstdwarf.underneath.world.dimension.CustomDimension;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderUnderneath extends WorldProvider {

    @Override
    public DimensionType getDimensionType() {
        return CustomDimension.underneathDimensionType;
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
    public boolean isSurfaceWorld()	{
        return false;
    }

    @Override
    public boolean canRespawnHere()	{
    	return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z)	{
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_)	{
        return new Vec3d(0.2, 0.2, 0.2);
    }
    
    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks)	{
    	return 0.5f;
    }
    
    @Override
    public boolean shouldClientCheckLighting()	{
        return true;
    }
    
    //There is no visible sky ever, but this in necessary to prevent major lighting bugs
    @Override
    public boolean hasSkyLight()	{
    	return true;
    }
    
    @Override
    public int getRespawnDimension(net.minecraft.entity.player.EntityPlayerMP player)	{
    	return this.getDimension();
    }
    
    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColor(net.minecraft.entity.Entity cameraEntity, float partialTicks)	{
        return new Vec3d(0, 0, 0);
    }
    
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1)	{
        return 0;
    }
    
    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean byWater)	{
        return false;
    }
    
    @Override
    public BlockPos getRandomizedSpawnPoint() {
    	return new BlockPos(8, 245, 8);
        //return this.getSpawnPoint();
    }
    
    @SideOnly(Side.CLIENT)
    public double getVoidFogYFactor()	{
    	
    	//Default is 0.03125
        return 1;
    }
}
