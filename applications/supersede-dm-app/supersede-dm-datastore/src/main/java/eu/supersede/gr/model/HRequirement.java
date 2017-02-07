package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_requirements")
public class HRequirement {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long	gameId;
	
    String			text;
    
    String			description;
    
    int				status;
    
    double			priority;
    
}
