package eu.supersede.analysis;

import static org.junit.Assert.*;

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
		String path = "stopwords.txt";
		Set<String> stopWords = Utils.readStopWords(path);
		assertNotNull(stopWords);
		assertFalse(stopWords.isEmpty());
	}

}
