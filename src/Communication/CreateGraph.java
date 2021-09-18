package Communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.queryparser.classic.ParseException;

import Common.User;
import Technical.CreateUserANDCountSA;
import Technical.userAnswererGraph;

public class CreateGraph {

	double numberOfTeamMembers = 5;
	
	Map<Integer, HashMap<Integer, Double>> Tech_Graph = new HashMap<Integer, HashMap<Integer, Double>>();
	
	Map<Integer, HashMap<Integer, Double>> Mng_Graph = new HashMap<Integer, HashMap<Integer, Double>>();
	
	Map<Integer, HashMap<String, Double>> User_SACount;
	Map<Integer, HashMap<Integer, HashMap<String, Double>>> Intimacy_Graph = new HashMap<Integer, HashMap<Integer, HashMap<String, Double>>>();
	Map<String, Double> SA_MaxDoc;
	Map<Integer, HashMap<String, Double>> askeruser_SACount;
	Map<String, HashMap<Integer,Double>> sauser ;
	
	Map<Integer, HashMap<Integer, Double>> finalgraph ;
	Map<Integer, HashMap<Integer, Double>> shortestPath_twopairnode = new HashMap<Integer, HashMap<Integer, Double>>();
	Map<Integer, HashMap<Integer, Double>> longestPath_twopairnode ;
	Map<Integer, HashMap<Integer, Double>> shortestPath_twopairnode_notmodified = new HashMap<Integer, HashMap<Integer, Double>>();

	Map<Integer, Map<Integer, HashMap<Integer,Double>>> finalshortestPath_twopairnode;
	Map<Integer, Map<Integer, HashMap<Integer,Double>>> finallongestPath_twopairnode;
	Map<Integer, Map<Integer, HashMap<Integer,Double>>> finalshortestPath_twopairnode_notmodified;
	
	private double min_intimacy_weight = 1;
	double max_value = numberOfTeamMembers;
	double threshold;
	
	public CreateGraph(CreateUserANDCountSA user_countSA_obj, double threshold_value) throws IOException, ParseException
	{
		
		threshold = threshold_value;
		//userAnswererGraph create = new userAnswererGraph(user_countSA_obj.getQId_SAList(), user_countSA_obj.getQId_Asker(), user_countSA_obj.getAnswerpostid_uid_parentid());
		//Intimacy_Graph = create.getAskeruser_answerer();
		//askeruser_SACount = create.getAskeruser_SACount();
		Intimacy_Graph = user_countSA_obj.getAskeruser_answerer();
		askeruser_SACount = user_countSA_obj.getAskeruser_SACount();
		
		User_SACount = user_countSA_obj.getUsersa();
		//SA_MaxDoc = user_countSA_obj.getSA_Maxuserdoc_normalized();
		SA_MaxDoc = user_countSA_obj.getSA_Maxuserdoc();
		sauser = user_countSA_obj.getSauser_normalized();
		//sauser = user_countSA_obj.getSauser();
		createMngGraph();
	}
	
	public Map<Integer, HashMap<Integer, Double>> generateGraphANDShortestPathes (List<User> team, ArrayList<String> project)
	{
		//System.out.print("generateGraphANDShortestPathes");
		finalgraph = new HashMap<Integer, HashMap<Integer, Double>>();
		finalshortestPath_twopairnode = new HashMap<Integer, Map<Integer, HashMap<Integer,Double>>>();
		finalshortestPath_twopairnode_notmodified = new HashMap<Integer, Map<Integer, HashMap<Integer,Double>>>();
		finallongestPath_twopairnode = new HashMap<Integer, Map<Integer, HashMap<Integer,Double>>>();
		
		List<Integer> teamm = new ArrayList<Integer>();
		for(User uteam: team)
			teamm.add(Integer.parseInt(uteam.getUserId()));
		
		Map<Integer, HashMap<Integer, Double>> subgraph = new HashMap<Integer, HashMap<Integer, Double>>();//for ShortestPathes
		////////////////////////////////////////////////////////
		
		for(int ut : teamm)//for team
			generateGraph(team, project, ut, User_SACount.get(ut), 1);
		for(int uteam: teamm) 
			subgraph.put(uteam, finalgraph.get(uteam));
		
		
		//////////////////////////////////////////////////////////
		for(Entry<Integer, HashMap<String, Double>> users: User_SACount.entrySet())//for candidate
		{
			//HashMap<String, Double> uteamSAs = User_SACount.get(Integer.parseInt(uteam.getUserId()));

			if(!teamm.contains(users.getKey()))
			{
				generateGraph(team, project, users.getKey(), users.getValue(), 0);
				
				//remove useless candidate
				HashMap<Integer, Double> uteam_score = finalgraph.get(users.getKey());
				int bad_score = team.size();
				int score = 0;
				for(Entry<Integer, Double>  s : uteam_score.entrySet())
					score += s.getValue();
				boolean isremoved = false;
				if(score == bad_score)
				{
					finalgraph.remove(users.getKey());
					isremoved = true;
				}
				
				///////////shortest path
				if(isremoved == false)
				{
					int candidateid = users.getKey();
					
					for(User ut : team)
					{
						HashMap<Integer, Double> ut_score = finalgraph.get(Integer.parseInt(ut.getUserId()));
						ut_score.put(candidateid, uteam_score.get(Integer.parseInt(ut.getUserId()))); //change weight to cost
						finalgraph.replace(Integer.parseInt(ut.getUserId()), ut_score);
					}
					///////////////////////ShortestPaths
					HashMap<Integer, Double> team_score = finalgraph.get(candidateid);
					if(!teamm.contains(candidateid))
					{
						//update subgraph
						subgraph.put(candidateid, team_score);
						Map<Integer, HashMap<Integer, Double>> subgraph_zegond = removeZeroEdge(subgraph);
						shortestPath_twopairnode_notmodified = generateShortestPaths(subgraph_zegond, candidateid);
						//shortestPath_twopairnode = Modify(shortestPath_twopairnode_notmodified);
						
						
						shortestPath_twopairnode = shortestPath_twopairnode_notmodified;////?
						shortestPath_twopairnode_notmodified = shortestPath_twopairnode;/////?
						//Map<Integer, HashMap<Integer, Double>> subgraph_prim = changeCost(subgraph); 
						//longestPath_twopairnode = generateShortestPaths(subgraph_prim, candidateid);
					}

					finalshortestPath_twopairnode.put(users.getKey(), shortestPath_twopairnode) ;
					finalshortestPath_twopairnode_notmodified.put(users.getKey(), shortestPath_twopairnode_notmodified) ;
					//finallongestPath_twopairnode.put(users.getKey(), longestPath_twopairnode);
					////////////////////remove
					subgraph.remove((Integer)users.getKey());
					for(User ut : team)
					{
						HashMap<Integer, Double> ut_score = finalgraph.get(Integer.parseInt(ut.getUserId()));
						ut_score.remove((Integer)users.getKey());
						finalgraph.replace(Integer.parseInt(ut.getUserId()), ut_score);
					}
				}//if
				
				
			}//if
			
			
		}//for users
		return finalgraph;
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
					if(value != threshold)
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
					if(value != threshold)
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
						temp.put(entry2.getKey(), threshold);
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
						temp.put(entry2.getKey(), threshold);
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

	private void generateGraph(List<User> team, ArrayList<String> project, int uid, HashMap<String, Double> userSAs, int isteam)
	{
		
		
		
		
		for(User uteam : team)
			//for(Entry<Integer, HashMap<String, Double>> users: User_SACount.entrySet())//candidate or team
			{
				HashMap<String, Double> uteamSAs = User_SACount.get(Integer.parseInt(uteam.getUserId()));
				if(uid != Integer.parseInt(uteam.getUserId()))
				{
					
					//HashMap<String, Double> userSAs = users.getValue();
					//double jaccard = 0;
					//double min = 0;
					//double max = 0;
					double max = project.size();
					double sum_max = 0;
					double sum_min = 0;
					for(String proj_SA : project)
					{
						double x = 0 ,y=0;
						if(uteamSAs.containsKey(proj_SA))
							x = sauser.get(proj_SA).get(Integer.parseInt(uteam.getUserId()));
						if(userSAs.containsKey(proj_SA))
							y = sauser.get(proj_SA).get(uid);
						
						//if(x == 1)
							//x = 0;
						//if(y==1 )
							//y=0;
						
						if(isteam == 0)
						{
							if(x!=0)
							{

								//jaccard
								
								
								if(x<y)
									sum_min += x;
								else
									sum_min += y;
								
								//if(x<y)
									//min = x;
								//else
									//min = y;
								
								//jaccard += (min/SA_MaxDoc.get(proj_SA));
								//sum_max += SA_MaxDoc.get(proj_SA);
								
								
								if(x>y)
									sum_max += x;
								else
									sum_max += y;
								
								//jaccard += min/max;
							}
						}//if
						else
						{
							if(x<y)
								sum_min += x;
							else
								sum_min += y;
							
							if(x>y)
							    sum_max += x;
						    else
							    sum_max += y;
						}
						
						
			
						
					}//for
					//jaccard /= project.size();
					
					//double jaccard = sum_min/max;
					double jaccard = sum_min/sum_max;
					
					Double intimacy_weight = min_intimacy_weight;
					
					if( Mng_Graph.containsKey(Integer.parseInt(uteam.getUserId())) &&  Mng_Graph.get(Integer.parseInt(uteam.getUserId())).containsKey(uid))
					   intimacy_weight = Mng_Graph.get(Integer.parseInt(uteam.getUserId())).get(uid);
					
					//double score = jaccard * intimacy_weight;
					double score = jaccard ;
					double cost = 1- score;
					
					//set threshold to score
					if(cost >= 0.95)
						cost = threshold;
						
					if(finalgraph.containsKey(uid))//candidate
					{
						HashMap<Integer, Double> uteam_score = finalgraph.get(uid);
						uteam_score.put(Integer.parseInt(uteam.getUserId()), (cost)); //change weight to cost
						finalgraph.replace(uid, uteam_score);
					}
					else
					{
						HashMap<Integer, Double> uteam_score = new HashMap<Integer, Double>();
						uteam_score.put(Integer.parseInt(uteam.getUserId()), (cost));
						finalgraph.put(uid, uteam_score);
					}
					
				}//if
			}//for
	}
	
	private Map<Integer, HashMap<Integer, Double>> generateShortestPaths(Map<Integer, HashMap<Integer, Double>> subgraph, int candidateid) 
	{
		
		Map<Integer, HashMap<Integer, Double>> Distance = new HashMap<Integer, HashMap<Integer,Double>>();
		for (Entry<Integer, HashMap<Integer, Double>> entry : subgraph.entrySet()) //users	
	    {
			Dijkstra_old dpq = new Dijkstra_old(subgraph.size()); 
	        dpq.dijkstra(subgraph, entry.getKey()); 
	        
	        
	        Distance.put(entry.getKey(), dpq.getDist());
	       
	    }
		return Distance;
	}

	private void createMngGraph()
	{
		for(Entry<Integer, HashMap<Integer, HashMap<String, Double>>> Asker_UsersSAs : Intimacy_Graph.entrySet())//asker e
		{
			int asker = Asker_UsersSAs.getKey();
			HashMap<String, Double> asker_SACount = askeruser_SACount.get(asker);
			HashMap<Integer, HashMap<String, Double>> Answerers_SAs= Asker_UsersSAs.getValue();
			for(Entry<Integer, HashMap<String, Double>> Answerers : Answerers_SAs.entrySet())
			{
				HashMap<String, Double> Answerer_SACount = Answerers.getValue();
				double sum_tf = 0;
				for(Entry<String, Double> salist : asker_SACount.entrySet())
				{
					double tf = 0;
					if(Answerer_SACount.containsKey(salist.getKey()))
						tf = Answerer_SACount.get(salist.getKey());
					double Q_SA = askeruser_SACount.get(asker).get(salist.getKey());
					
					sum_tf += (tf/Q_SA);
				}//sa
				sum_tf /= (double)asker_SACount.size();
				double sum_df = ((double)Answerer_SACount.size() / (double)asker_SACount.size());
				
				double weight = sum_tf * sum_df;
				
				if(weight>0 && weight < min_intimacy_weight)
					min_intimacy_weight = weight;
				
				if(Mng_Graph.containsKey(asker))
				{
					
					HashMap<Integer, Double> answerer_weight = Mng_Graph.get(asker);
					answerer_weight.put(Answerers.getKey(), weight);
					Mng_Graph.replace(asker, answerer_weight);
				}
				else
				{
					HashMap<Integer, Double> answerer_weight = new HashMap<Integer, Double>();
					answerer_weight.put(Answerers.getKey(), weight);
					Mng_Graph.put(asker, answerer_weight);
				}
				
			}//answerer
			
		}//asker
		
		
	}

	public Map<Integer, HashMap<Integer, Double>> getFinalgraph() {
		return finalgraph;
	}

	public void setFinalgraph(Map<Integer, HashMap<Integer, Double>> finalgraph) {
		this.finalgraph = finalgraph;
	}

	public Map<Integer, Map<Integer, HashMap<Integer, Double>>> getFinalshortestPath_twopairnode() {
		return finalshortestPath_twopairnode;
	}

	public void setFinalshortestPath_twopairnode(
			Map<Integer, Map<Integer, HashMap<Integer, Double>>> finalshortestPath_twopairnode) {
		this.finalshortestPath_twopairnode = finalshortestPath_twopairnode;
	}

	public Map<Integer, Map<Integer, HashMap<Integer, Double>>> getFinallongestPath_twopairnode() {
		return finallongestPath_twopairnode;
	}

	public void setFinallongestPath_twopairnode(
			Map<Integer, Map<Integer, HashMap<Integer, Double>>> finallongestPath_twopairnode) {
		this.finallongestPath_twopairnode = finallongestPath_twopairnode;
	}

	public Map<Integer, Map<Integer, HashMap<Integer, Double>>> getFinalshortestPath_twopairnode_notmodified() {
		return finalshortestPath_twopairnode_notmodified;
	}

	public void setFinalshortestPath_twopairnode_notmodified(
			Map<Integer, Map<Integer, HashMap<Integer, Double>>> finalshortestPath_twopairnode_notmodified) {
		this.finalshortestPath_twopairnode_notmodified = finalshortestPath_twopairnode_notmodified;
	}


	
	
}
