package Technical;
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

public class BalogApproach_Baseline extends AbstractModels
{
	
	private AbstractModels abstractmodel;
	TeamLeaderMain main;
	double lambda = 0.1;
	
	//parameter:
	
	//
	public BalogApproach_Baseline(TeamLeaderMain m) {
		super(m);
		main = m;
		
	}

	//P(saadv|e)
	public HashMap<Integer, Double> findProjectManagers(List<User> team , List<String> project, AbstractModels model, int k, 
			LinkedHashMap<String, LinkedHashMap<Integer, Double>> SA__userId_Balogprobability, Map<Integer, HashMap<String,Double>> usersa,
			Map<String, Double> SA_probability_sa_C) throws IOException, ParseException
	{
		abstractmodel = model;
		
		//Map<Integer, HashMap<String,Double>> usersa = main.user_countSA_obj.getUsersa();  //keys is candidate list and value is count of each skill area
		
		Map<Integer, Double> userscore = new HashMap<Integer, Double>();//pm 
		
		//List<String> skills = UnionSA(team, project);
		List<String> skills = project;

		for (Entry<Integer, HashMap<String, Double>> entry : usersa.entrySet()) 	
		{
			
			double score = 0; boolean isinitial = true;
			
			for(int i=0; i<skills.size(); i++)
			{
				double score_sa = 0;
				double tf_e_sa = 0;
				HashMap<String, Double> sa_count = entry.getValue();
				String sa = skills.get(i);
				
				if(sa_count.containsKey(sa))
					tf_e_sa = sa_count.get(sa);
				
				//if(sa_count.containsKey(sa) == true);
				  //   tf_e_sa = entry.getValue().get(skills.get(i));
				if(tf_e_sa == 1)
					tf_e_sa = 0;
				LinkedHashMap<Integer, Double> user_prob = SA__userId_Balogprobability.get(skills.get(i));
				
				double p_sa_C = SA_probability_sa_C.get(skills.get(i));
				
				double p_sa_e = user_prob.containsKey(entry.getKey()) ? user_prob.get(entry.getKey()) : 0;
				//if(tf_e_sa != 0)
					//System.out.print("");
				score_sa = ((1-lambda) * p_sa_e ) + lambda * p_sa_C;
				//score_sa = p_sa_e;
				double d = 0;
				//d = Math.pow(score_sa, 1/tf_e_sa);
				//if(tf_e_sa == 0)///?
					d = score_sa;
				
				if (Double.isNaN(d)|| Double.isInfinite(d))  
					d = 0;
				
				
				
			
				
				if(d == 0)///?
					d = 0.0000001;
				//if(tf_e_sa == 0)///?
					//continue;
			
				
				if(isinitial == true)  //initial
					{score = d; isinitial = false;}
				
				if(score == 0)
					score = d;
				else 
				  score *= d; 
				
				
				
				
				//score += tf_e_sa * d;
			}
			
			score *= Math.pow(10, +131);
			userscore.put(entry.getKey(), score);
		}//
		
		
		userscore = min_maxNormalization(userscore);
        HashMap<Integer, Double> sorted = abstractmodel.sortByValue(userscore);
		
        
		return sorted;
		
	}
	

private HashMap<Integer, Double> min_maxNormalization(Map<Integer, Double> sA_score_maximumCoverage) {
		
		HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
	
		
		double min = (double)Collections.min(sA_score_maximumCoverage.values());
		double max = (double)Collections.max(sA_score_maximumCoverage.values());
		
		if(!Double.isNaN(max))
	     for (Entry<Integer, Double> entry : sA_score_maximumCoverage.entrySet()) 
	     {
	    	 double d = (entry.getValue())/(max);
	    	 if(Double.isNaN(d))
	    		 d = 0;
	    	 
	        //if(d!= 1.1338838716832602E-4)
	    		// System.out.println(entry.getKey()+" "+d);
	    	 temp.put(entry.getKey(), d);
	     }
		
		//System.out.println(temp.get(751158));
	    return temp;
		
	}
	/////////////////

@Override
public HashMap<Integer, Double> findProjectManagers(List<User> team, Map<Integer, HashMap<Integer, Double>> graph,
		Map<Integer, HashMap<Integer, Double>> distance, Map<Integer, HashMap<Integer, List<Integer>>> distance_tree,
		double min_distance, double max_distance, double graph_threshold, int max_EdgeScore, AbstractModels model)
		throws IOException, Common.ParseException {
	// TODO Auto-generated method stub
	return null;
}
	

	
	
	
	
}
