package Communication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.queryparser.classic.ParseException;

import Common.TeamProjcetObj;
import Common.TeamLeaderMain;
import Common.User;
import Managerial.CalcProbNonTechnical;
import Managerial.CreateUserANDCountPMSA;
import Technical.CalcProbTechnical;
import Technical.CreateUserANDCountSA;

public class UserSampling {

	String dataSetName ;
	int numberOfProjectSA ;
	int numberOfTeamMembers ;
	
	int limit ;
	int threshold;
	List<Integer> user_list_sample = new ArrayList<>();;
	

	public UserSampling(Map<Integer, HashMap<String, Double>> all_Users, String dataSetName, int team_project_size, int threshold,
			  Map<String, User> technical_goldset, ArrayList<String> SAs_Tech) throws IOException, ParseException
	{
		
		limit = threshold;
		//threshold = thresholdd;
		numberOfProjectSA = team_project_size;
		this.dataSetName = dataSetName;
		
		detectTeamProject(numberOfTeamMembers);

		extendSampling(all_Users, technical_goldset); 
		

	}

	public UserSampling() {
		// TODO Auto-generated constructor stub
	}

	private void extendSampling(Map<Integer, HashMap<String, Double>> all_Users,  Map<String, User> technical_goldset) 
	{
		
		
		
		if(this.limit > 0 )
		{
			
			for(Entry<Integer, HashMap<String, Double>> user : all_Users.entrySet()) 
			{
				
				if(this.limit > 0 ) { 
					
					if(!user_list_sample.contains(user.getKey()) 
							) 
					{
						user_list_sample.add(user.getKey());
						limit --;
						
					}
			
				}
				
				else
				    break;
				 
			}
		}//if

	}


	private List<Integer> detectTeamProject(int numberOfTeamMembers) throws IOException, ParseException {

		//List<Integer> user_list_sample = new ArrayList<>();
		FileInputStream fstream = null;

		if (numberOfProjectSA == 3)
			fstream = new FileInputStream(Path.FilePaths.getFilePaths(dataSetName, "teamprojectlist_threesome"));
		else if (numberOfProjectSA == 4)
			fstream = new FileInputStream(Path.FilePaths.getFilePaths(dataSetName, "teamprojectlist_foursome"));
		else if (numberOfProjectSA == 5)
			fstream = new FileInputStream(Path.FilePaths.getFilePaths(dataSetName, "teamprojectlist_five"));

		BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
		String Line;
		while ((Line = br.readLine()) != null) 
		{
			String team_mem = Line.split(";")[0];
			String[] team = team_mem.split(",");
			
			for (String u : team)
				if(! user_list_sample.contains(Integer.parseInt(u)))
				   user_list_sample.add(Integer.parseInt(u));

		}
		return user_list_sample;

	}
	
	
	
	public List<Integer> getUser_list_sample() {
		return user_list_sample;
	}

	public void setUser_list_sample(List<Integer> user_list_sample) {
		this.user_list_sample = user_list_sample;
	}
	
	
}
