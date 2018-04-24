package eu.fbk.ict.fm.nlp.synaptic.analysis;

/**
 * This class represents the structure (fields names and their position in the
 * tsv file) of the dataset that is used for training the classifiers.
 * 
 * @author zanoli
 * 
 * @since December 2017
 *
 */
public class FileTSV {

	// the fields index in the tsv input file
	public static int ID = 0;
	public static int START_END_TIME = 1;
	public static int SENTIMENT = 2;
	public static int TYPE = 3;
	public static int CONTENT = 4;
	public static int FIELDS_NUMBER = 5;

}
