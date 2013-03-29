/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
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
    private BDD rules;
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
    		
    		ArrayList<Integer> array = getHorizontalArrayFromVar(n);
    		if(rules == null) {
    			rules = buildRuleFromVars(n, array);
    		}
    		else {
    			rules.and(buildRuleFromVars(n, array));
    		}
    	
    		//buildVerticalRule(n);
    		//buildUpwardsSlopingDiagonalRule(n);
    		//buildDownWardsSlopingDiagonalRule(n);
    	}
    	
    	
    	
	}

	public BDD buildRuleFromVars(int i, ArrayList<Integer> otherVars) {
		BDD rule = null;
		
		for (int j : otherVars) {
			if (rule == null) {
				rule = f.nithVar(j);
			}
			else {
				rule.and(f.nithVar(j));
			}
		}
		

		
		return f.ithVar(i).imp(rule);
	}
	
	
	public ArrayList<Integer> getHorizontalArrayFromVar(int i){
		ArrayList<Integer> array = new ArrayList<Integer>();
		for(int j = 0; j < x; j++) {
			int n = j + x * (i / y);
			if (n == i) continue; //don't add current var to restriction
			array.add(n);
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
      
        return true;
    }
}
