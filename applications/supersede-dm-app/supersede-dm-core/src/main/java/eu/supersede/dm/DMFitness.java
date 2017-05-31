package eu.supersede.dm;

public class DMFitness {
	
	int			value = 0;
	
	double		factor = 1.0;

	String		aspect = "";
	
	public DMFitness() {
		this( "", 0, 1.0 );
	}
	
	public DMFitness( int value ) {
		this( "", value, 1.0 );
	}
	
	public DMFitness( double factor ) {
		this( "", 0, factor );
	}
	
	public DMFitness( String aspect, int value, double factor ) {
		this.aspect = aspect;
		this.value = value;
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