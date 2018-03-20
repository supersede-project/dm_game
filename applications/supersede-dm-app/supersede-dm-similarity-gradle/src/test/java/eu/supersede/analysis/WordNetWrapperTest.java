package eu.supersede.analysis;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.mit.jwi.item.POS;
import edu.smu.tspell.wordnet.SynsetType;
import edu.stanford.nlp.ling.TaggedWord;

import eu.supersede.feedbackanalysis.clustering.*;

/**
 * 
 * @author fitsum
 *
 */
public class WordNetWrapperTest {

	WordNetWrapper wordnet;
	
	@Before
	public void init() {
		String ontologyFile = "SDO_ontology.ttl";
		String lang = "en";
		String wordnetDbPath = null; // null => will be looked up from classpath
		OntologyWrapper ow = new OntologyWrapper(ontologyFile, lang, false, false);
		wordnet = new WordNetWrapper(ow, wordnetDbPath, lang);
	}
	
	@Test
	public void testWordNetWrapper() {
		assertNotNull(wordnet);
	}

	@Test
	public void testExpandTerms() {
		Set<String> terms = new HashSet<>();
		terms.add("readings");
		Set<String> expandedTerms = wordnet.__expandTerms(terms, SynsetType.NOUN);
		for (String t : expandedTerms) {
			System.out.println(t);
		}
	}

	@Test
	public void testStem() {
		String term = "readings are truly wonderfully refreshingly relaxing classifies as good";
		for (String t : term.split(" ")) {
			TaggedWord tt = new TaggedWord(t, POS.NOUN.toString());
			String stem = wordnet.stem(tt, true);
			System.out.println(stem);
		}
		
	}

	@Test
	public void testGetTerms () {
		String sentence = "Hello I use your portal my consumption measurements thus I have good overview Suggestion data entry "
				+ "Would possible change program so I can enter electricity gas water photovoltaic total self consumption simultaneously "
				+ "one page without returning overview page each time In anticipation short feedback? ";
		Set<String> terms = wordnet.getTerms(sentence);
		for (String term : terms) {
			System.out.println(term);
		}
	}
	
}
