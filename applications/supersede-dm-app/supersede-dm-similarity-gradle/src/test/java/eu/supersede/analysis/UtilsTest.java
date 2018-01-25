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
		ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource("stopwords.txt");
		Set<String> stopWords = Utils.readStopWords(url.getFile());
		assertNotNull(stopWords);
		assertFalse(stopWords.isEmpty());
	}

}
