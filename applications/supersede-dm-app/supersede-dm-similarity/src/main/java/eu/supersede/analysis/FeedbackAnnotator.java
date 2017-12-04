package eu.supersede.analysis;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.stanford.nlp.util.ArrayUtils;

/**
 * 
 * @author fitsum
 *
 */
public class FeedbackAnnotator {

	private OntologyWrapper ontologyWrapper;
	// private POSWrapper posWrapper;
	private WordNetWrapper wordnetWrapper;

//	private AnalysisType analysisType;

	private StringBuffer statBuffer = new StringBuffer();
//	private int feedbackId = 1;
	
	public enum AnalysisType {
		ALL, NOUNS_ONLY, VERBS_ONLY, NOUNS_AND_VERBS
	}
	
	public FeedbackAnnotator(String ontologyFile) {
//		analysisType = at;

		ontologyWrapper = new OntologyWrapper(ontologyFile);
		// posWrapper = new POSWrapper(analysisType);
		wordnetWrapper = new WordNetWrapper(ontologyWrapper);

		// add header to stat buffer
		statBuffer.append("feedback_id,feedback_text,num_terms,num_expanded_terms,delta,num_concepts_found,terms,expanded_terms,concepts,original_category\n");
	}

	private void exportCSVEntry (FeedbackMessage feedback, Set<String> terms, Set<String> expandedTerms, Set<OntClass> concepts) {
		// write stats to csv
		// feedback_id,feedback_text,num_terms,num_expanded_terms,delta,num_concepts_found
		String csvTerms = stringSetToCSV(terms);
		String csvExpandedTErms = stringSetToCSV(expandedTerms);
		String csvConcepts = resourceSetToCSV(concepts);
		Appendable out = new StringWriter();
		CSVFormat format = CSVFormat.DEFAULT;
		CSVPrinter csvPrinter;
		try {
			csvPrinter = new CSVPrinter(out, format);
			csvPrinter.printRecord(feedback.getId(), feedback.getMessage(), terms.size(), expandedTerms.size(), (terms.size() + expandedTerms.size()), concepts.size(), csvTerms, csvExpandedTErms, csvConcepts, feedback.getCategory());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		statBuffer.append(out.toString());
	}
	
	public Map<FeedbackMessage, Map<String, Set<Resource>>> annotateFeedbacks (List<FeedbackMessage> feedbacks){
		Map<FeedbackMessage, Map<String, Set<Resource>>> annotations = new HashMap<>();
		for (FeedbackMessage feedback : feedbacks) {
			Map<String, Set<Resource>> annotatedFeedback = annotateFeedback(feedback);
			annotations.put(feedback, annotatedFeedback);
		}
		return annotations;
	}
	
	public Map<FeedbackMessage, Set<OntClass>> annotateFeedbacks2 (List<FeedbackMessage> feedbacks){
		Map<FeedbackMessage, Set<OntClass>> annotations = new LinkedHashMap<>();
		for (FeedbackMessage feedback : feedbacks) {
			Set<OntClass> annotatedFeedback = annotateFeedback2(feedback);
			annotations.put(feedback, annotatedFeedback);
		}
		return annotations;
	}
	
	public Map<String, Set<Resource>> annotateFeedback(FeedbackMessage feedback) {
		Map<String, Set<Resource>> annotations = new HashMap<>();

		Map<String, Set<String>> expandedTerms = new HashMap<String, Set<String>>();
		
		Set<String> allExpandedTerms = new HashSet<String>();
		
		Set<String> terms = wordnetWrapper.getTerms(feedback.getMessage());
		Set<Resource> conceptsFound = new HashSet<Resource>();
		for (String term : terms) {
			expandedTerms.put(term, new HashSet<String>());
			Synset[] allSenses = wordnetWrapper.getAllSenses(term);
			if (allSenses.length > 0) {
				int maxCount = 0;
				for (Synset sense : allSenses) {
					String[] synonyms = sense.getWordForms();
					Set<Resource> concepts = new HashSet<Resource>();
					for (String synonym : synonyms) {
						concepts.addAll(ontologyWrapper.lookupConcepts(synonym));
					}
					// is this the largest set?
					int count = concepts.size();
					if (count > maxCount) {
						maxCount = count;
						conceptsFound.clear();
						conceptsFound.addAll(concepts);
						
						allExpandedTerms.addAll(Arrays.asList(synonyms));
					}
				}
			}else {
				//System.err.println("No senses found for term: " + term);
			}
			annotations.put(term, conceptsFound);
			
		}
		// export entry to csv report
		Set<OntClass> allConceptsFound = new HashSet<OntClass>();
		for (Set<Resource> concepts : annotations.values()) {
			for (Resource concept : concepts) {
				OntClass ontClass = ontologyWrapper.getOntModel().getOntClass(concept.getURI());
				if (null != ontClass) {
					allConceptsFound.add(ontClass);
				}
			}
//			allConceptsFound.addAll(concepts);
		}
		exportCSVEntry(feedback, terms, allExpandedTerms, allConceptsFound);
		
		return annotations;
	}

	public Set<OntClass> annotateFeedback2(FeedbackMessage feedback) {
		Set<OntClass> annotations = new HashSet<>();

//		Map<String, Set<String>> expandedTerms = new HashMap<String, Set<String>>();
		
		Set<String> allExpandedTerms = new HashSet<String>();
		
		Set<String> terms = wordnetWrapper.getTerms(feedback.getMessage());
		for (String term : terms) {
			Set<OntClass> conceptsFound = new HashSet<OntClass>();
			Set<String> expandedTerms = new HashSet<String>();
			Synset[] allSenses = wordnetWrapper.getAllSenses(term);
			if (allSenses.length > 0) {
				int maxCount = 0;
				for (Synset sense : allSenses) {
					String[] synonyms = sense.getWordForms();
					Set<OntClass> concepts = new HashSet<OntClass>();
					for (String synonym : synonyms) {
						concepts.addAll(ontologyWrapper.lookupConcepts(synonym));
					}
					// is this the largest set?
					int count = concepts.size();
					if (count > maxCount) {
						maxCount = count;
						conceptsFound.clear();
						conceptsFound.addAll(concepts);
						
						expandedTerms.clear();
						expandedTerms.addAll(Arrays.asList(synonyms));
					}
				}
			}else {
				System.err.println("No senses found for term: " + term);
			}
			annotations.addAll(conceptsFound);
			allExpandedTerms.addAll(expandedTerms);
		}
		// export entry to csv report
		exportCSVEntry(feedback, terms, allExpandedTerms, annotations);
		
		return annotations;
	}

	private String resourceSetToCSV(Set<OntClass> classes) {
		StringBuffer buffer = new StringBuffer();
		for (OntClass cl : classes) {
			buffer.append(cl.getLocalName() + ";");
		}
		return buffer.toString();
	}

	private String stringSetToCSV(Set<String> terms) {
		StringBuffer buffer = new StringBuffer();
		for (String term : terms) {
			buffer.append(term + ";");
		}
		return buffer.toString();
	}

	public String getStat() {
		return statBuffer.toString();
	}

	public List<String> _getFeedbackMessages(String csvPath) {
		List<String> feedbackMessages = new ArrayList<String>();
		try {
			Reader reader = new FileReader(new File(csvPath));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			for (CSVRecord csvRecord : csvParser.getRecords()) {
				// int numValues = csvRecord.size();
				// System.out.println(csvRecord.get(0));
				feedbackMessages.add(csvRecord.get(0)); // take the first column only
			}
			csvParser.close();
		} catch (IOException ioException) {
			throw new RuntimeException("Error reading csv file: " + csvPath);
		}
		return feedbackMessages;
	}

	/**
	 * Read feedback text and additional attributes as well. Format of csv as follows:
	 * feedback_id, user_statement_translated,	creation_time,	Type,	Type2,	sentiment,	category
	 * @param csvPath
	 * @return a list of FeedbackMessage objects
	 */
	public List<FeedbackMessage> getFeedbackMessages(String csvPath) {
		List<FeedbackMessage> feedbackMessages = new ArrayList<FeedbackMessage>();
		try {
			Reader reader = new FileReader(new File(csvPath));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> records = csvParser.getRecords();
			for (int i = 1; i < records.size(); i++) { // start from 1, to skip header
				CSVRecord record = records.get(i);
				int id = Integer.parseInt(record.get(0));
				String message = record.get(1);
				String creationTime = record.get(2);
				String type = record.get(3);
				int sentiment = Integer.parseInt(record.get(5));
				String category = record.get(6);
				FeedbackMessage feedbackMessage = new FeedbackMessage(id, message, category, type, creationTime, sentiment);
				feedbackMessages.add(feedbackMessage);
			}
			csvParser.close();
		} catch (IOException ioException) {
			throw new RuntimeException("Error reading csv file: " + csvPath);
		}
		return feedbackMessages;
	}
	
	public static void main(String[] args) throws IOException {
//		if (args.length < 1) {
//			System.out.println("Usage: FeedbackAnnotator ontology.rdf");
//			System.exit(0);
//		}
		String ontologyFile = "SDO_ontology.ttl"; // args[0];
		FeedbackAnnotator feedbackAnnotator = new FeedbackAnnotator(ontologyFile);

		String csvPath = "SUPERSEDE_feedbacks_translated_ALL.csv";
		List<FeedbackMessage> feedbackMessages = feedbackAnnotator.getFeedbackMessages(csvPath);

		
		Map<FeedbackMessage, Set<OntClass>> annotatedFeedbacks = feedbackAnnotator.annotateFeedbacks2(feedbackMessages);
		
		// export stats
		String statFilePath = ontologyFile + ".stats.csv";
		String fvFilePath = ontologyFile + ".fv.csv";
		
		File statFile = new File(statFilePath);
		FileUtils.writeStringToFile(statFile, feedbackAnnotator.getStat());
		
		// export feature vector
		String fv = feedbackAnnotator.ontologyWrapper.conceptsToFeatureVector(annotatedFeedbacks);
		File fvFile = new File(fvFilePath);
		FileUtils.writeStringToFile(fvFile, fv);
	}

	public OntologyWrapper getOntologyWrapper() {
		return ontologyWrapper;
	}

	public void setOntologyWrapper(OntologyWrapper ontologyWrapper) {
		this.ontologyWrapper = ontologyWrapper;
	}
}
