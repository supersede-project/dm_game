/**
 * 
 */
package eu.supersede.analysis.similarity.pojo;

/**
 * @author fitsum
 *
 */
public class Requirement{
	private int _id;
	private String description;
	private String title;
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
