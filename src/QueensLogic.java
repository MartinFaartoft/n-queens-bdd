/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Martin Faartoft, Thorbj¿rn Nielsen
 *
 */
import java.util.*;
import net.sf.javabdd.*;

public class QueensLogic {
    private int x = 0;
    private int y = 0;
    private int[][] board;
    private final int nodes = 2000000;
    private final int cache = 200000; 
    public BDDFactory f = JFactory.init(nodes, cache);
    private BDD rules = f.one();
    private BDD restricted = f.one();
    private HashSet<Integer> queenPositions = new HashSet<Integer>();
    
    public QueensLogic() {
       //constructor
    }
    
    public BDD getRules() {
    	return rules;
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.board = new int[x][y];
        buildRules(x, y);
    }

    private void buildRules(int x, int y) {
    	int numVariables = x * y; 
    	f.setVarNum(numVariables);
    	
    	for(int n = 0; n < numVariables; n++) {
    		rules.andWith(buildRule(n, getVarsExcludedBy(n)));
    	}
    	
    	rules.andWith(oneQueenPerRowRule());
	}
    
    private Iterable<Integer> getVarsExcludedBy(int var) {
    	ArrayList<Integer> result = horizontal(var);
    	result.addAll(vertical(var));
    	result.addAll(diagonal(var));
    	
    	return result;
    }

	public BDD buildRule(int i, Iterable<Integer> excludedVars) {
		BDD rule = f.one();
		
		for (int excluded : excludedVars) {
			rule.andWith(f.nithVar(excluded));
		}
		
		return f.ithVar(i).imp(rule); //x(i) implies !x(n) and !x(n+1)...
	}
	
	public BDD oneQueenPerRowRule() {
		BDD rule = f.one();
		for (int row = 0; row < y; row++) {
			BDD innerRule = f.zero();
			
			for (int col = 0; col < x; col++) {
				innerRule.orWith(f.ithVar(row * x + col));
			}
			
			rule.andWith(innerRule);
		}
		
		return rule;
	}

	public ArrayList<Integer> vertical(int i) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int col = i % x;
		for(int row = 0; row < y; row++) {
			int n = col + row * x;
			if(n == i) continue;
			
			result.add(n);
		}
		
		return result;
	}
	
	public ArrayList<Integer> horizontal(int i){
		ArrayList<Integer> array = new ArrayList<Integer>();
		for(int col = 0; col < x; col++) {
			int n = col + x * (i / y);
			if (n == i) continue;
			array.add(n);
		}
		return array;
	}
	
	
	public ArrayList<Integer> diagonal(int i){
		ArrayList<Integer> array = new ArrayList<Integer>();
		
		int[][] directions = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
		
		for (int[] vector : directions) {
			int row = i / y + vector[0];
			int col = i % x + vector[1];  
			while (row >= 0 && row < y && col >= 0 && col < x){
				array.add(row * x + col);
				row += vector[0];
				col += vector[1];
			 }
		}
		
		return array;
	}

	public int[][] getGameBoard() {
        return board;
    }
	
	private BDD getRestrictions() {
		BDD res = f.one();
		for (int q :  queenPositions) {
			res.andWith(f.ithVar(q));
		}
		
		return res;
	}
	
	private void addQueen(int var) {
		queenPositions.add(var);
		updateRestrictions();
	}
	
	private void updateRestrictions() {
		restricted = rules.restrict(getRestrictions());
	}
	
	private void removeQueen(int var) {
		queenPositions.remove(var);
		updateRestrictions();
	}

    public boolean insertQueen(int column, int row) {
    	
        if (board[column][row] == -1) { //clicked red cross, do nothing
            return true;
        }
        
        if (board[column][row] == 1) { //if queen is already here, remove it
            board[column][row] = 0;
            removeQueen(row * x + column);
        } else {
        	board[column][row] = 1; //not a queen, place one
            addQueen(row * x + column);
        }
        
        boolean solved = restricted.pathCount() == 1;
        
        for(int r = 0; r < y; r++) {
	        for(int c = 0; c < x; c++) {
	        	if(board[c][r] == 1) continue;
	        	
	        	if(restricted.restrict(f.ithVar(r * x + c)).isZero()) {
	        		board[c][r] = -1;
	        	} else if (solved) { 
	        		board[c][r] = 1;
	        		addQueen(r * x + c);
	        	} else {
	        		board[c][r] = 0; //remove red cross that might be leftover form removed queen
	        	}
	        }
        }
       
        return true;
    }
}
