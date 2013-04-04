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
    //initialized to TRUE, because the rules will be a big conjunction, and TRUE is is the identity for conjunctions 
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

    //create the BDD representing the constraints of the N-Queens problem
    //and store the result in the the "rules" class member
    private void buildRules(int x, int y) {
    	int numVariables = x * y; 
    	f.setVarNum(numVariables); //init BDD to have one variable per space on the chessboard
    	
    	for(int n = 0; n < numVariables; n++) { //foreach variable
    		//conjoin existing rules with rule stating that variable n implies NOT (all the variables excluded by n)
    		rules.andWith(buildRule(n, getVarsExcludedBy(n)));
    	}
    	
    	//conjoin existing rules with the rule that every row must have at least one queen
    	//note that this is equivalent to "every columns must have at least one queen"
    	//because of rotational symmetry
    	rules.andWith(oneQueenPerRowRule());
	}
    
    //get all the vars that is mutually exclusive with parameter 'var'
    private Iterable<Integer> getVarsExcludedBy(int var) {
    	ArrayList<Integer> result = horizontal(var); //all variables on same row on the board
    	result.addAll(vertical(var)); //all variables on same column
    	result.addAll(diagonal(var)); //all variables lying on the 2 diagonals that intersect 'var'
    	
    	return result;
    }

	public BDD buildRule(int i, Iterable<Integer> excludedVars) {
		//initialized to TRUE, because the rule will be a big conjunction, and TRUE is is the identity for conjunctions
		BDD rule = f.one();
		
		for (int excluded : excludedVars) {
			rule.andWith(f.nithVar(excluded)); //build the big conjunction of negated variables
		}
		
		return f.ithVar(i).imp(rule); //add the "var 'i' implies" part
	}
	
	public BDD oneQueenPerRowRule() {
		BDD rule = f.one();
		for (int row = 0; row < y; row++) { //for each row
			BDD innerRule = f.zero(); //init to FALSE, since this rule is a disjunction
			
			for (int col = 0; col < x; col++) { //for each column in row
				innerRule.orWith(f.ithVar(row * x + col)); //x(i) OR x(i+1)...
			}
			
			rule.andWith(innerRule); //conjoin the individual row rules
		}
		
		return rule;
	}

	//get all the variable indices on the same column as variable 'i'
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
	
	//get all the variable indices on the same row as variable 'i'
	public ArrayList<Integer> horizontal(int i){
		ArrayList<Integer> array = new ArrayList<Integer>();
		for(int col = 0; col < x; col++) {
			int n = col + x * (i / y);
			if (n == i) continue;
			array.add(n);
		}
		return array;
	}
	
	//get all the variable indices on the two diagonals intersecting variable 'i'
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
	
	//returns a BDD with all the variables where the user has placed queens, restricted to TRUE
	private BDD getRestrictions() {
		BDD res = f.one();
		for (int q :  queenPositions) {
			res.andWith(f.ithVar(q));
		}
		
		return res;
	}
	
	//add a queen to the board at position 'var'
	private void addQueen(int var) {
		queenPositions.add(var);
		updateRestrictions();
	}
	
	//rebuild the restrictions, called when a queen is added or removed
	private void updateRestrictions() {
		restricted = rules.restrict(getRestrictions());
	}
	
	//remove a queen from the board at position 'var'
	private void removeQueen(int var) {
		queenPositions.remove(var);
		updateRestrictions();
	}

	//method called by QueensGUI, whenever a square is clicked
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
        
        boolean solved = restricted.pathCount() == 1; //if there's only one path leading to TRUE in the restricted BDD, consider the problem solved
        
        for(int r = 0; r < y; r++) { //foreach column
	        for(int c = 0; c < x; c++) { //foreach row
	        	if(board[c][r] == 1) continue; //if queen is present, skip this space
	        	
	        	if(restricted.restrict(f.ithVar(r * x + c)).isZero()) { //if we cannot place a queen here, without making the problem unsolvable, add a red cross to the board
	        		board[c][r] = -1;
	        	} else if (solved) { //if only one solution remaining (and space is not a queen or a red cross), add a queen here
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
