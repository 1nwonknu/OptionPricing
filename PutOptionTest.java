package de.dfine.options;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PutOptionTest {
	private Put putOption;
	
	@Before
	public void setUp() throws Exception {		
		
		putOption = new Put();
		putOption.strike = 100;
		putOption.initialPrice = 100;
		putOption.remainingLifeTime = 1f;
		putOption.riskFreeRate = 0.05f;
		putOption.volatility = 0.25f;
		
		putOption.d1 = putOption.compute_d1();
		putOption.d2 = putOption.compute_d2();
		
	}

	@Test
	public void testPut() {
		assertTrue(putOption.computePrice()>0);
	}
	
	@Test
	public void testDelta() {
		assertTrue(putOption.computeDelta()<0);
	}
	
}
