package demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="arguments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Argument {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long argumentId;
    private String content;
    
    public Argument() {
    }
 
    public Argument(String content) {
        this.content = content;
    }
 
    public Long getArgumentId() {
        return argumentId;
    }
 
    public void setArgumentId(Long argumentId) {
        this.argumentId = argumentId;
    }
 
    public String getContent() {
        return content;
    }
 
    public void setContent(String content) {
        this.content = content;
    }
}