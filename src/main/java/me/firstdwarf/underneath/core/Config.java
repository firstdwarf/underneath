package me.firstdwarf.underneath.core;

import net.minecraftforge.common.config.Configuration;

public class Config {

	private static final String tunnelDirection = "Generation: Tunnel Branching";
	private static final String tunnelSize = "Generation: Tunnel Size";
	private static final String caveSize = "Generation: Cave Size";
	private static final String fluidSize = "Generation: Fluid Size";
	private static final String weights = "Generation: Node Selection";
	private static final String general = "General Options";
	
	public static boolean debugMode = false;
	
	public static int incline = 8;
	
	public static int blankWeight = 60;
	public static int spawnWeight = 10;
	public static int shaftWeight = 10;
	public static int poolCaveWeight = 4;
	
	public static int tunnelBranchRemovalOdds = 80;
	public static int tunnelBranchRemovalCount = 3;
	
	public static int tunnelAirWeight = 60;
	public static int tunnelCellStageCount = 5;
	public static int tunnelRange = 1;
	public static int tunnelCellAirRule = 10;
	
	public static int fluidWeight = 70;
	public static int fluidCellStageCount = 5;
	public static int fluidCellRule = 14;
	
	public static int caveAirWeight = 70;
	public static int caveCellStageCount = 5;
	public static int caveCellAirRule = 14;
	
	public static void loadConfig()	{
		Configuration config = CommonProxy.config;
		config.load();
		
		config.addCustomCategoryComment(weights, "The relative probability of each tunnel feature, or node, being spawned. " +
				"Each weight can be tweaked independently, increasing its odds of being chosen relative to the others");
		poolCaveWeight = config.getInt("poolCaveWeight", weights, poolCaveWeight, 0, 100,
				"Weight of the cave node with a deep pool");
		shaftWeight = config.getInt("shaftWeight", weights, shaftWeight, 0, 100,
				"Weight of the large vertical shaft node");
		spawnWeight = config.getInt("spawnWeight", weights, spawnWeight, 0, 100,
				"Weight relative to spawn node variants- only one is chosen for each dimension");
		blankWeight = config.getInt("blankWeight", weights, blankWeight, 10, 10000,
				"Weight of no node being selected- this is recommended to be much, much higher than the sum of other weights. " +
				"The default value is three times the sum of every other weight, leaving a 75% chance for no node to be selected");
		
		config.addCustomCategoryComment(tunnelDirection, "How many branches tunnel networks generate");
		tunnelBranchRemovalCount = config.getInt("tunnelBranchRemovalCount", tunnelDirection, tunnelBranchRemovalCount, 1, 10,
				"The number of times to try to remove an extra branch");
		tunnelBranchRemovalOdds = config.getInt("tunnelBranchRemovalOdds", tunnelDirection, tunnelBranchRemovalOdds, 0, 100,
				"The percentage chance to prevent an extra tunnel from spawning");
		
		config.addCustomCategoryComment(tunnelSize, "How large tunnels generate. This uses a 'cellular automaton' algorithm. " +
				"Do not change these parameters without researching how this works!");
		tunnelAirWeight = config.getInt("tunnelAirWeight", tunnelSize, tunnelAirWeight, 0, 100,
				"Percentage chance for a cell to be selected as 'air' in the starting map- around the original tunnel");
		tunnelCellStageCount = config.getInt("tunnelCellStageCount", tunnelSize, tunnelCellStageCount, 1, 10,
				"Number of evolution stages for the cellular automaton");
		tunnelRange = config.getInt("tunnelRange", tunnelSize, tunnelRange, 0, 10,
				"The width a tunnel CAN generate up to- excluding the guaranteed path through");
		tunnelCellAirRule = config.getInt("tunnelCellAirRule", tunnelSize, tunnelCellAirRule, 0, 26,
				"The number of neighbor blocks that must be air to convert a cell position to air");
		
		config.addCustomCategoryComment(caveSize, "How large caves around nodes generate. This uses a 'cellular automaton' " +
				"algorithm. Do not change these parameters without researching how this works!");
		caveAirWeight = config.getInt("caveAirWeight", caveSize, caveAirWeight, 0, 100,
				"Percentage chance for a cell to be selected as 'air' in the starting map- around the original node");
		caveCellStageCount = config.getInt("caveCellStageCount", caveSize, caveCellStageCount, 1, 10,
				"The number of evolutions for the cave cell map to undergo");
		caveCellAirRule = config.getInt("caveCellAirRule", caveSize, caveCellAirRule, 0, 26,
				"The number of neighbor blocks that must be air to convert a cell position to air");
		
		config.addCustomCategoryComment(fluidSize, "How much fluid space generates around fluids. This uses a 'cellular automaton' " + 
				"algorithm. Do not change these parameters without researching how this works!");
		fluidWeight = config.getInt("fluidWeight", fluidSize, fluidWeight, 0, 100,
				"Percentage chance for a cell to be selected as 'fluid' in the starting map- around the original fluids");
		fluidCellStageCount = config.getInt("caveCellStageCount", fluidSize, fluidCellStageCount, 1, 10,
				"The number of evolutions for the fluid cell map to undergo");
		fluidCellRule = config.getInt("caveCellAirRule", fluidSize, fluidCellRule, 0, 26,
				"The number of neighbor blocks that must be fluid to convert a cell position to fluid");
		
		config.addCustomCategoryComment(general, "Assorted settings");
		debugMode = config.getBoolean("debugMode", general, false,
				"This modifies world generation to reveal the underlying structure");
		incline = config.getInt("incline", general, incline, 1, 10,
				"This is about how many blocks per chunk the tunnel network descends");
	}
}
