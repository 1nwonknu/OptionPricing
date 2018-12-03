package de.dfine.options;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CallOptionTest {

	private Call callOption;
	
	@Before
	public void setUp() throws Exception {		
						
		callOption = new Call();
		callOption.strike = 100;
		callOption.initialPrice = 100;
		callOption.remainingLifeTime = 1f;
		callOption.riskFreeRate = 0.05f;
		callOption.volatility = 0.25f;	
		
		callOption.d1 = callOption.compute_d1();
		callOption.d2 = callOption.compute_d2();
	}
	
	@Test
	public void testCall() {
		assertTrue(callOption.computePrice() > 0);
	}
	
	@Test
	public void testDelta() {
		assertTrue(callOption.computeDelta() > 0);
	}
	
	
	@Test
	public void testGamma() {
		assertTrue(callOption.computeGamma() > 0);
	}
	
	
	@Test
	public void testTheta() {
		assertTrue(callOption.computeTheta() < 0);
	}
	
	
	@Test
	public void testVega() {
		assertTrue(callOption.computeVega() > 0);
	}
	
	
	@Test
	public void testRho() {
		assertTrue(callOption.computeRho() > 0);
	}
}
