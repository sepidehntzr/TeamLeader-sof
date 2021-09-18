package Communication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

public class Dijkstra_old {
	
	private HashMap<Integer, Double> dist;
	private HashMap<Integer, List<Integer>> path;
	//private double dist[]; 
    private Set<Integer> settled; 
   // private PriorityQueue<Node> pq; 
    private FibonacciHeap<Node> fh; 
    private int V; // Number of vertices 
    Map<Integer, HashMap<Integer, Double>> adj; 
    static Map<Integer, HashMap<Integer, Double>> Distance;
   
    
	/*public static void main(String arg[]) throws IOException 
    {
		
		HashMap<Integer, HashMap<Integer, Double>> user_weight = BuildGraph();
		Distance = new HashMap<Integer, HashMap<Integer, Double>>();
		for (Entry<Integer, HashMap<Integer, Double>> entry : user_weight.entrySet()) //users	
	    {
			Dijkstra dpq = new Dijkstra(entry.getValue().size()+1); 
	        dpq.dijkstra(entry.getValue(), entry.getKey()); 
	        
	        
	        Distance.put(entry.getKey(), dpq.getDist());
	       
	    }
		
		System.out.println("Done"); 
		
    }*/

	public Dijkstra_old() 
	{
		
	}
	public Dijkstra_old(int V) 
    { 
        this.V = V; 
        setDist(new HashMap<Integer, Double>());
        path = new HashMap<Integer, List<Integer>>(); 
        settled = new HashSet<Integer>(); 
       // pq = new PriorityQueue<Node>(V, new Node()); 
        fh = new FibonacciHeap<Node>(); 
    } 
  
	public void dijkstra(Map<Integer, HashMap<Integer, Double>> subgraph, int src) 
    { 
        this.adj = subgraph; 
  
        for (Entry<Integer, HashMap<Integer, Double>> entry : subgraph.entrySet())//all nodes in graph
        	for(Entry<Integer, Double> entry2 : entry.getValue().entrySet())
        	    getDist().put(entry2.getKey(), Double.MAX_VALUE); 
  
        // Add source node to the priority queue 
        
        //pq.add(new Node(src, 0)); 
        fh.enqueue(new Node(src, 0),0);
        
        // Distance to the source is 0
        getDist().put(src, 0.0);  
        while (settled.size() != V) { 
  
            // remove the minimum distance node  
            // from the priority queue  
        	
           // int u = pq.remove().node; 
            Communication.FibonacciHeap.Entry<Node> nn = fh.dequeueMin();
            int u = nn.getValue().node;
            
            // adding the node whose distance is 
            // finalized 
            settled.add(u); 
  
            e_Neighbours(u); 
        } 
        
       // System.out.print("doneee");
    } 
  
    // Function to process all the neighbours  
    // of the passed node 
    private void e_Neighbours(int u) 
    { 
        double edgeDistance = -1; 
        double newDistance = -1; 
  
        // All the neighbors of v
        for (Entry<Integer, Double> entry : adj.get(u).entrySet())
        {
       
           
  
            // If current node hasn't already been processed 
            if (!settled.contains(entry.getKey())) 
            { 
                edgeDistance = entry.getValue(); 
                newDistance = getDist().get(u) + edgeDistance; 
  
                // If new distance is cheaper in cost 
                if (newDistance < getDist().get(entry.getKey())) 
                {
                	getDist().put(entry.getKey(), newDistance);
                	//path.put(entry.getKey(), u);
                }

  
                // Add the current node to the queue 
                
               // pq.add(new Node(entry.getKey(), getDist().get(entry.getKey()))); 
                fh.enqueue(new Node(entry.getKey(), getDist().get(entry.getKey())), getDist().get(entry.getKey()));
            } 
        } 
    } 
  
    /////////////////////////////////
	private static HashMap<Integer, HashMap<Integer, Double>> BuildGraph() throws IOException
	{
		
		String path = "D:\\1- Data, Datasets, project outputs\\proj-output\\d-thesis\\Results\\graph"+"\\"+"CommunicationGraph"+".csv";
		HashMap<Integer, HashMap<Integer, Double>> user_participation = new HashMap<Integer, HashMap<Integer, Double>>();
		FileInputStream fstream = new FileInputStream(path);
		 BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

	        br.readLine();
	        String Line;
	        while ((Line = br.readLine()) != null)
	        { 
	        	Line = Line.substring(0, Line.length()-2);
	        	int user1 = Integer.parseInt(Line.substring(0, Line.indexOf(",")));
	        	String temp = Line.substring(Line.indexOf(",")+2, Line.length());
	        	int user2 = Integer.parseInt(temp.substring(0, temp.indexOf(",")));
	        	temp = Line.substring(Line.indexOf(",")+2, Line.length());
	        	double weight = Double.parseDouble(temp.substring(0, temp.indexOf(",")));
	        	
	        	if(user_participation.containsKey(user2))
	        	{
	        		HashMap<Integer, Double> map = user_participation.get(user2);
	        		map.put(user1, weight);
	        		user_participation.replace(user2, map);
	        	}
	        	else
	        	{
	        		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		        	map.put(user1, weight);
		        	user_participation.put(user2, map);
	        	}
	        	
	        	
	        }
		return user_participation;
	}

	public static Map<Integer, HashMap<Integer, Double>> getDistance() {
		return Distance;
	}

	public static void setDistance(Map<Integer, HashMap<Integer, Double>> distance) {
		Distance = distance;
	}

	public HashMap<Integer, Double> getDist() {
		return dist;
	}

	public void setDist(HashMap<Integer, Double> dist) {
		this.dist = dist;
	}
	
	
}


class Node implements Comparator<Node> { 
    public int node; 
    public double cost; 
  
    public Node() 
    { 
    } 
  
    public Node(int node, double dist) 
    { 
        this.node = node; 
        this.cost = dist; 
    } 
  
    @Override
    public int compare(Node node1, Node node2) 
    { 
        if (node1.cost < node2.cost) 
            return -1; 
        if (node1.cost > node2.cost) 
            return 1; 
        return 0; 
    } 
} 
