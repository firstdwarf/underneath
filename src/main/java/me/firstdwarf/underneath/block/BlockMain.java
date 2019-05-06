package me.firstdwarf.underneath.block;

import java.util.ArrayList;

import me.firstdwarf.underneath.tile.TileMagmaticStone;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockMain {
	
	/**
	 * Create one ArrayList for each type of block extension
	 * NaturalBlock: world generation blocks. Will add control of hardness, mining, etc
	 * OreBlock: Resource-granting blocks potentially added by other mods; registered to the ore dictionary
	 */
	public static ArrayList<NaturalBlock> naturalBlockList = new ArrayList<>(0);
	public static ArrayList<OreBlock> oreBlockList = new ArrayList<>(0);
	public static ArrayList<TileBlock> tileBlockList = new ArrayList<>(0);
	
	/**
	 * To fully add a block, declare its field and type here, inserting an ObjectHolder annotation in lower case
	 * Then add a call to the setup method in the createBlocks() method below
	 * Finally, add blockstate, model, and texture JSON files patterned after the examples in the resources
	 */
	@GameRegistry.ObjectHolder("underneath:exampleblock")
	public static NaturalBlock exampleBlock;
	@GameRegistry.ObjectHolder("underneath:deepstone")
	public static NaturalBlock deepStone;
	
	@GameRegistry.ObjectHolder("underneath:magmaticstone")
	public static TileBlock magmaticStone;
	
	@GameRegistry.ObjectHolder("underneath:orecopper")
	public static OreBlock oreCopper;
	
	//TODO: Add default information or vanilla comparisons
	/**
	 * Call the setup method to initialize every new block added
	 * Current syntax: field, name of field (camelCase), hardness, blast resistance, light opacity, required tool type
	 */
	public static void createBlocks()	{
		setup(magmaticStone, TileMagmaticStone.class, "magmaticStone", 20f, 20f, 255, "pickaxe", true);
		setup(deepStone, "deepStone", 20f, 20f, 255, "pickaxe");
		setup(exampleBlock, "exampleBlock", 20f, 20F, 255, "pickaxe");
		setup(oreCopper, "oreCopper", 20f, 20f, 255, "pickaxe");
	}
	static void setup(NaturalBlock block, String name, float hardness, float resistance, int opacity, String toolType)	{
		block = new NaturalBlock(name, hardness, resistance, opacity, toolType);
		naturalBlockList.add(block);
	}
	static void setup(OreBlock block, String name, float hardness, float resistance, int opacity, String toolType)	{
		block = new OreBlock(name, hardness, resistance, opacity, toolType);
		oreBlockList.add(block);
	}
	static void setup(TileBlock block, Class<? extends TileEntity> tileEntityClass,
			String name, float hardness, float resistance, int opacity, String toolType, boolean hasModel)	{
		block = new TileBlock(name, tileEntityClass, hardness, resistance, opacity, toolType, hasModel);
		tileBlockList.add(block);
	}
}