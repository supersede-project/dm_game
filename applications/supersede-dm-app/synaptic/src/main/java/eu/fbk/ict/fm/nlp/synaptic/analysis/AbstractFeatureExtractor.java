package eu.fbk.ict.fm.nlp.synaptic.analysis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractFeatureExtractor {

	// The features index containing the mapping between the produced features
	// and their pre-computed idf values and numeric IDs used by the classifiers.
	// This index is produced during the classifier training phase and then is used
	// during the classifier test phase.
	protected HashMap<String, String> featuresWeightAndIndex;
	// It is used to create a mapping between the labels of the annotated data
	// set and their numeric IDs used by the classifiers.
	// This index is produced during the classifier training phase and then is used
	// during the classifier test phase.
	protected HashMap<String, Integer> labelsIndex;
	// The labels index containing the mapping between the labels IDs 
	// and their text.
	// It is used by the classifiers to change from their numeric predictions to the
	// corresponding labels.
	protected HashMap<Double, String> inverseLabelsIndex;
	// The list of stop words that have to be removed from the pre-processed
	// text in input.
	protected Set<String> stopWords;
	// The list of words weighted by their idf values. The weights are used to build weighted features vectors.
	protected Map<String,Float> weightedWords;
	// Enable stop words removal.
	protected boolean enableStopWordsRemoval;

	/**
	 * Generates the n-grams (i.e., bigrams) of the given tokens in input
	 * 
	 * @param tokens
	 *            the tokens
	 * @return the generated n-grams
	 * @throws Exception
	 */
	public String[] generateNGrams(String[] tokens) throws Exception {

		// unigrams and bigrams
		String[] result = new String[tokens.length + tokens.length - 1];

		// copy unigrams
		for (int i = 0; i < tokens.length; i++)
			result[i] = tokens[i];

		// generate bigrams
		for (int i = 0; i < tokens.length - 1; i++)
			result[tokens.length + i] = tokens[i] + "___" + tokens[i + 1];

		return result;

	}

	/**
	 * Removes the stop words from the tokens in input
	 * 
	 * @param tokens
	 *            the input tokens
	 * @return the tokens in input with the stop words removed
	 * 
	 * @throws Exception
	 */
	public String[] removeStopWords(String[] tokens) throws Exception {

		List<String> tmpList = new ArrayList<String>();
		for (int i = 0; i < tokens.length; i++)
			if (!this.stopWords.contains(tokens[i]))
				tmpList.add(tokens[i]);

		String[] result = tmpList.toArray(new String[tmpList.size()]);

		return result;

	}
	
	/**
	 * Given a token it gets its weight (idf value)
	 * 
	 * @param token
	 *            the input token
	 * @return the weight of the token in input
	 * 
	 * @throws Exception
	 */
	public float getWordWeight(String token) throws Exception {

		float result = (float) - 1;
		
		if (weightedWords.containsKey(token))
			result = this.weightedWords.get(token);

		return result;

	}

	/**
	 * Normalizes the tokens in input to lower case
	 * 
	 * @param tokens
	 *            the input tokens
	 * @return the normalized tokens
	 * 
	 * @throws Exception
	 */
	public String[] normalizeToLowerCase(String[] tokens) throws Exception {

		String[] result = new String[tokens.length];

		for (int i = 0; i < tokens.length; i++)
			result[i] = tokens[i].toLowerCase();

		return result;

	}

	/**
	 * Loads the stop words
	 * 
	 * @throws Exception
	 */
	public void loadStopWords() throws Exception {

		BufferedReader buffer = null;

		try {

			buffer = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/stopwords-de.txt"), "UTF-8"));

			String str;
			while ((str = buffer.readLine()) != null) {
				stopWords.add(str);
			}

		} catch (Exception ex) {
			throw (ex);
		} finally {
			if (buffer != null)
				buffer.close();
		}

	}
	
	/**
	 * Loads the list of n-grams weighted by their idf values. They are used
	 * to build weighted features vectors
	 * 
	 * 
	 * @throws Exception
	 */
	public void loadWeighteNgrams() throws Exception {
		
		BufferedReader buffer = null;
		double nDocuments = (double)1424635; // set by default: it is the number of documents in the data collection

		try {

			buffer = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/dewiki-20140216-ngram-1M.csv"), "UTF-8"));
			
			String str;
			while ((str = buffer.readLine()) != null) {
				String[] splitStr = str.split("\t");
				double wordFrequency = Double.parseDouble(splitStr[0]);
				String word = splitStr[2].toLowerCase().replace(" ", "___");
				float idf = (float)Math.log10(nDocuments/wordFrequency);
				weightedWords.put(word, idf);
			}

		} catch (Exception ex) {
			throw (ex);
		} finally {
			if (buffer != null)
				buffer.close();
		}
		
	}
	
	/**
	 * Gets the label string given its numeric id
	 * 
	 * @param labelId the label id
	 * 
	 * @return the label string
	 * 
	 */
	public String getLabel(double labelId) {

		String label = this.inverseLabelsIndex.get(labelId);

		return label;

	}

}
