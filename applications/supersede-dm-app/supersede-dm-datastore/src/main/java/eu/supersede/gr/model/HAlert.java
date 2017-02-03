package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "h_alerts")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HAlert {
	
    @Id
	public String id;
    
    public String applicationID;
    
    public long timestamp;
	
	public HAlert() {}
	
	public HAlert( String id, long timestamp ) {
		this.id = id;
		this.timestamp = timestamp;
	}
	
}
