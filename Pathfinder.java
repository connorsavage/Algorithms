package main.pathfinder.informed;

import java.util.*;

//import main.pathfinder.uninformed.MazeState;
//import main.pathfinder.uninformed.SearchTreeNode;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {
    
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
	public static ArrayList<String> solve (MazeProblem problem) {
	
	    SearchTreeNode start = new SearchTreeNode(problem.getInitialState(), null, null, 0, manhattanDistance(problem.getInitialState(), problem.getKeyState()));
	    SearchTreeNode path = aStar(problem, start, problem.getKeyState());
	    ArrayList<String> initialPath = solution(path);
	    System.out.println(initialPath);
	    SearchTreeNode closestGoal = null;
	    int pathCost = 1000000;
	    for(MazeState goalState : problem.getGoalStates()) {
	    	 SearchTreeNode keyNode = new SearchTreeNode(problem.getKeyState(), null, null, 0, manhattanDistance(problem.getKeyState(), goalState));
	    	 SearchTreeNode goal = aStar(problem, keyNode, goalState);
	    	 if(goal == null) {
	    		 return null;
	    	 }
	    	 if(goal.pastCost < pathCost) {
	    			closestGoal = goal;
	    			
	    	 }
	    }
	    initialPath.addAll(solution(closestGoal));
	    return initialPath;
	}

    private static SearchTreeNode aStar(MazeProblem problem, SearchTreeNode start, MazeState targetState) {
    	
    	PriorityQueue<SearchTreeNode> frontier = new PriorityQueue<>();
    	HashSet<MazeState> graveyard = new HashSet<MazeState>();
    	frontier.add(start);
    	while(!frontier.isEmpty()){
    		SearchTreeNode parent = frontier.poll();
    		graveyard.add(parent.state);
    	
    		if(parent.state.equals(targetState)) {
    			return parent;
    		}
    		for(Map.Entry<String, MazeState> transition : problem.getTransitions(parent.state).entrySet()){
    			if(!graveyard.contains(transition.getValue())) {
    				MazeState childState = transition.getValue();
    				String action = transition.getKey();
    				MazeState target = problem.getKeyState();
    				int previousCost = parent.pastCost + problem.getCost(transition.getValue());
    				
    				SearchTreeNode childNode = new SearchTreeNode(childState, action, parent, previousCost, manhattanDistance(childState, target));
    				frontier.add(childNode);
    			}
    		}
    	}
    	return null;
	}

	private static int manhattanDistance(MazeState current, MazeState target) {
		int distance = Math.abs(current.col - target.col) + Math.abs(current.row - target.row);
		return distance;
	}
	
	public static ArrayList<String> solution(SearchTreeNode finalNode){
		if(finalNode == null) {
			return null;
		}
		ArrayList<String> solution = new ArrayList<String>();
		while(finalNode.parent != null) {
			solution.add(0, finalNode.action);
			finalNode = finalNode.parent;
		}
		System.out.println(solution.toString());
		return solution;
	}
}
