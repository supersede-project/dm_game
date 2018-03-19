package eu.supersede.analysis;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.Resource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.supersede.feedbackanalysis.clustering.*;

/**
 * 
 * @author fitsum
 *
 */
public class FeedbackAnnotatorTest {

	List<FeedbackMessage> feedbackMessages = null;
	String ontologyFile = "SDO_ontology.ttl";
	FeedbackAnnotator feedbackAnnotator = new FeedbackAnnotator(ontologyFile);
	
	@Before
	public void init() {
		String feedback1 = "meter readings appearing incorrect. can you please have a look? thank you.";
		String feedback2 = "Hello I use your portal my consumption measurements thus I have good overview Suggestion data entry Would possible change program so I can enter electricity gas water photovoltaic total self consumption simultaneously one page without returning overview page each time In anticipation short feedback ";
		String feedback3 = "My electricity consumption should 871 higher than similar households What's wrong ";
		String feedback4 = "Hello my engergy savings account heating diagram do shows oil level input from 01.12.2015 What should I do Best wishes ";
		String feedback5 = "I wonder about my consumption my current provider classifies me to monthly 130 euros since january how the consumptions/costs in your app arise?";
		
		feedbackMessages = new ArrayList<FeedbackMessage>();
		feedbackMessages.add(new FeedbackMessage(feedback1));
		feedbackMessages.add(new FeedbackMessage(feedback2));
		feedbackMessages.add(new FeedbackMessage(feedback3));
		feedbackMessages.add(new FeedbackMessage(feedback4));
		feedbackMessages.add(new FeedbackMessage(feedback5));

//		File statFile = new File (feedbackAnnotator.getStatFilePath());
//		FileUtils.writeStringToFile(statFile , feedbackAnnotator.getStat());
	}
	
	@Test
	public void testAnnotateFeedback() {
		for (FeedbackMessage feedbackMessage : feedbackMessages) {
			Map<String, Set<Resource>> annotatedFeedback = feedbackAnnotator.annotateFeedback(feedbackMessage);
			for (Entry<String, Set<Resource>> entry : annotatedFeedback.entrySet()) {
				System.out.println("TERM: " + entry.getKey() + " Num Concepts found: " + entry.getValue().size());
				for (Resource concept: entry.getValue()) {
					System.out.println("CONCEPT: " + concept.getLocalName() + " : " + concept.getURI());
				}
			}	
		}
	}

	@Test
	public void testAnnotateFeedback2() {
		Map<FeedbackMessage, Set<OntClass>> annotatedFeedbacks = feedbackAnnotator.annotateFeedbacks2(feedbackMessages);
		assertTrue(annotatedFeedbacks.size() == feedbackMessages.size());
	}
	
	@Test
	public void testGetFeedbackMessages () {
		String csvPath = "feedback_messages.csv";
		int numMessages = 3;
		List<FeedbackMessage> feedbackMessages = feedbackAnnotator.getFeedbackMessages(csvPath);
		assertEquals(numMessages, feedbackMessages.size());
		for (FeedbackMessage message : feedbackMessages) {
			System.out.println(message.toString());
		}
	}
	
}
