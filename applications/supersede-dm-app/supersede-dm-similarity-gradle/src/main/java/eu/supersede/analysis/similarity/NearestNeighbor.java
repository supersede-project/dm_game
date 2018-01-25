/**
 * 
 */
package eu.supersede.analysis.similarity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.supersede.analysis.similarity.pojo.SimilarityResult;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.converters.CSVLoader;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;

/**
 * @author fitsum
 *
 */
public class NearestNeighbor {
	/*
	 * the entire set of instances (neighbors)
	 */
	Instances instances;
	
	/*
	 * the target instances for which we are searching for nearest neighbors
	 */
	Instance target;
	
	
	/**
	 * this class computes the nearest k neighbors for a given feedback
	 * @param requirementFvs list of feature vectors, without header, each representing the requirements
	 * @param feedbackFv a feature vector, WITH header, representing the feedback message
	 */
	public NearestNeighbor(String requirementFvs, String feedbackFv) {
		// preppend the feedback fv to the list
		String dataset = feedbackFv + "\n" + requirementFvs;
		
		// read to Weka instances
		ByteArrayInputStream is = new ByteArrayInputStream(dataset.getBytes());
		try {
			CSVLoader csvLoader = new CSVLoader();
			csvLoader.setSource(is);
			instances = csvLoader.getDataSet();
			instances.setClassIndex(instances.numAttributes() - 1);
			// remove first element, and save to target
			target = instances.remove(0);
		} catch (Exception e) {
			throw new RuntimeException("failed to load feature vector to Weka Instances");
		}
	}
	
	public List<SimilarityResult> computeNearestNeighbors (int k) {
		List<SimilarityResult> results = new ArrayList<SimilarityResult>();

		DistanceFunction distanceFunction = new ManhattanDistance(instances);
		NearestNeighbourSearch nn = new LinearNNSearch(instances);
		try {
			nn.setDistanceFunction(distanceFunction);
			Instances nearestNeighbours = nn.kNearestNeighbours(target, k);
			Iterator<Instance> iterator = nearestNeighbours.iterator();
			while (iterator.hasNext()) {
				Instance i = iterator.next();
				int reqId = (int) i.value(i.numAttributes() - 1);
				double d = distanceFunction.distance(target, i);
				d = d / (d + 1d); // convert to similarity
				System.out.println(reqId + " : " + d);
				SimilarityResult result = new SimilarityResult<>();
				result.setId(reqId);
				result.setScore(d);
				results.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
}
