
package student;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Edge;
import models.Node;
import models.ReturnStage;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class Paths {

	/** Return the shortest path from start to end, or the empty list if a path
     * does not exist. Keeps track of the number of hostile planets visited on said path. 
     * If the number of hostile planets visited is greater than or equal to 3, then the method
     * shortestPath2() is called, which represents a "back-up" method for finding another path that
     * avoids visiting too many hostile planets. 
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
    public static List<Node> shortestPath(Node start, Node end, ReturnStage state) {
        /* TODO Read note A7 FAQs on the course piazza for ALL details. */
        Heap<Node> F= new Heap<Node>(); // As in lecture slides

        // map contains an entry for each node in S or F. Thus,
        // |map| = |S| + |F|.
        // For each such key-node, the value part contains the shortest known
        // distance to the node and the node's backpointer on that shortest path.
        HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();
        List<Node> list = new LinkedList<Node>();
        
        SFdata sfObj = new SFdata(0, null, 0);

        F.add(start, 0);
        map.put(start, sfObj); 
        // invariant: as in lecture slides, together with def of F and map
        
        while (F.size() != 0) {
            Node f= F.poll();
            
            if (f == end) {
            	if (map.get(end).numHP >= 3) {
            		return shortestPath2(start, end, state);
            	} else {
	            	return constructPath(end, map);
	            }
            }
            
            SFdata fData = map.get(f);
            int numHp = fData.numHP;
            
            double fDist= map.get(f).distance;
            
            for (Edge e : f.getExits()) { // for each neighbor w of f. Consider if f.getExits leads to hostile planet
                Node w= e.getOther(f);
                double newWdist= fDist + e.length;
                SFdata wData= map.get(w);

                if (wData == null) { //if w not in S or F
                	if (w.isHostile()) {
                		numHp++; // increment number of hostile planets 
              	    }
            		F.add(w, newWdist);
            		map.put(w, new SFdata(newWdist, f, numHp));
                }
                
                else if (newWdist < wData.distance) {
                	if (w.isHostile()) {
                		numHp++;
                	}
                	wData.distance= newWdist; 
                    wData.backPointer= f; 
                    wData.numHP = numHp;
                    F.updatePriority(w, newWdist); 
                }    
            }
        }
        

        // no path from start to end
        return new LinkedList<Node>();
        
    }
    
    /** Return the shortest path from start to end, or the empty list if a path
     * does not exist. This is considered a backup Dijkstra's. It is called when in the
     * original Dijkstra's (shortestPath()), a path is found with 3 hostile planets. In that case, 
     * we call this method. Rather than only update the priority for a third hostile planet, this method updates
     * the priority of a second hostile planet, as well, by increasing the distance to a hostile planet by 
     * an arbitrary value e.length stored in variable modifier. 
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
    public static List<Node> shortestPath2(Node start, Node end, ReturnStage state) {
    	/* TODO Read note A7 FAQs on the course piazza for ALL details. */
    	Heap<Node> F= new Heap<Node>(); // As in lecture slides
    	
    	// map contains an entry for each node in S or F. Thus,
    	// |map| = |S| + |F|.
    	// For each such key-node, the value part contains the shortest known
    	// distance to the node and the node's backpointer on that shortest path.
    	HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();
    	List<Node> list = new LinkedList<Node>();
    	
    	SFdata sfObj = new SFdata(0, null, 0);
    	
    	F.add(start, 0);
    	map.put(start, sfObj); 
    	// invariant: as in lecture slides, together with def of F and map
    	
    	while (F.size() != 0) {
    		Node f= F.poll();
    		
    		if (f == end) {
    			if (map.get(end).numHP >= 3) {
    				return shortestPath3(start, end, state);
    			}
            	return constructPath(end, map);
	        }
       
    		SFdata fData = map.get(f);
    		
    		double fDist= map.get(f).distance;
    		double speed = state.getSpeed();
    		
    		for (Edge e : f.getExits()) { // for each neighbor w of f. Consider if f.getExits leads to hostile planet
        		int numHp = fData.numHP;
    			Node w= e.getOther(f);
    			double newWdist= fDist + e.length/speed;
    			SFdata wData= map.get(w);
    			double modifier = e.length;
    			
    			if (wData == null) { //if w not in S or F
    				if (w.isHostile()) {
    					numHp++; // increment number of hostile planets 
    					if (numHp == 3) {
    						newWdist += modifier;
    					} else if (numHp == 2) {
    						newWdist += modifier;
    					}
    				}
    				F.add(w, newWdist);
    				map.put(w, new SFdata(newWdist, f, numHp));
    			}
    			
    			else if (newWdist < wData.distance) {
    				if (w.isHostile()) {
    					numHp++;
    					if (numHp == 3) {
    						newWdist += modifier;
    					} else if (numHp == 2) {
    						newWdist += modifier;
    					}
    				}
    				wData.distance= newWdist; 
    				wData.backPointer= f; 
    				wData.numHP = numHp;
    				F.updatePriority(w, newWdist); 
    			}
    		}
    	}
    	
    	
    	// no path from start to end
    	return new LinkedList<Node>();
    	
    }
    
    /** Return the shortest path from start to end, or the empty list if a path
     * does not exist. This is considered another backup Dijkstra's. If a third hostile planet
     * is still found in a path in this method, this method will call shortestPath4().
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
    public static List<Node> shortestPath3(Node start, Node end, ReturnStage state) {
    	/* TODO Read note A7 FAQs on the course piazza for ALL details. */
    	Heap<Node> F= new Heap<Node>(); // As in lecture slides
    	
    	// map contains an entry for each node in S or F. Thus,
    	// |map| = |S| + |F|.
    	// For each such key-node, the value part contains the shortest known
    	// distance to the node and the node's backpointer on that shortest path.
    	HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();
    	List<Node> list = new LinkedList<Node>();
    	
    	SFdata sfObj = new SFdata(0, null, 0);
    	
    	F.add(start, 0);
    	map.put(start, sfObj); 
    	// invariant: as in lecture slides, together with def of F and map
    	
    	while (F.size() != 0) {
    		Node f= F.poll();
    		
    		if (f == end) {
    			if (map.get(end).numHP >= 3) {
    				return shortestPath4(start, end, state);
    			}
            	return constructPath(end, map);
	        }
    		
    		SFdata fData = map.get(f);
    		
    		double fDist= map.get(f).distance;
    		
    		for (Edge e : f.getExits()) { // for each neighbor w of f. Consider if f.getExits leads to hostile planet
        		int numHp = fData.numHP;
    			Node w= e.getOther(f);
    			double newWdist= fDist + e.length;
    			SFdata wData= map.get(w);
    			double modifier = e.length;
    			
    			if (wData == null) { //if w not in S or F
    				if (w.isHostile()) {
    					numHp++; // increment number of hostile planets
    					if (numHp >= 2) {
        					newWdist += modifier;
    					}
    				}
    				F.add(w, newWdist);
    				map.put(w, new SFdata(newWdist, f, numHp));
    			}
    			
    			else if (newWdist < wData.distance) {
    				if (w.isHostile()) {
    					numHp++;
    					if (numHp >= 2) {
        					newWdist += modifier;
    					}
    				}
    				wData.distance= newWdist; 
    				wData.backPointer= f; 
    				wData.numHP = numHp;
    				F.updatePriority(w, newWdist); 
    			}	
    		}
    	}
    	
    	
    	// no path from start to end
    	return new LinkedList<Node>();
    	
    }

    /** Return the shortest path from start to end, or the empty list if a path
     * does not exist. This is considered a backup Dijkstra's. It is called when in shortestPath3(), 
     * a path is found with 3 hostile planets. In that case, we call this method. Rather than 
     * only update the priority for a third hostile planet, this method updates
     * the priority of a second hostile planet, as well, by increasing the distance to a hostile planet by 
     * a factor of 1000 in order to guarantee avoidance of that hostile planet. 
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
    public static List<Node> shortestPath4(Node start, Node end, ReturnStage state) {
    	/* TODO Read note A7 FAQs on the course piazza for ALL details. */
    	Heap<Node> F= new Heap<Node>(); // As in lecture slides
    	
    	// map contains an entry for each node in S or F. Thus,
    	// |map| = |S| + |F|.
    	// For each such key-node, the value part contains the shortest known
    	// distance to the node and the node's backpointer on that shortest path.
    	HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();
    	List<Node> list = new LinkedList<Node>();
    	
    	SFdata sfObj = new SFdata(0, null, 0);
    	
    	F.add(start, 0);
    	map.put(start, sfObj); 
    	// invariant: as in lecture slides, together with def of F and map
    	
    	while (F.size() != 0) {
    		Node f= F.poll();
    		
    		if (f == end) {
    			return constructPath(end, map);
    		}
    		
    		SFdata fData = map.get(f);
    		double fDist= map.get(f).distance;
    		
    		for (Edge e : f.getExits()) { // for each neighbor w of f. Consider if f.getExits leads to hostile planet
        		int numHp = fData.numHP;
    			Node w= e.getOther(f);
    			double newWdist= fDist + e.length;
    			SFdata wData= map.get(w);
    			double modifier = 1000;
    			
    			if (wData == null) { //if w not in S or F
    				if (w.isHostile()) {
    					numHp++; // increment number of hostile planets
    					newWdist *= modifier;
    				}
    				F.add(w, newWdist);
    				map.put(w, new SFdata(newWdist, f, numHp));
    			}
    			
    			else if (newWdist < wData.distance) {
    				if (w.isHostile()) {
    					numHp ++;
    					newWdist *= modifier;	
    				}
    				wData.distance= newWdist; 
    				wData.backPointer= f; 
    				wData.numHP = numHp;
    				F.updatePriority(w, newWdist); 
    			}	
    		}
    	}
    	
    	// no path from start to end
    	return new LinkedList<Node>();
    	
    }
    
    
    /** Return the path from the start node to node end.
     *  Precondition: nData contains all the necessary information about
     *  the path. */
    public static List<Node> constructPath(Node end, HashMap<Node, SFdata> nData) {
    	LinkedList<Node> path= new LinkedList<Node>();
    	Node p= end;
    	// invariant: All the nodes from p's successor to the end are in
    	//            path, in reverse order.
    	while (p != null) {
    		path.addFirst(p);
    		p= nData.get(p).backPointer;
    	}
    	
    	return path;
    }

    /** Return the sum of the weights of the edges on path path. */
    public static int pathDistance(List<Node> path) {
        if (path.size() == 0) return 0;
        synchronized(path) {
            Iterator<Node> iter= path.iterator();
            Node p= iter.next();  // First node on path
            int s= 0;
            // invariant: s = sum of weights of edges from start to p
            while (iter.hasNext()) {
                Node q= iter.next();
                s= s + p.getConnect(q).length;
                p= q;
            }
            return s;
        }
    }
    

    /** An instance contains information about a node: the previous node
     *  on a shortest path from the start node to this node and the distance
     *  of this node from the start node. */
    private static class SFdata {
        private Node backPointer; // backpointer on path from start node to this one
        private double distance; // distance from start node to this one
        private int numHP; // number of hostile planets visited

        /** Constructor: an instance with distance d from the start node and
         *  backpointer p.*/
        private SFdata(double d, Node p, int h) {
            distance= d;     // Distance from start node to this one.
            backPointer= p;  // Backpointer on the path (null if start node)
            numHP = h;       // Number of hostile planets visited before reaching this node
        }

        /** return a representation of this instance. */
        public String toString() {
            return "dist " + distance + ", bckptr " + backPointer;
        }
    }
}
