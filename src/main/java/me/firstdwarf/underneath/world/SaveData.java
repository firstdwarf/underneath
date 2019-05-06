package me.firstdwarf.underneath.world;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import me.firstdwarf.underneath.utilities.Functions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SaveData extends WorldSavedData {

	//This stores the spawn points of all underneath dimensions- used because setting world spawn is unresponsive
    //public static HashMap<DimensionType, BlockPos> spawns = new HashMap<>();
    public BlockPos spawn;
    public ConcurrentHashMap<String, ArrayList<BlockPos>> airBlocks;
    private ConcurrentHashMap<String, ArrayList<String>> stateMap;
    private ConcurrentHashMap<String, ArrayList<byte[]>> positionMap;
    public ArrayList<String> toClear;
    public int tunnelsAvailable;
    
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
	
	public void addState(BlockPos blockPos, ChunkPos chunkPos, IBlockState state)	{
		if (this.stateMap.containsKey(chunkPos.toString()))	{
			this.stateMap.get(chunkPos.toString()).add(state.getBlock().getRegistryName().toString());
		}
		else	{
			ArrayList<String> stateList = new ArrayList<>(1);
			stateList.add(state.getBlock().getRegistryName().toString());
			this.stateMap.put(chunkPos.toString(), stateList);
		}
		
		BlockPos localCoords = Functions.worldCoordsToChunkCoords(blockPos);
		byte[] data = {(byte) ((byte) localCoords.getX() << 4 | (byte) localCoords.getZ() & 0x0f), (byte) localCoords.getY()};
		if (this.positionMap.containsKey(chunkPos.toString()))	{
			this.positionMap.get(chunkPos.toString()).add(data);
		}
		else	{
			ArrayList<byte[]> positionList = new ArrayList<>(1);
			positionList.add(data);
			this.positionMap.put(chunkPos.toString(), positionList);
		}
	}
	
	public void placeFromData(World world, ChunkPos chunkPos)	{
		if (this.stateMap.containsKey(chunkPos.toString()) && this.positionMap.containsKey(chunkPos.toString()))	{
			ArrayList<String> stateList = stateMap.get(chunkPos.toString());
			ArrayList<byte[]> positionList = positionMap.get(chunkPos.toString());
			int size = stateList.size();
			if (size != positionList.size())	{
				System.out.println("ERROR: Data map size mismatch");
				return;
			}
			else	{
				IBlockState state;
				byte[] data;
				BlockPos pos;
				for (int i = 0; i < size; i++)	{
					state = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stateList.get(i))).getDefaultState();
					data = positionList.get(i);
					pos = new BlockPos(data[0] >> 4 & 0x0f, data[1] & 0xff, data[0] & 0x0f);
					pos = Functions.chunkCoordsToWorldCoords(pos, chunkPos);
					//world.setBlockState(pos, state);
				}
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for (String s : nbt.getKeySet())	{
			this.airBlocks = new ConcurrentHashMap<>(0);
			this.toClear = new ArrayList<>(0);
			if (s.equals("Spawn"))	{
				int[] coords = nbt.getIntArray("spawn");
				this.spawn = new BlockPos(coords[0], coords[1], coords[2]);
			}
			else if (s.equals("tunnelsAvailable"))	{
				this.tunnelsAvailable = nbt.getInteger(s);
			}
			else	{
				ArrayList<BlockPos> air = new ArrayList<>(0);
				byte[] blocks = nbt.getByteArray(s);
				boolean isY = false;
				for (byte b : blocks)	{
					BlockPos pos = new BlockPos(0, 0 ,0);
					if (!isY)	{
						pos.add((b >> 4) & 0xff, 0, b & 0xff);
						isY = !isY;
					}
					else	{
						pos.add(0, b & 0xff, 0);
						air.add(pos);
						isY = !isY;
						pos = new BlockPos(0, 0, 0);
					}
				}
				this.airBlocks.put(s, air);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		
		System.out.println("Writing to saved data");
		int[] coords = {spawn.getX(), spawn.getY(), spawn.getZ()};
		compound.setIntArray("spawn", coords);
		
		compound.setInteger("tunnelsAvailable", this.tunnelsAvailable);
		
		if (!this.airBlocks.keySet().isEmpty())	{
			for (String s : this.airBlocks.keySet())	{
				System.out.println(this.airBlocks.get(s).size());
				if (this.airBlocks.get(s).size() > 0)	{
					byte[] data = new byte[2*this.airBlocks.get(s).size() - 1];
					int index = 0;
					for (BlockPos pos : this.airBlocks.get(s))	{
						data[index] = (byte) ((byte) pos.getX() << 4 | (byte) pos.getZ());
						data[index + 1] = (byte) pos.getY();
						index += 2;
					}
					compound.setByteArray(s, data);
				}
			}
		}
		
		for (String s : this.toClear)	{
			compound.removeTag(s);
		}
		
		this.toClear = new ArrayList<>();
		return compound;
	}
	
	public static SaveData getData(World world)	{
		MapStorage s = world.getPerWorldStorage();
		SaveData instance = (SaveData) s.getOrLoadData(SaveData.class, name);
		if (instance == null)	{
			instance = new SaveData();
			s.setData(name, instance);
			instance.airBlocks = new ConcurrentHashMap<>(0);
			instance.toClear = new ArrayList<>(0);
		}
		return instance;
	}

}
