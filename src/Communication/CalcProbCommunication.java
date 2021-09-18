package Communication;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;

import Common.AbstractModels;
import Common.TeamLeaderMain;
import Common.User;
import Technical.EKApproach_Baseline;

public class CalcProbCommunication {
	
	TeamLeaderMain testmain;
	String Approach_Type = "";
	HashMap<Integer, Double> user_communicationscore;

	List<CandidateCAObj> CandidateObj = null;
	
	public CalcProbCommunication(TeamLeaderMain main, String Approach_name) {
		testmain = main;
		Approach_Type = Approach_name;
	}

	public Map<Integer, Double> executeApproachs(List<User> team, Map<Integer, HashMap<Integer, Double>> graph, Map<Integer, HashMap<Integer, Double>> distance, 
			 Map<Integer, HashMap<Integer, List<Integer>>> distance_tree, double min_distance, double max_distance, double graph_threshold, int max_EdgeScore) throws IOException, ParseException 
	{
		user_communicationscore = new HashMap<Integer, Double>();
		AbstractModels model = null;
		///////////////////////////
		
		
		////////////////////////////////////////////////centrality
		
		 if(Approach_Type.equals("LCOM_BC"))
		{
			model = new LCOM_BC_Approach(testmain);
			user_communicationscore = model.findProjectManagers(team, graph, distance, distance_tree ,graph_threshold,max_EdgeScore, model);
		}
		////////////////////////////////////////////////////////////
		else if(Approach_Type.equals("LCOM_Density"))
		{
			model = new LCOM_Density_Approach(testmain);
			user_communicationscore = model.findProjectManagers(team, graph, null, null ,graph_threshold,max_EdgeScore, model);
		}
		else if(Approach_Type.equals("LCOM_Voting"))
		{
			model = new LCOM_Voting_Approach(testmain);
			LCOM_Voting_Approach modell = (LCOM_Voting_Approach) model;
			user_communicationscore = model.findProjectManagers(team, graph, distance, distance_tree, min_distance, max_distance ,graph_threshold,max_EdgeScore, model);
			CandidateObj = modell.getCandidateObj();
		}
		
		/////////////////////////////////////////////////////////////////////mst based
		else if(Approach_Type.equals("LCOM_DC_MST"))
		{
			model = new LCOM_DC_MST_Approach(testmain);
			user_communicationscore = model.findProjectManagers(team, graph, distance, null ,graph_threshold,max_EdgeScore, model);
		}
		
		/////////////////////////////////////////////////////////////////// based on SumShortestPath
		else if(Approach_Type.equals("LCOM_DC_SumShortestPath"))
		{
			model = new LCOM_DC_SumShortestPath_Approach(testmain);
			user_communicationscore = model.findProjectManagers(team, graph, distance, null ,graph_threshold,max_EdgeScore, model);
		}
		
		
		else if(Approach_Type.equals("LCOM_MaximunDistance"))
		{
			model = new LCOM_MaximunDistance_Approach(testmain);
			user_communicationscore = model.findProjectManagers(team, graph, distance, null ,graph_threshold,max_EdgeScore, model);
		}
		
	
		
		
		return user_communicationscore;
	}

	public List<CandidateCAObj> getCandidateObj() {
		return CandidateObj;
	}

}
