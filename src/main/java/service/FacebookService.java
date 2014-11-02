package service;

import com.restfb.FacebookClient;
import dao.FacebookUser;

import java.util.List;
import java.util.Map;

public interface FacebookService {

	public FacebookUser getUserWithInfo(String id);
	public FacebookUser addUserImages(FacebookUser facebookUser);
	public FacebookUser getUserWithCompleteInfo(String id);
	public Map<String, FacebookUser> getAllUsers();

}
