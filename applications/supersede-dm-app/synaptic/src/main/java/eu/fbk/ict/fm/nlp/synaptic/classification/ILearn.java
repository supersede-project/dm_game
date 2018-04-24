package eu.fbk.ict.fm.nlp.synaptic.classification;

/**
 * ILearn is the interface to implement to train the classifier on a new dataset.
 * 
 * @author zanoli
 * 
 * @since December 2017
 * 
 */
public interface ILearn {

	/**
	 * Trains the classifier on the given data set and saves the generated
	 * model.
	 * 
	 * @param inputDataFileName
	 *            the dataset
	 * @param modelFileName
	 *            the generated model
	 * @throws Exception
	 */
	void learn(String inputDataFileName, String modelFileName) throws Exception;

}
