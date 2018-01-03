package eu.supersede.analysis;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.Resource;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author fitsum
 *
 */
public class OntologyWrapperTest {

	private OntologyWrapper ontologyWrapper;
	
	@Before
	public void init() {
		String ontologyFile = "SDO_ontology.ttl"; //"saref.ttl"; //
		ontologyWrapper = new OntologyWrapper(ontologyFile);
	}
	
	@Test
	public void testOntologyWrapper() {
		assertNotNull(ontologyWrapper);
	}

	@Test
	public void testLookupConcepts() {
		String term = "consumption";
		Set<OntClass> concepts = ontologyWrapper.lookupConcepts(term);
		assertNotNull(concepts);
		for (Resource concept : concepts) {
			System.out.println(concept.getURI());
		}
	}

//	@Test
//	public void testGetModel() {
//		assertNotNull(ontologyWrapper.getModel());
//	}

	@Test
	public void testGetAllConcepts() {
		List<OntClass> classes = ontologyWrapper.getAllClasses();
		assertNotNull(classes);
		assertFalse(classes.isEmpty());
		for (OntClass cl : classes) {
			if (cl.getLocalName() != null){
				System.out.println("Found class: " + cl.getLocalName() + " : " + cl.getLabel(null) + " : " + cl.toString());
			}else {
				System.err.println("Cannot be viewed as a class.");
			}
		}
	}
	
	@Test
	public void testFindTopConcepts() {
		Set<OntClass> concepts = new HashSet<OntClass>();
		ontologyWrapper.findTopConcepts(concepts );
	}
}
