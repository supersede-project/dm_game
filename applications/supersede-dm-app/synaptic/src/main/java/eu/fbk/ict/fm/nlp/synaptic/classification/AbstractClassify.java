package eu.fbk.ict.fm.nlp.synaptic.classification;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public abstract class AbstractClassify implements IClassify {

	// the trained model
	public svm_model model;

	/**
	 * Annotates the example in input and that was produced by the FeatureExtactorClassify component; it 
	 * returns the assigned label with its score value
	 * 
	 * @param example
	 *            the example to annotate
	 */
	public double[] classify(String[] example) {

		//int svm_type = svm.svm_get_svm_type(model);
	    int nr_class = svm.svm_get_nr_class(model);
	    int[] labels = new int[nr_class];
	    svm.svm_get_labels(model, labels);
	    //boolean support_probabilities = svm.svm_check_probability_model(model) == 1;
	    double[] prediction = new double[2]; //contains the predicted label and its score
		
		// read the features vector in input
		svm_node[] x = new svm_node[example.length];
		for (int j = 0; j < example.length; j++) {
			String[] index_value = example[j].split(":");
			x[j] = new svm_node();
			x[j].index = atoi(index_value[0]);
			x[j].value = atof(index_value[1]);
		}
		
		//if(support_probabilities) // returns the real probabilities
	    //{
	    // svm.svm_predict_probability(model, x, scores);
	    //  return scores;
	    // }
	    // or gives 100% to the best label
		
		// annotates the current example
	    int winner = (int)svm.svm_predict(model, x);
	    prediction[0] = winner; //label
	    prediction[1] = 100d; //score value
	    
	    return prediction;

	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

}
