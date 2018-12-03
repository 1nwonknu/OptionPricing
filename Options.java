package de.dfine.options;


public class Options {
	public int strike;
	public int initialPrice;
	public float remainingLifeTime; // unit: years
	public float riskFreeRate;
	public float volatility;
	
	public float d1;
	public float d2;
	
	public float delta;
	public float gamma;
	public float vega;
	public float theta;
	public float rho;
	
	public Options() {
		
	}
	
	
	public float cumulativeDistributionFunction(float x) {
		float k = 1.0f/(1.0f + 0.2316419f*x);
	    float k_sum = k*(0.319381530f + k*(-0.356563782f + k*(1.781477937f + k*(-1.821255978f + 1.330274429f*k))));
	    
	    if (x >= 0.0f) {
	        return (float) (1.0f - distributionFunction(x) * Math.exp(-0.5f*x*x) * k_sum);
	    } else {
	        return 1.0f - cumulativeDistributionFunction(-x);
	    }
	    
	}
	
	
	public float distributionFunction(float x) {
		
	    return (float) (1.0f / (Math.pow(2*Math.PI,0.5f)));
	    
	}
	
	
	public float compute_d1() {
		d1 = (float) (1 / volatility * Math.sqrt(remainingLifeTime));
		
		d1*=Math.log(initialPrice / strike) + (riskFreeRate + (volatility*volatility)/2)*remainingLifeTime;
		
		return d1;
	}
	
	
	public float compute_d2() {
		d2 = (float) (d1 - volatility*Math.sqrt(remainingLifeTime));
		return d2;
	}
}


class Call extends Options {	
	public float computePrice() {
		float callPrice = (float) (initialPrice*cumulativeDistributionFunction(d1)-strike * Math.exp(-riskFreeRate*remainingLifeTime)*cumulativeDistributionFunction(d2));
		//System.out.println(callPrice);
		return callPrice;
	}
	
	
	public float computeDelta() {		
		delta = cumulativeDistributionFunction(d1);		
		//System.out.println(delta);
		return delta;
		
	}
	
	
	public float computeGamma() {		
		gamma = (float) (distributionFunction(d1) / (initialPrice * volatility * Math.sqrt(remainingLifeTime)));
		//System.out.println(gamma);
		return gamma;
	}
	
	
	public float computeTheta() {
		theta = (float) (-(initialPrice*distributionFunction(d1)*volatility) / (2*Math.sqrt(remainingLifeTime)) - riskFreeRate * strike * Math.exp(-riskFreeRate * remainingLifeTime) * cumulativeDistributionFunction(d2));
		//System.out.println(theta);
		return theta;		 
	}
	
	
	public float computeVega() {
		vega = (float) (initialPrice * distributionFunction(d1) * Math.sqrt(remainingLifeTime));
		//System.out.println(vega);
		return vega;		 
	}
		
	
	public float computeRho() {
		rho = (float) (strike * remainingLifeTime * Math.exp(-riskFreeRate * remainingLifeTime) * cumulativeDistributionFunction(d2));
		//System.out.println(rho);
		return rho;		 
	}
}


class Put extends Options {
	public float computePrice() {
		float putPrice = (float) (strike * Math.exp(-riskFreeRate * remainingLifeTime) * cumulativeDistributionFunction(-d2)-initialPrice*cumulativeDistributionFunction(-d1));
		//System.out.println(putPrice);
		return putPrice;
	}
	
	public float computeDelta() {		
		delta = cumulativeDistributionFunction(d1) - 1;		
		//System.out.println(delta);
		return delta;
		
	}
}