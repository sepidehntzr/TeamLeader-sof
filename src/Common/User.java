package Common;
import java.util.List;

public class User {

	private String userId;
	private String shape;
	private List<String> sa_adv;
	private List<String> sa_int;
	private List<String> sa_beg;
	
	int no_doc;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String id) {
		this.userId = id;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public List<String> getSa_adv() {
		return sa_adv;
	}
	public void setSa_adv(List<String> sa_adv) {
		this.sa_adv = sa_adv;
	}
	public List<String> getSa_int() {
		return sa_int;
	}
	public void setSa_int(List<String> sa_int) {
		this.sa_int = sa_int;
	}
	public List<String> getSa_beg() {
		return sa_beg;
	}
	public void setSa_beg(List<String> sa_beg) {
		this.sa_beg = sa_beg;
	}
	
	
	public int getNo_doc() {
		return no_doc;
	}
	public void setNo_doc(int no_doc) {
		this.no_doc = no_doc;
	}
	
	
	
}
