package Technical;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class userAnswererGraph {
	//directed graph
	// weight is number of answers and answer score
	static userAnswererGraph m;
	private static Analyzer analyzer;
	private static IndexSearcher searcher;
	private static IndexReader reader;
    private static ScoreDoc[] hits;
	private static long numTotalHits;
	List<String> dates; 
	String dataSetName = "PM";
	String PostIndex = "E:\\1- Data, Datasets, project outputs\\proj-output\\Dataset-Thesis\\Results\\ProjectManager\\Index\\ManagerialIndexs\\PostIndex";
	Map<Integer, HashMap<Integer,HashMap<String, Double>>> askeruser_answerer = new HashMap<Integer, HashMap<Integer,HashMap<String, Double>>>();//keys is candidate list and value is count of each Type
	Map<Integer, HashMap<String, Double>> askeruser_SACount= new HashMap<Integer, HashMap<String, Double>>();//keys is candidate list and value is count of each Type
	
	private LinkedHashMap<Integer, ArrayList<String>> QId_SAList;
	private HashMap<Integer, Integer> QId_Asker;
	HashMap<Integer, int[]> answerid_uid;
	//Map<Integer, List<Integer>> co_answerers_temp = new HashMap<Integer, List<Integer>>();//keys is candidate list and value is count of each Type
	//Map<Integer, List<int[]>> co_answerers = new HashMap<Integer, List<int[]>>();
	public userAnswererGraph(LinkedHashMap<Integer, ArrayList<String>> Q_SAList, HashMap<Integer, Integer> Q_Asker, HashMap<Integer, int[]> answerpostid_uid_parentid) throws IOException, ParseException
	{
		QId_SAList = Q_SAList;
		QId_Asker = Q_Asker;
		answerid_uid = answerpostid_uid_parentid;
		findDailyActivity();
		
	}
	

	public void findDailyActivity() throws IOException, ParseException {	
		postResult(406751);
	
		
	}

	

	
	
	 public void postResult(int hitsPerPage) throws IOException {
	
	     HashMap<Integer, HashMap<String, Double>> user_sacount = null;
	     
	     for (Entry<Integer, int[]> entry : answerid_uid.entrySet()) //users	
		 {
	    	 
	    	

	    	 int[] a1 = entry.getValue();
	    	 int answereriud = a1[0];
	    	 int parentid = a1[1];

	    	 
	    	 ArrayList<String> QSAs = QId_SAList.get(parentid);
	    	 int askeruid = QId_Asker.get(parentid);
	    	 
	    	 
	    	 //
	    	 if(answereriud!=-1 && askeruid!=-1 && answereriud != askeruid)
	    	 {
	    		 
	    		 if(QSAs != null)
	    		 for(String sa : QSAs)
	    		 {
	    			 
	    			 if(askeruser_answerer.containsKey(askeruid))
	 		       	{
	 		       		
	 		       			if(askeruser_answerer.get(askeruid).containsKey(answereriud))
	 		       			{
	 		       				HashMap<String, Double> sa_count = askeruser_answerer.get(askeruid).get(answereriud);
	 		       				
	 		       				if(sa_count.containsKey(sa))
	 		       				{
	 		       					sa_count.replace(sa, sa_count.get(sa)+1);
	 		       				    askeruser_answerer.get(askeruid).replace(answereriud, sa_count);
	 		       				}
	 		       				else
	 		       				{
	 		       				   sa_count.put(sa, 1.0);
	 		       				   askeruser_answerer.get(askeruid).replace(answereriud, sa_count);
	 		       				}
	 		       				
	 		       			}
	 		       				
	 		           		else
	 		           		{
	 		           		    HashMap<String, Double> sa_count = new HashMap<String, Double>();
	 		           		    sa_count.put(sa, 1.0);
	 		           			askeruser_answerer.get(askeruid).put(answereriud, sa_count);
	 		           		}
	 		       		
	 		       	}
	 		       	else
	 		       	{

	 		       		user_sacount = new HashMap<Integer, HashMap<String, Double>>();
	 		       	    HashMap<String, Double> sa_count = new HashMap<String, Double>();
	           		    sa_count.put(sa, 1.0);
	           		    user_sacount.put(answereriud, sa_count);
	 		       		askeruser_answerer.put(askeruid, user_sacount);


	 		       		
	 		       		
	 		       	}
	    			 
	    			 if(askeruser_SACount.containsKey(askeruid))
		 		     {
	    				 if(askeruser_SACount.get(askeruid).containsKey(sa))
	    				 {
	    					 HashMap<String, Double> aa = askeruser_SACount.get(askeruid);
	    					 aa.replace(sa, aa.get(sa)+1);
	    					 askeruser_SACount.replace(askeruid, aa);
	    				 }
	    				 else
	    				 {
	    					 HashMap<String, Double> aa = askeruser_SACount.get(askeruid);
	    					 aa.put(sa, 1.0);
	    					 askeruser_SACount.replace(askeruid, aa);
	    				 }
		 		     }
	    			 else
	    			 {
	    				 HashMap<String, Double> aa = new HashMap<String, Double>();
    					 aa.put(sa, 1.0);
    					 askeruser_SACount.put(askeruid, aa);
	    			 }
	    			 
	    			 //askeruser_SACount
	    			 
	    		 }//for
	    		 
	    		 
		    	
			    
	    	 }//if
		 }
  
	}
	 
	 //////////////////////////////////
	 private HashMap<Integer, Double> min_maxNormalization(List<Integer> list) {
			
		 HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
		
		 
			double min = Collections.min(list);
			double max = Collections.max(list);
			
			if(!Double.isNaN(max))
		     for (int entry : list) 
		     {
		    	 
		    	 temp.put(entry, (double)(entry-min)/(double)(max-min));
		     }
			
			
		    return temp;
			
		}
	 
	 public static Analyzer getAnalyzer() {
			return analyzer;
		}

		public static IndexSearcher getSearcher() {
			return searcher;
		}


		public static IndexReader getReader() {
			return reader;
		}


		public static ScoreDoc[] getHits() {
			return hits;
		}


		public static long getNumTotalHits() {
			return numTotalHits;
		}


		public Map<Integer, HashMap<Integer, HashMap<String, Double>>> getAskeruser_answerer() {
			return askeruser_answerer;
		}


		public void setAskeruser_answerer(Map<Integer, HashMap<Integer, HashMap<String, Double>>> askeruser_answerer) {
			this.askeruser_answerer = askeruser_answerer;
		}


		public Map<Integer, HashMap<String, Double>> getAskeruser_SACount() {
			return askeruser_SACount;
		}


		public void setAskeruser_SACount(Map<Integer, HashMap<String, Double>> askeruser_SACount) {
			this.askeruser_SACount = askeruser_SACount;
		}

		

		/*public Map<Integer, List<int[]>> getCo_answerers() {
			return co_answerers;
		}

		public void setCo_answerers(Map<Integer, List<int[]>> co_answerers) {
			this.co_answerers = co_answerers;
		}
		*/
		
}
