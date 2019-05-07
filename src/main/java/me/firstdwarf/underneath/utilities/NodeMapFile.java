package me.firstdwarf.underneath.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import me.firstdwarf.underneath.core.Underneath;
import me.firstdwarf.underneath.world.node.Node;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class NodeMapFile {
	
	private String filename = "nodemap.txt";
	private File file;
	//TODO: Implement record of node placements...
	public NodeMapFile(World world)	{
		this.file = new File(DimensionManager.getCurrentSaveRootDirectory().getPath() + File.separator
				+ world.provider.getSaveFolder(), filename);
		this.file.setWritable(true);
		if (!file.exists())	{
			try {
				this.file.createNewFile();
			} catch (IOException e) {
				Underneath.logger.error("Failed to create file " + filename);
				e.printStackTrace();
			}
		}
	}
	
	public void writeNode(Node node, BlockPos nodeOrigin, ChunkPos chunkPos)	{
		
		String nodeData = "Creating node type " + node.getName() + " in chunk " + chunkPos.toString() + " with origin at block "
				+ nodeOrigin.toString() + System.lineSeparator();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.file, true));
//			System.out.println("Writing: " + nodeData);
			writer.write(nodeData);
			writer.close();
		} catch (IOException e) {
			Underneath.logger.error("Failed to record node map data");
			e.printStackTrace();
		}
	}
}
