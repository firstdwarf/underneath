package me.firstdwarf.underneath.block;

import me.firstdwarf.underneath.core.Underneath;
import me.firstdwarf.underneath.utilities.CustomMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileBlock extends Block {

	private Class<? extends TileEntity> tileEntityClass;
	private boolean hasModel;
	
	public TileBlock(String name, Class<? extends TileEntity> tileEntityClass,
			float hardness, float resistance, int opacity, String toolType, boolean hasModel) {
		super(CustomMaterial.NATURAL);
		this.tileEntityClass = tileEntityClass;
		this.setUnlocalizedName("underneath." + name.toLowerCase());
		this.setRegistryName(name.toLowerCase());
		this.setCreativeTab(Underneath.underneathTab);
		this.setHardness(hardness);
		this.setResistance(resistance);
		this.setLightOpacity(opacity);
		this.setHarvestLevel(toolType, 3);
		this.hasModel = hasModel;
	}
	
	@Override
	public boolean isBlockNormalCube(IBlockState state)	{
		return !hasModel;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)	{
		return !hasModel;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state)	{
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)	{
		System.out.println("Creating tile entity");
		try {
			return this.tileEntityClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
