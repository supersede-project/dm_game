/**
 * 
 */
package eu.supersede.dm.iga.utils;

import java.util.Arrays;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 * @author fitsum
 *
 */
public class StatisticsUtils {

	public static double distancePearson(double[] x, double[] y){
		double correlation = new PearsonsCorrelation().correlation(x, y);
		return correlation;
	}
	
	public static double distanceSpearman(double[] x, double[] y){
		double correlation = new SpearmansCorrelation().correlation(x, y);
		return correlation;
	}
	
	public static double distanceKendallTau(int[] ranking1, int[] ranking2){
		double correlation = new KendallsCorrelation().correlation(toDoubleArray(ranking1), toDoubleArray(ranking2));
		// convert to minimization
		// correlation in [-1,1] ==> 1-correlation in [1,2]
		correlation = 1 - correlation; 
		return correlation;
	}

	private static double[] toDoubleArray(int[] a){
		double[] x = new double[a.length];
		for (int i = 0; i < a.length; i++){
			x[i] = a[i];
		}
		return x;
	}
	
	
	public static double distanceDelta (int[] l1, int[] l2){
		double d = 0;
		int weight = l1.length;
		for (int i = 0; i < l1.length; i++){
			// FIXME: the 'weight' below is commented out because it's wrong! Needs to be based on the ranks!!
			d += Math.abs(l1[i] - l2[i]); // * (weight --);
		}
		d /= l1.length;
		return d;
	}
	
	public static double distanceDeltaIndex (int[] l1, int[] l2){
		double d = 0;
		int weight = l1.length;
		for (int i = 0; i < l1.length; i++){
			// FIXME: the 'weight' below is commented out because it's wrong! Needs to be based on the ranks!!
			d += Math.abs(Arrays.asList(l1).indexOf(i) - Arrays.asList(l2).indexOf(i)); // * (weight --);
		}
		d /= l1.length;
		return d;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] x = {6, 5, 4, 3, 2, 1};
		int[] y = {1, 4, 5, 3, 2, 6};
		double distancePearson = StatisticsUtils.distancePearson(toDoubleArray(x), toDoubleArray(y));
		System.out.println(distancePearson);
		double distanceSpearman = StatisticsUtils.distanceSpearman(toDoubleArray(x), toDoubleArray(y));
		System.out.println(distanceSpearman);
		double distanceKendall = StatisticsUtils.distanceKendallTau(x, y);
		System.out.println(distanceKendall);
	}

}
