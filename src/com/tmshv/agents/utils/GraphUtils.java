package com.tmshv.agents.utils;

import pathfinder.*;

public class GraphUtils{
	public static String bake(Graph graph){
		GraphNode[] nodes = graph.getNodeArray();
		GraphEdge[] edges = graph.getAllEdgeArray();

		String text = "";

		for(GraphNode node : nodes){
			int id = node.id();
			float x = node.xf();
			float y = node.yf();
			String t = "graph.addNode(new GraphNode(" +id+ ", " +x+ ", " +y+ "));\n";
			text += t;
		}

		for(GraphEdge e : edges){
			int fi = e.from().id();
			int ti = e.to().id();
			double w = e.getCost();
			String t = "graph.addEdge(" +fi+ ", " +ti+ ", " +w+ ");\n";
			text += t;
		}

		return text;
	}
}