package me.firstdwarf.underneath.utilities;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.core.Underneath;
import me.firstdwarf.underneath.world.dimension.CustomDimension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ChunkSaveFile {
	private String fileName;
	private String parentDir = "chunkData";
	private File file;
	private boolean hasFile = false;
	public ChunkPos chunkPos;
	private ConcurrentHashMap<BlockPos, IBlockState> stateMap;
	
	//Constructor will create or access files
	public ChunkSaveFile(World world, ChunkPos chunkPos, boolean canCreateFile)	{
		this.chunkPos = chunkPos;
		this.fileName = chunkPos.x + "_" + chunkPos.z + ".dat";
		
		this.file = new File(DimensionManager.getCurrentSaveRootDirectory().getPath()
				+ File.separator + world.provider.getSaveFolder() + File.separator + parentDir, fileName);
		this.file.setReadable(true);
		this.file.setWritable(true);
		
		if (canCreateFile)	{
			try {
				if (!this.file.getParentFile().mkdir())	{
					Underneath.logger.error("Failed to create parent directory for file " + fileName);
				}
				if (this.file.createNewFile())	{
					System.out.println(this.file.getPath());
				}
			} catch (IOException e) {
				Underneath.logger.error("Failed to create file " + fileName);
				e.printStackTrace();
			}
		}
		else	{
			if (file.exists())	{
				this.hasFile = true;
			}
		}
	}
	
	//Writes data into a file
	private void writeData(NBTTagCompound nbt)	{
		try {
			CompressedStreamTools.write(nbt, file);
			System.out.println("Wrote to file " + file.getPath());
		} catch (IOException e) {
			Underneath.logger.error("Failed to write to file " + fileName);
			e.printStackTrace();
		}
	}
	
	//Reads data from file into nbt tag compound
	private NBTTagCompound readData()	{
		NBTTagCompound nbt = new NBTTagCompound();
		try {
			if (file.exists())	{
				FileInputStream fileIn = new FileInputStream(file);
				DataInputStream dataIn = new DataInputStream(fileIn);
				nbt = CompressedStreamTools.read(dataIn);
				fileIn.close();
				dataIn.close();
			}
			else	{
				System.out.println("Fuck you old man you ain't got no file");
			}
		} catch (FileNotFoundException e) {
			Underneath.logger.error("Could not find file " + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			Underneath.logger.error("Could not read from file " + fileName);
			e.printStackTrace();
		}
		return nbt;
	}
	
	public void clearData()	{
		//this.file.delete();
		//CustomDimension.chunkSaves.remove(this.chunkPos);
	}
	
	//Adds a blockstate and position to the statemap for this chunk
	public void addToMap(BlockPos pos, IBlockState state)	{
		this.stateMap.put(pos, state);
	}
	
	//Saves the data from the statemap into nbt and writes it to a file
	public void saveMap()	{
		NBTTagList posList = new NBTTagList();
		NBTTagList stateList = new NBTTagList();
		
		BlockPos localCoords;
		byte[] data;
		String stateString;
		
		for (BlockPos pos : this.stateMap.keySet())	{
			localCoords = Functions.worldCoordsToChunkCoords(pos);
			data = new byte[] {(byte) ((byte) localCoords.getX() << 4 | (byte) localCoords.getZ() & 0x0f), (byte) localCoords.getY()};
			stateString = this.stateMap.get(pos).getBlock().getRegistryName().toString();
			posList.appendTag(new NBTTagString(stateString));
			stateList.appendTag(new NBTTagByteArray(data));
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("pos", posList);
		nbt.setTag("states", stateList);
		
		this.writeData(nbt);
	}
	
	//Loads data from file into statemap
	public void loadMap()	{
		NBTTagCompound nbt = this.readData();
		NBTTagList posList = nbt.getTagList("pos", Constants.NBT.TAG_BYTE_ARRAY);
		NBTTagList stateList = nbt.getTagList("states", Constants.NBT.TAG_STRING);
		
		int size = posList.tagCount();
		if (size != 0 && size == stateList.tagCount())	{
			BlockPos pos;
			IBlockState state;
			byte[] data;
			String stateString;
			
			if (this.stateMap == null)	{
				this.stateMap = new ConcurrentHashMap<>(0);
			}
			
			for (int i = 0; i < size; i++)	{
				data = ((NBTTagByteArray) posList.get(i)).getByteArray();
				stateString = ((NBTTagString) stateList.get(i)).getString();
				state = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stateString)).getDefaultState();
				pos = new BlockPos(data[0] >> 4 & 0x0f, data[1] & 0xff, data[0] & 0x0f);
				pos = Functions.chunkCoordsToWorldCoords(pos, chunkPos);
				this.stateMap.put(pos, state);
			}
		}
	}
	
	//Loads data from file and sets blocks based on data
	public void setBlocksFromMap(World world)	{
		
		if (this.hasFile)	{
			this.loadMap();
			for (BlockPos pos : this.stateMap.keySet())	{
				System.out.println("Set block at " + pos.toString() +
						" to state " + this.stateMap.get(pos).getBlock().getRegistryName().toString());
				world.setBlockState(pos, this.stateMap.get(pos));
			}
		}
	}
	
	//Retrieves a save object, creating it if one isn't already in memory- this is independent from file creation
	public static ChunkSaveFile getSave(World world, ChunkPos chunkPos, boolean canCreateFile)	{
		if (CustomDimension.chunkSaves.containsKey(chunkPos))	{
			System.out.println("Returning map from memory for chunk " + chunkPos.toString());
			return CustomDimension.chunkSaves.get(chunkPos);
		}
		else	{
			ChunkSaveFile save = new ChunkSaveFile(world, chunkPos, canCreateFile);
			save.stateMap = new ConcurrentHashMap<>(0);
			CustomDimension.chunkSaves.put(chunkPos, save);
			return save;
		}
	}
}
