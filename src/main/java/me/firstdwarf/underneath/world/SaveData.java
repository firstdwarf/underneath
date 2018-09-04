package me.firstdwarf.underneath.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class SaveData extends WorldSavedData {

	//This stores the spawn points of all underneath dimensions- used because setting world spawn is unresponsive
    //public static HashMap<DimensionType, BlockPos> spawns = new HashMap<>();
    public BlockPos spawn;
    
	private static String name = "underneath.chunkData";
	public SaveData() {
		super(name);
	}
	
	public SaveData(String nameIn)	{
		super(nameIn);
	}
	
	public void setSpawn(BlockPos pos)	{
		this.spawn = pos;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		int[] coords = nbt.getIntArray("spawn");
		this.spawn = new BlockPos(coords[0], coords[1], coords[2]);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		
		System.out.println("Writing to saved data");
		int[] coords = {spawn.getX(), spawn.getY(), spawn.getZ()};
		compound.setIntArray("spawn", coords);
		return compound;
	}
	
	public static SaveData getData(World world)	{
		MapStorage s = world.getPerWorldStorage();
		SaveData instance = (SaveData) s.getOrLoadData(SaveData.class, name);
		if (instance == null)	{
			instance = new SaveData();
			s.setData(name, instance);
		}
		return instance;
	}

}
