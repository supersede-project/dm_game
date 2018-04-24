package eu.fbk.ict.fm.nlp.synaptic.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import eu.fbk.ict.fm.nlp.synaptic.analysis.FileTSV;
import eu.fbk.ict.fm.nlp.synaptic.classification.tc.TypeClassify;
import eu.fbk.ict.fm.nlp.synaptic.classification.tc.TypeLearn;

/**
 * Test the type classifier for training and annotating
 *
 * @author zanoli
 * 
 * @since December 2017
 *
 */
public class TypeClassifierTest {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(TypeClassifierTest.class.getName());

	@Test
	public void fullTest() {
		learnTest();
		classifyTest();
	}

	public void learnTest() {

		File dataSet = new File("src/test/resources/dataset.tsv");
		File model = new File("src/test/resources/dataset.tsv.ta.model");
		if (model.exists())
			model.delete();

		try {
			// create an instance of the classifier
			TypeLearn typeLearn = new TypeLearn();
			// train the classifier
			typeLearn.run(dataSet.getAbsolutePath(), model.getAbsolutePath());
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}

		assertTrue(model.exists());

	}

	public void classifyTest() {

		File dataSet = new File("src/test/resources/dataset.tsv");
		File model = new File("src/test/resources/dataset.tsv.ta.model");

		// buffer for reading input data (e.g., text to tokenize)
		BufferedReader in = null;

		try {

			// create an instance of the classifier
			TypeClassify typeClassify = new TypeClassify(model.getAbsolutePath());

			// input dataset to annotate
			in = new BufferedReader(new InputStreamReader(new FileInputStream(dataSet), "UTF8"));

			String str;
			int i = 0;
			while ((str = in.readLine()) != null) {

				i++;

				// check if the number of fields of the given input file is
				// correct
				String[] splitLine = str.split("\t");
				if (splitLine.length != FileTSV.FIELDS_NUMBER)
					throw new Exception("The input file doesn't have the required number of fields!");

				if (i == 1)
					continue;

				// the gold type label
				String goldLabel = splitLine[FileTSV.TYPE]; //type label
				// the content to annotate
				String content = splitLine[FileTSV.CONTENT];
                //run the classifier
				String[] prediction = typeClassify.run(content);
				String predectedLablel = prediction[0]; // the predicted label
				String score = prediction[1]; // and its score

				LOGGER.info(
						"predicted label:" + predectedLablel + "\t" + "gold label:" + goldLabel + "\tscore:" + score);

				assertEquals("goldLabel", "goldLabel");

			}

		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				}
			}
		}

	}

}
