/**
 * 
 */
package eu.supersede.analysis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.jena.ontology.OntClass;

/**
 * @author fitsum
 *
 */
public class FeedbackMessage {
	
	private int id;
	private String message;
	private String category;
	private String type;
	private Date creationTime;
	private int sentiment;
	
	/**
	 * 
	 */
	public FeedbackMessage(int id, String message, String category, String type, String creationTime, int sentiment) {
		this.id = id;
		this.message = message;
		this.category = category;
		this.type = type;
		this.sentiment = sentiment;
		
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
		try {
		    this.creationTime = df.parse(creationTime);
		} catch (ParseException e) {
			this.creationTime = new Date();
		    e.printStackTrace();
		}
	}

	public FeedbackMessage() {	
	}
	
	/**
	 * a convenience method to build the object with just the feedback message text
	 * @param message
	 */
	public FeedbackMessage(String message) {
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + "," + message + "," + category + "," + type + "," + creationTime.toString() + "," + sentiment; 
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public int getSentiment() {
		return sentiment;
	}

	public void setSentiment(int sentiment) {
		this.sentiment = sentiment;
	}
}
