package eu.fbk.ict.fm.nlp.synaptic.classification;

/**
 * IClassify is the interface to implement to annotate a new example.
 * 
 * @author zanoli
 * 
 * @since December 2017
 *
 */
public interface IClassify {

	/**
	 * Classifies a new given examples consisting of a vector of features
	 * extracted by the FeatureExtractorClassify component.
	 * 
	 * @param example
	 *            the vector of features representing the example to classify
	 * 
	 * @return an array where the first element is the assigned label and the
	 *         second element is the score assigned to that label.
	 */
	double[] classify(String[] example);

}
