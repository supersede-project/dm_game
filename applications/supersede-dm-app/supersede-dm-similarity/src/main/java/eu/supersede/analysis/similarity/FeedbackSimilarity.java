/**
 * 
 */
package eu.supersede.analysis.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.ontology.OntClass;

import eu.supersede.analysis.FeedbackAnnotator;
import eu.supersede.analysis.FeedbackMessage;
import eu.supersede.analysis.OntologyWrapper;
import eu.supersede.analysis.Utils;
import eu.supersede.analysis.similarity.pojo.Feedback;
import eu.supersede.analysis.similarity.pojo.RequestObject;
import eu.supersede.analysis.similarity.pojo.Requirement;
import eu.supersede.analysis.similarity.pojo.SimilarityResult;

/**
 * @author fitsum
 *
 */
public class FeedbackSimilarity {
	
	public enum SimilarityMeasure {
		HAMMING, JACCARD, KNN
	}
	
	private SimilarityMeasure similarityMeasure = SimilarityMeasure.JACCARD;
	private String ontologyFile = "SDO_ontology.ttl";
	
	/**
	 * 
	 */
	public FeedbackSimilarity(String ontologyFilePath, SimilarityMeasure sm) {
		ontologyFile = ontologyFilePath;
		similarityMeasure = sm;
	}
	
	public FeedbackSimilarity(SimilarityMeasure sm) {
		similarityMeasure = sm;
	}
	
	/**
	 * 
	 */
	public FeedbackSimilarity() {
		
	}
	
	/*
	 * returns the top k requirements similar to the given feedback message
	 */
	public List<SimilarityResult> getSimilarRequirements (RequestObject request){
		List<SimilarityResult> result = new ArrayList<SimilarityResult>();
		
		Feedback userFeedback = request.getFeedback();
		int k = request.getK();
		Requirement[] requirements = request.getRequirements();
		
		// first find the set of concepts for the feedback and each requirement
		FeedbackAnnotator feedbackAnnotator = new FeedbackAnnotator(ontologyFile);
		OntologyWrapper ontologyWrapper = feedbackAnnotator.getOntologyWrapper();
		
		// get concepts from the feedback
		FeedbackMessage feedback = new FeedbackMessage (userFeedback.getText());
		Set<OntClass> feedbackConcepts = feedbackAnnotator.annotateFeedback2(feedback);
		
		// get concepts from the requirements, map them to feature vectors, and compute the distance from the feedback FV
		Map<Integer, Set<OntClass>> requirementConcepts = new LinkedHashMap<>();
		for (Requirement r : requirements) {
			FeedbackMessage req = new FeedbackMessage (r.getTitle() + " " + r.getDescription());
			Set<OntClass> reqConcepts = feedbackAnnotator.annotateFeedback2(req);
			requirementConcepts.put(r.get_id(), reqConcepts);
		}


		// compute similar requirements
		
		// use the k-nearest neighbor algorithm in Weka
		
		/*
		 * FIXME this should be handled better
		 * when mapping concepts to FV, the following approach is used:
		 *  - Requirements are mapped to FVs with their IDs as the 'class' attribute value, without any header for the csv
		 *    because the FV of the feedback (which has the header) will be pre-pended to the beginning of the requirement FVs
		 *    
		 *  - Feedback concepts are mapped to a FV with '?' as 'class' attribute value, and headers for the csv
		 *  
		 *  The NearestNeighbor class merges the feedback FV and requirement FVs so that ultimately there's a valid Weka dataset that can be 
		 *  loaded using Weka's CSVReader into weka instances. Then the instances are split again into requirements and target (feedback)
		 */
		if (similarityMeasure == SimilarityMeasure.KNN) {
			StringBuffer reqFvs = new StringBuffer();
			for (Entry<Integer, Set<OntClass>> entry : requirementConcepts.entrySet()) {
				String vectorString = ontologyWrapper.conceptsToFeatureVectorString(entry.getValue(), false, false);
				reqFvs.append(vectorString + "," + entry.getKey() + "\n");
			}
			// delete last \n
			reqFvs.deleteCharAt(reqFvs.length() - 1);
			
			String fbFv = ontologyWrapper.conceptsToFeatureVectorString(feedbackConcepts, true, true);
			NearestNeighbor nn = new NearestNeighbor(reqFvs.toString(), fbFv);
			result.addAll(nn.computeNearestNeighbors(k));
		}else {
			for (Entry<Integer, Set<OntClass>> entry : requirementConcepts.entrySet()) {
				double d;
				
				if (similarityMeasure == SimilarityMeasure.HAMMING) {
					// map feedback and requirement concepts to feature vector
					int[] feedbackFV = ontologyWrapper.conceptsToFeatureVector(feedbackConcepts);
					int[] requirementFV = ontologyWrapper.conceptsToFeatureVector(entry.getValue());
					
					/*
					 * Compute the similarity based on Hamming distance between the FV of the feedback and each requirement.
					 * Hamming distance is basically the count of the number of times corresponding vector values differ.
					 * Since we want similarity, the method returns d / (d+1)
					 */
					d = Utils.computeHammingSimilarity (feedbackFV, requirementFV);
				} else if (similarityMeasure == SimilarityMeasure.JACCARD) {
					/*
					 *  compute Jaccard similarity index: size of intersection divided by size of union
					 */
					d = Utils.computeJaccardSimilarity (feedbackConcepts, entry.getValue());
				} else {
					throw new RuntimeException("Unknown similarity measure: " + similarityMeasure);
				}
				
				// save to results
				SimilarityResult qr = new SimilarityResult();
				qr.setId(entry.getKey());
				qr.setScore(d);
				
				result.add(qr);
				
			}
		}
		
		// compute ranks based on the distances
		computeRanks (result);
		
		return result.subList(0, k);
	}
	
	
	/**
	 * @param result
	 */
	private void computeRanks(List<SimilarityResult> results) {
		// TODO for now just sorting is enough, since the objects implement Comparable
		Collections.sort(results, Collections.reverseOrder());
		
		// assign the index as ranks
		int rank = 1;
		for (SimilarityResult result : results) {
			result.setRank(rank++);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
