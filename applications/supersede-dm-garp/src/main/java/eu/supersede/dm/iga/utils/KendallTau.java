/**
 * 
 */
package eu.supersede.dm.iga.utils;

import java.util.Random;

/**
 * @author fitsum
 *
 */
/******************************************************************************
 *  Compilation:  javac KendallTau.java
 *  Execution:    java KendallTau n
 *  Dependencies: StdOut.java Inversions.java
 *  
 *  Generate two random permutations of size N and compute their
 *  Kendall tau distance (number of inversions).
 *
 ******************************************************************************/

public class KendallTau { 
    public static void main(String[] args) { 
        int n = 6; //Integer.parseInt(args[0]);

        // first permutation
        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            int r = (int) (Math.random() * (i+1));
            p[i] = p[r];
            p[r] = i;
        }

        // second permutation
        int[] q = new int[n];
        for (int i = 0; i < n; i++) {
            int r = (int) (Math.random() * (i+1));
            q[i] = q[r];
            q[r] = i;
        }

        // print permutations
        for (int i = 0; i < n; i++)
            System.out.println(p[i] + " " + q[i]);
        System.out.println();

        // inverse of 2nd permutation
        int[] inv = new int[n];
        for (int i = 0; i < n; i++)
            inv[q[i]] = i;


        // calculate Kendall tau distance
        int tau = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                // check if p[i] and p[j] are inverted
                if (inv[p[i]] > inv[p[j]]) tau++;
            }
        }

    }
}
