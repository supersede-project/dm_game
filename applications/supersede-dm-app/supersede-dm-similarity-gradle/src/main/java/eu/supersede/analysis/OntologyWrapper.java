package eu.supersede.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;

import com.github.andrewoma.dexx.collection.HashMap;

import edu.smu.tspell.wordnet.SynsetType;

/**
 * 
 * @author fitsum
 *
 */

public class OntologyWrapper {
	private String rdfFileName;
//	private Model model;
	private OntModel ontModel;

	private Map<String, Set<OntClass>> termCache = new java.util.HashMap<String, Set<OntClass>>();

	private List<OntClass> classes;
	
	public OntologyWrapper(String ontology) {
		rdfFileName = ontology;

		ClassLoader classLoader = getClass().getClassLoader();
		InputStream in = classLoader.getResourceAsStream(rdfFileName);
//		InputStream in = FileManager.get().open(rdfFileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + rdfFileName + " not found");
		}

//		model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		ontModel.read(in, null, "TTL");
		
		classes = getAllClasses();
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO this is added to handle classes with owl:Classs, rather than rdf:Class. Is there a better way?
		if (classes.isEmpty()) {
			in = classLoader.getResourceAsStream(rdfFileName);
			ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
			ontModel.read(in, null, "TTL");
			classes = getAllClasses();
		}
	}

	/**
	 * Returns a List of all the classes in the ontology. 
	 * It returns a List, instead of a Set, to guarantee same ordering in a given execution.
	 * Note that the order may be different in different executions. 
	 * @return
	 */
	public List<OntClass> getAllClasses() {
		Set<OntClass> allClasses = new HashSet<OntClass>();
		ExtendedIterator<OntClass> classeIterator = ontModel.listClasses();
		while (classeIterator.hasNext()) {
			OntClass cl = classeIterator.next();
			if (cl.getLocalName() != null) {
				allClasses.add(cl);
			}
		}
		List<OntClass> classes = new LinkedList<OntClass>();
		classes.addAll(allClasses);
		return classes;
	}

	public String conceptsToFeatureVector(Map<FeedbackMessage, Set<OntClass>> annotatedFeedbacks) {
		StringBuffer buffer = new StringBuffer();

		// collect an array of all concepts
//		Set<OntClass> allClasses = getAllClasses();
		for (OntClass cl : classes) {
			buffer.append(cl.getLocalName() + ",");
		}
		// append label
		buffer.append("class,feedback_id\n");
//		buffer.deleteCharAt(buffer.lastIndexOf(","));


		// map each feedbacks concepts to a feature vector
		for (Entry<FeedbackMessage, Set<OntClass>> entry : annotatedFeedbacks.entrySet()) {
			for (OntClass c : classes) {
				if (entry.getValue().contains(c)) {
					buffer.append("1,");
				} else {
					buffer.append("0,");
				}
			}
			// append label, in this case category
			String category = entry.getKey().getCategory().trim();
			if (category.isEmpty()) {
				category = "UNLABELED";
			}
			buffer.append(category + "," + entry.getKey().getId() + "\n");
		}

		return buffer.toString();
	}

	public int[] conceptsToFeatureVector(Set<OntClass> concepts) {
		int[] fv = new int[classes.size()];
		int i = 0;
		for (OntClass concept : classes) {
			if (concepts.contains(concept)) {
				fv[i++] = 1;
			}else {
				fv[i++] = 0;
			}
		}
		return fv;
	}
	
	/**
	 * this is just a convenience method to get the vector as a String so that it can be easily parsed to Weka Instances
	 * @param concepts
	 * @return
	 */
	public String conceptsToFeatureVectorString(Set<OntClass> concepts, boolean header, boolean addClass) {
		StringBuffer fv = new StringBuffer();
		
		if (header) {
			for (OntClass cl : classes) {
				fv.append(cl.getLocalName() + ",");
			}
			if (addClass) {
				fv.append("class");
			}else {
				fv.deleteCharAt(fv.length() - 1);
			}
			fv.append("\n");
		}
		
		for (OntClass concept : classes) {
			if (concepts.contains(concept)) {
				fv.append("1,");
			}else {
				fv.append("0,");
			}
		}
		if (addClass) {
			fv.append("?");
		}else {
			fv.deleteCharAt(fv.length() - 1);
		}
		return fv.toString();
	}
	
	public Set<OntClass> lookupConcepts(String term) {
		term = term.trim().toLowerCase();

		Set<OntClass> concepts = null;

		// first check in cache
		concepts = termCache.get(term);
		if (concepts == null) {
			concepts = new HashSet<OntClass>();

//			ResIterator subjects = model.listSubjects();
			for (OntClass cl : classes) {
//				Resource resource = subjects.next();

				String resourceName = cl.getLocalName();
				if (resourceName != null) {
					// System.err.println(resourceName);
					if (isSimilar(term, cl)) {
						concepts.add(cl);
					}
				}
			}
			termCache.put(term, concepts);
		}
		return concepts;
	}

	private boolean isSimilar(String term, OntClass concept) {
		boolean similar = false;
//		String localName = resource.getLocalName();
		String label = concept.getLabel(null).toLowerCase(); // null = don't care, "en" = English
		Set<String> labelTerms = new HashSet<String>();
		labelTerms.addAll(Arrays.asList(label.split(" ")));
		if (labelTerms.contains(term)) {
			similar = true;
		}
		return similar;
	}

	public String getEnglishLabel(Resource resource) {
		StmtIterator i = resource.listProperties(RDFS.label);
		while (i.hasNext()) {
			Literal l = i.next().getLiteral();

			if (l.getLanguage() != null && l.getLanguage().equals("en")) {
				return l.getLexicalForm().toLowerCase();
			}
		}

		return resource.getLocalName().toLowerCase();
	}

//	public Model getModel() {
//		return model;
//	}
//
//	public void setModel(Model model) {
//		this.model = model;
//	}
	
	public Set<OntClass> findTopConcepts (Set<OntClass> concepts){
		Set<OntClass> topConcepts = new HashSet<OntClass>();
//		for (OntClass concept : concepts) {
			Graph graph = ontModel.getGraph();
			ExtendedIterator<Triple> iterator = graph.find();
			while (iterator.hasNext()) {
				Triple triple = iterator.next();
				System.out.println(triple);
			}
//		}
		return topConcepts;
	}
	
	public OntModel getOntModel() {
		return ontModel;
	}
}
