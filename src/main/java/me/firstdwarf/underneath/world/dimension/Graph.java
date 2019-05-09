package me.firstdwarf.underneath.world.dimension;

import java.util.ArrayList;

import me.firstdwarf.underneath.world.node.NodeGen;
import net.minecraft.world.World;

public class Graph {
	
	private int remainingConnections = 0;
	private int age = 0;
	private ArrayList<GraphNode[]> graphData = new ArrayList<>(1);
	
	public Graph(World world)	{
		GraphNode spawn = new GraphNode(NodeGen.nodeTypes.get(0), 0, null);
		this.remainingConnections += spawn.getChildCount();
		this.graphData.add(new GraphNode[] {spawn});
	}
	
	public void evolve()	{
		GraphNode[] nextGeneration = new GraphNode[this.remainingConnections];
		int index = 0;
		for (GraphNode g : this.graphData.get(age))	{
			g.selectChildren();
			if (g.getChildren() != null)	{
				for (GraphNode child : g.getChildren())	{
					this.remainingConnections += child.getChildCount();
					nextGeneration[index] = child;
					index++;
				}
			}
			this.remainingConnections -= g.getChildCount();
		}
		this.graphData.add(nextGeneration);
		this.age++;
	}
}
