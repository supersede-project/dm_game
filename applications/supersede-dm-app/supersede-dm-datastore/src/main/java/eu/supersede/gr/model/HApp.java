package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "h_apps")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HApp {
	
    @Id public String id;
    
	public HApp() {}
	
	public HApp( String id ) {
		this.id = id;
	}
	
}
