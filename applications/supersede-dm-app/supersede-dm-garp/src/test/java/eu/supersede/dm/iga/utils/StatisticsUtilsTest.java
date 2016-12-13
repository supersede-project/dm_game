/**
 * 
 */
package eu.supersede.dm.iga.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author fitsum
 *
 */
public class StatisticsUtilsTest {

	@Test
	public void testDistances() {
		int[] ranking1 = {3, 4, 2, 0, 1};
		int[] ranking2 = {3, 4, 2, 1, 0};
		double kendallTau = StatisticsUtils.distanceKendallTau(ranking1, ranking2);
		double deltaDist = StatisticsUtils.distanceDelta(ranking1, ranking2);
		double pearsonDist = StatisticsUtils.distancePearson(ranking1, ranking2);
		double spearmanDist = StatisticsUtils.distanceSpearman(ranking1, ranking2);
		System.out.println(kendallTau);
		System.out.println(deltaDist);
		System.out.println(pearsonDist);
		System.out.println(spearmanDist);
	}

}
