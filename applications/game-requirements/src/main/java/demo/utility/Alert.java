package demo.utility;

public class Alert {
	
	Long		timestamp;
	AlertLevel	level;
	AlertType	type;
	String		id;
	String		message;
	Double		value;
	
	public Alert() {}
	
	public Alert( AlertLevel type, String id, String message, Double value ) {
		this.level = type;
		this.id = id;
		this.message = message;
		this.value = value;
	}
	
	public AlertLevel getType() {
		return this.level;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Double getValue() {
		return this.value;
	}
	
}
