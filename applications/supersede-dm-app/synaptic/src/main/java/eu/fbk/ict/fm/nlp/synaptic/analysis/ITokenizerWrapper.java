package eu.fbk.ict.fm.nlp.synaptic.analysis;

import java.io.IOException;

import opennlp.tools.util.InvalidFormatException;

/**
 * ITokenizerWrapper is the interface implemented by TokenizerWrapper for
 * tokenizing data. Two methods have to be implemented: {@link tokenize(String
 * content)} accepts in input a string text and returns an array of tokenized
 * tokens. {@link tokenize(String fileIn, String fileOut)} accepts in input a
 * tsv file containing the text to tokenize, analyzes it and then saves the
 * produced tokens into the provided output file. The input file is a tsv file
 * containing the fields: id, start/end time, sentiment, type and content (the
 * field 'content' contains the text to tokenize). The output is exactly the
 * same file in input but with the field 'content' tokenized.
 * 
 * @author zanoli
 * 
 * @since December 2017
 * 
 */
public interface ITokenizerWrapper {

	/**
	 * Initializes the tokenizer by loading the needed resources (e.g., the
	 * model for tokenization)
	 */
	public void init() throws Exception;

	/**
	 * Tokenizes the input raw text and returns it as an array of tokenized
	 * tokens
	 * 
	 * @param text
	 *            the text to tokenize
	 * @return the tokenized text
	 * 
	 * @exception InvalidFormatException
	 * @exception IOException
	 * @exception Exception
	 * 
	 */
	public String[] tokenize(String text) throws Exception;

	/**
	 * Tokenizes the text that is in the input file (field 'content') in the tsv
	 * file format and saves the result into the output file. The output
	 * contains exactly the same data of the input file but the field 'content'
	 * is tokenized.
	 * 
	 * @param fileIn
	 *            the input file in tsv format and containing the field
	 *            'content' to tokenize
	 * @param fileOut
	 *            the output file in tsv format and containing the field
	 *            'content' tokenized
	 * 
	 * @exception Exception
	 * 
	 */
	public void tokenize(String fileIn, String fileOut) throws Exception;

}
