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
    		rules = rules.and(buildRule(n, horizontal(n))).
    					  and(buildRule(n, vertical(n))).
    					  and(buildRule(n, diagonal(n))).
    					  and(oneQueenPerRowRule()); 
    	}
	}

	public BDD buildRule(int i, ArrayList<Integer> otherVars) {
		BDD rule = f.one();
		
		for (int j : otherVars) {
			rule = rule.and(f.nithVar(j));
		}
		
		return f.ithVar(i).imp(rule);
	}
	
	public BDD oneQueenPerRowRule(){
		BDD rule = f.one();
		for (int j = 0; j < y; j++) {
			BDD innerRule = f.zero();
			
			for (int k = 0; k < x; k++) {
				innerRule = innerRule.or(f.ithVar(j * x + k));
			}
			
			rule = rule.and(innerRule);
		}
		return rule;
	}

	public ArrayList<Integer> vertical(int i) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int col = i % x;
		for(int j = 0; j < y; j++) {
			int n = col + j * x;
			if(n == i) continue;
			
			result.add(n);
		}
		
		return result;
	}
	
	public ArrayList<Integer> horizontal(int i){
		ArrayList<Integer> array = new ArrayList<Integer>();
		for(int j = 0; j < x; j++) {
			int n = j + x * (i / y);
			if (n == i) continue; //don't add current var to restriction
			array.add(n);
		}
		return array;
	}
	
	
	public ArrayList<Integer> diagonal(int i){
		ArrayList<Integer> array = new ArrayList<Integer>();
		
		int[][] operations = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
		
		for (int[] vector : operations) {
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

    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }
        
        board[column][row] = 1;
        
        // put some logic here..
        rules = rules.restrict(f.ithVar(row * x + column));
        
        for(int r = 0; r < y; r++) {
	        for(int c = 0; c < x; c++) {
	        	if(board[c][r] == 1) continue;
	        	
	        	if(rules.restrict(f.ithVar(r * x + c)).isZero()) {
	        		board[c][r] = -1;
	        	}
	        }
        }
       
        return true;
    }
}
