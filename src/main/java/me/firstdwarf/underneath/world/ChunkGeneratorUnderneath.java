package me.firstdwarf.underneath.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;

import me.firstdwarf.underneath.block.BlockMain;
import me.firstdwarf.underneath.core.Config;
import me.firstdwarf.underneath.utilities.Functions;
import me.firstdwarf.underneath.world.node.Node;
import me.firstdwarf.underneath.world.node.NodeGen;
import me.firstdwarf.underneath.world.node.TunnelGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//This is constructed when the world provider is used
public class ChunkGeneratorUnderneath implements IChunkGenerator {
 
    private World world;
    private Random random;

    /**
     * Constructor
     *
     * @param world The world
     * @param seed The world seed
     */
    public ChunkGeneratorUnderneath(World world, long seed) {
        this.world = world;
        this.random = new Random(seed);
    }

    //Called whenever a chunk is generated. The first chunk generated in an empty world is the chunk the player is in
    @Override
    public Chunk generateChunk(int x, int z) {
    	
    	//Sets the seed of the chunk based on the position of the chunk
        this.random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        
        //Temporary setting object copied into the chunk object just before returning it
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        
        //Position of the chunk to generate
        ChunkPos chunkPos = new ChunkPos(x, z);
        
        //Set the entire chunkPrimer to deep stone. Eventually this should be more cleverly populated with raw materials
        for (int i = 0; i <= 15; i++)	{
        	for (int j = 1; j <= 254; j++)	{
        		for (int k = 0; k <= 15; k++)	{
        			chunkPrimer.setBlockState(i, j, k, BlockMain.deepStone.getDefaultState());
        			
        			//Useful alternative to stone used for debug purposes- allows vision of nodes within range without lag
        			//chunkPrimer.setBlockState(i, j, k, Blocks.ICE.getDefaultState());
        		}
        	}
        }
        
        for (int i = 0; i <= 15; i++)	{
        	for (int k = 0; k <= 15; k++)	{
        		chunkPrimer.setBlockState(i, 0, k, Blocks.BEDROCK.getDefaultState());
        		chunkPrimer.setBlockState(i, 255, k, Blocks.BEDROCK.getDefaultState());
        	}
        }
        
        //Choose a random placement and orientation within the chunk for a node
        int nodeRotation = 90*random.nextInt(4);
        BlockPos nodeOrigin = new BlockPos(random.nextInt(12) + 2, random.nextInt(128) + 63, random.nextInt(12) + 2);
        
        //Load in the spawn location for this world
        SaveData data = SaveData.getData(world);
		BlockPos spawn = data.spawn;
		
		//If the spawn is about to be set, move the nodeOrigin to the relative position of the old world spawn. For default spawning
		if (spawn == null)	{
			nodeOrigin = Functions.worldCoordsToChunkCoords(world.getSpawnPoint());
		}
        
		//Keep track of which neighboring faces have available tunnels linked to them
		ArrayList<EnumFacing> facesLinked = new ArrayList<>();
		
        //Check all four neighboring chunks and move node to match the last found neighbor, linking to necessary north entrance
        if (world.isChunkGeneratedAt(x, z - 1))	{
        	
        	//Force chunk to load, placing tunnel information into a ConcurrentHashMap (see EventHandler)a
        	world.getChunkFromChunkCoords(x, z - 1);
        	
        	//Load tunnel information for matching
        	byte[] tunnelInfo = TunnelGen.chunkTunnelEndpoints.get(new ChunkPos(x, z - 1).toString());
        	
        	if ((tunnelInfo[0] & 0x04) != 0)	{
        		nodeRotation = 0;
        		nodeOrigin = new BlockPos(nodeOrigin.getX(), tunnelInfo[2] + random.nextInt(5) - 3, nodeOrigin.getZ());
        		facesLinked.add(EnumFacing.NORTH);
        	}
        }
        if (world.isChunkGeneratedAt(x - 1, z))	{
        	
        	//Force chunk to load, placing tunnel information into a ConcurrentHashMap (see EventHandler)
        	world.getChunkFromChunkCoords(x - 1, z);
        	
        	//Load tunnel information for matching
        	byte[] tunnelInfo = TunnelGen.chunkTunnelEndpoints.get(new ChunkPos(x - 1, z).toString());
        	
        	if ((tunnelInfo[0] & 0x01) != 0)	{
        		nodeRotation = 90;
        		nodeOrigin = new BlockPos(nodeOrigin.getX(), tunnelInfo[4] + random.nextInt(5) - 3, nodeOrigin.getZ());
        		facesLinked.add(EnumFacing.WEST);
        	}
        }
        if (world.isChunkGeneratedAt(x, z + 1))	{
        	
        	//Force chunk to load, placing tunnel information into a ConcurrentHashMap (see EventHandler)
        	world.getChunkFromChunkCoords(x, z + 1);
        	
        	//Load tunnel information for matching
        	byte[] tunnelInfo = TunnelGen.chunkTunnelEndpoints.get(new ChunkPos(x, z + 1).toString());
        	
        	if ((tunnelInfo[0] & 0x08) != 0)	{
        		nodeRotation = 180;
        		nodeOrigin = new BlockPos(nodeOrigin.getX(), tunnelInfo[1] + random.nextInt(5) - 3, nodeOrigin.getZ());
        		facesLinked.add(EnumFacing.SOUTH);
        	}
        }
        if (world.isChunkGeneratedAt(x + 1, z))	{
        	
        	//Force chunk to load, placing tunnel information into a ConcurrentHashMap (see EventHandler)
        	world.getChunkFromChunkCoords(x + 1, z);
        	
        	//Load tunnel information for matching
        	byte[] tunnelInfo = TunnelGen.chunkTunnelEndpoints.get(new ChunkPos(x + 1, z).toString());
        	
        	if ((tunnelInfo[0] & 0x01) != 0)	{
        		nodeRotation = 270;
        		nodeOrigin = new BlockPos(nodeOrigin.getX(), tunnelInfo[3] + random.nextInt(5) - 3, nodeOrigin.getZ());
        		facesLinked.add(EnumFacing.EAST);
        	}
        }
        
        //Select a node to spawn in the chunk, indicated by the node index
        int nodeIndex = NodeGen.selectNodes(world, random, chunkPos, nodeOrigin, nodeRotation, facesLinked);
        
        //All nodes extend the abstract class node
        Node node;
        
        /*
         * Check a ConcurrentHashMap of all loaded chunks containing their node index.
         * This chunk shouldn't have an entry yet (it's still being generated).
         * If it does, another chunk flagged it because its node needed more room.
         */
        if (NodeGen.chunkNodes.containsKey(chunkPos.toString()))	{
        	//Sets the node index to -2 if it has been flagged as -2 (the space needed flag)
        	if (NodeGen.chunkNodes.get(chunkPos.toString()) == -2)	{
        		node = null;
        		nodeIndex = -2;
        	}
        	//Catch-all for any future node index preset conventions
        	else	{
        		node = null;
        	}
        }
        
        /*
         * Now that this node is not forced to be empty for space, it can implement its selection.
         * A node index of -1 indicates that the chunk has been chosen to be empty.
         */
        else if (nodeIndex != -1)	{
        	
        	//Retrieve the selected node type from an ArrayList of all node types
        	node = NodeGen.nodeTypes.get(nodeIndex);
        }
        
        //Catch-all for any future node index preset conventions. Potentially should be inverted to check conventions then set node
        else	{
        	node = null;
        }
        
        //Flag any node neighbors that must be empty to provide extra room. This uses a method from the abstract class Node
        if (node != null)	{
        	node.flagNeighbors(chunkPos, nodeOrigin, nodeRotation);
        }
        
        //Place node selection and placement information in a ConcurrentHashMap
        NodeGen.chunkNodes.put(chunkPos.toString(), nodeIndex);
        NodeGen.chunkNodes.put(chunkPos.toString() + ".rotation", nodeRotation);
        NodeGen.chunkNodes.put(chunkPos.toString() + ".origin.x", nodeOrigin.getX());
        NodeGen.chunkNodes.put(chunkPos.toString() + ".origin.y", nodeOrigin.getY());
        NodeGen.chunkNodes.put(chunkPos.toString() + ".origin.z", nodeOrigin.getZ());
        
        //Chooses where the tunnel endpoints should be for this chunk based on node and neighbor information
        chunkPrimer = TunnelGen.generateTunnelEndpoints(random, world, chunkPrimer, x, z, node, nodeOrigin, nodeRotation, nodeIndex);
        
        //Links all tunnel endpoints as needed, generating caves- node entrances to chunk sides, chunk sides to common midpoint, etc
    	chunkPrimer = TunnelGen.generateTunnelLinks(random, world, chunkPrimer, x, z, node, nodeOrigin, nodeRotation, nodeIndex);
    	
    	//Copies chunkPrimer into chunk object and returns it
        Chunk chunk = new Chunk(this.world, chunkPrimer, x, z);
        return chunk;
    }

    //Called when a chunk is populated. Apparently called in groups of four and rarely happens right after generating the same chunk
    @Override
    public void populate(int x, int z) {
    	
    	//All nodes extend the abstract class Node
    	Node node;
    	
    	//Position of the chunk to populate
    	ChunkPos chunkPos = new ChunkPos(x, z);
    	
    	/*
    	 * Retrieve node placement information from a ConcurrentHashMap.
    	 * Populate is only called for loaded chunks, so the map has entries.
    	 * (See EventHandler)
    	 */
    	int nodeIndex = NodeGen.chunkNodes.get(chunkPos.toString());
    	int nodeRotation = NodeGen.chunkNodes.get(chunkPos.toString() + ".rotation");
    	BlockPos nodeOrigin = new BlockPos(NodeGen.chunkNodes.get(chunkPos.toString() + ".origin.x"),
    			NodeGen.chunkNodes.get(chunkPos.toString() + ".origin.y"),
    			NodeGen.chunkNodes.get(chunkPos.toString() + ".origin.z"));
    	
    	//Retrieve node object from an ArrayList of nodes if one was selected
    	if (nodeIndex >= 0)	{
    		node = NodeGen.nodeTypes.get(nodeIndex);
    	}
    	else	{
    		node = null;
    	}
    	
    	//Generate the node in question, placing all blocks in the node, clearing out a cave, etc
    	NodeGen.generateNodes(world, random, chunkPos, node, nodeOrigin, nodeRotation);
    	
    	//Resets terrain for debug mode
    	if (Config.debugMode)	{
    		for (int i = 0; i <= 15; i++)	{
            	for (int j = 1; j <= 254; j++)	{
            		for (int k = 0; k <= 15; k++)	{
            			Chunk c = world.getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
            			IBlockState state = c.getBlockState(i, j, k);
            			if (state.equals(Blocks.AIR.getDefaultState()))	{
            				c.setBlockState(new BlockPos(i, j, k), Blocks.REDSTONE_ORE.getDefaultState());
            			}
            			else	{
            				c.setBlockState(new BlockPos(i, j, k), Blocks.AIR.getDefaultState());
            			}
            			
            			//Useful alternative to stone used for debug purposes- allows vision of nodes within range without lag
            			//chunkPrimer.setBlockState(i, j, k, Blocks.ICE.getDefaultState());
            		}
            	}
            }
    	}
    	world.getChunkFromChunkCoords(x, z).resetRelightChecks();
    }

    //Our dimensions won't use vanilla structures
    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    //Our dimensions currently have no biomes or possible creatures
    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return new ArrayList<Biome.SpawnListEntry>();
    }

    //Our dimensions don't use vanilla structure techniques
    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return new BlockPos(0, 0, 0);
    }

    //Our dimensions don't use vanilla structure techniques
    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    //Our dimensions don't use vanilla structure techniques
    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }
}
