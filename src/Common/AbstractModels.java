package Common;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import Technical.CreateUserANDCountSA;

public abstract class AbstractModels {

	
	/*private static Analyzer analyzer;
	private static IndexSearcher searcher;
	private static IndexReader reader;*/
	static TeamLeaderMain main;

	
	public abstract HashMap<Integer, Double> findProjectManagers(List<User> team , List<String> project, AbstractModels model, int k, 
			LinkedHashMap<String, LinkedHashMap<Integer, Double>> SA__userId_Balogprobability, Map<Integer, HashMap<String,Double>> usersa, Map<String, Double> SA_probability_sa_C) throws IOException, ParseException;//balog
	
	
	//communication
	
	public abstract HashMap<Integer, Double> findProjectManagers(List<User> team , Map<Integer, HashMap<Integer, Double>> graph, Map<Integer, HashMap<Integer, Double>> distance, Map<Integer, HashMap<Integer, List<Integer>>> distance_tree, double min_distance, double max_distance, double graph_threshold, int max_EdgeScore, AbstractModels model) throws IOException, ParseException;
	
	
	
	public AbstractModels(TeamLeaderMain m )
	{
		main = m;
		/*analyzer = m.getAnalyzer();
		searcher = m.getSearcher();
		reader = m.getReader();
		hits = m.getHits();
		numTotalHits = m.getNumTotalHits();
		userdoc = m.getUserdoc();*/
		
	}
	public AbstractModels()
	{
		
		
		
	}
	/*public  void search(String query , int MaxhitsPerPage) throws IOException, ParseException
	{
		
		
		//query = query.replace("-", " AND ");
		parseQuery(indexpath);
		QueryParser bodyparser = new QueryParser(SOFFields.AnswerSkillArea.toString(), this.getAnalyzer());
        Query bodyquery = bodyparser.parse(query);  
        BooleanQuery booleanQuery = new BooleanQuery.Builder()
        	    .add(bodyquery, BooleanClause.Occur.MUST)
        	    .build();
       
        
        TopDocs results = this.getSearcher().search(booleanQuery, MaxhitsPerPage);
        hits = results.scoreDocs;
        numTotalHits =  results.totalHits;
        System.out.println(numTotalHits + " total matching documents\n-------");
	}*/
	
	 
	public HashMap<Integer, Double> sortByValue(Map<Integer, Double> userscore)
	{
		HashMap<Integer, Double> sorted = new LinkedHashMap<Integer, Double>(); 
        userscore.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        return sorted;
	}
	
	public List<String> UnionSA(List<User> team, List<String> project) {
		
		 List<String> SAs = new ArrayList<String>();
		 SAs.addAll(project);
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
		return SAs;
	}
	
/*public Map<Integer, HashMap<String, Integer>> Result(int hitsPerPage) throws IOException {
		
	
	
		
	    int start = 0;
        //long end = Math.min(this.getNumTotalHits(), hitsPerPage);
        long end = this.getNumTotalHits();
        Map<Integer, HashMap<String,Integer>> result = new HashMap<Integer, HashMap<String,Integer>>();//keys is candidate list and value is count of each skill area
        HashMap<String,Integer> doc_sa = null;
	    int limit = start;
	    List<Post> docs;
	    
        for (ScoreDoc i : Arrays.asList(this.getHits()))
        {
            if (limit < end) 
            {
            	
            	Document doc = this.getSearcher().doc(i.doc);
            	int uid = Integer.parseInt(doc.get(SOFFields.OwnerUserId.toString()));
            	String sa = doc.get(SOFFields.AnswerSkillArea.toString());

            	/////////////////////////////////////
            	if(result.containsKey(uid))
	        	{
            		if(result.get(uid).containsKey(sa))
            			result.get(uid).replace(sa, result.get(uid).get(sa)+1);
            		else
            			result.get(uid).put(sa, 1);
	        	}
	        	else
	        	{
	        		doc_sa = new HashMap<String,Integer>();
	        		doc_sa.put(sa, 1);
	        		result.put(uid, doc_sa);
	        	}
            	///////////////////////////////////////////
            	StringBuilder sb = new StringBuilder();
				sb.append(doc.get(SOFFields.DocSize.toString()));
				sb.append(",");
				sb.append(doc.get(SOFFields.DocId.toString()));
				Post post = new Post();
				post.setDocsize(doc.get(SOFFields.DocSize.toString()));
				post.setDocId(doc.get(SOFFields.DocId.toString()));
				post.setBody(doc.get(SOFFields.Body.toString()));
				
            	if(userdoc.containsKey(uid))
	        	{
	        		
            		userdoc.get(uid).add(post);
	        	}
	        	else
	        	{
	        		docs = new ArrayList<Post>();
	        		docs.add(post);
	        		userdoc.put(uid, docs);
	        	}
            	
            } 
            else 
            {
                break;
            }
            limit++;
        }
        

	        return result;
	}*/

	/*public void parseQuery(String dir) throws IOException
	{
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(dir)));
      searcher = new IndexSearcher(getReader());
      analyzer = new StandardAnalyzer();
	}*/

	
/*	public IndexReader getReader() {
		return reader;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}


	public IndexSearcher getSearcher() {
		return searcher;
	}

	public ScoreDoc[] getHits() {
		return hits;
	}

	public void setHits(ScoreDoc[] hits) {
		AbstractModels.hits = hits;
	}

	public long getNumTotalHits() {
		return numTotalHits;
	}

	public void setNumTotalHits(long numTotalHits) {
		AbstractModels.numTotalHits = numTotalHits;
	}
	public Map<Integer, List<Post>> getUserdoc() {
		return userdoc;
	}
	public void setUserdoc(Map<Integer, List<Post>> userdoc) {
		this.userdoc = userdoc;
	}

	
	
	
	
*/
	
}


