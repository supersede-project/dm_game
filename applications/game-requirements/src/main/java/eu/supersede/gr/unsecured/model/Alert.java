package eu.supersede.gr.unsecured.model;

public class Alert {

	private Long timestamp;
	private AlertLevel level;
	private AlertType type;
	private String id;
	private String message;
	private Double value;
	
	public Alert() {}
	
	public Alert( AlertLevel type, String id, String message, Double value ) {
		this.level = type;
		this.id = id;
		this.message = message;
		this.value = value;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public AlertLevel getLevel() {
		return level;
	}

	public void setLevel(AlertLevel level) {
		this.level = level;
	}

	public AlertType getType() {
		return type;
	}

	public void setType(AlertType type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	
}
