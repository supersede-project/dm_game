package eu.supersede.dm.datamodel;

public class UserRequest {
	
	String					ID;
	RequestClassification	classification;
	double					accuracy;
	String					description;
	int						positiveSentiment;
	int						negativeSentiment;
	int						overallSentiment;
	String[]				feedbackIDs;
	String[]				features;
	
	public UserRequest() {}
	
	public UserRequest( 
			String					ID, 
			RequestClassification	classification,
			double					accuracy,
			String					description,
			int						positiveSentiment,
			int						negativeSentiment,
			int						overallSentiment,
			String[]				feedbackIDs,
			String[]				features
			) {
		
		this.ID = ID;
		this.classification = classification;
		this.accuracy = accuracy;
		this.description = description;
		this.positiveSentiment = positiveSentiment;
		this.negativeSentiment = negativeSentiment;
		this.overallSentiment = overallSentiment;
		this.feedbackIDs = feedbackIDs;
		this.features = features;
		
	}
	
}
