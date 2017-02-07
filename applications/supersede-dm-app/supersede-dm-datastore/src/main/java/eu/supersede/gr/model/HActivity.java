package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "h_activity_instances")
public class HActivity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public String methodName;
    
}
