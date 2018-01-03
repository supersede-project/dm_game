/**
 * 
 */
package eu.supersede.analysis.similarity.pojo;

/**
 * @author fitsum
 *
 */
public class SimilarityResult<T> implements Comparable<T> {
	private int id;
	private int rank;
	private double score;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SimilarityResult) {
			if (obj == this) {
				return true;
			}else {
				SimilarityResult other = (SimilarityResult)obj;
				if (other.id == this.id &&
						other.rank == this.rank && 
						Double.compare(other.score, this.score) == 0) {
					return true;
				}else {
					return false;
				}
			}
		}else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(T obj) {
		if (obj instanceof SimilarityResult) {
			SimilarityResult other = (SimilarityResult)obj;
			return Double.compare(this.score, other.score);
		}
		return 0;
	}

}
