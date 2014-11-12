package dao;


import com.restfb.types.User;

import java.util.Map;

public class FacebookUser {
	private String fullName;
	private String id;
	private String birthDate;
	private String address;
	private String email;
	private Map<String, String> images;

	public FacebookUser(User user) {
		this.fullName = user.getName();
		this.id = user.getId();
		this.birthDate = user.getBirthday();
		this.address = user.getHometownName();
		this.email = user.getEmail();
	}

	public FacebookUser(String fullName, String id, String birthDate, String address, String email) {
		this.fullName = fullName;
		this.id = id;
		this.birthDate = birthDate;
		this.address = address;
		this.email = email;
	}

	public Map<String, String> getImages() {
		return images;
	}

	public void setImages(Map<String, String> images) {
		this.images = images;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
