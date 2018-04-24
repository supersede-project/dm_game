package eu.fbk.ict.fm.nlp.synaptic.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public abstract class AbstractLearn implements ILearn {

	private svm_parameter param; // classifiers parameters
	private svm_problem prob; // set by read_problem
	private svm_model model; // the model to generate
	private int crossValidation = 0; // to enable cross validation
	private int nFold; // number of folds to use in cross-validation
	
	/**
	 * Learns a model given the input training dataset.
	 * 
	 * @param inputDataFileName the input training dataset file name
	 * @param modelFileName the output model file name
	 * 
	 */
	public void learn(String inputDataFileName, String modelFileName) throws Exception {

		// set basic svm classifier parameters
		param = new svm_parameter();
		//param.probability = 1;
		//param.gamma = 0.5;
		//param.nu = 0.5;
		param.C = 1.0;
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		//param.kernel_type = svm_parameter.POLY;
		//param.degree = 2;
		param.cache_size = 20000;
		param.eps = 0.001;
		nFold = 10;

		// load the training dataset that contains the features vectors
		loadDataSet(inputDataFileName);

		// cross validation or training the classifier
		if (crossValidation != 0) {
			crossValidation();
		} else {
			svm.svm_set_print_string_function(new libsvm.svm_print_interface(){
			    @Override public void print(String s) {} // make svm output quiet
			});
			model = svm.svm_train(prob, param); //generate the model
			svm.svm_save_model(modelFileName, model); // save the model
		}
	}
	
	/**
	 * Sets if the classifier has to perform cross-validation or training
	 * 
	 * @param crossValidation 1 for cross-validation; 0 otherwise
	 */
	public void setCrossValidation(int crossValidation) {
		
		this.crossValidation = crossValidation;
		
	}
	
	
	/**
	 * Gets if the classifier has to perform cross-validation or training
	 * 
	 * @return 1 for cross-validation; 0 otherwise
	 */
	public int getCrossValidation() {
		
		return this.crossValidation;
		
	}
	

	/**
	 * Reads the file containing the features vectors produced by the FeatureExtractorLearn component,
	 * puts the features and their weights into the svmlib data structure.
	 * 
	 * @param inputDataFileName the input dataset
	 * 
	 * @throws IOException
	 */
	private void loadDataSet(String inputDataFileName) throws IOException {

		BufferedReader fp = new BufferedReader(new FileReader(inputDataFileName));
		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		int max_index = 0;

		while (true) {
			
			String line = fp.readLine();
			if (line == null)
				break;

			// tokenize the features vector in input and put the features and their weights
			// into the svmlib data structure
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			vy.addElement(atof(st.nextToken()));
			int m = st.countTokens() / 2;
			svm_node[] x = new svm_node[m];
			for (int j = 0; j < m; j++) {
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}
			if (m > 0)
				max_index = Math.max(max_index, x[m - 1].index);
			vx.addElement(x);
		}
		
		prob = new svm_problem();
		prob.l = vy.size();
		prob.x = new svm_node[prob.l][];
		for (int i = 0; i < prob.l; i++)
			prob.x[i] = vx.elementAt(i);
		prob.y = new double[prob.l];
		for (int i = 0; i < prob.l; i++)
			prob.y[i] = vy.elementAt(i);

		fp.close();
	}

	/**
	 * Performs cross-validation
	 * 
	 */
	private void crossValidation() {

		// the confusion matric to calculate precision and recall
		int[][] confusionMatrix = new int[10][10];
		
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[prob.l];

		svm.svm_cross_validation(prob, param, nFold, target);
		if (param.svm_type == svm_parameter.EPSILON_SVR || param.svm_type == svm_parameter.NU_SVR) {
			for (i = 0; i < prob.l; i++) {
				double y = prob.y[i];
				double v = target[i];
				total_error += (v - y) * (v - y);
				sumv += v;
				sumy += y;
				sumvv += v * v;
				sumyy += y * y;
				sumvy += v * y;
			}
			System.out.print("Cross Validation Mean squared error = " + total_error / prob.l + "\n");
			System.out.print("Cross Validation Squared correlation coefficient = "
					+ ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy))
							/ ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy))
					+ "\n");
		} else {
			for (i = 0; i < prob.l; i++) {
				if (target[i] == prob.y[i])
					++total_correct;
				// fill the confusion matrix
				confusionMatrix[(int)target[i]][(int)prob.y[i]] = confusionMatrix[(int)target[i]][(int)prob.y[i]] + 1;
			
			}
			// print the confusion matrix
			/*
			for (int x = 0; x < confusionMatrix.length; x++) {
				for (int y = 0; y < confusionMatrix.length; y++)
					System.out.print(confusionMatrix[x][y] + "\t");
				System.out.println();
			}
			*/
			
			System.out.println("\n\nCross Validation");
			System.out.println("================\n");
			
			//System.out.print("Accuracy = " + 100.0 * total_correct / prob.l + "%\n");
			
			// total number of true positive, false positive, false negative and true negative
			int tp_tot = 0;
			int fp_tot = 0;
			int fn_tot = 0;
			int tn_tot = 0;
			
			for (int l = 0; l < confusionMatrix.length; l++) {
				// true positive, false positive, false negative and true negative
				// for the current label
				int tp = 0;
				int fp = 0;
				int fn = 0;
				int tn = 0;
				for (int x = 0; x < confusionMatrix.length; x++) {
					for (int y = 0; y < confusionMatrix.length; y++) {
						if (l == x) {
							if (x == y)
								tp = tp + confusionMatrix[x][y];
							else
								fp = fp + confusionMatrix[x][y];
						}
						else {
							if (l == y)
								fn = fn + confusionMatrix[x][y];
							else
								tn = tn + confusionMatrix[x][y];
						}
					}
					tp_tot = tp_tot + tp;
					fp_tot = fp_tot + fp;
					fn_tot = fn_tot + fn;
					tn_tot = tn_tot + tn;
				}
				// calculate precision, recall and F1 measure for the current label
				if (tp != 0 || fn != 0) {
				    float precision = (float)tp/(float)(tp + fp);
				    float recall = (float)tp/(float)(tp + fn);
				    float f1 = 2*precision*recall/(precision+recall);
					System.out.println("label:" + l + "\tprecison:" + precision + "\trecall:" + recall + "\tf1:" + f1);
				}
				
			}
			
			float accuracy = (float)(tp_tot)/(float)(tp_tot + fp_tot);
			System.out.println("\nTotal Accuracy:" + accuracy);
			
		}
	}
	
	private static double atof(String str) {
		
		double d = Double.valueOf(str).doubleValue();
		
		return(d);
		
	}

	private static int atoi(String str) {
		
		return Integer.parseInt(str);
		
	}

}