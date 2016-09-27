package eu.supersede.dm;

public class DMFitness {
	
	int			value = 0;
	
	double		factor = 1.0;
	
	public DMFitness() {}
	
	public DMFitness( int value ) {
		this.value = value;
	}
	
	public DMFitness( double factor ) {
		this.factor = factor;
	}
	
	public double cost() {
		return this.value;
	}
	
	public double factor() {
		return this.factor;
	}
	
	public double value() {
		return value * factor;
	}
	
	public void add( DMFitness other ) {
		value += other.value;
		factor *= other.factor;
	}
	
	public String toString() {
		return "" + this.value;
	}
	
}