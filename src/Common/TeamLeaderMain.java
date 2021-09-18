package Common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import Managerial.CalcProbNonTechnical;
import Managerial.CreateUserANDCountPMSA;
import Technical.BalogData;
import Technical.CalcProbTechnical;
import Technical.CreateUserANDCountSA;
import Technical.SkillArea;
import Common.User;
import Communication.CalcProbCommunication;
import Communication.CreateGraph;
import Communication.CreateGraph_Indep;
import Communication.UserSampling;

public class TeamLeaderMain
{

	/*
	 * String skillAreasPath, questionIndexPath, answerIndexPath, shapesPath,
	 * approachPath, SkillShapesXML;
	 */

	static Map<String, User> Technical_goldset;
	static List<User> users;
	public ArrayList<String> SAs_Tech;
	public Map<String, String> SA_levelName;

	// parameterss
	public String dataSetName = "C#";//Java or C#
	int numberOfProjectSA = 5;
	int numberOfTeamMembers = 5;
	int team_project_size = 5;
	
	int max_EdgeScore = 1;
	double graph_threshold = 1; //0.95
	double max_weight_graph = team_project_size * 20;
	
	
	
	String[] approaches = {};
	
	String[] eval = {"mentorship"};
	String[] rank_eval = {"ndcg@10", "ndcg@20", "meanGAP@10", "meanGAP@20"};
	
	String[] approaches_TechBaseline = { "Balog"
			};
	
	
	String[] approaches_communication = {"LCOM_Voting"};
	

	 FileWriter writer_combshape;
	 static Map<Integer, HashMap<Integer,Double>> Asker_Answerer;
	 //
	//
	LinkedHashMap<String, LinkedHashMap<Integer, Double>> TechSA__userId_Balogprobability;
	Map<String, Double> TechSA_probability_sa_C = new HashMap<>();
	
	
	
	List<TeamProjcetObj> TeamProjcet_obj_list = new ArrayList<TeamProjcetObj>();

	public CreateUserANDCountSA user_countSA_obj;

	CreateGraph creategraph;
	CreateGraph_Indep creategraph_Indep;
	

	Map<Integer, HashMap<Integer, Double>> graph = null;
	
	List<User> userlist = new ArrayList<User>();

	HashMap<List<User>, List<Integer>> final_result = new HashMap<List<User>, List<Integer>>();
	double count_query = 0;

	final int bits = 3;
	final long factor = (long) Math.pow(10, 3);

	Map<String , ModelObj> model_obj = new HashMap<String , ModelObj>();
	
	FileWriter writer_temp ;
	
	int count = 0;

	////////////////////////////////////////////
	public TeamLeaderMain()
	{
		
	}
	
	public static void main(String[] args)
			throws IOException, ParseException, ParserConfigurationException, SAXException, InterruptedException {

		Date start = new Date();
		TeamLeaderMain m = new TeamLeaderMain();
		
		
		HashSet<String> unionset = new HashSet<>(); 
		unionset.addAll(Arrays.asList(m.approaches_TechBaseline));
		unionset.addAll(Arrays.asList(m.approaches_communication));
		unionset.addAll(Arrays.asList(m.approaches));
		String[] union = {};
		m.approaches = unionset.toArray(union);
	    

		m.SAs_Tech = m.getTechSkills();
		m.SA_levelName = m.getSkillsLevelName();

		String Technical_goldset_file = m.getPath(m.dataSetName, "Technical_goldset");
		Technical_goldset = m.parsGold(new File(Technical_goldset_file), "Technical");


		
		// Technical
		m.user_countSA_obj = new CreateUserANDCountSA(m.getPath(m.dataSetName, "Clustering"),
				m.getPath(m.dataSetName, "IndexQuestions"), m.getPath(m.dataSetName, "IndexAnswers"),
				m.getPath(m.dataSetName, "SACategores"));
		m.TechSA__userId_Balogprobability = BalogData.readBalogProbabilitiesFromFiles(
				m.getPath(m.dataSetName, "Clustering"), m.getPath(m.dataSetName, "Balog"));
		for(String sa : m.SAs_Tech)
		{
			LinkedHashMap<Integer, Double> user_prob = m.TechSA__userId_Balogprobability.get(sa);
			double p_sa_C = 0;
			for(Entry<Integer, Double> s : user_prob.entrySet())
			{
				p_sa_C += s.getValue();
			}
			m.TechSA_probability_sa_C.put(sa, p_sa_C);
		}

		
		
		/////////////////////////////////////////////////////////////////////////
		for(String model : m.approaches)
		{
			ModelObj mo = new ModelObj();
			m.model_obj.put(model, mo);
		}
		//////////////////
		for(String model : m.approaches)
		{
			
			  m.model_obj.get(model).setRankedMeasure_writer_Model( new BufferedWriter (new
			  FileWriter(m.getPath(m.dataSetName, model)+"\\"+model+"RankedMeasure_BasedOnFmeasure.csv")));
			  m.model_obj.get(model).getRankedMeasure_writer_Model().
			  write("team, project, ndcg_measure_10, ndcg_measure_20," +
			  " gap_measure_10, gap_measure_20"+"\n");
			  m.model_obj.get(model).getRankedMeasure_writer_Model().flush();
			
			
			m.model_obj.get(model).setBesedOnMentorship_writer_Model(new BufferedWriter (new FileWriter(m.getPath(m.dataSetName,
					  model)+"\\"+model+"RankedMeasure_BasedOnMentorship.csv")));
			m.model_obj.get(model).getBesedOnMentorship_writer_Model(). write("team, project, ndcg_measure_10, ndcg_measure_20,"
			  		+ " gap_measure_10, gap_measure_20"+"\n");
			m.model_obj.get(model).getBesedOnMentorship_writer_Model().flush();
			
		}
		 

		m.detectTeamProject(m.numberOfTeamMembers);

        ///////////////////////////////////////////////////////////////////////communication
        //m.creategraph = new CreateGraph(m.user_countSA_obj, m.max_weight_graph);

		if(m.approaches_communication.length != 0)
		{
			UserSampling usersample = new UserSampling(m.user_countSA_obj.getUsersa(), m.dataSetName, (int)m.team_project_size, 1000,
	        		 Technical_goldset,  m.SAs_Tech);
			
			m.graph_threshold = m.max_EdgeScore;
			m.max_weight_graph *=  m.max_EdgeScore;
	        m.creategraph_Indep = new CreateGraph_Indep(m.user_countSA_obj, m.SAs_Tech, m.max_weight_graph, m.graph_threshold, m.max_EdgeScore, usersample.getUser_list_sample(), Technical_goldset);
			System.out.println("graph generated");
		}
		
        
		////////////////////////////////////////////////////////////////////////
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		
		m.writer_combshape = new FileWriter("E:\\1- Data, Datasets, project outputs\\proj-output\\Dataset-Thesis\\Results\\ProjectManager\\Result\\Java\\shape.csv");
		
		//
		m.Asker_Answerer = new HashMap<Integer, HashMap<Integer,Double>>();
		FileInputStream fstream = new FileInputStream("E:\\1- Data, Datasets, project outputs\\proj-output\\Dataset-Thesis\\Results\\ProjectManager\\Graph\\PM\\PMuserAnswererGraph_GT.csv");
	     BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

	        br.readLine();
	        String Line;
	        while ((Line = br.readLine()) != null)
	        {
	        	Line = Line.substring(0, Line.length()-2);
	        	String acceptedcount_rate = Line.substring(Line.lastIndexOf(",")+2, Line.length());
	        	double score = Double.parseDouble(acceptedcount_rate);
	        	Line = Line.substring(0, Line.lastIndexOf(","));
	        	String Answereruserid = Line.substring(Line.lastIndexOf(",")+2, Line.length());
	        	String Askeruserid = Line.substring(0, Line.indexOf(","));
	        	int askeruid = Integer.parseInt(Askeruserid);
	        	int answereriud = Integer.parseInt(Answereruserid);

	        	
	        	if(Asker_Answerer.containsKey(askeruid))
		       	{
		       		
		       			if(Asker_Answerer.get(askeruid).containsKey(answereriud))
		       			{
		       				double a = Asker_Answerer.get(askeruid).get(answereriud);
		       				a = a+score;
		       				Asker_Answerer.get(askeruid).replace(answereriud, a);
		       			}
		       				
		           		else
		           		{
		           			Asker_Answerer.get(askeruid).put(answereriud, score);
		           		}	
		       	}
	        	else
		       	{
	        		HashMap<Integer,Double> date_count = new HashMap<Integer,Double>();
		       		date_count.put(answereriud, score);
		       		Asker_Answerer.put(askeruid, date_count);	
		       	}
	        	
	        }//while
		
		
		
		
		//
		for (TeamProjcetObj TeamProjcet : m.TeamProjcet_obj_list) 
		{
			
			
			for (String approach : m.approaches) 
			{
				m.writer_combshape.write("approach: "+approach+"\n");
				m.writer_combshape.flush();
				m.exeApproach(TeamProjcet, approach);
			} // approaches
			
			System.out.println(m.count++);
			
			System.gc();
		} 
		
		
		
		for (String eval : m.rank_eval)
			   m.print_ModelsMeasure(eval);
		
		for (String approach : m.approaches) {

			m.model_obj.get(approach).getBestPM_writer_Model().close();
			m.model_obj.get(approach).getBestPM_writer_Model().close();
			m.model_obj.get(approach).getBesedOnMentorship_writer_Model().close();
		}

		//m.writer_temp.close();
		m.writer_combshape.close();
		
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		System.out.println("Done");

	}
	
	
	private Map<String, String> getSkillsLevelName() throws IOException {
		Map<String, String> sa_level = new HashMap<>();
		
		
		LineNumberReader reader = new LineNumberReader(new FileReader(Path.FilePaths.getFilePaths(dataSetName, "SALevelName")));
        String line = "";
        while((line = reader.readLine())!= null){
        	String levelname = line.split(":")[0];
        	String skills = line.split(":")[1];
        	skills = skills.replace("|", ",");
        	List<String> list = Arrays.asList(skills.split(","));   
        	for(String sa : list)
        	   sa_level.put(sa, levelname);
        }
		
		
		return sa_level;
	}
	
	private ArrayList<String> getTechSkills() throws IOException {
		ArrayList<String> SA_categores = new ArrayList<String>();
		
		
		LineNumberReader reader = new LineNumberReader(new FileReader(Path.FilePaths.getFilePaths(dataSetName, "SACategores")));
        String line = "";
        while((line = reader.readLine())!= null){
        	line = line.replace("|", ",");
        	List<String> list = Arrays.asList(line.split(","));   
        	SA_categores.addAll(list);
        	
        	
        }
		
        max_EdgeScore += SA_categores.size();
		return SA_categores;
	}
	
	private void exeApproach(TeamProjcetObj TeamProjcet, String approach)
			throws IOException, ParseException, InterruptedException {

		
		//HashMap<Integer, Double> user_ManagerialScore = null;
		Map<Integer, Double> user_TechnicalScore = null;
		Map<Integer, Double> user_CommunicationScore = null; 
		
		
		if(Arrays.asList(approaches_TechBaseline).contains(approach))
		{
			user_TechnicalScore = new CalcProbTechnical(this, approach, TechSA__userId_Balogprobability, user_countSA_obj, TechSA_probability_sa_C, 
					getPath(dataSetName, "EK_testset_Info"), getPath(dataSetName, "EK_prediction_results"), getPath(dataSetName, "EK_testset_knn_shuffled"))
					.executeApproachs(TeamProjcet.getTeam(), TeamProjcet.getProject());
		}
		
		if(Arrays.asList(approaches_communication).contains(approach))
		{
			user_CommunicationScore = new CalcProbCommunication(this, approach )
					.executeApproachs(TeamProjcet.getTeam(), creategraph_Indep.getFinalgraph(), creategraph_Indep.getShortestPath_twopairnode(),
							creategraph_Indep.getShortestPath_twopairnode_tree(), creategraph_Indep.getMin_distance(), 
							creategraph_Indep.getMax_distance() , max_weight_graph, max_EdgeScore);
		}

	
		new CalcFinalProb_v1(this, Technical_goldset, approach).calculateProb(TeamProjcet.getTeam(),
				TeamProjcet.getProject(), user_TechnicalScore, user_CommunicationScore, writer_combshape, Asker_Answerer);
		

	}

	private void detectTeamProject(int numberOfTeamMembers) throws IOException, ParseException {

		FileInputStream fstream = null;

		if (numberOfProjectSA == 3)
			fstream = new FileInputStream(Path.FilePaths.getFilePaths(dataSetName, "teamprojectlist_threesome"));
		else if (numberOfProjectSA == 4)
			fstream = new FileInputStream(Path.FilePaths.getFilePaths(dataSetName, "teamprojectlist_foursome"));
		else if (numberOfProjectSA == 5)
			fstream = new FileInputStream(Path.FilePaths.getFilePaths(dataSetName, "teamprojectlist_five"));
		    //fstream = new FileInputStream(Path.FilePaths.getFilePaths(dataSetName, "teamprojectlist_five"));

		BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
		String Line;
		while ((Line = br.readLine()) != null) {
			String team_mem = Line.split(";")[0];
			String project_sa = Line.split(";")[1];
			//project_sa = project_sa.replace("|", ","); 

			String[] team = team_mem.split(",");
			String[] project = project_sa.split(",");

			List<User> ulist = new ArrayList<User>();
			for (String u : team)
				ulist.add(Technical_goldset.get(u));
			TeamProjcetObj TeamProjcet_obj = new TeamProjcetObj();
			TeamProjcet_obj.setTeam(ulist);
			ArrayList<String> proj = new ArrayList<String>();
			proj.addAll(Arrays.asList(project));
			TeamProjcet_obj.setProject(proj);
			TeamProjcet_obj_list.add(TeamProjcet_obj);
			count_query++;

		}

	}

	private void print_ModelsMeasure(String eval) throws IOException {

		FileWriter writer = new FileWriter(
				getPath(dataSetName, "Approaches") + "\\"  + eval + " ModelsMeasure.csv");

		if(eval.equals("ndcg@10"))
		{
			writer.write("model, Mentorship("+eval+")");
			writer.write("\n");
			
			for (String Approach_name : approaches) 
			{
				
				double ment_measure = (double)((long)(model_obj.get(Approach_name).getNDCG_avg_mentorship_Model_10()*factor))/factor;
				ment_measure /= count_query;
				ment_measure = (double)((long)(ment_measure*factor))/factor;
				
				writer.write(Approach_name + ", " + ment_measure
						);
				writer.write("\n");
			}
		}
		else if(eval.equals("ndcg@20"))
		{
			writer.write("model, Mentorship("+eval+")");
			writer.write("\n");
			
			for (String Approach_name : approaches) 
			{
				
				double ment_measure = (double)((long)(model_obj.get(Approach_name).getNDCG_avg_mentorship_Model_20()*factor))/factor;
				ment_measure /= count_query;
				ment_measure = (double)((long)(ment_measure*factor))/factor;
				
				writer.write(Approach_name + ", " + ment_measure
						);
				writer.write("\n");
			}
		}
		else if(eval.equals("meanGAP@10"))
		{
				writer.write("model, Mentorship("+eval+")");
				writer.write("\n");
				
				for (String Approach_name : approaches) 
				{
					
					double ment_measure = (double)((long)(model_obj.get(Approach_name).getGAP_avg_mentorship_Model_10()*factor))/factor;
					ment_measure /= count_query;
					ment_measure = (double)((long)(ment_measure*factor))/factor;
					
					writer.write(Approach_name + ", " + ment_measure
							);
					writer.write("\n");
				}
		}
		else if(eval.equals("meanGAP@20"))
		{
			
			writer.write("model, Mentorship("+eval+")");
			writer.write("\n");
			
			for (String Approach_name : approaches) 
			{
				
				double ment_measure = (double)((long)(model_obj.get(Approach_name).getGAP_avg_mentorship_Model_20()*factor))/factor;
				ment_measure /= count_query;
				ment_measure = (double)((long)(ment_measure*factor))/factor;
				
				writer.write(Approach_name + ", " + ment_measure
						);
				writer.write("\n");
			}
			
		}
		
		
		
			
		
		
		writer.write("\n");

		writer.close();
	}

	public String getPath(String dataSetName, String fileName) {
		return Path.FilePaths.getFilePaths(dataSetName, fileName);
	}

	////////////////////////////////////////////////////////////

	//////////////////////////
	private Map<String, User> parsGold(File inputFile, String type)
			throws IOException, ParserConfigurationException, SAXException {

		Map<String, User> tempreldocs = new HashMap<String, User>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("row");

		int i = 0;
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				User u = new User();
				if (eElement.hasAttribute("UserId"))
					u.setUserId(eElement.getAttribute("UserId"));
				if (eElement.hasAttribute("Shape"))
					u.setShape(eElement.getAttribute("Shape"));
				if (eElement.hasAttribute("Advanced"))
					u.setSa_adv(Arrays.asList(eElement.getAttribute("Advanced").split(",")));
				if (eElement.hasAttribute("Intermediate"))
					u.setSa_int(Arrays.asList(eElement.getAttribute("Intermediate").split(",")));
				if (eElement.hasAttribute("Beginner"))
					u.setSa_beg(Arrays.asList(eElement.getAttribute("Beginner").split(",")));

				
				else if (type.equals("Technical") ) 
				{
					tempreldocs.put(eElement.getAttribute("UserId"), u);
					// while(i<50) {
					userlist.add(u);
					// i++; break;}
				}

			} // if
		}

		return tempreldocs;

	}

	

}
