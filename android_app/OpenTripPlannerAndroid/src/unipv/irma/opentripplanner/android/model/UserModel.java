package unipv.irma.opentripplanner.android.model;

public class UserModel {
	private int id;
	private String password;
	private String email;
	private String username;
	private String age;
	private String sex;
	private String category;

	private static UserModel instance = null;

	public UserModel() {
	}

	public static UserModel getInstance() {
		if (instance == null) {
			instance = new UserModel();
		}
		return instance;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
