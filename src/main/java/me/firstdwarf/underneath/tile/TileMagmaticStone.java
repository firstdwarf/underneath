package me.firstdwarf.underneath.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileMagmaticStone extends TileEntity implements ITickable	{
	
	private int max = 4;
	private int counter = 0;
	private int capacity = (int) ((max/2) * (Math.random() + 1));
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)	{
		this.capacity = nbt.getInteger("capacity");
		super.readFromNBT(nbt);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)	{
		nbt.setInteger("capacity", this.capacity);
		nbt = super.writeToNBT(nbt);
		return nbt;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)	{
		boolean refresh = true;
		return refresh;
	}

	@Override
	public NBTTagCompound getUpdateTag()	{
		NBTTagCompound nbt = new NBTTagCompound();
		return this.writeToNBT(nbt);
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound nbt)	{
		this.readFromNBT(nbt);
	}
	
	@Override
	public void update() {
		if (!world.isRemote)	{
			if (this.counter == 19)	{
				this.counter = 0;
				System.out.println("Ticking tile entity at " + this.getPos().toString());
			
				if (this.capacity > 0)	{
					int[] offset = {-1, 1};
					BlockPos neighbor;
					for (int x : offset)	{
						neighbor = new BlockPos(this.pos.getX() + x, this.pos.getY(), this.pos.getZ());
						if (this.world.isBlockLoaded(neighbor))	{
							if (this.world.getBlockState(neighbor).equals(Blocks.GLOWSTONE.getDefaultState()))	{
								this.capacity--;
							}
						}
					}
					for (int z : offset)	{
						neighbor = new BlockPos(this.pos.getX(), this.pos.getY(), this.pos.getZ() + z);
						if (this.world.isBlockLoaded(neighbor))	{
							if (this.world.getBlockState(neighbor).equals(Blocks.GLOWSTONE.getDefaultState()))	{
								this.capacity--;
							}
						}
					}
				}
			
				if (this.capacity == 0)	{
					System.out.println("Empty!");
					this.world.setBlockState(this.pos, Blocks.END_STONE.getDefaultState());
				}
			}
			else	{
				counter++;
			}
		}
	}
}
