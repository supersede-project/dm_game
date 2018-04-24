package eu.fbk.ict.fm.nlp.synaptic.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * TokenizerWrapper implements the interface ITokenizerWrapper for tokenizing
 * data input.
 * 
 * @author zanoli
 * 
 * @since December 2017
 * 
 */
public class TokenizerWrapper implements ITokenizerWrapper {

	// the logger
	// private static final Logger LOGGER = Logger.getLogger(TokenizerWrapper.class.getName());

	// the OpenNLP tokenizer
	private Tokenizer tokenizer;

	/**
	 * Initializes the tokenizer by loading the needed resources (e.g., the
	 * model for tokenization)
	 */
	public void init() throws Exception {

		InputStream modelIn = null;

		try {
			// Get model from resources folder
			modelIn = getClass().getResourceAsStream("/de-token.bin");
			TokenizerModel model = new TokenizerModel(modelIn);
			// Create an instance of the tokenizer
			tokenizer = new TokenizerME(model);
		} finally {
			if (modelIn != null) {
				modelIn.close();
			}
		}

	}

	public String[] tokenize(String text) throws InvalidFormatException, IOException, Exception {

		// the tokenized text
		String tokens[] = null;

		if (text == null)
			return tokens;

		tokens = tokenizer.tokenize(text);

		return tokens;

	}

	public void tokenize(String fileIn, String fileOut) throws Exception {

		// buffer for reading input data (e.g., text to tokenize)
		BufferedReader in = null;
		// buffer for writing output data (e.g., tokenized text)
		BufferedWriter out = null;

		try {

			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF8"));
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut), "UTF-8"));

			String str;
			while ((str = in.readLine()) != null) {

				// check if the number of fields of the given input file is
				// correct
				String[] splitLine = str.split("\t");

				// for (String f : splitLine)
				// System.out.println(f);

				int lineCounter = 0;
				if (splitLine.length != FileTSV.FIELDS_NUMBER) {
					// System.out.println(str);
					throw new Exception("Error in line " + lineCounter + ": wrong number of fields in the input file!");
				}
				lineCounter++;

				// the ID
				String id = splitLine[FileTSV.ID];
				out.write(id);
				out.write("\t");

				// the start/end time
				String StartEndTime = splitLine[FileTSV.START_END_TIME];
				out.write(StartEndTime);
				out.write("\t");

				// the sentiment
				String sentiment = splitLine[FileTSV.SENTIMENT];
				out.write(sentiment);
				out.write("\t");

				// the type
				String type = splitLine[FileTSV.TYPE];
				out.write(type);
				out.write("\t");

				// the content to tokenize
				String content = splitLine[FileTSV.CONTENT];
				String[] tokenizedContent = tokenize(content);
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < tokenizedContent.length; i++) {
					builder.append(tokenizedContent[i]);
					if (i < tokenizedContent.length - 1) // to avoid an addition
															// space after the
															// tokenized content
						builder.append(" "); // a space character to separate
												// tokens
				}
				out.write(builder.toString());

				// end of line
				out.write("\n");
			}

		} catch (Exception ex) {
			// LOGGER.info(ex.getMessage());
			throw (ex);
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}

	}

	/*
	public static void main(String args[]) {

		String text = "Denn es macht nicht mehr, als Hallo Welt"; // All it
																	// does
																	// is
																	// display:
																	// Hello
																	// World

		try {

			// Tokenize a given text string
			//
			// create an instance of the tokenizer
			TokenizerWrapper tokenizerWrapper = new TokenizerWrapper();
			// initialize it
			tokenizerWrapper.init();
			// tokenize the text
			String[] tokenizedText = tokenizerWrapper.tokenize(text);
			// print the tokenized tokens
			for (String t : tokenizedText)
				LOGGER.info(t);
			//
			//
			// Tokenize a text from file
			//
			String fileIn = "src/main/java/dataset_example.tsv";
			String fileOut = "src/main/java/dataset_example.tsv.token";
			tokenizerWrapper.tokenize(fileIn, fileOut);

		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}

	}*/

}
