package service;

import dao.FacebookUser;

import java.util.Map;

public interface FacebookService {

	public FacebookUser getUserWithInfo(String id);

	public FacebookUser addUserImages(FacebookUser facebookUser);

	public FacebookUser getUserWithCompleteInfo(String id);

	public Map<String, FacebookUser> getAllUsers();

}
