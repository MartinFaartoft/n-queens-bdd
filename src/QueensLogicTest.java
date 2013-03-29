import static org.junit.Assert.*;

import java.util.ArrayList;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

import org.junit.Test;


public class QueensLogicTest {

	@Test
	public void testHorizontalRule() {
		QueensLogic q = new QueensLogic();
		q.initializeGame(2);
		BDD restricted = q.getRules().restrict(q.f.nithVar(0).and(q.f.ithVar(1)));
		assertTrue(restricted.isOne());
		
		restricted = q.getRules().restrict(q.f.nithVar(1).and(q.f.ithVar(0)));
		assertTrue(restricted.isOne());
		
		restricted = q.getRules().restrict(q.f.ithVar(0).and(q.f.ithVar(1)));
		assertTrue(restricted.isZero());
	
	}
	
	
	
	@Test
	public void testGetHorizontalArrayFromVar(){
		QueensLogic q = new QueensLogic();
		q.initializeGame(2);
		
		ArrayList<Integer> integers = q.getHorizontalArrayFromVar(0);
		ArrayList<Integer> otherIntegers = q.getHorizontalArrayFromVar(1);
		
		assertEquals(1, integers.size());
		
		assertTrue(integers.contains(1));
		
		assertEquals(1, otherIntegers.size());
		
		assertTrue(otherIntegers.contains(0));
		
		
	}

}
