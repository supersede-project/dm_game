package eu.supersede.analysis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.lucene.analysis.en.EnglishAnalyzer;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.synset.NounReferenceSynset;
import edu.smu.tspell.wordnet.impl.file.synset.VerbReferenceSynset;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;
import eu.supersede.analysis.FeedbackAnnotator.AnalysisType;

/**
 * 
 * @author fitsum
 *
 */

public class WordNetWrapper {

	private WordNetDatabase database;
	private OntologyWrapper ontologyWrapper;
	private String WORDNET_DB_PATH; // = "WordNet-3.0-dict/";

	private WnStemmer stemmer;
//	private String modelFile = "stanford-postagger-2017-06-09/models/english-left3words-distsim.tagger";
//	private MaxentTagger maxentTagger = new MaxentTagger(modelFile);;

	private StanfordCoreNLP pipeline;
	private AnalysisType analysisType;

	private String stopWordsFile = "/stopwords.txt";
	private Set<String> stopWords;

	public WordNetWrapper(OntologyWrapper ow) {
		
		stopWords = Utils.readStopWords(stopWordsFile);
		
		// set WordNet db path
		WORDNET_DB_PATH = Thread.currentThread().getContextClassLoader().getResource("WordNet-3.0-dict").getFile();
//		System.out.println("WordnetDbPath: " + WORDNET_DB_PATH);
		ontologyWrapper = ow;
		System.setProperty("wordnet.database.dir", WORDNET_DB_PATH);

		database = WordNetDatabase.getFileInstance();

		// setup the WordnetStemmer
		final Dictionary dict = new Dictionary(new File(WORDNET_DB_PATH));
		dict.getCache().setMaximumCapacity(Integer.MAX_VALUE);

		try {
			dict.open();
		} catch (IOException e) {
			e.printStackTrace();
		}

		stemmer = new WnStemmer(dict);

		// setup CoreNLP pipeline
		Properties props;
		props = new Properties();
		String annotators = "tokenize, ssplit, pos, lemma";
		props.put("annotators", annotators);
		pipeline = new StanfordCoreNLP(props);
	}

	public Synset[] getAllSenses (String term){
		Synset[] synsets = database.getSynsets(term);
		return synsets;
	}
	
	public Set<String> __expandTerms(Set<String> terms, SynsetType synsetType) {
		Set<String> expandedTerms = new HashSet<String>();
		for (String wordForm : terms) {
			// System.out.println("WORD: " + wordForm);
			// Get the synsets containing the wrod form
			Synset[] synsets = database.getSynsets(wordForm, synsetType, true);

			// Display the word forms and definitions for synsets retrieved
			if (synsets.length > 0) {
				// System.out.println("The following synsets contain '" + wordForm + "' or a
				// possible base form " + "of that text:");
				for (Synset synset : synsets) {
					// for (String wf : synset.getWordForms()) {
					// expandedTerms.add(wf);
					// }
					if (synset.getType() == SynsetType.NOUN && synsetType == SynsetType.NOUN) {
						NounReferenceSynset nounSynset = (NounReferenceSynset) synset;
						// System.out.println("NOUN");
						NounSynset[] hypernyms = nounSynset.getHypernyms();
						NounSynset[] hyponyms = nounSynset.getHyponyms();

						for (NounSynset hyper : hypernyms) {
							expandedTerms.addAll(Arrays.asList(hyper.getWordForms()));
						}

						for (NounSynset hypo : hyponyms) {
							expandedTerms.addAll(Arrays.asList(hypo.getWordForms()));
						}

						// String[] wordForms = synset.getWordForms();
						//
						// for (int j = 0; j < wordForms.length; j++) {
						// System.out.print((j > 0 ? ", " : "") + wordForms[j]);
						// }
						// System.out.println(": " + synset.getDefinition());
					} else if (synset.getType() == SynsetType.VERB && synsetType == SynsetType.VERB) {
						VerbReferenceSynset verbSynset = (VerbReferenceSynset) synset;
						VerbSynset[] verbGroup = verbSynset.getVerbGroup();
						for (VerbSynset vs : verbGroup) {
							expandedTerms.addAll(Arrays.asList(vs.getWordForms()));
						}
					}
				}
			} else {
				System.err.println("No synsets exist that contain " + "the word form '" + wordForm + "'");
			}
		}
		return expandedTerms;
	}

	public String stem(TaggedWord word, boolean filterByPOS) {
		// pos = null ==> check for all POS types
		// POS pos = null; //edu.mit.jwi.item.POS.NOUN

		Set<String> allStems = new HashSet<String>();
		
		POS pos = null;
		if (filterByPOS) {
			pos = getPOS(word.tag());
		}
		if (!filterByPOS || pos != null) {
			List<String> stems = stemmer.findStems(word.word(), pos);
			allStems.addAll(stems);
		}

//		System.err.println("Stemming: " + word);
//		for (String s : allStems)
//			System.err.println(s);

		if (allStems.isEmpty()) {
			return word.word();
		} else {
			return allStems.iterator().next();
		}
	}

	private POS getPOS(String tag) {
		POS pos = null;

		switch (tag) {
		case "NN":
		case "NNS":
		case "NNP":
		case "NNPS":
			pos = POS.NOUN;
			break;
		case "VB":
		case "VBZ":
		case "VBG":
		case "VBN":
		case "VBP":
			pos = POS.VERB;
			break;
//		default:
//			System.err.println(tag);
		}

		return pos;
	}

	public Set<String> getTermsCoreNLP(String text) {
		Set<String> terms = new HashSet<String>();
		Annotation document = new Annotation(text);
		// run all Annotators on this text
		this.pipeline.annotate(document);
		// Iterate over all of the sentences found
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String lemma = token.get(LemmaAnnotation.class);
				// if stop word, skip it
				if (isStopWord(lemma)) {
					System.err.println("Skipping stop word: " + token.word());
					continue;
				}
				
				// get POS tag and collect the lemma only for NOUNS, ADJ, VERB
				String pos = token.get(PartOfSpeechAnnotation.class);

				if (includeCoreNLPTag(pos)) {
					// Retrieve and add the lemma for each word into the
					// list of lemmas
					terms.add(lemma);
				}
			}
		}
		
		return terms;
	}
	
	private boolean includeCoreNLPTag(String posTag) {
		if (posTag.startsWith("NN") ||
				posTag.startsWith("VB") ||
				posTag.startsWith("JJ")){
			return true;
		}else {
			System.err.println("Skipping POS: " + posTag);
			return false;
		}
	}

	private boolean isStopWord(String text) {
		return text.trim().length() < 2 || stopWords.contains(text.trim().toLowerCase());
	}
	
//	public Set<String> getTermsMaxentTagger(String sentence) {
//		Set<String> terms = new HashSet<>();
////		List<? extends HasWord> words = new ArrayList<>();
//		List<List<HasWord>> tokenizedText = MaxentTagger.tokenizeText(new StringReader(sentence));
//		// List<? extends HasWord> tokenizedSentence = tokenizedText.get(0); // take the
//		// first, since there's only one sentence
//		List<List<TaggedWord>> processedTerms = maxentTagger.process(tokenizedText);
//		for (List<TaggedWord> taggedSentence : processedTerms) {
//			for (TaggedWord taggedWord : taggedSentence) {
////				if (getPOS(taggedWord.tag()) != null) {
//				if (!isStopWord(taggedWord.word()) && getPOS(taggedWord.tag()) != null) {
//					terms.add(stem(taggedWord, true));
//				}else {
//					// System.err.println("Stop Word: " + taggedWord.word());
//				}
//			}
//		}
//		return terms;
//	}

	public Set<String> getTerms(String sentence) {
		return getTermsCoreNLP(sentence);
//		return getTermsMaxentTagger(sentence);
	}
}
