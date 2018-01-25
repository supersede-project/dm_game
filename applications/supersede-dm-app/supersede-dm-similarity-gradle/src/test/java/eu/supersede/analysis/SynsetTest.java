package eu.supersede.analysis;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import gate.creole.POSTagger;
import pt.tumba.spell.SpellChecker;

/**
 * 
 * @author fitsum
 *
 */

public class SynsetTest {

	String sentence = "meter readings appearing incorrect. can you please have a look? thank you.";
	String rdfFileName = "SDO_ontology.ttl";
	
	@Before
	public void setUp (){
		String wordnetDbPath = Thread.currentThread().getContextClassLoader().getResource("WordNet-3.0-dict").getFile();
		System.setProperty("wordnet.database.dir", wordnetDbPath);
	}
	
	
	@Test
	public void testNLPAnnotation () {
		Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
		Annotation annotation = new Annotation(sentence);
		pipeline.annotate(annotation);
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
        	for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        		System.out.println(token.toShorterString());
        		String [] strToken=token.toShorterString().split(" ");
//                String word=strToken[2].split("=")[1];
                String word=strToken[2].substring(strToken[2].indexOf("=")+1,strToken[2].length());
                System.out.println(word);     
                String partOfSpeech=token.toShorterString().substring(token.toShorterString().indexOf("PartOfSpeech")+13,token.toShorterString().indexOf("Lemma"));
                System.out.println(partOfSpeech);
        	}
        }
	}

/*	
	@Test
	public void testPOSTagging(){
		MaxentTagger maxentTagger = new MaxentTagger("stanford-postagger-2017-06-09/models/english-left3words-distsim.tagger");
		String tagged = maxentTagger.tagString(sentence);
		for (String part : tagged.split(" ")) {
			String[] parts = part.split("_");
			String word = parts[0];
			String tag = parts[1];
			
			switch (tag) {
			case "NN":
				System.out.print("Noun: ");
				break;
			case "NNS":
				System.out.println("Noun plural: ");
				break;
			case "JJ":
				System.out.println("Adjective: ");
				break;
			case "MD":
				System.out.println("Modal verb: ");
				break;
			case "VB":
				System.out.println("Verb: ");
				break;
			case "VBP":
				System.out.println("Verb: ");
				break;
			case "DT":
				System.out.println("Verb (direct?): ");
				break;
			case "PRP":
				System.out.println("Preposition?: ");
				break;
			default:
				System.err.println(tag);
			}
			System.out.print(word);
			System.out.println();
		}
		System.out.println(tagged);
	}
*/
	
//	@Test
//	public void testSpellChecker(){
//		SpellChecker spellChecker = new SpellChecker();
//		boolean useFrequency = true;
//		for (String word : sentence.split(" ")){
//			if (word.trim().isEmpty())
//				continue;
//			String spellChecked = spellChecker.findMostSimilar(word, useFrequency);
//			System.out.print(spellChecked + " ");
//		}
//	}
	
	@Test
	public void test() {
//		String[] args = { sentence };
//		
//		// Concatenate the command-line arguments
//		StringBuffer buffer = new StringBuffer();
//		for (int i = 0; i < args.length; i++) {
//			buffer.append((i > 0 ? " " : "") + args[i]);
//		}
//		String wordForm = buffer.toString();
		
		POSTagger posTagger = new POSTagger();
		
		for (String wordForm : sentence.split(" ")){
		
			// Get the synsets containing the wrod form
			WordNetDatabase database = WordNetDatabase.getFileInstance();
			Synset[] synsets = database.getSynsets(wordForm, null, true);
			
			// Display the word forms and definitions for synsets retrieved
			if (synsets.length > 0) {
				System.out.println("The following synsets contain '" + wordForm + "' or a possible base form " + "of that text:");
				for (Synset synset : synsets) {
					if (synset.getType() == SynsetType.VERB){
						System.out.println("VERB");
						String[] wordForms = synset.getWordForms();
						
						for (int j = 0; j < wordForms.length; j++) {
							System.out.print((j > 0 ? ", " : "") + wordForms[j]);
						}
						System.out.println(": " + synset.getDefinition());
					}
				}
			} else {
				System.err.println("No synsets exist that contain " + "the word form '" + wordForm + "'");
			}
		}

	}
	
}
