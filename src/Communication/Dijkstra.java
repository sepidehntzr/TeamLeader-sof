package Communication;

import java.util.*; // For HashMap
import java.util.Map.Entry;

public final class Dijkstra {
    /**
     * Given a directed, weighted graph G and a source node s, produces the
     * distances from s to each other node in the graph.  If any nodes in
     * the graph are unreachable from s, they will be reported at distance
     * +infinity.
     *
     * @param graph The graph upon which to run Dijkstra's algorithm.
     * @param source The source node in the graph.
     * @return A map from nodes in the graph to their distances from the source.
     */
	static Map<Integer, List<Integer>> paths;
	static double min_distance = Double.MAX_VALUE;
	static double max_distance = 0;
	
	
    public static <T> Map<T, Double> shortestPaths(DirectedGraph<T> graph, T source, double max_value) {
    	
    	paths = new HashMap<Integer, List<Integer>>();
    	min_distance = Double.MAX_VALUE;
    	max_distance = 0;
        /* Create a Fibonacci heap storing the distances of unvisited nodes
         * from the source node.
         */
    	
    	//if(source.equals(625424))
    		//System.out.print("");
        FibonacciHeap<T> pq = new FibonacciHeap<T>();

        /* The Fibonacci heap uses an internal representation that hands back
         * Entry objects for every stored element.  This map associates each
         * node in the graph with its corresponding Entry.
         */
        Map<T, FibonacciHeap.Entry<T>> entries = new HashMap<T, FibonacciHeap.Entry<T>>();

        /* Maintain a map from nodes to their distances.  Whenever we expand a
         * node for the first time, we'll put it in here.
         */
        Map<T, Double> result = new HashMap<T, Double>();

        Map<T, T> node_parent = new HashMap<>();
        /* Add each node to the Fibonacci heap at distance +infinity since
         * initially all nodes are unreachable.
         */
        for (T node: graph)
            entries.put(node, pq.enqueue(node, Double.POSITIVE_INFINITY));

        /* Update the source so that it's at distance 0.0 from itself; after
         * all, we can get there with a path of length zero!
         */
        pq.decreaseKey(entries.get(source), 0.0);

        /* Keep processing the queue until no nodes remain. */
        while (!pq.isEmpty()) {
            /* Grab the current node.  The algorithm guarantees that we now
             * have the shortest distance to it.
             */
            FibonacciHeap.Entry<T> curr = pq.dequeueMin();
            //if(curr.getValue().equals(163186))
        		//System.out.print( curr.getValue());
            detectPath(curr.getValue(), node_parent, source);
            
            if(curr.getValue() != source && curr.getPriority() != 0)
            {
            	if(curr.getPriority() < min_distance)
            		min_distance = curr.getPriority(); // update
            	if(curr.getPriority() > max_distance && curr.getPriority() != max_value)
            		max_distance = curr.getPriority(); // update
            }
            /* Store this in the result table. */
            result.put(curr.getValue(), curr.getPriority());

            /* Update the priorities of all of its edges. */
            for (Map.Entry<T, Double> arc : graph.edgesFrom(curr.getValue()).entrySet()) {
                /* If we already know the shortest path from the source to
                 * this node, don't add the edge.
                 */
                if (result.containsKey(arc.getKey())) continue;

                /* Compute the cost of the path from the source to this node,
                 * which is the cost of this node plus the cost of this edge.
                 */
                double pathCost = curr.getPriority() + arc.getValue();

                /* If the length of the best-known path from the source to
                 * this node is longer than this potential path cost, update
                 * the cost of the shortest path.
                 */
                FibonacciHeap.Entry<T> dest = entries.get(arc.getKey());
                if (pathCost < dest.getPriority()) {
                    pq.decreaseKey(dest, pathCost);
                    node_parent.put(arc.getKey(),  curr.getValue());
                    //if(arc.getKey().equals(163186))
                		//System.out.print( curr.getValue());
                }
            }
        }

        /* Finally, report the distances we've found. */
       //System.out.print( node_parent.get(163186));
       // System.out.print( paths.get(163186));
        return result;
    }

     
    
	private static <T> void detectPath(T node, Map<T, T> node_parent, T source) 
	{
		List<Integer> node_path = new ArrayList<Integer>();
		
		if(!node_parent.isEmpty())
		{
			T p = node_parent.get(node);
			while(p != source)
			{
				node_path.add((Integer) p);
				p = node_parent.get(p);
			}
		}
		

		paths.put((Integer) node, node_path);
	}



	public static Map<Integer, List<Integer>> getPaths() {
		return paths;
	}



	public static double getMin_distance() {
		return min_distance;
	}


	public static double getMax_distance() {
		return max_distance;
	}





	
	
}