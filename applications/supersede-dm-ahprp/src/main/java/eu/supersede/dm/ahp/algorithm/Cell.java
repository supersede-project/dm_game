package eu.supersede.dm.ahp.algorithm;

public class Cell<T> {
	
	T value;

	public void setValue( T value ) {
		this.value  = value;
	}

	public T getValue() {
		return this.value;
	}
}