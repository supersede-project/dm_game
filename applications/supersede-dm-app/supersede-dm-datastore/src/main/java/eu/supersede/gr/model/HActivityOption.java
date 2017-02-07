package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_activity_options")
public class HActivityOption {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long		id;
	
    private Long		activityId;
    
    private String		optionName;
    
    private String		optionValue;
	
}
