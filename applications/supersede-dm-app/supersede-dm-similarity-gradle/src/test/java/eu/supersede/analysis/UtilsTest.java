package eu.supersede.analysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Set;

import org.junit.Test;

/**
 * 
 * @author fitsum
 *
 */
public class UtilsTest {

	@Test
	public void testReadStopWords() {
		Set<String> stopWords = Utils.readStopWords("stopwords.txt");
		assertNotNull(stopWords);
		assertFalse(stopWords.isEmpty());
		assertFalse(stopWords.isEmpty());
	}

}
