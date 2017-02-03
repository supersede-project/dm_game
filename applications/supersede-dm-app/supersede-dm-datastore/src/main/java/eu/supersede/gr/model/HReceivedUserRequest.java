package eu.supersede.gr.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "h_received_userrequests")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class HReceivedUserRequest {
	
	@Id
	String					ID;
    
	public String			alertID;
	
	String					classification;
	double					accuracy;
	String					description;
	int						positiveSentiment;
	int						negativeSentiment;
	int						overallSentiment;
//	String[]				feedbackIDs;
//	String[]				features;
	
	public HReceivedUserRequest() {}
	
	public HReceivedUserRequest( String id ) {
		this.ID = id;
	}
	
    public String getID() {
		return ID;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPositiveSentiment() {
		return positiveSentiment;
	}
	public void setPositiveSentiment(int positiveSentiment) {
		this.positiveSentiment = positiveSentiment;
	}
	public int getNegativeSentiment() {
		return negativeSentiment;
	}
	public void setNegativeSentiment(int negativeSentiment) {
		this.negativeSentiment = negativeSentiment;
	}
	public int getOverallSentiment() {
		return overallSentiment;
	}
	public void setOverallSentiment(int overallSentiment) {
		this.overallSentiment = overallSentiment;
	}

	public void setID( String id ) {
		this.ID = id;
	}
	
}
