package eu.fbk.ict.fm.nlp.synaptic.classification.tc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import eu.fbk.ict.fm.nlp.synaptic.analysis.FeatureExtractorClassify;
import eu.fbk.ict.fm.nlp.synaptic.analysis.Preprocessor;
import eu.fbk.ict.fm.nlp.synaptic.classification.AbstractClassify;
import libsvm.svm;

/**
 * TypeClassify is the class that implements the classifier for annotating an
 * example in input with its 'type' category The classifier can be used from
 * Command Line Interface or its API by calling the method 'run', e.g.,
 * 
 * CLI:
 * 
 * 		java TypeClassifier -c content -m modelFileName
 * 
 * API: 
 * 
 * 		TypeClassify typeClassify = new TypeClassify(modelFileName); 
 * 		String[] annotation = typeClassify.run(content); 
 * 		String label = annotation[0]; // the predicted label
 * 		String score = annotation[1]; // and its score
 * 		System.out.println("predicted label:" + label + " score:" + score);
 *
 *
 * WHERE: 
 * 
 * 		content is the text string to classify 
 * 		modelFileName is the model generated during the classifier training phase
 * 
 * 
 * @author zanoli
 * 
 * @since December 2017
 *
 */
public class TypeClassify extends AbstractClassify {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(TypeClassify.class.getName());

	// enable stop words removal
	private static boolean enableStopWordsRemoval = true;

	// the preprocessor for pre-processing data
	private Preprocessor preprocessor;
	// the feature extractor for producing the features vectors
	private FeatureExtractorClassify featureExtractor;

	/**
	 * Class constructor; it uses the model generated during the classifier
	 * training phase and the two other files (modelFileName.features.index,
	 * modelFileName.labels.index) always produced by the classifier while
	 * training to initializes the classifier itself as well as the pipeline for
	 * pre-processing data to be annotated.
	 * 
	 * @param modelFileName
	 *            the model to use for classifying data
	 */
	public TypeClassify(String modelFileName) throws Exception {

		// load the model generated during the classifier training phase
		model = svm.svm_load_model(modelFileName);
		// initialize the preprocessor for pre-processing data
		preprocessor = new Preprocessor();
		// the index of the features and labels generated during the training
		// phase to produce the model
		String featuresIndexFileName = modelFileName + ".features.index";
		String labelsIndexFileName = modelFileName + ".labels.index";
		// initialize the feature extractor for generating the features from the
		// dataset
		featureExtractor = new FeatureExtractorClassify(featuresIndexFileName, labelsIndexFileName,
				enableStopWordsRemoval);

	}

	/**
	 * Classifiers the given text; it returns the predicted label and its score
	 * 
	 * @param text
	 *            the content text to classify
	 * 
	 * @return the label assigned to the given text and its score value
	 * 
	 * @throws Exception
	 * 
	 */
	public String[] run(String text) throws Exception {

		String[] result = null;

		try {

			// pre-process the text
			String[] preprocessedContent = preprocessor.process(text);
			// extract the features vector
			String[] featuresVector = featureExtractor.extract(preprocessedContent);
			// classify
			double[] prediction = classify(featuresVector);
			// get the predicted label
			String label = featureExtractor.getLabel(prediction[0]);
			// and its score
			String score = String.valueOf(prediction[1]);
			result = new String[2]; // label and its score
			result[0] = label;
			result[1] = score;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;

	}

	/**
	 * The classifier entry point
	 * 
	 * Usage: java TypeClassifier -c content -m model
	 * 
	 * WHERE: content is the text to classify model is the model generated
	 * during the classifier training phase
	 * 
	 */
	public static void main(String[] args) {

		// create Options object
		Options options = new Options();

		// add data set option
		Option content = new Option("c", "content", true, "content to classify");
		content.setRequired(true);
		options.addOption(content);

		// add model set option
		Option model = new Option("m", "model", true, "generated model");
		model.setRequired(true);
		options.addOption(model);

		// create the command line parser
		CommandLineParser parser = new BasicParser();
		// the formatter for parse exception
		HelpFormatter formatter = new HelpFormatter();
		StringWriter out = new StringWriter();
		PrintWriter pw = new PrintWriter(out);

		try {

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			// the text to classify
			String text = cmd.getOptionValue("content");
			// the model to use for classifying
			String modelFileName = cmd.getOptionValue("model");
			// create an instance of the classifier
			TypeClassify typeClassify = new TypeClassify(modelFileName);
			// run the classifier
			String[] result = typeClassify.run(text);
			String label = result[0]; // the predicted label
			String score = result[1]; // and its score

			LOGGER.info("predicted label:" + label + " score:" + score);

		} catch (ParseException e) {

			formatter.printHelp(pw, 80, "", "TypeClassify", options, formatter.getLeftPadding(),
					formatter.getDescPadding(), "");
			pw.flush();
			LOGGER.log(Level.WARNING, out.toString());

		} catch (Exception ex) {

			LOGGER.log(Level.SEVERE, ex.getMessage());

		}

	}

}
