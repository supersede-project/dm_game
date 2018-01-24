package eu.supersede.analysis.similarity;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.analysis.similarity.FeedbackSimilarity.SimilarityMeasure;
import eu.supersede.analysis.similarity.pojo.RequestObject;
import eu.supersede.analysis.similarity.pojo.SimilarityResult;

/**
 * 
 * @author fitsum
 *
 */

@RestController
@RequestMapping("/api")
public class SimilarityController {
	
//	@RequestMapping(value="/public/similarity", headers="Content-Type=application/json", method=RequestMethod.POST)
//	@RequestMapping(value="/public/similarity", method=RequestMethod.POST)
//	public List<SimilarityResult> similarity (@RequestBody RequestObject request){
//		/*
//		 * Currently three similarity measures are implemented: HammingDistance-based, Jaccard-based, KNN-based.
//		 * The default is Jaccard-based. The enum SimilarityMeasure contains the possible values.
//		 */
//		SimilarityMeasure sm = SimilarityMeasure.JACCARD;
//		FeedbackSimilarity feedbackSimilarity = new FeedbackSimilarity(sm);
//		List<SimilarityResult> similarRequirements = feedbackSimilarity.getSimilarRequirements(request);
//		return similarRequirements;
//	}	
	

	@RequestMapping(value="/similarity", method=RequestMethod.GET)
	public String similarityGet(){
		return "Similarity app works! This is a test!";
	}	

}
