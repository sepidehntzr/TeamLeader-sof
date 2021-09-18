package Common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.lucene.queryparser.classic.ParseException;
import Common.User;

public class CalcFinalProb_v1 
{
	

	TeamLeaderMain testmain;
	Map<String,User> Technical_goldset;
	
	
	private Evaluation ev;
	
	//parameters
	
	LinkedHashMap<String, LinkedHashMap<Integer, Double>> SA__userId_Balogprobability;

	final int bits = 3;
    final long factor = (long) Math.pow(10, 3);
    
    
	double NDCG_avg_fmeasure_10 = 0;
	double NDCG_avg_fmeasure_20 = 0;
	double GAP_avg_fmeasure_10 = 0;
	double GAP_avg_fmeasure_20 = 0;

	
	String Approach_Type = "";
	
	FileWriter writer_temp;
	
	public CalcFinalProb_v1(TeamLeaderMain main, Map<String, User> technical_goldset, String Approach_name) throws IOException, ParseException {
		testmain = main;
		
		Technical_goldset = technical_goldset;
		Approach_Type = Approach_name;
		ev = new Evaluation();
		
		
	}
	
	
	
	public void calculateProb(List<User> team, ArrayList<String> project, 
			Map<Integer, Double> user_TechnicalScore, Map<Integer, Double> user_CommunicationScore, FileWriter writer_combshape, Map<Integer, HashMap<Integer,Double>> Asker_Answerer) throws IOException, ParseException, InterruptedException 
	{
		
		HashMap<Integer, Double> projectmanagers = null;
		if(user_CommunicationScore!= null && user_TechnicalScore != null)
		{
			user_CommunicationScore = min_maxNormalization(user_CommunicationScore);
			user_TechnicalScore = min_maxNormalization(user_TechnicalScore); 
			projectmanagers = multipleCommunication(user_CommunicationScore, user_TechnicalScore);
		}
            
		else if(user_CommunicationScore== null && user_TechnicalScore != null)
			projectmanagers = (HashMap<Integer, Double>) user_TechnicalScore;
        
		
        //////////////////
		
       HashMap<Integer, Double> projectmanagers_sorted  = new HashMap<Integer, Double>();
		projectmanagers_sorted = sortByValue(projectmanagers);
	
		
		 List<Integer> projectmanagers_result = new ArrayList<Integer>();
		 projectmanagers_sorted.entrySet()
	        .stream()
	        .forEach(entry ->
	        projectmanagers_result.add(entry.getKey())
	        );
	        
		if(projectmanagers_result != null)
		{
			
			
			HashMap<Integer, Double> uid_mentorshipScore = ev.Mentorship(projectmanagers_result, project, team, Asker_Answerer);
			
			List<Integer> RankedUsers = projectmanagers_result;
			
			/////////////////////////////////////////////////////////////////////
			//make print project
			
			
			TreeSet<String> print_project = new TreeSet<>();
			for(String HP : project)
			{
				HP = HP.replace("|", ",");
				List<String> list = Arrays.asList(HP.split(","));
				for(String sa : list)
				    print_project.add(testmain.SA_levelName.get(sa));
			}
			Object[] obj = print_project.toArray();
			String[] projj =  Arrays.copyOf(obj, obj.length, String[].class);
			ArrayList<String> proj =  new ArrayList<String>(Arrays.asList(projj));
			
			for (int y=0; y<20; y++) 
			{
				
					writer_combshape.write(project +";;;"+ (projectmanagers_result.get(y) +" : "+ uid_mentorshipScore.get(projectmanagers_result.get(y)))+"\n");
					
				
				
			}
			writer_combshape.flush();
			
            /////////////////////////////////////////////////////////////////////based on other evals
			
			RankedMeasure_eachMeasure(team, proj, uid_mentorshipScore, RankedUsers, "mentorship");
			
			
            /////////////////////////////////////////////////////////////////////////// bestpm
			int uid_bestPM = projectmanagers_result.get(0);
			int i = 0; Double d1;
			while((d1 = uid_mentorshipScore.get(uid_bestPM)) == null)//
				 uid_bestPM = projectmanagers_result.get(i++);			
			
		
			long i1 = (long)(uid_mentorshipScore.get(uid_bestPM) * factor);
            print_BestPM(team, proj, uid_bestPM, i1);
			
			
		}
		
		
	}

	



	private void RankedMeasure_eachMeasure(List<User> team, ArrayList<String> project, HashMap<Integer, Double> uid_Score, List<Integer> RankedUsers, String eval) throws IOException
	{
		
		
		//Relevancy Dif
		LinkedHashMap<Integer, Double> UId_rel = new LinkedHashMap<Integer, Double>();
		for(int entry: RankedUsers)
            UId_rel.put(entry,   uid_Score.containsKey(entry) ? uid_Score.get(entry):0);
        
       
                    
        //ranked measures
		NDCG_avg_fmeasure_10 = ev.NDCG_AtK(RankedUsers, UId_rel, 10, project);
		NDCG_avg_fmeasure_20 = ev.NDCG_AtK(RankedUsers, UId_rel, 20, project);
		GAP_avg_fmeasure_10 = ev.GAP_AtK(RankedUsers, UId_rel, 10, project);
		GAP_avg_fmeasure_20 = ev.GAP_AtK(RankedUsers, UId_rel, 20, project);
		
		/////////////////////////////////////////////////////////////////////
		
		
		long i1 = (long)(NDCG_avg_fmeasure_10 * factor);
		long i2 = (long)(NDCG_avg_fmeasure_20 * factor);
		long i3 = (long)(GAP_avg_fmeasure_10 * factor);
		long i4 = (long)(GAP_avg_fmeasure_20 * factor);
		
		BufferedWriter bf = null;
		
		
			
			testmain.model_obj.get(Approach_Type).setNDCG_avg_mentorship_Model_10( testmain.model_obj.get(Approach_Type).getNDCG_avg_mentorship_Model_10() + (double)i1/factor);
			testmain.model_obj.get(Approach_Type).setNDCG_avg_mentorship_Model_20(testmain.model_obj.get(Approach_Type).getNDCG_avg_mentorship_Model_20() + (double)i2/factor);
			testmain.model_obj.get(Approach_Type).setGAP_avg_mentorship_Model_10(testmain.model_obj.get(Approach_Type).getGAP_avg_mentorship_Model_10() + (double)i3/factor);
			testmain.model_obj.get(Approach_Type).setGAP_avg_mentorship_Model_20(testmain.model_obj.get(Approach_Type).getGAP_avg_mentorship_Model_20() + (double)i4/factor);
			
			bf = testmain.model_obj.get(Approach_Type).getRankedMeasure_writer_Model();
		  
		
		
		print_RankedMeasure(team, project, i1, i2, i3, i4, bf );
		
	}
	
	

	private HashMap<Integer, Double> multipleCommunication(Map<Integer, Double> user_CommunicationScore,
			Map<Integer, Double> projectmanagers)
	{
		
		HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
		for (Entry<Integer, Double> entry : projectmanagers.entrySet()) 
		{
			int uid = entry.getKey();
			double TechninalScore = entry.getValue();
			if(user_CommunicationScore.containsKey(uid))
			{
				double CommunicationScore = user_CommunicationScore.get(uid);
				
				temp.put(uid, TechninalScore * CommunicationScore);
				
			}	 
		}
		

		return temp;
	}
	
	
	
	private HashMap<Integer, Double> min_maxNormalization(Map<Integer, Double> sA_score_maximumCoverage) {
		
		HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
	
		
		double min = (double)Collections.min(sA_score_maximumCoverage.values());
		double max = (double)Collections.max(sA_score_maximumCoverage.values());
		
		if(!Double.isNaN(max))
	     for (Entry<Integer, Double> entry : sA_score_maximumCoverage.entrySet()) 
	     {
	    	 double d = (entry.getValue()-min)/(max-min);
	    	 temp.put(entry.getKey(), d);
	     }
		
		
	    return temp;
		
	}
	private void print_BestPM(List<User> team, ArrayList<String> project, Integer PM_uid,  long mentorship_bestPM) throws IOException {
		
		BufferedWriter bf = testmain.model_obj.get(Approach_Type).getBestPM_writer_Model();
		for(User u : team)
			bf.write(u.getUserId()+";");
		bf.write(", ");
		for(String sa : project)
			bf.write(sa+";");
		bf.write(", ");
		bf.write(PM_uid+", ");
		bf.write((double)mentorship_bestPM/factor+", ");
		bf.write("\n");
		bf.flush();
		
	}

	private synchronized void print_RankedMeasure(List<User> team, ArrayList<String> project, long ndcg_measure_fmeasure_10, long ndcg_measure_fmeasure_20, long gap_measure_fmeasure_10, long gap_measure_fmeasure_20, BufferedWriter bf) throws IOException 
	{
		
		
		for(User u : team)
			bf.write(u.getUserId()+";");
		bf.write(", ");
		for(String sa : project)
			bf.write(sa+";");
		bf.write(", ");
		bf.write((double)ndcg_measure_fmeasure_10/factor+", "+(double)ndcg_measure_fmeasure_20/factor+", "+
				(double)gap_measure_fmeasure_10/factor+", "+(double)gap_measure_fmeasure_20/factor);
		bf.write("\n");
		bf.flush();
		
		
		
	}
	
	public HashMap<Integer, Double> sortByValue(Map<Integer, Double> userscore)
	{
		HashMap<Integer, Double> sorted = new LinkedHashMap<Integer, Double>(); 
        userscore.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        return sorted;
	}
	


}
