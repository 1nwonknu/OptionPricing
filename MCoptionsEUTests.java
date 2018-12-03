package de.dfine.options;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.portable.RemarshalException;

public class MCoptionsEUTests {
	
	private MonteCarloEuropeanOptions callOption;
	
	private MCbarrierUpOut callUpOut;
	
	private AsianDiscrete callAsianDiscrete;
	
	private LookBack callLookBack;
	
	@Before
	public void setUp() throws Exception {		
						
		int strike = 90;
		int initialPrice = 100;
		float remainingLifeTime = 1f;
		float steps = 750;
		
		callOption = new MonteCarloEuropenOptionCall();
		callOption.strike = strike;
		callOption.initialPrice = initialPrice;
		callOption.remainingLifeTime = remainingLifeTime;
		callOption.riskFreeRate = 0.05f;
		callOption.volatility = 0.25f;
		callOption.numberSimulations = 2000;
		
		callUpOut = new MCbarrierUpOutCall();
		callUpOut.strike = strike;
		callUpOut.initialPrice = initialPrice;
		callUpOut.remainingLifeTime = remainingLifeTime;
		callUpOut.riskFreeRate = 0.05f;
		callUpOut.volatility = 0.25f;	
		callUpOut.barrier = 120;
		callUpOut.numSteps = (int) (callUpOut.remainingLifeTime * steps);
		callUpOut.numberSimulations = 2000;
		
		callAsianDiscrete = new AsianDiscrete();
		callAsianDiscrete.strike = strike;
		callAsianDiscrete.initialPrice = initialPrice;
		callAsianDiscrete.remainingLifeTime = remainingLifeTime;
		callAsianDiscrete.riskFreeRate = 0.05f;
		callAsianDiscrete.volatility = 0.25f;	
		callAsianDiscrete.barrier = 120;
		callAsianDiscrete.numSteps = (int) (callAsianDiscrete.remainingLifeTime * steps);
		callAsianDiscrete.numberSimulations = 2000;	

		callLookBack = new LookBack();
		callLookBack.strike = strike;
		callLookBack.initialPrice = initialPrice;
		callLookBack.remainingLifeTime = remainingLifeTime;
		callLookBack.riskFreeRate = 0.05f;
		callLookBack.volatility = 0.25f;	
		//callLookBack.barrier = 120;
		callLookBack.numSteps = (int) (callLookBack.remainingLifeTime * steps);
		callLookBack.numberSimulations = 2000;	
		
	}
	
	@Test
	public void testCall() {
		float value = callOption.computePriceMC();
		System.out.println("Price for vanilla call " + value);
		assertTrue(value > 0);
	}
	
	@Test
	public void testUpOutCall() {
		
		float value = callUpOut.computePrice();
		assertTrue(value > 0);
		System.out.println("Price for Up and Out Call " + value);
		
	}
	
	
	@Test
	public void testAsian() {
		float value = callAsianDiscrete.computePrice();
		assertTrue(value > 0);
		System.out.println("Price for Asian Call " + value);
	}
	

	@Test
	public void testLookBack() {
		float value = callLookBack.computePrice();
		assertTrue(value > 0);
		System.out.println("Price for Look Back Call " + value);
	}
	
}
