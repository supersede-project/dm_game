package eu.supersede.algos;

public class Cell<T> {
	
	T value;

	public void setValue( T value ) {
		this.value  = value;
	}

	public T getValue() {
		return this.value;
	}
}