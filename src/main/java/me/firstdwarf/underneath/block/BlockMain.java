package me.firstdwarf.underneath.block;

import java.util.ArrayList;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockMain {
	
	/**
	 * Create one ArrayList for each type of block extension
	 * NaturalBlock: world generation blocks. Will add control of hardness, mining, etc
	 * OreBlock: Resource-granting blocks potentially added by other mods; registered to the ore dictionary
	 */
	public static ArrayList<NaturalBlock> naturalBlockList = new ArrayList<>(0);
	public static ArrayList<OreBlock> oreBlockList = new ArrayList<>(0);
	
	/**
	 * To fully add a block, declare its field and type here, inserting an ObjectHolder annotation in lower case
	 * Then add a call to the setup method in the createBlocks() method below
	 * Finally, add blockstate, model, and texture JSON files patterned after the examples in the resources
	 */
	@GameRegistry.ObjectHolder("underneath:exampleblock")
	public static NaturalBlock exampleBlock;
	
	/**
	 * Call the setup method to initialize every new block added
	 * Current syntax: field, name of field
	 */
	public static void createBlocks()	{
		setup(exampleBlock, "exampleBlock");
	}
	public static void setup(NaturalBlock block, String name)	{
		block = new NaturalBlock(name);
		naturalBlockList.add(block);
	}
	public static void setup(OreBlock block, String name)	{
		block = new OreBlock();
		oreBlockList.add(block);
	}
}