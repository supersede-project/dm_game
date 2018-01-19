/**
 * 
 */
package eu.supersede.analysis.similarity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.jena.ontology.OntClass;
import org.junit.Before;
import org.junit.Test;

import eu.supersede.analysis.FeedbackAnnotator;
import eu.supersede.analysis.Utils;
import eu.supersede.analysis.similarity.FeedbackSimilarity.SimilarityMeasure;
import eu.supersede.analysis.similarity.pojo.Feedback;
import eu.supersede.analysis.similarity.pojo.RequestObject;
import eu.supersede.analysis.similarity.pojo.Requirement;
import eu.supersede.analysis.similarity.pojo.SimilarityResult;

/**
 * @author fitsum
 *
 */
public class FeedbackSimilarityTest {

	String ontologyFile = "SDO_ontology.ttl";
	FeedbackAnnotator feedbackAnnotator = new FeedbackAnnotator(ontologyFile);
	Feedback fm = new Feedback();
	
	Requirement r1 = new Requirement();
	Requirement r2 = new Requirement();
	Requirement r3 = new Requirement();
	Requirement r4 = new Requirement();
	Requirement r5 = new Requirement();

	int k = 3;
	RequestObject request;

	@Before
	public void init() {
		String feedback1 = "meter readings appearing incorrect. can you please have a look? thank you.";
		String feedback2 = "Hello I use your portal my consumption measurements thus I have good overview Suggestion data entry Would possible change program so I can enter electricity gas water photovoltaic total self consumption simultaneously one page without returning overview page each time In anticipation short feedback ";
		String feedback3 = "My electricity consumption should 871 higher than similar households What's wrong ";
		String feedback4 = "Hello my engergy savings account heating diagram do shows oil level input from 01.12.2015 What should I do Best wishes ";
		String feedback5 = "I wonder about my consumption my current provider classifies me to monthly 130 euros since january how the consumptions/costs in your app arise?";
		
//		String csvPath = "/data/SUPERSEDE/WP2/SENERCON-data/SENERCON_translated_300_feedback_3_scale.csv";
//		feedbackMessages = feedbackAnnotator.getFeedbackMessages(csvPath);
		
		fm.setText(feedback3);
		
		r1.setDescription(feedback2);
		r1.setTitle(feedback2);
		r1.set_id(1);
		
		r2.setDescription(feedback1);
		r2.setTitle(feedback1);
		r2.set_id(2);
		
		r3.setDescription(feedback4);
		r3.setTitle(feedback4);
		r3.set_id(3);
		
		r4.setDescription(feedback5);
		r4.setTitle(feedback5);
		r4.set_id(4);
		
		r5.setDescription(feedback5);
		r5.setTitle(feedback5);
		r5.set_id(5);
	
		Requirement[] requirements = {r1, r2, r3, r4, r5};
		
		
		request = new RequestObject();
		request.setFeedback(fm);
		request.setK(k);
		request.setRequirements(requirements);
		
	}
	
	/**
	 * Test method for
	 * {@link eu.supersede.analysis.similarity.FeedbackSimilarity#FeedbackSimilarity()}.
	 */
	@Test
	public void testFeedbackSimilarity() {
		FeedbackSimilarity sim = new FeedbackSimilarity();
		assertNotNull(sim);
	}

	/**
	 * Test method for
	 * {@link eu.supersede.analysis.similarity.FeedbackSimilarity#getSimilarRequirements(eu.supersede.analysis.similarity.FeedbackSimilarity.EndUserFeedback, java.util.List, int)}.
	 */
	@Test
	public void testGetSimilarRequirements() {


		
		
		FeedbackSimilarity similarity = new FeedbackSimilarity();
		List<SimilarityResult> similarRequirements = similarity.getSimilarRequirements(request);

		for (SimilarityResult qr : similarRequirements) {
			System.out.println(qr.getRank() + " : " + qr.getId() + " : " + qr.getScore());
		}
	}
	
	@Test
	public void testJaccardSimilarity() {
		Set<Integer> set1 = new HashSet<>();
		set1.add(12);
		set1.add(10);
		set1.add(11);
		set1.add(13);
		
		Set<Integer> set2 = new HashSet<>();
		set2.add(1);
		set2.add(10);
		set2.add(11);
		set2.add(21);
		set2.add(31);
		set2.add(41);
		
		double jaccardSimilarity = Utils.computeJaccardSimilarity(set1, set2);
		assertTrue(jaccardSimilarity == 0.25);
		System.out.println(jaccardSimilarity);
	}

	
	@Test
	public void testHammingDistance() {
		int FV_LEN = 50;
		int[] fv1 = new int[FV_LEN];
		int[] fv2 = new int[FV_LEN];
		Random random = new Random(1);
		for (int i = 0; i < FV_LEN; i++) {
			fv1[i] = random.nextInt(2);
			fv2[i] = random.nextInt(2);
		}
		
		double distance = Utils.computeHammingSimilarity(fv1, fv2);
		assertTrue((Double.compare(0.5, distance) == 0));
		System.out.println(distance);
	}
	
	@Test
	public void testKNN() {
		FeedbackSimilarity similarity = new FeedbackSimilarity(SimilarityMeasure.KNN);
		List<SimilarityResult> similarRequirements = similarity.getSimilarRequirements(request);
		assertTrue(request.getK() == similarRequirements.size());
		assertTrue(similarRequirements.get(0).getScore() >= similarRequirements.get(similarRequirements.size()-1).getScore());
	}
}
