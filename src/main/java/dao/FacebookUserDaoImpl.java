package dao;


import com.restfb.FacebookClient;
import com.restfb.exception.FacebookNetworkException;
import com.restfb.exception.FacebookOAuthException;
import service.FacebookService;
import service.FacebookServiceImpl;

import java.util.Map;

public class FacebookUserDaoImpl implements FacebookUserDao {
	Map<String, FacebookUser> facebookUsers;
	FacebookService facebookService;

	public FacebookUserDaoImpl(FacebookClient facebookClient) {
		this.facebookService = new FacebookServiceImpl(facebookClient);
		System.out.println("Please wait until friends data is loaded");
		try {
			this.facebookUsers = facebookService.getAllUsers();
		} catch (FacebookOAuthException | FacebookNetworkException e) {
			System.out.println("Exception " + e.getClass() + " prevented dao initialization");
		}
	}

	@Override
	public Map<String, FacebookUser> getAllFacebookUsers() {
		return facebookUsers;
	}

	@Override
	public FacebookUser getFacebookUserWithBasicInfo(String id) {
		return this.facebookUsers.get(id);
	}

	@Override
	public FacebookUser getFacebookUserWithCompleteInfo(String id) {
		FacebookUser facebookUser = null;
		try {
			facebookUser = this.facebookService.getUserWithInfo(id);
		} catch (FacebookOAuthException | FacebookNetworkException e) {
			System.out.println("Exception " + e.getClass() + " prevented adding info for user with id " + id);
		}

		try {
			facebookUser = this.facebookService.addUserImages(facebookUser);
		} catch (FacebookOAuthException | FacebookNetworkException e) {
			System.out.println("Exception " + e.getClass() + " prevented adding images for user with id " + id);
		}

		return facebookUser;
	}
}
