package eu.fbk.ict.fm.nlp.synaptic.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * FeatureExtractorLearn is used during the classifier learning phase to produce
 * the features vectors of the examples in input and that have already been
 * pre-processed by the Preprocessor component @see
 * eu.fbk.ict.fm.nlp.analysisPreprocessor. The output is a file containing the
 * generated features index, a file with the labels index and the file
 * containing the features vectors of the given examples of the dataset.
 * 
 * @author zanoli
 * 
 * @since December 2017
 * 
 */
public class FeatureExtractorLearn extends AbstractFeatureExtractor {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(FeatureExtractorLearn.class.getName());

	/**
	 * Class constructor that initialized some data structures, and loads the
	 * stop words
	 */
	public FeatureExtractorLearn(boolean enableStopWordsRemoval) throws Exception {

		this.featuresWeightAndIndex = new HashMap<String, String>();
		this.labelsIndex = new HashMap<String, Integer>();
		this.inverseLabelsIndex = new HashMap<Double, String>();
		this.enableStopWordsRemoval = enableStopWordsRemoval;
		this.stopWords = new HashSet<String>();
		this.weightedWords = new HashMap<String,Float>();
		// load the list of stop words that are in the resources directory
		if (enableStopWordsRemoval)
			loadStopWords();
		
		this.loadWeighteNgrams();

	}

	/**
	 * Extracts the features vectors of the given dataset in input and saves
	 * them into a file. Other files produced are the file of the features index
	 * and the file of the labels index.
	 * 
	 * @param datasetFileName
	 *            the input file containing the pre-processed dataset
	 * @param featuresVectorFileName
	 *            the output file of the features vectors
	 * @param featuresIndexFileName
	 *            the output file of the features index
	 * @param labelsIndexFileName
	 *            the output file of the labels index
	 * @param datasetLabelIndex
	 *            the label field position in the input dataset corresponding to
	 *            the gold label to learn (e.g., 2 that is sentiment)
	 * 
	 * @throws Exception
	 */
	public void extract(String datasetFileName, String featuresVectorFileName, String featuresIndexFileName,
			String labelsIndexFileName, int datasetLabelIndex) throws Exception {

		LOGGER.info("Extracting features....");

		// the pre-processed dataset in input
		BufferedReader in = null;
		// the file that will contain the features index
		BufferedWriter outFeaturesIndex = null;
		// the file that will contain the labels index
		BufferedWriter outLabelsIndex = null;
		// the file that will contain the generated features vectors
		BufferedWriter outFeaturesVector = null;

		try {

			in = new BufferedReader(new InputStreamReader(new FileInputStream(datasetFileName), "UTF8"));
			outLabelsIndex = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(labelsIndexFileName), "UTF-8"));
			outFeaturesIndex = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(featuresIndexFileName), "UTF-8"));
			outFeaturesVector = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(featuresVectorFileName), "UTF-8"));

			String str;
			int lineCounter = 0;
			// for each example in the pre-processed dataset
			while ((str = in.readLine()) != null) {

				lineCounter++;

				if (lineCounter == 1) // this line contains the fields names
										// (e.g., start/end time, sentiment)
					continue;

				// check if the number of fields of the current example is
				// correct
				String[] splitLine = str.split("\t");
				if (splitLine.length != FileTSV.FIELDS_NUMBER)
					throw new Exception("Error in line " + lineCounter + ": wrong number of fields in the input file!");

				// get the label of the current example
				String label = splitLine[datasetLabelIndex];
				// check the current label
				//if (label.indexOf(" ") != -1)
				//throw new Exception("Error in line " + lineCounter + ": labels can not contain space characters!");
				if (label.length() == 0)
					throw new Exception(
							"Error in line " + lineCounter + ": labels must consist of at least one character!");
				// build the labels index
				int index = 0;
				if (labelsIndex.containsKey(label))
					index = labelsIndex.get(label);
				else {
					index = labelsIndex.size() + 1; // labels start from index 1
					labelsIndex.put(label, index);
					outLabelsIndex.write(label + "\t" + index + "\n"); // update
																		// the
																		// labels
																		// index
				}
				outFeaturesVector.write(String.valueOf(index)); // print the
																// label as the
																// first element
																// of the
																// feature
																// vector

				// get the pre-processed text
				String[] preprocessedText = splitLine[FileTSV.CONTENT].split(" ");
				// text normalization
				preprocessedText = normalizeToLowerCase(preprocessedText);
				// stop words removal
				if (enableStopWordsRemoval)
					preprocessedText = removeStopWords(preprocessedText);

				// generate the features vector of the current example
				String[] features = generateNGrams(preprocessedText);
				for (String feature : features) {
					float featureWeight = 0;
					int featureIndex = 0;
					if (featuresWeightAndIndex.containsKey(feature)) {
						String featureWeightAndIndex = featuresWeightAndIndex.get(feature);
						featureWeight = Float.parseFloat(featureWeightAndIndex.split("___")[0]);
						featureIndex = Integer.parseInt(featureWeightAndIndex.split("___")[1]);
					}
					else {
						featureWeight = getWordWeight(feature);
						featureIndex = featuresWeightAndIndex.size() + 1;
						String featureWeightAndIndex = featureWeight + "___" + featureIndex;
						featuresWeightAndIndex.put(feature, featureWeightAndIndex);
						if (featureWeight > 0)
							outFeaturesIndex.write(feature + "\t" + featureWeight + "\t" + featureIndex + "\n");
					}
					// write the example: all features have weight equals to 1
					//outFeaturesVector.write(" " + featureIndex + ":1"); // print
																		// the
																		// feature
																		// (all
																		// the
																		// features
																		// have
																		// the
																		// same
																		// weight
																		// equals
																		// to 1)
					// write the example: all features have weight equals to their idf value
					if (featureWeight > 0)
						outFeaturesVector.write(" " + featureIndex + ":" + featureWeight);
				}
				outFeaturesVector.write("\n");

			}

			LOGGER.info("done.");

		} catch (Exception ex) {
			throw (ex);
		} finally {
			if (in != null)
				in.close();
			if (outLabelsIndex != null)
				outLabelsIndex.close();
			if (outFeaturesIndex != null)
				outFeaturesIndex.close();
			if (outFeaturesIndex != null)
				outFeaturesIndex.close();
			if (outFeaturesVector != null)
				outFeaturesVector.close();
		}

	}
	
	/*
	public static void main(String args[]) {

		try {

			FeatureExtractorLearn featureExtractor = new FeatureExtractorLearn(true);
			String dataSet = "src/main/java/dataset.tsv.token";
			String featuresVectors = "src/main/java/dataset.tsv.token.vectors";
			String featuresIndex = "src/main/java/dataset.tsv.token.features.index";
			String labelsIndex = "src/main/java/dataset.tsv.token.labels.index";
			int labelPosition = 2; // sentiment
			featureExtractor.extract(dataSet, featuresVectors, featuresIndex, labelsIndex, labelPosition);

		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}

	}*/

}
