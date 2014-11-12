package service;

import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookNetworkException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.Photo;
import com.restfb.types.User;
import dao.FacebookUser;

import java.util.HashMap;
import java.util.Map;

public class FacebookServiceImpl implements FacebookService {
	private FacebookClient facebookClient;

	public FacebookServiceImpl(FacebookClient facebookClient) {
		this.facebookClient = facebookClient;
	}

	public void setFacebookClient(FacebookClient facebookClient) {
		this.facebookClient = facebookClient;
	}

	@Override
	public FacebookUser getUserWithInfo(String id) throws FacebookOAuthException {
		User user = facebookClient.fetchObject(id, User.class);
		return new FacebookUser(user);
	}

	@Override
	public FacebookUser addUserImages(FacebookUser facebookUser) throws FacebookOAuthException {
		Map<String, String> mapOfImages = new HashMap<>();
		if (facebookUser == null && facebookUser.getId() == null) {
			return facebookUser;
		}

		Connection<Photo> allPhotos = facebookClient.fetchConnection(facebookUser.getId() + "/photos/uploaded", Photo.class);
		if (allPhotos.getData().size() != 0) {
			do {
				for (Photo photo : allPhotos.getData()) {
					if (photo != null && photo.getImages() != null && photo.getImages().get(0) != null && photo.getId() != null) {
						mapOfImages.put(photo.getId(), photo.getImages().get(0).getSource());
					}
				}

				try {
					allPhotos = facebookClient.fetchConnectionPage(allPhotos.getNextPageUrl(), Photo.class);
				} catch (FacebookNetworkException e) {
					System.out.println("Can't find a new pictures page for user:" + facebookUser.getFullName());
				}
			} while (allPhotos.hasNext());
		}

		facebookUser.setImages(mapOfImages);
		return facebookUser;
	}


	@Override
	public FacebookUser getUserWithCompleteInfo(String id) throws FacebookOAuthException {
		FacebookUser facebookUser = getUserWithInfo(id);
		facebookUser = addUserImages(facebookUser);
		return facebookUser;
	}

	@Override
	public Map<String, FacebookUser> getAllUsers() throws FacebookOAuthException {
		Connection<User> allFriends = facebookClient.fetchConnection("me/friends", User.class);
		Map<String, FacebookUser> usersMap = new HashMap<String, FacebookUser>();
		for (User friend : allFriends.getData()) {
			if (friend.getId() != null) {
				usersMap.put(friend.getId(), new FacebookUser(friend.getName(), friend.getId(), null, null, null));
			}
		}

		return usersMap;
	}
}
