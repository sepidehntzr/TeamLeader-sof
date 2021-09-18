package Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamProjcetObj 
{
	private List<User> team;
	private ArrayList<String> project;
	
	//for communication
	Map<Integer, HashMap<Integer, Double>> graph;
	Map<Integer, Map<Integer, HashMap<Integer,Double>>> shortestPath_twopairnode;
	Map<Integer, Map<Integer, HashMap<Integer,Double>>> shortestPath_twopairnode_notmodified;
	Map<Integer, Map<Integer, HashMap<Integer,Double>>> longestPath_twopairnode;
	
	private double mrrgain;
	public double getMrrgain() {
		return mrrgain;
	}
	public void setMrrgain(double mrrgain) {
		this.mrrgain = mrrgain;
	}
	public List<User> getTeam() {
		return team;
	}
	public void setTeam(List<User> team) {
		this.team = team;
	}
	public ArrayList<String> getProject() {
		return project;
	}
	public void setProject(ArrayList<String> project) {
		this.project = project;
	}
	public Map<Integer, HashMap<Integer, Double>> getGraph() {
		return graph;
	}
	public void setGraph(Map<Integer, HashMap<Integer, Double>> graph) {
		this.graph = graph;
	}
	public Map<Integer, Map<Integer, HashMap<Integer, Double>>> getShortestPath_twopairnode() {
		return shortestPath_twopairnode;
	}
	public void setShortestPath_twopairnode(Map<Integer, Map<Integer, HashMap<Integer, Double>>> shortestPath_twopairnode) {
		this.shortestPath_twopairnode = shortestPath_twopairnode;
	}
	public Map<Integer, Map<Integer, HashMap<Integer, Double>>> getLongestPath_twopairnode() {
		return longestPath_twopairnode;
	}
	public void setLongestPath_twopairnode(Map<Integer, Map<Integer, HashMap<Integer, Double>>> longestPath_twopairnode) {
		this.longestPath_twopairnode = longestPath_twopairnode;
	}
	public Map<Integer, Map<Integer, HashMap<Integer, Double>>> getShortestPath_twopairnode_notmodified() {
		return shortestPath_twopairnode_notmodified;
	}
	public void setShortestPath_twopairnode_notmodified(
			Map<Integer, Map<Integer, HashMap<Integer, Double>>> shortestPath_twopairnode_notmodified) {
		this.shortestPath_twopairnode_notmodified = shortestPath_twopairnode_notmodified;
	}
	
	
	
}
