package main.pathfinder.informed;

//import java.util.ArrayList;
//import java.util.List;

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
class SearchTreeNode implements Comparable<SearchTreeNode> {
    // [!] TODO: You're free to modify this class to your heart's content
    
    MazeState state;
    String action;
    SearchTreeNode parent;
    public int pastCost;
    public int futureCost;
    
    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (row, col) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    public SearchTreeNode (MazeState state, String action, SearchTreeNode parent, int pastCost, int futureCost) {
        // [!] TODO: You may modify the constructor as you please
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.pastCost = pastCost;
        this.futureCost = futureCost;
        
    }


    @Override
    public int compareTo(SearchTreeNode n) {
    	return this.futureCost - n.futureCost;
    } //higher cost nodes should be sent to the back
    
    /*public int manhattan(SearchTreeNode target){
    	int distance = Math.abs(this.state.col - target.state.col) + Math.abs(this.state.row - target.state.row);
    		return distance;
    }*/
    
    // Parent in the path
    //public Node parent = null;

    /*public List<Edge> neighbors;

    // Evaluation functions
    public double f = Double.MAX_VALUE;
    public double g = Double.MAX_VALUE;
    // Hardcoded heuristic
    public double h; */

    /*public SearchTreeNode(double h){
          this.h = h;
          //this.id = idCounter++;
          this.neighbors = new ArrayList<>();
    }*/
    
    /*public static class Edge {
          Edge(int weight, SearchTreeNode node){
                this.weight = weight;
                this.node = node;
          }

          public int weight;
          public SearchTreeNode node;
    }*/

    /*public void addBranch(int weight, SearchTreeNode node){
          Edge newEdge = new Edge(weight, node);
          neighbors.add(newEdge);
    }*/

    
    // [!] TODO: Any other fields you want to add
    

    
    // [!] TODO: Any methods you wish to define, private or otherwise
    
}