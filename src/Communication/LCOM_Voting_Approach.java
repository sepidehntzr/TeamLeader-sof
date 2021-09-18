package Communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.queryparser.classic.ParseException;

import Common.AbstractModels;
import Common.TeamLeaderMain;
import Common.User;
import Technical.CreateUserANDCountSA;

public class LCOM_Voting_Approach extends AbstractModels
{

	private AbstractModels abstractmodel;
	TeamLeaderMain main;
			
     //parameter:
			
			
	public LCOM_Voting_Approach(TeamLeaderMain m) {
		super(m);
		main = m;
		
	}
	
	
	public HashMap<Integer, Double> findProjectManagers(List<User> teamm ,Map<Integer, HashMap<Integer, Double>> user_team_graph, Map<Integer, HashMap<Integer, Double>> shortestPath_twopairnode,  Map<Integer, HashMap<Integer, List<Integer>>> distance_tree, double min_distance, double max_distance, double graph_threshold, int max_EdgeScore, AbstractModels model) throws IOException, ParseException
	{
		abstractmodel = model;
		Map<Integer, Double> userscore = new HashMap<Integer, Double>();
		
		List<Integer> team = new ArrayList<Integer>();
		for(User uteam: teamm)
			team.add(Integer.parseInt(uteam.getUserId()));
		
		
		Map<Integer, Double> teamuser_power = new HashMap<Integer, Double>();
		
		for(Integer teamuser : team)
		{
			
			////
			teamuser_power.put(teamuser, 1.0);
		}
		
		
		for(Entry<Integer, HashMap<Integer, Double>> candidate : user_team_graph.entrySet())
		{
			int candidateid = candidate.getKey();
			
			if(!team.contains(candidateid))
			{	
				double candidate_score = 0;
				
				for(int u : team)
				{
					
					double teamUser_CC = 0;
					double teamUser_BC = 0;
					
					
					double min = 1/max_distance;
					double max = 1/min_distance;
					double w2=0;
					w2 = shortestPath_twopairnode.get(u).get(candidateid); 
					if( w2 != 0) 
					{
						teamUser_CC = 1 / w2;
					}
					else
						w2 = 0;

					if( w2 == 0) 
						teamUser_CC = 1 ;
				
					
					
					
					for(User uteam: teamm)
					{
						double BC = 0;
						int v = Integer.parseInt(uteam.getUserId());
						
						if(u != v)
						{
						    List<Integer> pathes = distance_tree.get(u).get(v);
							
							if(pathes!= null && pathes.contains(candidateid))
								BC++;
							
							
							BC /= pathes.size();
						}
						
						teamUser_BC += BC;
					}
					
				    
				    
					
					
				
					double alpha2 = 0.63;
					double alpha3 = 0.37;
					candidate_score += (alpha2*teamUser_CC + alpha3*teamUser_BC) * teamuser_power.get(u);
					
					
				}
				
				
					
			
				double final_score = candidate_score;
				if(final_score<0)
					final_score = 0;
				
				userscore.put(candidateid, final_score);
            }//if
			
			
		}//candidate

		HashMap<Integer, Double> sorted = abstractmodel.sortByValue(userscore);
		return sorted;

	}


	@Override
	public HashMap<Integer, Double> findProjectManagers(List<User> team, List<String> project, AbstractModels model,
			int k, LinkedHashMap<String, LinkedHashMap<Integer, Double>> SA__userId_Balogprobability,
			Map<Integer, HashMap<String, Double>> usersa, Map<String, Double> SA_probability_sa_C)
			throws IOException, Common.ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	

}
