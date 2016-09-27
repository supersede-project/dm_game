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
	
}
