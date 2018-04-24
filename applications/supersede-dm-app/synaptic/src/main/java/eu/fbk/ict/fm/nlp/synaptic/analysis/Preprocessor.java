package eu.fbk.ict.fm.nlp.synaptic.analysis;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Preprocessor implements the interface IPreprocessor for pre-processing input
 * data.
 * 
 * @author zanoli
 * 
 * @since December 2017
 * 
 */
public class Preprocessor implements IPreprocessor {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(Preprocessor.class.getName());

	// the tokenizer
	TokenizerWrapper tokenizerWrapper;

	/**
	 * Class constructor
	 */
	public Preprocessor() throws Exception {

		// create an instance of the tokenizer
		tokenizerWrapper = new TokenizerWrapper();
		// and initializes it
		tokenizerWrapper.init();

	}

	public void process(String fileIn, String fileOut) throws Exception {

		LOGGER.info("Pre-processing data...");

		tokenizerWrapper.tokenize(fileIn, fileOut);

		LOGGER.info("done.");

	}

	public String[] process(String content) throws Exception {

		String[] tokenizedContent = tokenizerWrapper.tokenize(content);

		return tokenizedContent;

	}

	/**
	 * Creates an instance of the preprocessor and runs it on the given example
	 * 
	 * @param args
	 */
	public static void main(String args[]) throws Exception {

		// create an instance of the preprocessor
		Preprocessor preprocessor = new Preprocessor();

		try {

			String fileIn = "src/main/java/dataset.tsv";
			String fileOut = "src/main/java/dataset.tsv.token";
			// run the preprocessor on the input file and saves the results into
			// the output file
			preprocessor.process(fileIn, fileOut);

		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}

	}

}
