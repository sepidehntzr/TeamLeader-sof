package Technical;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
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

import Common.SOFFields;

public class CreateUserANDCountSA 
{


	private static Analyzer analyzer;
	private static IndexSearcher searcher;
	private static IndexReader reader;
    private static ScoreDoc[] hits;
	private static long numTotalHits;
	
	private LinkedHashMap<Integer, ArrayList<String>> QId_SAList = new LinkedHashMap<>();//soal va SA ye soal
	private HashMap<Integer, Integer> QId_Asker = new HashMap<>();
	private HashMap<Integer, Integer> QId_AcceptedAnswerId = new HashMap<>();
	
	Map<Integer, HashMap<Integer,HashMap<String, Double>>> askeruser_answerer = new HashMap<Integer, HashMap<Integer,HashMap<String, Double>>>();//keys is candidate list and value is count of each Type
	Map<Integer, HashMap<String, Double>> askeruser_SACount= new HashMap<Integer, HashMap<String, Double>>();//keys is candidate list and value is count of each Type
	
	//List<String> dataset_Skills;
	
	Map<Integer, HashMap<String,Double>> usersa = new HashMap<Integer, HashMap<String,Double>>();//keys is candidate list and value is count of each skill area
	Map<String, HashMap<Integer,Double>> sauser = new HashMap<String, HashMap<Integer,Double>>();//keys is skill area and value is count of each candidate list
	Map<String, Double> sasumdoc = new HashMap<String, Double>(); //key is skill area and value sum of doc
	Map<String, Double> SA_Maxuserdoc = new HashMap<String, Double>();
	Map<String, Double> SA_Minuserdoc = new HashMap<String, Double>();
	Map<String, Double> SA_standard_div = new HashMap<String, Double>();
	Map<String, Double> SA_median = new HashMap<String, Double>();
	Map<String, Double> SA_MAD = new HashMap<String, Double>();
	
	Map<String, HashMap<Integer,Double>> sauser_normalized = new HashMap<String, HashMap<Integer,Double>>();
	Map<String, Double> SA_Maxuserdoc_normalized = new HashMap<String, Double>();
	
	ArrayList<String> SAs_Tech;
	ArrayList<String> HighleveltechSkills ;
	Map<Integer, HashMap<String,Double>> usersa_Highlevel = new HashMap<Integer, HashMap<String,Double>>();//keys is candidate list and value is count of each skill area
	Map<String, HashMap<Integer,Double>> sauser_Highlevel = new HashMap<String, HashMap<Integer,Double>>();
	Map<String, HashMap<Integer,Double>> sauser_normalized_Highlevel = new HashMap<String, HashMap<Integer,Double>>();
	Map<String, Double> sasumdoc_Highlevel = new HashMap<String, Double>();
	public static double totalDoc = 0;
	private  LinkedHashMap<String,String> Tag_SA ;
	
	Map<String, Double> sasumdoc_withone = new HashMap<String, Double>();
	
	Map<String, HashMap<String, Double>> confusion_matrix = new HashMap<String, HashMap<String, Double>>();
	
	public CreateUserANDCountSA(String skillAreasPath, String questionIndexPath, String answerIndexPath, String SACategores) throws IOException, ParseException {
		
		
		
		SkillArea SAObject = new SkillArea(skillAreasPath);
		Tag_SA = SAObject.getTagSALists();
		//dataset_Skills = SAObject.getSkills();
		String q = "*:*";
		search(q, 406751, questionIndexPath, answerIndexPath);
		usersa = Result(406751);
		sasumdoc = CalcSA_SumDoc(sauser);
		sasumdoc_withone = CalcSA_SumDoc_withone(sauser);
		totalDoc = reader.numDocs();
		SA_Maxuserdoc_normalized = min_maxNormalization2(SA_Maxuserdoc);
		
		for(Entry<String, HashMap<Integer, Double>> sa_user : sauser.entrySet())
			sauser_normalized.put(sa_user.getKey(), min_maxNormalization(sa_user.getValue()));
		
		SAs_Tech = getTechSkills(SACategores);
		////////////// for comm
		HighleveltechSkills = getHighlevelTechSkills(SACategores);
		usersa_Highlevel = makeHighlevelUserSa(usersa, HighleveltechSkills);
		for(Entry<String, HashMap<Integer, Double>> sa_user : sauser_Highlevel.entrySet())
			sauser_normalized_Highlevel.put(sa_user.getKey(), min_maxNormalization(sa_user.getValue()));
		sasumdoc_Highlevel = CalcSA_SumDoc_withone(sauser_Highlevel);
		
		buildConfusionMatrix(); // for LM model
		//System.out.print("");
		
	}
	
	
	
	

	private void buildConfusionMatrix() 
	{
		for(int i=0; i<HighleveltechSkills.size(); i++)
		{
			String q_sa = HighleveltechSkills.get(i);
			confusion_matrix.put(q_sa, new HashMap<String, Double>());
			
			Map<Integer, Integer> neguser_count = new HashMap<Integer,Integer>();
			Map<String, Integer> negsa_count = new HashMap<String,Integer>();
			
			for(Entry<Integer, HashMap<String, Double>> user_sa : usersa_Highlevel.entrySet())
			{
				if((!sauser_Highlevel.get(q_sa).containsKey(user_sa.getKey())) || (sauser_Highlevel.get(q_sa).get(user_sa.getKey()) <= 6) )
				{
					if(neguser_count.containsKey(user_sa.getKey()))
						neguser_count.replace(user_sa.getKey(), neguser_count.get(user_sa.getKey())+1);
					else
						neguser_count.put(user_sa.getKey(), 1);
					
					
					for(Entry<String, Double> neg_sa : user_sa.getValue().entrySet())
					{
						if(negsa_count.containsKey(neg_sa.getKey()))
							negsa_count.replace(neg_sa.getKey(), negsa_count.get(neg_sa.getKey())+1);
						else
							negsa_count.put(neg_sa.getKey(), 1);
					}
				}
			}//user_sa
			double neguser_size = neguser_count.size();
			double colluser_size = usersa_Highlevel.size();
			for(Entry<String, Integer> nsa_cont : negsa_count.entrySet())
			{
				double p_negsa = nsa_cont.getValue()/neguser_size;
				double tf_sa_coll = 0;
				HashMap<Integer, Double> collectionusers= sauser_Highlevel.get(nsa_cont.getKey());
				for(Entry<Integer, Double> collu_contdoc : collectionusers.entrySet())
					if(collu_contdoc.getValue() >= 6)
						tf_sa_coll ++;
				double p_negsa_coll = tf_sa_coll/colluser_size;
				double lambda = 0.5;
				double p_negsa_qsa = (lambda * p_negsa) + (1-lambda)*p_negsa_coll ;
				
				HashMap<String , Double> sa_p =  confusion_matrix.get(q_sa);
				sa_p.put(nsa_cont.getKey(), p_negsa_qsa);
				confusion_matrix.replace(q_sa, sa_p);
			}
		}
	}





	private Map<Integer, HashMap<String, Double>> makeHighlevelUserSa(Map<Integer, HashMap<String, Double>> usersa,
			ArrayList<String> HighleveltechSkills) 
	{
		
		for(Entry<Integer, HashMap<String, Double>> user : usersa.entrySet())
		{
			int i = 0;
			for(String HSkill: HighleveltechSkills)
			{
				HSkill = HSkill.replace("|", ",");
				String[] SAs = HSkill.split(",");
				
				double sum_sa = 0;
				double max_sa = 0;
				for(String sa : SAs)
				{
					if(user.getValue().containsKey(sa))
					{
						//sum_sa += user.getValue().get(sa);
						double d = user.getValue().get(sa);
						if(d > max_sa)
							max_sa = d;
						//if(d!=0 && d!= 1)
							sum_sa += d;//////////////////?
						//else//////////////////////////////////?
						{
							//sum_sa = 0;
							//break;
						}
						
					}
					    
				}
				HSkill = HSkill.replace(",", "|");
				////
				if(max_sa != 0)
				{
					if(usersa_Highlevel.containsKey(user.getKey()))
					{
						HashMap<String,Double> sa_count = usersa_Highlevel.get(user.getKey());
						sa_count.put(HSkill, max_sa);
						usersa_Highlevel.replace(user.getKey(), sa_count);
					}
					else
					{
						HashMap<String,Double> sa_count = new HashMap<String,Double>();
						sa_count.put(HSkill, max_sa);
						usersa_Highlevel.put(user.getKey(), sa_count);
					}
					
					
					//////////////////////////////////////
					
					if(sauser_Highlevel.containsKey(HSkill))
					{
						HashMap<Integer,Double> user_count = sauser_Highlevel.get(HSkill);
						user_count.put(user.getKey(), max_sa);
						sauser_Highlevel.replace(HSkill, user_count);
					}
					else
					{
						HashMap<Integer,Double> user_count = new HashMap<Integer,Double>();
						user_count.put(user.getKey(), max_sa);
						sauser_Highlevel.put(HSkill, user_count);
					}
				}
				
				//////////////////////////////////////
				i++;
			}// HSkill for
			
		}
		
		
		
		return usersa_Highlevel;
	}





	private  void search(String query , int MaxhitsPerPage, String questionIndexPath, String answerIndexPath) throws IOException, ParseException
	 {
		 String qindex = questionIndexPath;
		 String aindex = answerIndexPath;
		 	
		 	
		 	parseQuery(qindex);
		 	QueryParser bodyparser = new QueryParser("Id", analyzer);
		     Query bodyquery = bodyparser.parse(query);  

		     TopDocs results = searcher.search(bodyquery, Integer.MAX_VALUE);
		     hits = results.scoreDocs;
		     numTotalHits =  results.totalHits;
		     System.out.println(numTotalHits + " total matching documents\n-------");//2282399
		     
		     for (ScoreDoc scoreDoc : results.scoreDocs) {
		            Document doc = searcher.doc(scoreDoc.doc);
		            int QId = Integer.parseInt(doc.getField("Id").stringValue());
		            String[] tags = doc.getValues("Tags");
		            ArrayList<String> skillAreaList = getSAsFromTags(tags);
		            if (!skillAreaList.isEmpty()) {//yani to list tag hayi k mikhym bashe //impo
		                QId_SAList.put(QId, skillAreaList);
		            }
		            int asker = Integer.parseInt(doc.getField("OwnerUserId").stringValue());
		            QId_Asker.put(QId, asker);
		            
		            int AcceptedAnswerId  = 0;
	         		if(doc.get("AcceptedAnswerId")!= null)
	         		  AcceptedAnswerId = Integer.parseInt(doc.get("AcceptedAnswerId"));
	         		QId_AcceptedAnswerId.put(QId, AcceptedAnswerId);
		           // QId_Asker
		        }
		     //////////////////////////////////////finish question detect
		     
		     parseQuery(aindex);
			 	QueryParser bodyparser2 = new QueryParser(SOFFields.Id.toString(), analyzer);
			     Query bodyquery2 = bodyparser2.parse(query);  
			     results = searcher.search(bodyquery2, Integer.MAX_VALUE);
			     hits = results.scoreDocs;
			     numTotalHits =  results.totalHits;
			     System.out.println(numTotalHits + " total matching documents\n-------");//2282399
			     
			     
	 }
	 private void parseQuery(String dir) throws IOException
	 {
	 	reader = DirectoryReader.open(FSDirectory.open(Paths.get(dir)));
	   searcher = new IndexSearcher(reader);
	   analyzer = new StandardAnalyzer();
	 }

	 public Map<Integer, HashMap<String, Double>> Result(int hitsPerPage) throws IOException {
	 	
	 	
		 Map<Integer, HashMap<String,Double>> result = new HashMap<Integer, HashMap<String,Double>>();//keys is candidate list and value is count of each skill area
		 HashMap<String,Double> doc_sa = null;
	     HashMap<Integer,Double> doc_uid = null;
	     
		 for (ScoreDoc scoreDoc : hits) 
		 {
	            Document doc = searcher.doc(scoreDoc.doc);
	            int parantId = Integer.parseInt(doc.getField("ParentId").stringValue());
	            int uid = Integer.parseInt(doc.getField("OwnerUserId").stringValue());
	            
	           
	            int postid = Integer.parseInt(doc.get("Id"));
         		
	            //for trainingset : postid == QId_AcceptedAnswerId.get(parantId) ?????
	            if (QId_SAList.containsKey(parantId) && uid != -1 )//&& postid == QId_AcceptedAnswerId.get(parantId)) ///???
	            {
	            	List<String> SAList = QId_SAList.get(parantId);//SAs
	            	/////////////////////////////////////////////////////////////
	            	if(result.containsKey(uid))
		         	{
		         		for(int j=0; j<SAList.size(); j++)
		         		{
		         			if(result.get(uid).containsKey(SAList.get(j)))
		             			result.get(uid).replace(SAList.get(j), result.get(uid).get(SAList.get(j))+1);
		             		else
		             			result.get(uid).put(SAList.get(j), 1.0);
		         		}	
		         	}
		         	else
		         	{
		         		for(int j=0; j<SAList.size(); j++)
		         		{
		         			if(j==0) {
		         			doc_sa = new HashMap<String,Double>();
		             		doc_sa.put(SAList.get(j), 1.0);
		             		result.put(uid, doc_sa);
		         			}
		             		else
		             			result.get(uid).put(SAList.get(j),1.0);
		         		}
		         		
		         	}//else
	            	/////////////////////////////////////////////////////////
	            	
	            	for(int j=0; j<SAList.size(); j++)
		         	{
		         		if(sauser.containsKey(SAList.get(j)))
		             	{
		             		if(sauser.get(SAList.get(j)).containsKey(uid))
		             			sauser.get(SAList.get(j)).replace(uid, sauser.get(SAList.get(j)).get(uid)+1);
		             		else
		             			sauser.get(SAList.get(j)).put(uid, 1.0);
		             	}
		             	else
		             	{
		             		doc_uid = new HashMap<Integer,Double>();
		             		doc_uid.put(uid, 1.0);
		             		sauser.put(SAList.get(j), doc_uid);
		             	}
		         	}
	            	/////////////////////////////////////////////for graph(intimacy)
	            	
	            	createGraph(Integer.parseInt(doc.get("Id")), uid, parantId);
	            	
	            }//if
	        } //docs
		 

		          return result;
	 }
	 private void createGraph(int postid, int answereriud, int parentid) {
		 
		 ArrayList<String> QSAs = QId_SAList.get(parentid);
    	 int askeruid = QId_Asker.get(parentid);
    	 
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

 		       	    HashMap<Integer, HashMap<String, Double>> user_sacount = new HashMap<Integer, HashMap<String, Double>>();
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



	private Map<String, Double> CalcSA_SumDoc_withone(Map<String, HashMap<Integer, Double>> sauser)
	 {
           Map<String, Double> result = new HashMap<String, Double>();
		 
		 for (Entry<String, HashMap<Integer, Double>> entry : sauser.entrySet())
		  	{
		  		
		      	String sa = entry.getKey();
		      	HashMap<Integer, Double> userdocs = entry.getValue();
		  		double sum = 0;
		  		for (Entry<Integer, Double> entry2 : userdocs.entrySet()) 
		  		{
		  			double value = entry2.getValue();
		  			
		  			sum += value;
		  		}
		  		result.put(sa,sum);
		  	}
		 return result;
		  		
	 }
	 private Map<String, Double> CalcSA_SumDoc(Map<String, HashMap<Integer, Double>> sauser)
	 {
		 Map<String, Double> result = new HashMap<String, Double>();
		 
		 for (Entry<String, HashMap<Integer, Double>> entry : sauser.entrySet())
		  	{
		  		
		      	String sa = entry.getKey();
		      	HashMap<Integer, Double> userdocs = entry.getValue();
		  		double sum = 0;
		  		SA_Maxuserdoc.put(sa, 0.0);
		  		SA_Minuserdoc.put(sa, Double.MAX_VALUE);
		  		
		  		double median = 0;
				 double standard_div = 0;
				 double MAD = 0;
				 
				 ArrayList<Double> list = new ArrayList<Double>();
				 TreeSet<Double> set = new TreeSet<Double>();  
		  		for (Entry<Integer, Double> entry2 : userdocs.entrySet()) 
		  		{
		  			double value = entry2.getValue();
		  			if(value == 1)
		  				value = 0;
		  			sum += value;
		  			if(value > SA_Maxuserdoc.get(sa))
		  				SA_Maxuserdoc.replace(sa, value);
		  			if(value < SA_Minuserdoc.get(sa))
		  				SA_Minuserdoc.replace(sa, value);
		  			
		  			//////////////////////for new idea
		  			
		  			 standard_div +=Math.pow(entry2.getValue(), 2);
					 set.add(entry2.getValue());
					 list.add(entry2.getValue());
		  		}
		  		Collections.sort(list);
		  		median = list.get(list.size()/2);
		  		
		  		 int count = 0;
				 /*Iterator<Double> itr=set.iterator();  
				  while(itr.hasNext() && (count++) <set.size()/2)
					  median = itr.next();*/
				  
				  set = new TreeSet<Double>();  
				  for (Entry<Integer, Double> entry2 : userdocs.entrySet()) 
						set.add(Math.abs((entry2.getValue()-median)));
				  
				  count = 0;
				  Iterator<Double> itr2=set.iterator(); 
				  while(itr2.hasNext() && (count++) <set.size()/2)
				     MAD = itr2.next(); 
		  		
				SA_standard_div.put(sa, standard_div);  
				SA_median.put(sa, median); 
				SA_MAD.put(sa, MAD); 
		  		result.put(sa,sum);
		  	}
		 return result;
	 }
	 private ArrayList<String> getSAsFromTags(String[] tags) {
	        ArrayList<String> SAList = new ArrayList<>();
	        for (String tag : tags) {
	            if (Tag_SA.containsKey(tag)) {
	                String SA = Tag_SA.get(tag);
	                if (!SAList.contains(SA)) {
	                    SAList.add(SA);
	                }
	            }
	        }
	        return SAList;
	    }
	 
	 private ArrayList<String> getHighlevelTechSkills(String SACategores) throws IOException {
			ArrayList<String> SA_categores = new ArrayList<String>();
			
			
			LineNumberReader reader = new LineNumberReader(new FileReader(SACategores));
	        String line = "";
	        while((line = reader.readLine())!= null){
	        	//line = line.replace("|", ",");
	        	//List<String> list = Arrays.asList(line.split(","));   
	        	//SA_categores.addAll(list);
	        	SA_categores.add(line);
	        }
			
			
			return SA_categores;
		}
	 
	 private ArrayList<String> getTechSkills(String SACategores) throws IOException {
			ArrayList<String> SA_categores = new ArrayList<String>();
			
			
			LineNumberReader reader = new LineNumberReader(new FileReader(SACategores));
	        String line = "";
	        while((line = reader.readLine())!= null){
	        	line = line.replace("|", ",");
	        	List<String> list = Arrays.asList(line.split(","));   
	        	SA_categores.addAll(list);
	        }
			
			
			return SA_categores;
		}
	 private HashMap<Integer, Double> min_maxNormalization(Map<Integer, Double> user_value) {
			
			HashMap<Integer, Double> temp = new HashMap<Integer, Double>();
		
			double min = 0;
			//double min = (double)Collections.min(user_value.values());
			double max = (double)Collections.max(user_value.values());
			
			if(!Double.isNaN(max))
		     for (Entry<Integer, Double> entry : user_value.entrySet()) 
		     {
		    	double dd = entry.getValue();
		    	//if(dd == 1)
		    		//dd = 0;
		    	//if(min == 1)
		    		//min =0;
		    	
		    	 double d = (dd-min)/(max-min);
		    	 if(Double.isNaN(d))
		    		 d = 0;
		    	 temp.put(entry.getKey(), d);
		     }
			
			
		    return temp;
			
		}
	 private HashMap<String, Double> min_maxNormalization2(Map<String, Double> sA_score_maximumCoverage) {
			
			HashMap<String, Double> temp = new HashMap<String, Double>();
		
			
			double min = (double)Collections.min(sA_score_maximumCoverage.values());
			double max = (double)Collections.max(sA_score_maximumCoverage.values());
			
			if(!Double.isNaN(max))
		     for (Entry<String, Double> entry : sA_score_maximumCoverage.entrySet()) 
		     {
		    	 double d = (entry.getValue()-min)/(max-min);
		    	 if(Double.isNaN(d))
		    		 d = 0;
		    	 temp.put(entry.getKey(), d);
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
		 public Map<Integer, HashMap<String, Double>> getUsersa() {
		 	return usersa;
		 }
		 public Map<String, HashMap<Integer, Double>> getSauser() {
		 	return sauser;
		 }
		 public Map<String, Double> getSasumdoc() {
		 	return sasumdoc;
		 }



		public Map<String, Double> getSA_Maxuserdoc() {
			return SA_Maxuserdoc;
		}



		public Map<String, Double> getSA_Minuserdoc() {
			return SA_Minuserdoc;
		}



		public void setSA_Minuserdoc(Map<String, Double> sA_Minuserdoc) {
			SA_Minuserdoc = sA_Minuserdoc;
		}



		public void setSA_Maxuserdoc(Map<String, Double> sA_Maxuserdoc) {
			SA_Maxuserdoc = sA_Maxuserdoc;
		}



		public Map<String, Double> getSA_standard_div() {
			return SA_standard_div;
		}



		public void setSA_standard_div(Map<String, Double> sA_standard_div) {
			SA_standard_div = sA_standard_div;
		}



		public Map<String, Double> getSA_median() {
			return SA_median;
		}



		public void setSA_median(Map<String, Double> sA_median) {
			SA_median = sA_median;
		}



		public Map<String, Double> getSA_MAD() {
			return SA_MAD;
		}



		public void setSA_MAD(Map<String, Double> sA_MAD) {
			SA_MAD = sA_MAD;
		}



		public Map<String, Double> getSasumdoc_withone() {
			return sasumdoc_withone;
		}



		public void setSasumdoc_withone(Map<String, Double> sasumdoc_withone) {
			this.sasumdoc_withone = sasumdoc_withone;
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



		public Map<String, Double> getSA_Maxuserdoc_normalized() {
			return SA_Maxuserdoc_normalized;
		}



		public void setSA_Maxuserdoc_normalized(Map<String, Double> sA_Maxuserdoc_normalized) {
			SA_Maxuserdoc_normalized = sA_Maxuserdoc_normalized;
		}



		public Map<String, HashMap<Integer, Double>> getSauser_normalized() {
			return sauser_normalized;
		}



		public void setSauser_normalized(Map<String, HashMap<Integer, Double>> sauser_normalized) {
			this.sauser_normalized = sauser_normalized;
		}





		public Map<Integer, HashMap<String, Double>> getUsersa_Highlevel() {
			return usersa_Highlevel;
		}





		public void setUsersa_Highlevel(Map<Integer, HashMap<String, Double>> usersa_Highlevel) {
			this.usersa_Highlevel = usersa_Highlevel;
		}





		public Map<String, HashMap<Integer, Double>> getSauser_Highlevel() {
			return sauser_Highlevel;
		}





		public void setSauser_Highlevel(Map<String, HashMap<Integer, Double>> sauser_Highlevel) {
			this.sauser_Highlevel = sauser_Highlevel;
		}





		public Map<String, HashMap<Integer, Double>> getSauser_normalized_Highlevel() {
			return sauser_normalized_Highlevel;
		}





		public void setSauser_normalized_Highlevel(Map<String, HashMap<Integer, Double>> sauser_normalized_Highlevel) {
			this.sauser_normalized_Highlevel = sauser_normalized_Highlevel;
		}





		public ArrayList<String> getHighleveltechSkills() {
			return HighleveltechSkills;
		}





		public void setHighleveltechSkills(ArrayList<String> highleveltechSkills) {
			HighleveltechSkills = highleveltechSkills;
		}





		public Map<String, HashMap<String, Double>> getConfusion_matrix() {
			return confusion_matrix;
		}





		public void setConfusion_matrix(Map<String, HashMap<String, Double>> confusion_matrix) {
			this.confusion_matrix = confusion_matrix;
		}





		public Map<String, Double> getSasumdoc_Highlevel() {
			return sasumdoc_Highlevel;
		}





		public void setSasumdoc_Highlevel(Map<String, Double> sasumdoc_Highlevel) {
			this.sasumdoc_Highlevel = sasumdoc_Highlevel;
		}





		public ArrayList<String> getSAs_Tech() {
			return SAs_Tech;
		}





		public void setSAs_Tech(ArrayList<String> sAs_Tech) {
			SAs_Tech = sAs_Tech;
		}



		
		 
		
		 
}
