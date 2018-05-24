package unipv.irma.opentripplanner.android.model;

public class FeedbackModel {
	private int id;
	private String user_id;
	private String username;
	private String sex;
	private String timestamp;
	private String text;
	private String reply;

	private static FeedbackModel instance = null;

	public FeedbackModel() {
	}

	public static FeedbackModel getInstance() {
		if (instance == null) {
			instance = new FeedbackModel();
		}
		return instance;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

}
