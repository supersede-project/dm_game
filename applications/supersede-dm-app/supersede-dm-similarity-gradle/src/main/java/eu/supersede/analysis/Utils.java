package eu.supersede.analysis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author fitsum
 *
 */
public class Utils {
	/**
	 * reads a list of words from a file
	 * 
	 * @param filename
	 * @return
	 */
	public static Set<String> readStopWords(String path) {
		Set<String> stopWords = new HashSet<String>();
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			while (bufferedReader.ready()) {
				stopWords.add(bufferedReader.readLine());
			}
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stopWords;
	}

	/**
	 * computes the distance between the two feature vectors
	 * 
	 * @param feedbackFV
	 * @param fv
	 * @return
	 */
	public static double computeHammingSimilarity(int[] fv1, int[] fv2) {
		return 1d - (double) Utils.hammingDistance(fv1, fv2) / (double) fv1.length;
	}

	/**
	 * Computes the Hamming distance between the two integer arrays.
	 */
	public static int hammingDistance(int[] x, int[] y) {
		if (x.length != y.length)
			throw new IllegalArgumentException(
					String.format("Arrays have different length: x[%d], y[%d]", x.length, y.length));

		int dist = 0;
		for (int i = 0; i < x.length; i++) {
			if (x[i] != y[i])
				dist++;
		}

		return dist;
	}

	/**
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static <T> double computeJaccardSimilarity(Set<T> set1, Set<T> set2) {
		if (set1.isEmpty() || set2.isEmpty()) {
			return 0;
		}
		Set<T> intersection = new HashSet<T>(set1);
		intersection.retainAll(set2);
		Set<T> union = new HashSet<T>();
		union.addAll(set2);
		union.addAll(set1);
		return (double) intersection.size() / (double) union.size();
	}

	
	/**
	 * Extract resources from Jar
	 * 
	 */
	public String extractResourcesToTempFolder() {
		String destPath = "";
	    try {
	    	String prefix = "WN_";
	    	String suffix = "_WN";
	    	File tempDir = File.createTempFile(prefix, suffix);
	    	destPath = tempDir.getAbsolutePath();
	        JarFile jarFile = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()));
	        Enumeration<JarEntry> enums = jarFile.entries();
	        while (enums.hasMoreElements()) {
	            JarEntry entry = enums.nextElement();
	            if (entry.getName().startsWith("resources")) {
	                File toWrite = new File(destPath + entry.getName());
	                if (entry.isDirectory()) {
	                    toWrite.mkdirs();
	                    continue;
	                }
	                InputStream in = new BufferedInputStream(jarFile.getInputStream(entry));
	                OutputStream out = new BufferedOutputStream(new FileOutputStream(toWrite));
	                byte[] buffer = new byte[2048];
	                for (;;) {
	                    int nBytes = in.read(buffer);
	                    if (nBytes <= 0) {
	                        break;
	                    }
	                    out.write(buffer, 0, nBytes);
	                }
	                out.flush();
	                out.close();
	                in.close();
	            }
	            System.out.println(entry.getName());
	        }
	        jarFile.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
		}
	    return destPath;
	}
	
}
