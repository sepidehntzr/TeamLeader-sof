package Common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class CreateSampleQuery {

	static Map<String,User> Technical_goldset;
	 List<User> userlist = new ArrayList<User>();
	 FileWriter writer;
	//parameters	
	 String dataSetName = "C#";
	 int numberOfProjectSA = 5;
	 int numberOfTeamMembers = 5;
	 static List<String> SA_categores;
	 static Map<String, Integer> SA_categores_HighLevel;
	 double count_query = 1;	
	 int max_query = 1000;
	  Map<String, List<User>> SA_users;
	  Map<User, List<String>> user_SAs;
	  
	public static void main(String[] args) throws IOException, ParseException, ParserConfigurationException, SAXException 
	{
		CreateSampleQuery m = new CreateSampleQuery();
		SA_categores = m.makeCategore();
		SA_categores_HighLevel = m.makeHighLevelCategore();
		
		String Technical_goldset_file = Path.FilePaths.getFilePaths(m.dataSetName, "Technical_goldset");
		m.SA_users = m.parsGold(new File(Technical_goldset_file), "Technical");
		if(m.numberOfProjectSA == 3)
		    m.writer = new FileWriter(Path.FilePaths.getFilePaths(m.dataSetName, "teamprojectlist_threesome"));
		else if(m.numberOfProjectSA == 4)
			m.writer = new FileWriter(Path.FilePaths.getFilePaths(m.dataSetName, "teamprojectlist_foursome"));
		else if(m.numberOfProjectSA == 5)
			m.writer = new FileWriter(Path.FilePaths.getFilePaths(m.dataSetName, "teamprojectlist_query_five"));
		
		
		m.projectFormation(SA_categores, m.numberOfTeamMembers);
		//m.max_query = m.userlist.size()/m.numberOfTeamMembers;
		//m.createSampleTeamProject(m.userlist, m.numberOfTeamMembers);
		m.writer.close();
		System.out.print("Done");

	}
	
	
	private Map<String, Integer> makeHighLevelCategore() throws IOException 
	{
		
		SA_categores_HighLevel = new HashMap<String, Integer>();
		List<String> list = new ArrayList<String>();
		
		LineNumberReader reader = new LineNumberReader(new FileReader(Path.FilePaths.getFilePaths(dataSetName, "SAHightCategores")));
        String line = "";
        while((line = reader.readLine())!= null){
        	int levelnumber = Integer.parseInt( line.substring(0, line.indexOf(":")));
        	line = line.substring(line.indexOf(":")+1, line.length());
        	SA_categores_HighLevel.put(line, levelnumber);
        }
		
		
		return SA_categores_HighLevel;
	}
	
	private List<String> makeCategore() throws IOException 
	{
		SA_categores = new ArrayList<String>();
		List<String> list = new ArrayList<String>();
		
		
		LineNumberReader reader = new LineNumberReader(new FileReader(Path.FilePaths.getFilePaths(dataSetName, "SACategores")));
        String line = "";
        int i = 1;
        while((line = reader.readLine())!= null){
        	//list = Arrays.asList(line.split(","));   
        	SA_categores.add(line);
        	
        }
		
		
		return SA_categores;
	}

	/*private void createSampleTeamProject(List<User> userlist, int numberOfTeamMembers) throws IOException, ParseException 
	 {
		
		   for(int i=0; i<max_query; i++)
		  //for(int i=0; i<10; i++)
		  {
			  Collections.shuffle(userlist);
			  List<User> userlist_sample = new ArrayList<User>(userlist.subList(0, numberOfTeamMembers));//sampling 50 user
			  userlist.removeAll(userlist_sample);
			  teamFormation(userlist_sample, numberOfTeamMembers); 
		  }
		  
		  // teamFormation(userlist, numberOfTeamMembers); 
		   writer.close();
			
	 }*/
	
	void teamFormation(List<User> userlist, int k) throws IOException, ParseException {
    	User[] subset = new User[k];
    	processLargerSubsetsTeams(userlist, subset, 0, 0);
    }
    
	 void processLargerSubsetsTeams(List<User> userlist2, User[] subset, int subsetSize, int nextIndex) throws IOException, ParseException {
    	//if (subsetSize == subset.length & count_query < max_query ) 
        if (subsetSize == subset.length ) 
    	{
    		processTeams(subset);
        }
    	else if(subsetSize != subset.length  )
    	{
            for (int j = nextIndex; j < userlist2.size(); j++) {
            	subset[subsetSize] = userlist2.get(j);
            	processLargerSubsetsTeams(userlist2, subset, subsetSize + 1, j + 1);
            }
        }
    }
	 void processTeams(User[] subset) throws IOException, ParseException {
	    	List<User> team = new ArrayList<User>();
	    	for(User s : subset)
	    		team.add(s);
	    	
	    	List<ArrayList<String>> Projects = detectProjects(team); 
	    	
	    	if(Projects != null)
	    	   for(ArrayList<String> project : Projects)
	    	   {
	    		   count_query ++; System.out.println(count_query);
	    		 //  if(count_query < max_query)

	    		   {
	    			   for(User u : team)
		    			   writer.write(u.getUserId()+",");
		    		   writer.write(";");
		    		   for(String u : project)
		    			   writer.write(u+",");
		    		   writer.write(";");
		    		   writer.write("\n");
	    		   }
	    		   /*else
	    			   break;*/
	    	   }

	    }
	 
 private List<ArrayList<String>> detectProjects(List<User> team) throws IOException, ParseException 
 {
		

	 List<String> SAs = new ArrayList<String>();
    	for(User user : team)
    	{
    		if(user.getSa_int()!= null)
    		   for(String sa : user.getSa_int())
				   if(!SAs.contains(sa))
					   SAs.add(sa);
    		if(user.getSa_adv()!= null)
    		    for(String sa : user.getSa_adv())
				   if(!SAs.contains(sa))
					   SAs.add(sa);
    	}
    	
    	List<ArrayList<String>> Projects = new ArrayList<ArrayList<String>>();
    	if(SAs.size() >= numberOfProjectSA)
    	    Projects = projectFormation(SAs, numberOfProjectSA);
    	
    return Projects;	
 }
 

 
 ////////////////////////////////////////////////////////////////////////////////////////////////////
private List<ArrayList<String>> projectFormation(List<String> SAs, int k) throws IOException
 {
	 List<ArrayList<String>> Projects = new ArrayList<ArrayList<String>>();
 	
 	processSubsets(SAs, k , Projects);
 	
 	return Projects;
 }
  void processSubsets(List<String> SAs, int k, List<ArrayList<String>> projects) throws IOException {
 	String[] subset = new String[k];
     processLargerSubsets(SAs, subset, 0, 0 , projects);
 }
  void processLargerSubsets(List<String> SAs, String[] subset, int subsetSize, int nextIndex, List<ArrayList<String>> projects) throws IOException {
		if (subsetSize == subset.length) {
			processProject(subset, projects);
	    } else {
	        for (int j = nextIndex; j < SAs.size(); j++) {
	        	subset[subsetSize] = SAs.get(j);
	            processLargerSubsets(SAs, subset, subsetSize + 1, j + 1, projects);
	        }
	    }
	}

  void processProject(String[] subset, List<ArrayList<String>> projects) throws IOException {
	 ArrayList<String> project = new ArrayList<String>();
	 
	 
	 //check
	 Boolean cheak = false;
	 
	 int[] levelnum = new int[SA_categores.size()];
	 int i =0;
	 for(int num : levelnum)
		 levelnum[i++] = 0;
	 
	 for(String s : subset)
		 levelnum [SA_categores_HighLevel.get(s)] = levelnum [SA_categores_HighLevel.get(s)]+1;
	
	for(int num : levelnum)
	{
		if(num > 1)
		{
			cheak = true;
			break;
		}
	}
	
	if(cheak == false)
	{
		
		for(String s : subset)
	 		project.add(s);
	 	
		List<List<User>> userslist = new ArrayList<List<User>>();
		List<User> totalintersect = new ArrayList<User>();
		List<User> totalintersect2 = new ArrayList<User>();
		int total_first_time = 0;
		int total_check_even = 0;
		
		
		//String sa_single = project.get(0);
		//project.remove(0);
		
	 	for(String catSkill : project)
	 	{
	 		String[] skills = catSkill.split("|");
	 		List<User> intersect = new ArrayList<User>();
	 		//List<User> ulist = new ArrayList<User>();
	 		int first_time = 0;
	 		for(String sa : skills)//A
	 		{
	 			List<User> list = SA_users.get(sa);
	 			/*
	 			if(list!= null)
	 			  for(User u : list)
	 				if(!ulist.contains(u))
	 					ulist.add(u);
	 			*/		
	 			
	 			if(first_time == 0 && list!= null)
	 			   intersect = intersection(list, list);
	 			else if(list!= null)
	 				 intersect = intersection(list, intersect);
	 			first_time++;
	 		}
	 		
	 		if(total_first_time == 0)
	 			totalintersect = intersection(intersect, intersect);
	 		else
	 			totalintersect = intersection(intersect, totalintersect);
	 		total_first_time++;
	 		
	 		if(total_check_even == 0)
	 			totalintersect2 = intersection(intersect, intersect);
	 		else if ( (total_check_even & 1) == 0 ) //check even
	 			totalintersect2 = intersection(intersect, totalintersect2);
	 		total_check_even ++;
	 			
	 		////////////////////////////////////////////// sampeling
	 		int numberOfrandommember = 3;
	 		Collections.shuffle(intersect);
	 		 List<User> user_sample = new ArrayList<User>(intersect.subList(0, numberOfrandommember));
	 		userslist.add(user_sample);
	 	}
	 	
	 	
	 	List<User> team = new ArrayList<User>();
	 	//List<List<User>> teams = new ArrayList<List<User>>();

	 	List<TempContainer<User>> containers = new ArrayList<TempContainer<User>>(numberOfTeamMembers);
	 	List<List<User>> userslist_final = new ArrayList<List<User>>();
	 	
	 	for(List<User> uu: userslist)
	 	{
	 		uu.remove((User)uu.get(0));
	 		uu.remove((User)uu.get(0));
	 		
	 		if(totalintersect.contains(uu.get(0)))
	 			totalintersect.remove((User)uu.get(0));
	 		if(totalintersect2.contains(uu.get(0)))
	 			totalintersect2.remove((User)uu.get(0));
	 		
	 		uu.add(totalintersect.get(0));
	 		
	 		
	 		if(totalintersect2.contains(totalintersect.get(0)))
	 		    totalintersect2.remove((User)totalintersect.get(0));
	 		
	 		totalintersect.remove((User)totalintersect.get(0));
	 		

	 		uu.add(totalintersect2.get(0));
	 		totalintersect2.remove((User)totalintersect2.get(0));
	 		
	 		userslist_final.add(uu);
	 	}
	 	
	 	//List<User> user_sample = forSingleSA(sa_single, userslist_final);
	 	//userslist.add(user_sample);
	 	//project.add(sa_single);
	 	
	 	for(List<User> uu: userslist_final)
	 	{
	 		TempContainer container1 = new TempContainer();
	 		container1.setItems(uu);
	 		containers.add(container1);
	 	}
	 	List<List<User>> teams = getCombination(0, containers);
	 	
	 	
	 	if(teams != null)
	 	   for(List<User> teamm : teams)
	 	   {
	 		   count_query ++; System.out.println(count_query);
	 		   
	 		   int c = 0; boolean membersuniqe = true;
		 		  for(User u : teamm)
		 		  {
		 			 for(User u2 : teamm)
		 				 if(u2.getUserId().equals(u.getUserId()))
		 					 c++;
		 			 if(c>1)
		 			 {
		 				membersuniqe = false;
		 				break;
		 			 }
		 			 c=0;
		 		  }
		 		   
		 		  if(membersuniqe == true)
		 		  {
		 			 for(User u : teamm)
		    			   writer.write(u.getUserId()+",");
		    		   writer.write(";");
		    		   for(String u : project)
		    			   writer.write(u+",");
		    		   writer.write(";");
		    		   writer.write("\n");
		    		   writer.flush();
		 		  }
   
	 	   }
	 	
	}//if
	 
 	
 }
  private List<User> forSingleSA(String sa_single, List<List<User>> userslist_final) 
  {
	 
	  List<User> usersforsampeling = new ArrayList<User>();
	  List<User> user_sample = null;
	  //////////////////////////////////////////////////
	  String[] skills = sa_single.split("|");
		List<User> intersect = new ArrayList<User>();
		int first_time = 0;
		for(String sa : skills)//A
		{
			List<User> list = SA_users.get(sa);
		
			if(first_time == 0 && list!= null)
			   intersect = intersection(list, list);
			else if(list!= null)
				 intersect = intersection(list, intersect);
			first_time++;
		}
		
			
		////////////////////////////////////////////// sampeling
		for(User u : intersect)
		{
			boolean isremove = false;
			List<String> u_sa = user_SAs.get(u);
			List<String> posi_sa = new ArrayList<>();
			
			//for(int i = userslist_final.size()-1; i>=0 ; i--)
			{
				List<User> posi_users = userslist_final.get(0);
				User user = posi_users.get(0);
				//for(User user : posi_users)
				{
					List<String> sa = user_SAs.get(user);
					for(String s : sa)
						if(!posi_sa.contains(s))
							posi_sa.add(s);
				}
					////
			}
			for(String s : posi_sa)
			   if(u_sa.contains(s))
			   {
				   isremove = true; break;}
			
			if(isremove == false)
				usersforsampeling.add(u);
		}
		/////////////////////////////////////////////
		if(!usersforsampeling.isEmpty())
		{
			int numberOfrandommember = 3;
			Collections.shuffle(usersforsampeling);
			 user_sample = new ArrayList<User>(usersforsampeling.subList(0, numberOfrandommember));
			
		}
		else
		{
			int numberOfrandommember = 3;
			Collections.shuffle(intersect);
			 user_sample = new ArrayList<User>(intersect.subList(0, numberOfrandommember));
			
		}
		return user_sample;
}


private List<List<User>> getCombination(int currentIndex, List<TempContainer<User>> containers) {
	    if (currentIndex == containers.size()) {
	        // Skip the items for the last container
	        List<List<User>> combinations = new ArrayList<List<User>>();
	        combinations.add(new ArrayList<User>());
	        return combinations;
	    }
	    List<List<User>> combinations = new ArrayList<List<User>>();
	    TempContainer<User> container = containers.get(currentIndex);
	    List<User> containerItemList = container.getItems();
	    // Get combination from next index
	    List<List<User>> suffixList = getCombination(currentIndex + 1, containers);
	    int size = containerItemList.size();
	    for (int ii = 0; ii < size; ii++) {
	    	User containerItem = containerItemList.get(ii);
	        if (suffixList != null) {
	            for (List<User> suffix : suffixList) {
	                List<User> nextCombination = new ArrayList<User>();
	                if(!nextCombination.contains(containerItem))
	                   nextCombination.add(containerItem);
	                else
	                	break;
	                nextCombination.addAll(suffix);
	                combinations.add(nextCombination);
	            }
	        }
	    }
	    return combinations;
	}
  public class TempContainer<T> {
	    private List<T> items; 
	    public void setItems(List<T> items) {
	       this.items = items;
	    }

	    public List<T> getItems() {
	         return items;
	    }
	}
	private Map<String, List<User>> parsGold(File inputFile, String type) throws IOException, ParserConfigurationException, SAXException
    {
		
		Map<String, List<User>> SA_users  = new HashMap<String, List<User>>();
		user_SAs = new HashMap<User, List<String>>();
    	//Map<String,User> tempreldocs = new HashMap<String,User>();   
    	
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        
        NodeList nList = doc.getElementsByTagName("row");

        int i = 0; 
        for (int temp = 0; temp < nList.getLength(); temp++) {
           Node nNode = nList.item(temp);

          
           if (nNode.getNodeType() == Node.ELEMENT_NODE) 
           {
              Element eElement = (Element) nNode;
              User u = new User();
              if(eElement.hasAttribute("UserId"))
            	  u.setUserId(eElement.getAttribute("UserId"));
              if(eElement.hasAttribute("Shape"))
        		  u.setShape(eElement.getAttribute("Shape"));
        	  if(eElement.hasAttribute("Advanced"))
        		  u.setSa_adv(Arrays.asList(eElement.getAttribute("Advanced").split(",")));
        	  if(eElement.hasAttribute("Intermediate"))
        		  u.setSa_int(Arrays.asList(eElement.getAttribute("Intermediate").split(",")));
        	  if(eElement.hasAttribute("Beginner"))
        		  u.setSa_beg(Arrays.asList(eElement.getAttribute("Beginner").split(",")));
    	  
            	  
            
             if(type.equals("Technical") && (u.getSa_adv()!= null || u.getSa_int()!= null))//coverage
            {
            	 List<String> sa = new ArrayList<String>();
            	 if(u.getSa_adv()!= null)
            	    sa.addAll(u.getSa_adv());
            	 if(u.getSa_int()!= null)
            	    sa.addAll(u.getSa_int());
            	
            	 for(String s : sa)
            	 {
            		 if(SA_users.containsKey(s))
            		 {
            			 List<User> list = SA_users.get(s);
            			 list.add(u);
            			 SA_users.replace(s, list);
            		 }
            		 else
            		 {
            			 List<User> list = new ArrayList<User>();
            			 list.add(u);
            			 SA_users.put(s, list);
            		 }
            	 }
            	 
            	////////////////////SA_categores
            	 for(String HSkill: SA_categores)
     			{
     				HSkill = HSkill.replace("|", ",");
     				List<String> SAs = Arrays.asList(HSkill.split(","));  
     				if(sa.containsAll(SAs))
     				{
     					if(user_SAs.containsKey(u))
     	            	 {
     	            		 List<String> list = user_SAs.get(u);
     	            		if(!list.contains(HSkill))
 	            				list.add(HSkill);
     	            		 user_SAs.replace(u, list);
     	            	 }
     	            	 else
     	            	 {
     	            		 List<String> list = new ArrayList<String>();
     	        			 list.add(HSkill);
     	            		 user_SAs.put(u, list);
     	            	 }
     				}
     				
     			}
            	 
            }//if
            	  
              
             
           }//if
        }
    	
     
		return SA_users;

	}
	public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

}
