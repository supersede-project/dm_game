package eu.supersede.dm.ga.data;

public class GAGame {
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	Long			id;
	
	Long			owner;
	
	String			date = "";
	
	String			status = "open";
	
	public Long getOwner() {
		return this.owner;
	}

	public Long getId() {
		return this.id;
	}
	
}
