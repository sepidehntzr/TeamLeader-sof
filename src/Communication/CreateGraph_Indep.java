package Communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;

import Common.User;
import Technical.CreateUserANDCountSA;


public class CreateGraph_Indep {

	//String dataSetName = "Java";
	double team_project_size = 5;
	
	Map<Integer, HashMap<Integer, Double>> Tech_Graph = new HashMap<Integer, HashMap<Integer, Double>>();
	
	Map<Integer, HashMap<Integer, Double>> Mng_Graph = new HashMap<Integer, HashMap<Integer, Double>>();
	
	Map<Integer, HashMap<String, Double>> User_SACount;
	Map<String, Double> SA_MaxDoc;
	Map<Integer, HashMap<String, Double>> askeruser_SACount;
	Map<String, HashMap<Integer,Double>> sauser ;
	
	//Map<Integer, HashMap<Integer, Double>> finalgraph ;
	Map<Integer, HashMap<Integer, Double>> shortestPath_twopairnode = new HashMap<Integer, HashMap<Integer, Double>>();
	Map<Integer, HashMap<Integer, List<Integer>>> shortestPath_twopairnode_tree ;
	
	Map<Integer, HashMap<Integer, Double>> longestPath_twopairnode ;
	Map<Integer, HashMap<Integer, Double>> shortestPath_twopairnode_notmodified = new HashMap<Integer, HashMap<Integer, Double>>();
	
	 DirectedGraph<Integer> finalgraph = new  DirectedGraph<Integer>();
	
	Map<Integer, Map<Integer, HashMap<Integer,Double>>> finalshortestPath_twopairnode;
	Map<Integer, Map<Integer, HashMap<Integer,Double>>> finallongestPath_twopairnode;
	Map<Integer, Map<Integer, HashMap<Integer,Double>>> finalshortestPath_twopairnode_notmodified;
	
	Map<String, User> Technical_goldset;
	
	List<Integer> user_list ;
	
	double min_distance = Double.MAX_VALUE;
	double max_distance = 0;
	
	double graph_threshold = 0.95;
	double max_value = team_project_size * 10;
	int max_EdgeScore = 1;
	//double threshold;
	
	public static void main(String[] args) throws IOException, ParseException 
	{
		CreateGraph_Indep m = new CreateGraph_Indep();
		CreateUserANDCountSA user_countSA_obj = new CreateUserANDCountSA(Path.FilePaths.getFilePaths(m.dataSetName, "Clustering"),
				Path.FilePaths.getFilePaths(m.dataSetName, "IndexQuestions"), Path.FilePaths.getFilePaths(m.dataSetName, "IndexAnswers"));
	
	   // m.askeruser_SACount = user_countSA_obj.getAskeruser_SACount();
		
		m.User_SACount = user_countSA_obj.getUsersa();
		//SA_MaxDoc = user_countSA_obj.getSA_Maxuserdoc_normalized();
		//m.SA_MaxDoc = user_countSA_obj.getSA_Maxuserdoc();
		m.sauser = user_countSA_obj.getSauser_normalized();
		//sauser = user_countSA_obj.getSauser();
		
		UserSampling usersample = new UserSampling(user_countSA_obj.getUsersa(), m.dataSetName, (int)m.team_project_size, 1000);
		
		m.user_list = usersample.getUser_list_sample();
		//m.generateGraphANDShortestPathes(user_countSA_obj.getUsersa(), user_countSA_obj.getDataset_Skills());
		m.generateGraphANDShortestPathes(m.user_list, user_countSA_obj.getDataset_Skills());
	}
	
	public CreateGraph_Indep() {
		// TODO Auto-generated constructor stub
	}
	
	public CreateGraph_Indep(CreateUserANDCountSA user_countSA_obj, ArrayList<String> sAs_Tech, double max_weight, double threshold_value, int max_edgescore, List<Integer> user_list, Map<String, User> Technical_goldsett) throws IOException, ParseException
	{
		
		graph_threshold = threshold_value;
		max_value = max_weight;
		max_EdgeScore = max_edgescore;
		Technical_goldset = Technical_goldsett;
		
		User_SACount = user_countSA_obj.getUsersa_Highlevel();
		sauser = user_countSA_obj.getSauser_normalized_Highlevel();
		
		generateGraphANDShortestPathes(user_list, sAs_Tech);
	}
	
	
	public Map<Integer, HashMap<Integer, Double>> generateGraphANDShortestPathes (List<Integer> user_list, List<String> skills)
	{
		
		//finalgraph = new HashMap<Integer, HashMap<Integer, Double>>();
		finalshortestPath_twopairnode = new HashMap<Integer, Map<Integer, HashMap<Integer,Double>>>();
		
		
        ///////////////////////////////////////////////// generate final graph
		ArrayList<Integer> user_list_prim = (ArrayList)user_list;
		user_list_prim = (ArrayList<Integer>) user_list_prim.clone();
		for(Integer users: user_list_prim)
		     generateGraph(user_list, skills, users, User_SACount.get(users));
		
		
        ///////////////////////////////////////////////// generate ShortestPaths
		shortestPath_twopairnode = generateShortestPaths(finalgraph.getmGraph());
	
		
		return finalgraph.getmGraph();
	}//
	
	private Map<Integer, HashMap<Integer, Double>> Modify(Map<Integer, HashMap<Integer, Double>> shortestPath_twopairnode) 
	
	{
		Map<Integer, HashMap<Integer, Double>> subgraph_prim = new HashMap<Integer, HashMap<Integer, Double>>();
		for(Entry<Integer, HashMap<Integer, Double>> entry : shortestPath_twopairnode.entrySet())
			for(Entry<Integer, Double> entry2 : entry.getValue().entrySet())
			{
				if(subgraph_prim.containsKey(entry.getKey()))
				{
					
					double value = entry2.getValue();
					if(value != graph_threshold)
					{
						HashMap<Integer, Double> temp =subgraph_prim.get(entry.getKey());
						temp.put(entry2.getKey(), value);
						subgraph_prim.replace(entry.getKey(), temp);
					}
					else
					{
						HashMap<Integer, Double> temp =subgraph_prim.get(entry.getKey());
						temp.put(entry2.getKey(), max_value);
						subgraph_prim.replace(entry.getKey(), temp);
					}
					
				}
				else
				{
					
					double value = entry2.getValue();
					if(value != graph_threshold)
					{
						HashMap<Integer, Double> temp = new HashMap<Integer, Double >();
						temp.put(entry2.getKey(), value);
						subgraph_prim.put(entry.getKey(), temp);
					}
					else
					{
						HashMap<Integer, Double> temp = new HashMap<Integer, Double >();
						temp.put(entry2.getKey(), max_value);
						subgraph_prim.put(entry.getKey(), temp);
					}
					
				}
				
			}
		
		return subgraph_prim;
	}

	private Map<Integer, HashMap<Integer, Double>> removeZeroEdge(Map<Integer, HashMap<Integer, Double>> subgraph) 
	{
		Map<Integer, HashMap<Integer, Double>> subgraph_prim = new HashMap<Integer, HashMap<Integer, Double>>();
		for(Entry<Integer, HashMap<Integer, Double>> entry : subgraph.entrySet())
			for(Entry<Integer, Double> entry2 : entry.getValue().entrySet())
			{
				if(subgraph_prim.containsKey(entry.getKey()))
				{
					
					double value = entry2.getValue();
					if(value < 0.95)
					{
						HashMap<Integer, Double> temp =subgraph_prim.get(entry.getKey());
						temp.put(entry2.getKey(), value);
						subgraph_prim.replace(entry.getKey(), temp);
					}
					else
					{
						HashMap<Integer, Double> temp =subgraph_prim.get(entry.getKey());
						temp.put(entry2.getKey(), graph_threshold);
						subgraph_prim.replace(entry.getKey(), temp);
					}
				}
				else
				{
					
					double value = entry2.getValue();
					if(value < 0.95)
					{
						HashMap<Integer, Double> temp = new HashMap<Integer, Double >();
						temp.put(entry2.getKey(), value);
						subgraph_prim.put(entry.getKey(), temp);
					}
					else
					{
						HashMap<Integer, Double> temp = new HashMap<Integer, Double >();
						temp.put(entry2.getKey(), graph_threshold);
						subgraph_prim.put(entry.getKey(), temp);
					}
					
				}
				
			}
		
		return subgraph_prim;
	}

	private Map<Integer, HashMap<Integer, Double>> changeCost(Map<Integer, HashMap<Integer, Double>> subgraph) 
	{
		Map<Integer, HashMap<Integer, Double>> subgraph_prim = new HashMap<Integer, HashMap<Integer, Double>>();
		for(Entry<Integer, HashMap<Integer, Double>> entry : subgraph.entrySet())
			for(Entry<Integer, Double> entry2 : entry.getValue().entrySet())
			{
				if(subgraph_prim.containsKey(entry.getKey()))
				{
					HashMap<Integer, Double> temp =subgraph_prim.get(entry.getKey());
					temp.put(entry2.getKey(), 1-entry2.getValue());
					subgraph_prim.replace(entry.getKey(), temp);
				}
				else
				{
					HashMap<Integer, Double> temp = new HashMap<Integer, Double >();
					temp.put(entry2.getKey(), 1-entry2.getValue());
					subgraph_prim.put(entry.getKey(), temp);
				}
				
			}
		
		return subgraph_prim;
	}

	private void generateGraph(List<Integer> user_list, List<String> skills, int uid, HashMap<String, Double> userSAs)
	{
		
		for(Integer uteam : user_list)
		{
			
			{
				HashMap<String, Double> uteamSAs = User_SACount.get(uteam);
				if(uid != uteam)
				{
					
					
					double sum_max = 0;
					double sum_min = 0;
					for(Entry<String, Double> skill : uteamSAs.entrySet())
					//for(String proj_SA : skills)
					{
						String SA = skill.getKey();
						double x = 0 ,y=0;
						if(uteamSAs.containsKey(SA))
							x = sauser.get(SA).get(uteam);
						if(userSAs.containsKey(SA))
							y = sauser.get(SA).get(uid);
						
						
								//jaccard
								
								
								if(x<y)
									sum_min += x;
								else
									sum_min += y;
								
								
								if(x>y)
									sum_max += x;
								else
									sum_max += y;
			
					}//for
					int common_SA = 0;
					int count_skills = 0;
					for(Entry<String, Double> skill : uteamSAs.entrySet())
					{
						String HSkill = skill.getKey();
						HSkill = HSkill.replace("|", ",");
						String[] SAs = HSkill.split(",");
						for(String sa : SAs)
						{
							if(Technical_goldset.containsKey(uteam+"") && Technical_goldset.containsKey(uid+""))
							{
								if(Technical_goldset.get(uteam+"").getSa_adv() != null && Technical_goldset.get(uteam+"").getSa_adv().contains(sa))
								{	
									if(Technical_goldset.get(uteam+"").getSa_adv() != null && Technical_goldset.get(uteam+"").getSa_adv().contains(sa))
										common_SA++;
									else if(Technical_goldset.get(uteam+"").getSa_int() != null && Technical_goldset.get(uteam+"").getSa_int().contains(sa))
										common_SA++; 			
								}
								else if(Technical_goldset.get(uteam+"").getSa_int() != null && Technical_goldset.get(uteam+"").getSa_int().contains(sa))
								{	
									if(Technical_goldset.get(uteam+"").getSa_adv() != null && Technical_goldset.get(uteam+"").getSa_adv().contains(sa))
										common_SA++;
									else if(Technical_goldset.get(uteam+"").getSa_int() != null && Technical_goldset.get(uteam+"").getSa_int().contains(sa))
										common_SA++; 			
								}
							}//if
							count_skills ++;
						}//for
						
					}//for
					
					
					double jaccard = sum_min/sum_max;//////////////////////////////////?
					if (Double.isNaN(jaccard)|| Double.isInfinite(jaccard))
						jaccard = 0;
					
					if(jaccard != 0)
						System.out.print("");

					double score = jaccard ;
					if(max_EdgeScore != 1)
					    score += common_SA; //or +
					double cost = max_EdgeScore - score;
					
					//set threshold to score
					if(cost >= graph_threshold)
						cost = max_value;
						
					finalgraph.addNode(uid);
					finalgraph.addNode(uteam);
					finalgraph.addEdge(uid, uteam, cost);
					finalgraph.addEdge(uteam, uid, cost);
					
					
					
				}//if
			}
				
			}//for
		
		user_list.remove((Integer)uid);
	}
	
	private Map<Integer, HashMap<Integer, Double>> generateShortestPaths(Map<Integer, HashMap<Integer, Double>> subgraph) 
	{
		
		new Dijkstra();
		Map<Integer, HashMap<Integer, Double>> Distance = new HashMap<Integer, HashMap<Integer,Double>>();
		Map<Integer, HashMap<Integer, List<Integer>>> Pathes = new HashMap<Integer, HashMap<Integer,List<Integer>>>();
		for (Entry<Integer, HashMap<Integer, Double>> entry : subgraph.entrySet()) //users	
	    {
			        
			//if(entry.getKey() == 625424)
	        	//System.out.print("");
			Distance.put(entry.getKey(), (HashMap<Integer, Double>) Dijkstra.shortestPaths(finalgraph, entry.getKey(), max_value));
	        Pathes.put(entry.getKey(),  (HashMap<Integer, List<Integer>>) Dijkstra.getPaths());
	       // if(entry.getKey() == 625424)
	         //  System.out.print(Dijkstra.getPaths().get(163186));
	        if(Dijkstra.getMin_distance() < min_distance)
        		min_distance = Dijkstra.getMin_distance() ; // update
        	if(Dijkstra.getMax_distance() > max_distance)
        		max_distance = Dijkstra.getMax_distance(); // update
	    }
		shortestPath_twopairnode_tree = Pathes;
		//System.out.print(Pathes.get(625424).get(163186));
		return Distance;
	}

	
	

	
	public Map<Integer, HashMap<Integer, Double>> getFinalgraph() {
		return finalgraph.getmGraph();
	}

	
	public Map<Integer, HashMap<Integer, Double>> getShortestPath_twopairnode() {
		return shortestPath_twopairnode;
	}

	public void setShortestPath_twopairnode(Map<Integer, HashMap<Integer, Double>> shortestPath_twopairnode) {
		this.shortestPath_twopairnode = shortestPath_twopairnode;
	}

	public Map<Integer, HashMap<Integer, List<Integer>>> getShortestPath_twopairnode_tree() {
		return shortestPath_twopairnode_tree;
	}

	public void setShortestPath_twopairnode_tree(
			Map<Integer, HashMap<Integer, List<Integer>>> shortestPath_twopairnode_tree) {
		this.shortestPath_twopairnode_tree = shortestPath_twopairnode_tree;
	}

	public double getMin_distance() {
		return min_distance;
	}

	

	public double getMax_distance() {
		return max_distance;
	}

	
	
	
}
