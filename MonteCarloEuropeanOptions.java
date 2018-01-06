package de.dfine.options;
import java.util.Arrays;

import de.dfine.options.Options;


public abstract class MonteCarloEuropeanOptions {
	public int strike;
	public int initialPrice;
	public float remainingLifeTime; // unit: years
	public float riskFreeRate;
	public float volatility;
	
	public int numberSimulations;


	public float payOff(double simulatedPrice, int strike) {
		return (float) (0.15f);
	}
	
	public float boxMueller() {
		double uniformRand1 = Math.random();
		double uniformRand2 = Math.random();
		double theta = 2 * Math.PI * uniformRand2;
		double rho = Math.sqrt(-2 * Math.log(uniformRand1));		
		return (float) (rho * Math.cos(theta));			
	}
	
	
	public float computePriceMC() {		
		float sumPayOffs = 0;
		for(int simulationCounter = 1; simulationCounter <= numberSimulations; simulationCounter++){
			float gaussRand = boxMueller();
			//System.out.println(gaussRand);
			double simulatedPrice = initialPrice * Math.exp(riskFreeRate - 0.5 * volatility * volatility * remainingLifeTime + volatility * Math.sqrt(remainingLifeTime) * gaussRand);
			//System.out.println(simulatedPrice);
			sumPayOffs += payOff(simulatedPrice, strike);
		}	
		//System.out.println(sumPayOffs);
		float optionPrice = (float) (sumPayOffs / numberSimulations * Math.exp(-riskFreeRate * remainingLifeTime));
		//System.out.println(optionPrice);
		return optionPrice;
	}
}

class MonteCarloEuropenOptionCall extends MonteCarloEuropeanOptions {
	public float payOff(double simulatedPrice, int strike) {
		return (float) (Math.max(simulatedPrice - strike, 0));
	}
}

class MonteCarloEuropenOptionPut extends MonteCarloEuropeanOptions {
	public float payOff(double simulatedPrice, int strike) {
		return (float) (Math.max(strike - simulatedPrice, 0));
	}
}


abstract class MCbarrier extends MonteCarloEuropeanOptions {
	public float barrier;
	public int numSteps;
	
	
	public float simulatePrice(float previousPrice, float timeIncrement, float gaussRand){
		//System.out.println(timeIncrement);
		float nextPrice = (float) (previousPrice * Math.exp((riskFreeRate - 0.5 * volatility * volatility) * timeIncrement + volatility * gaussRand * Math.sqrt(timeIncrement)));		
		
		return nextPrice;
	}
	
	
	public float simulatePriceAnthitetic(float previousPrice, float timeIncrement, float gaussRand){
		
		float priceNotAV = (float) (previousPrice * Math.exp((riskFreeRate - 0.5 * volatility * volatility) * timeIncrement + volatility * gaussRand * Math.sqrt(timeIncrement)));		
		float priceAV =    (float) (previousPrice * Math.exp((riskFreeRate - 0.5 * volatility * volatility) * timeIncrement - volatility * gaussRand * Math.sqrt(timeIncrement)));		
		
		float nextPrice = 0.5f*(priceNotAV + priceAV);		
		return nextPrice;
	}	
	
	
	public float computePriceControlVariate(float priceWithoutCV){
		
		/* V_CV = \hat(V) + (V^* - \hat(V)^*) */
		// V^* is the analytical solution for the control variate
		// \hat(V)^* is the MC solution for the control variate
		// \hat(V) is the MC solution for the option price to be computed
		
		Call callOption;
		callOption = new Call();
		callOption.strike = this.strike;
		callOption.initialPrice = this.initialPrice;
		callOption.remainingLifeTime = this.remainingLifeTime;
		callOption.riskFreeRate = this.riskFreeRate;
		callOption.volatility = this.volatility;	
		
		float priceCVanalytical = callOption.computePrice();
		float priceCVMC = this.computePriceMC();
		
		System.out.println(priceCVanalytical);
		System.out.println(priceCVMC);
		System.out.println(priceWithoutCV);
		float nextPrice = priceWithoutCV + (priceCVanalytical - priceCVMC);
		return nextPrice;
	}	
	
	
	public float[] pathGenerator(){
		float[] pricePath = new float[numSteps];
		pricePath[0] = initialPrice;
		float timeIncrement = remainingLifeTime / numSteps;
			
		for (int simulationCounter = 1; simulationCounter < pricePath.length; simulationCounter++) {			
			float gaussRand = boxMueller();
			pricePath[simulationCounter] = simulatePriceAnthitetic(pricePath[simulationCounter - 1], timeIncrement, gaussRand);
		}
		return pricePath;
	}
	
	public float computePrice() {
		
		float sumPayOffs = 0;
		for(int simulationCounter = 1; simulationCounter <= numberSimulations; simulationCounter++){
			float[] pricePath = pathGenerator();
			if (optionLostValue(pricePath)){
				sumPayOffs += 0;
			}
			else{
				//sumPayOffs += Math.max(pricePath[pricePath.length-1] - strike, 0); // todo: generalize pay off function and pass it as parameter.
				sumPayOffs += payOff(pricePath[pricePath.length-1], strike);
			}
		}	
		//System.out.println(sumPayOffs);
		float optionPrice = (float) (sumPayOffs / numberSimulations * Math.exp(-riskFreeRate * remainingLifeTime));
		//System.out.println("Price with Control Variate" + optionPrice);
		return optionPrice; // computePriceControlVariate(optionPrice);
	}


	private boolean optionLostValue(float[] pricePath) {
		// TODO Auto-generated method stub
		return false;
	}
}


abstract class MCbarrierUpOut extends MCbarrier {
	public boolean optionLostValue(float[] pricePath) {
		for (int i = 0; i < pricePath.length; i++) {
			if (pricePath[i] > barrier) {
				return true;
			}
		}
		return false;
	}	
}

abstract class MCbarrierDownOut extends MCbarrier {
	public boolean optionLostValue(float[] pricePath) {
		for (int i = 0; i < pricePath.length; i++) {
			if (pricePath[i] < barrier) {
				return true;
			}
		}
		return false;
	}	
}

abstract class MCbarrierDownIn extends MCbarrier {
	public boolean optionLostValue(float[] pricePath) {
		for (int i = 0; i < pricePath.length; i++) {
			if (pricePath[i] < barrier) {
				return false;
			}
		}
		return true;
	}	
}

abstract class MCbarrierUpIn extends MCbarrier {
	public boolean optionLostValue(float[] pricePath) {
		for (int i = 0; i < pricePath.length; i++) {
			if (pricePath[i] > barrier) {
				return false;
			}
		}
		return true;
	}	
}

class MCbarrierUpOutCall extends MCbarrierUpOut {
	public float payOff(double simulatedPrice, int strike) {
		return (float) (Math.max(simulatedPrice - strike, 0));
	}	
}

class MCbarrierDownOutCall extends MCbarrierDownOut {
	public float payOff(double simulatedPrice, int strike) {
		return (float) (Math.max(simulatedPrice - strike, 0));
	}	
}

class MCbarrierUpOutPut extends MCbarrierUpOut {
	public float payOff(double simulatedPrice, int strike) {
		return (float) (Math.max(strike - simulatedPrice, 0));
	}	
}


class AsianDiscrete extends MCbarrier {
	
	public static float mean(float[] m) {
	    float sum = 0;
	    for (int i = 0; i < m.length; i++) {
	        sum += m[i];
	    }
	    return sum / m.length;
	}
	
	public float computePrice() {
		
		float sumPayOffs = 0;
		for(int simulationCounter = 1; simulationCounter <= numberSimulations; simulationCounter++){
			float avrgPrice = mean(pathGenerator());
			
			sumPayOffs += Math.max(avrgPrice - strike, 0); // todo: generalize pay off function and pass it as parameter.
			
		}	
		//System.out.println(sumPayOffs);
		float optionPrice = (float) (sumPayOffs / numberSimulations * Math.exp(-riskFreeRate * remainingLifeTime));
		//System.out.println("Price Asian Option" + optionPrice);
		return optionPrice; // computePriceControlVariate(optionPrice);
	}
	
	
}



class LookBack extends MCbarrier {
	
	public float find_max_value(float [] pricePath) {
	    float highest = pricePath[0];
	    for (int index = 1; index < pricePath.length; index ++) {
	        if (pricePath[index] > highest) {
	            highest = pricePath[index];
	        }
	    }
	    return highest;
	}
	
	
	public float computePrice() {
		
		float sumPayOffs = 0;
		for(int simulationCounter = 1; simulationCounter <= numberSimulations; simulationCounter++){
			float[] pricePath = pathGenerator();
			float max_price = find_max_value(pricePath);
			sumPayOffs += Math.max(max_price - strike, 0); // todo: generalize pay off function and pass it as parameter.
		}	
		//System.out.println(sumPayOffs);
		float optionPrice = (float) (sumPayOffs / numberSimulations * Math.exp(-riskFreeRate * remainingLifeTime));
		//System.out.println("Price with Control Variate" + optionPrice);
		return optionPrice; // computePriceControlVariate(optionPrice);
	}
}





