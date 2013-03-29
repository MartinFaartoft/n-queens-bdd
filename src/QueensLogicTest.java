import static org.junit.Assert.*;

import java.util.ArrayList;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

import org.junit.Before;
import org.junit.Test;


public class QueensLogicTest {
	private QueensLogic q;
	
	@Before
	public void Setup() {
		q = new QueensLogic();
		q.initializeGame(5);
	}
	
	@Test
	public void testTestFramework() {
		BDDFactory f = JFactory.init(1000, 100);
		f.setVarNum(2);
		BDD rule = f.ithVar(0).or(f.ithVar(1));
		shouldBeTrue(f, rule, trues(0), falses(1));
		shouldBeTrue(f, rule, trues(1), falses());
	}
	
	/*
	 * BOARD:
	 * 0  1  2  3  4
	 * 5  6  7  8  9
	 * 10 11 12 13 14
	 * 15 16 17 18 19
	 * 20 21 22 23 24
	 */
	
	@Test
	public void testHorizontalRule() {
		shouldBeUnsatisfiable(trues(0, 3), falses());
		shouldBeUnsatisfiable(trues(5, 9), falses());
		shouldBeUnsatisfiable(trues(23, 24), falses());
	}
	
	@Test
	public void testVerticalRule() {
		shouldBeUnsatisfiable(trues(0, 5), falses());
		shouldBeUnsatisfiable(trues(2, 22), falses());
		shouldBeUnsatisfiable(trues(19, 24), falses());
	}
	
	@Test
	public void testDiagonalRule() {
		shouldBeUnsatisfiable(trues(0, 6), falses());
		shouldBeUnsatisfiable(trues(12, 4), falses());
		shouldBeUnsatisfiable(trues(12, 0), falses());
	}
	
	@Test
	public void testOneQueenPerRow(){
		shouldBeUnsatisfiable(trues(), falses(0,1,2,3,4));
	}
	
	@Test
	public void testSolution() {
		shouldBeTautology(trues(0, 7, 14, 16, 23), falses(1,2,3,4,5,6,8,9,10,11,12,13,15,17,18,19,20,21,22,24));
	}
	
	
	private int[] trues(int... values) {
		return values;
	}
	
	private int[] falses(int... values) {
		return values;
	}
	
	private BDD restrict(BDDFactory f, BDD rule, int[] trues, int[] falses) {
		BDD restrictions = null;
		for(int n: trues) {
			if(restrictions == null) {
				restrictions = f.ithVar(n);
			} else {
				restrictions = restrictions.and(f.ithVar(n));
			}
		}
		
		for(int n: falses) {
			if(restrictions == null) {
				restrictions = f.nithVar(n);
			} else {
				restrictions = restrictions.and(f.nithVar(n));
			}
		}
		
		return rule.restrict(restrictions);
	}
	
	private void shouldBeTautology(int[] trues, int[] falses) {
		shouldBeTrue(q.f, q.getRules(), trues, falses);
	}
	
	private void shouldBeSatisfiable(int[] trues, int[] falses) {
		double solutions = restrict(q.f, q.getRules(), trues, falses).satCount();
		assertTrue(solutions > 0);
		
	}
	
	private void shouldBeUnsatisfiable(int[] trues, int[] falses) {
		shouldBeFalse(q.f, q.getRules(), trues, falses);
	}
	
	private void shouldBeTrue(BDDFactory f, BDD rule, int[] trues, int[] falses) {
		assertTrue(restrict(f, rule, trues, falses).isOne());
	}
	
	private void shouldBeFalse(BDDFactory f, BDD rule, int[] trues, int[] falses) {
		assertTrue(restrict(f, rule, trues, falses).isZero());
	}
	
	private void shouldNotBeFalse(BDDFactory f, BDD rule, int[] trues, int[] falses) {
		assertFalse(restrict(f, rule, trues, falses).isZero());
	}
	
	@Test
	public void testHorizontal(){
		QueensLogic q = new QueensLogic();
		q.initializeGame(2);
		ArrayList<Integer> first = q.horizontal(0);
		ArrayList<Integer> second = q.horizontal(1);
		
		assertEquals(1, first.size());
		assertTrue(first.contains(1));
		
		assertEquals(1, second.size());
		assertTrue(second.contains(0));
	}
	
	@Test
	public void testVertical() {
		QueensLogic q = new QueensLogic();
		q.initializeGame(2);
		ArrayList<Integer> first = q.vertical(0);
		ArrayList<Integer> second = q.vertical(1);
		
		assertEquals(1, first.size());
		assertTrue(first.contains(2));
		
		assertEquals(1, second.size());
		assertTrue(second.contains(3));
	}
	
	@Test
	public void testDiagonal() {
		assertFalse(q.diagonal(0).contains(4));
	}

}
