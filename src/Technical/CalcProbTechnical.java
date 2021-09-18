package Technical;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import Common.AbstractModels;
import Common.Evaluation;
import Common.TeamLeaderMain;
import Common.User;
import ExtraKnowladge.DistanceBaseApproach;
import ExtraKnowladge.KNN_ML_Approach;
import ExtraKnowladge.LambdaMart_ML_Approach;
import ExtraKnowladge.LanguageModel;
import ExtraKnowladge.Lr_ML_Approach;
import ExtraKnowladge.NaiveBayes_ML_Approach;
import ExtraKnowladge.RankNet_ML_Approach;
import ExtraKnowladge.SVMLinear_ML_Approach;
import ExtraKnowladge.SVMRanking_ML_Approach;
import ExtraKnowladge.SVMRbf_ML_Approach;
import ExtraKnowladge.VectorSpaceModel;
import Managerial.CreateUserANDCountPMSA;



public class CalcProbTechnical
{

	TeamLeaderMain testmain;
	
	
	
	//parameters
	int k = 10; //number of output// based on @k
	//
	LinkedHashMap<String, LinkedHashMap<Integer, Double>> SA__userId_Balogprobability;
	Map<String, Double> SA_probability_sa_C; 
	CreateUserANDCountSA user_countSA_obj;
	String EK_testset_Info;
	String EK_prediction_results, EK_prediction_results_new;
	String EK_testset_new_shuffled;
	
	String Approach_Type = "";
	
	Map<Integer, Double> user_technicalscore = new ConcurrentHashMap<Integer, Double>();
	//HashMap<Integer, Double> user_technicalscore;
	
	public CalcProbTechnical(TeamLeaderMain main, String Approach_name, LinkedHashMap<String, LinkedHashMap<Integer, Double>> sA__userId_Balogprobability, 
			CreateUserANDCountSA user_countTechSA_obj, Map<String, Double> TechSA_probability_sa_C, String EK_testset_Info_path, String EK_prediction_results_path, String EK_testset_new_shuffled_path) throws IOException, ParseException {
		testmain = main;
		Approach_Type = Approach_name;
		SA__userId_Balogprobability = sA__userId_Balogprobability;
		user_countSA_obj = user_countTechSA_obj;	
		SA_probability_sa_C = TechSA_probability_sa_C;
		EK_testset_Info = EK_testset_Info_path;
		EK_testset_new_shuffled = EK_testset_new_shuffled_path;
	    EK_prediction_results = EK_prediction_results_path;  EK_prediction_results_new = EK_prediction_results_path;
	}
	
public CalcProbTechnical(String Approach_name, CreateUserANDCountSA user_countTechSA_obj) {
	user_countSA_obj = user_countTechSA_obj;
	Approach_Type = Approach_name;
	}
	//synchronized?
	public  Map<Integer, Double> executeApproachs(List<User> team, ArrayList<String> project_main) throws IOException, ParseException 
	{
		ArrayList<String> project = new ArrayList<>();
		for(String HP : project_main)
		{
			HP = HP.replace("|", ",");
			List<String> list = Arrays.asList(HP.split(","));
			for(String sa : list)
				project.add(sa);
		}
		//project = proj;
		
		user_technicalscore = new HashMap<Integer, Double>();
		AbstractModels model = null;
		
        //////////////////////////////////////////////EKA
		if(Approach_Type.equals("EKA") || Approach_Type.equals("EKA_Tech") 
				)
		{
			model = new EKApproach_Baseline(testmain);
			user_technicalscore = model.findProjectManagers(team, project, model, k, user_countSA_obj.getUsersa(), user_countSA_obj.totalDoc, user_countSA_obj.getSasumdoc_withone());
		}
		//////////////////////////////////////////////Balog
		else if(Approach_Type.equals("Balog") 
				)
		{
			model = new BalogApproach_Baseline(testmain);
			user_technicalscore = model.findProjectManagers(team, project, model, k, SA__userId_Balogprobability, user_countSA_obj.getUsersa(), SA_probability_sa_C);
		}
    
		
        
		//////////////////////////////////////////////////// finish baselines
		//////////////////////////////////////////////////// new models
		/*
		 * else if(Approach_Type.equals("NewModel_v1")) { model = new
		 * NewModel_v1(testmain); user_technicalscore = model.findProjectManagers(team,
		 * project, model, k); }
		 */
		
		
		
		return user_technicalscore;
        //////////////////////////////////////////////approach
	}//

	public Map<Integer, Double> getUser_TechnicalScore() {
		return  user_technicalscore;
	}



	
	

	
	
}
