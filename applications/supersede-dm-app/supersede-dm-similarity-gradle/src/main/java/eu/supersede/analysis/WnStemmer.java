package eu.supersede.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IExceptionEntry;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.SimpleStemmer;

/**
 * 
 * @author fitsum
 *
 */
public class WnStemmer extends SimpleStemmer {

    private final IDictionary dict;

	/**
	 * Constructs a WordnetStemmer that, naturally, requires a Wordnet
	 * dictionary.
	 * 
	 * @param dict
	 *            the dictionary to use; may not be <code>null</code>
	 * @throws NullPointerException
	 *             if the specified dictionary is <code>null</code>
	 * @since JWI 1.0
	 */
    public WnStemmer(IDictionary dict) {
        if(dict == null)
        	throw new NullPointerException();
        this.dict = dict;
    }
    
    /** 
     * Returns the dictionary in use by the stemmer; will not return <code>null</code>
     *
     * @return the dictionary in use by this stemmer
     * @since JWI 2.2.0
     */
    public IDictionary getDictionary(){
    	return dict;
    }

    /* 
     * (non-Javadoc) 
     *
     * @see edu.mit.jwi.morph.SimpleStemmer#findStems(java.lang.String, edu.mit.jwi.item.POS)
     */
    public List<String> findStems(String word, POS pos) {
    	
        word = normalize(word);
    	
    	if(pos == null) 
    		return super.findStems(word, null);

        SortedSet<String> result = new TreeSet<String>();
        
        // first look for the word in the exception lists
        IExceptionEntry entry = dict.getExceptionEntry(word, pos);
        boolean isException = false;
        if (entry != null){
        	isException = true;
        	result.addAll(entry.getRootForms());
        }

        // then look and see if it's in Wordnet; if so, the form itself is a stem
        if (dict.getIndexWord(word, pos) != null) { 
        	result.add(word);
        	return new ArrayList<String>(result);
        }
        
        if(isException) 
        	return new ArrayList<String>(result);

        // go to the simple stemmer and check and see if any of those stems are in WordNet
        List<String> possibles = super.findStems(word, pos);
        
    	// Fix for Bug015: don't allow empty strings to go to the dictionary
        for(Iterator<String> i = possibles.iterator(); i.hasNext(); )
        	if(i.next().trim().length() == 0)
        		i.remove();
        
        // check each algorithmically obtained root to see if it's in WordNet
        for (String possible : possibles) {
            if(dict.getIndexWord(possible, pos) != null){
                if (result == null) 
                	result = new TreeSet<String>();
                result.add(possible);
            }
        }

        if(result.isEmpty()) 
//        	if(possibles.isEmpty()){
        		return Collections.<String>emptyList();
//        	} else{
//        		return new ArrayList<String>(possibles);
//        	}
        return new ArrayList<String>(result);
    }
}
