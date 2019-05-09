package me.firstdwarf.underneath.world.dimension;

import java.util.ArrayList;

import me.firstdwarf.underneath.world.node.Node;
import me.firstdwarf.underneath.world.node.NodeGen;
import net.minecraft.util.math.BlockPos;

public class GraphNode {
	
	private GraphNode[] children;
	private int generation;
	private Node node;
	private BlockPos[] bounds;
	private GraphNode parent;
	public GraphNode(Node node, int generation, GraphNode parent)	{
		this.node = node;
		this.generation = generation;
		this.bounds = node.getBounds();
		this.parent = parent;
		if (node.getEntrances().size() > 1)	{
			this.children = new GraphNode[node.getEntrances().size() - 1];
		}
		else	{
			this.children = null;
		}
	}
	
	public void selectChildren()	{
		Node childNode;
		for (int i = 0; i < children.length; i++)	{
			childNode = NodeGen.selectChild();
			children[i] = new GraphNode(childNode, generation + 1, this);
		}
	}
	
	public int getChildCount()	{
		return this.children != null ? this.children.length : 0;
	}
	
	public GraphNode[] getChildren()	{
		return this.children;
	}
}
