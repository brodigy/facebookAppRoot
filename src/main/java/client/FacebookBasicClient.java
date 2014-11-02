package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.exception.FacebookNetworkException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.Photo;
import storage.DiskUtils;
import com.restfb.types.User;

import java.io.File;
import java.io.IOException;

public class FacebookBasicClient {

	/**
	 * Method that iterates through all user pictures and writes them to disk
	 * @param facebookClient client for facebook graph API
	 * @param userFacebookId facebook user platform ID
	 * @param userDir root path to facebook user where all pictures will be saved
	 * @return the number of pictures written to disk
	 */
	public int writeAllPictures(FacebookClient facebookClient, String userFacebookId, String userDir) throws FacebookNetworkException {
		Connection<Photo> allPhotos = facebookClient.fetchConnection(userFacebookId + "/photos/uploaded", Photo.class);
		int counter = 0;
		if (allPhotos.getData().size() != 0) {
			do {
				for (Photo photo : allPhotos.getData()) {
					counter += 1;
					try {
						if (photo != null && photo.getImages() != null && photo.getId() != null) {
							DiskUtils diskUtils = new DiskUtils();
							diskUtils.writeImageToDisk(userDir + File.separator + photo.getId() + ".jpg", photo.getImages().get(0).getSource());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				allPhotos = facebookClient.fetchConnectionPage(allPhotos.getNextPageUrl(), Photo.class);
			} while (allPhotos.hasNext());
		}
		return counter;
	}

	/**
	 * Method that create a custom directory for each facebook friend and stores all the images and a info txt file
	 * @param facebookClient client for facebook graph API
	 * @param facebookUser user name
	 * @param outputFolder destination folder where info and all data will be stored
	 */
	public boolean storeDataForUser(FacebookClient facebookClient, User facebookUser, String outputFolder) throws FacebookOAuthException {
		DiskUtils diskUtils = new DiskUtils();
		String userDir = diskUtils.createNewUserDir(outputFolder, facebookUser.getName());
		writeUserInfo(facebookClient, facebookUser.getId(), userDir);
		writeAllPictures(facebookClient, facebookUser.getId(), userDir);

		return true;
	}

	/**
	 * Method that stores user facebook info as json file
	 * @param facebookClient client for facebook graph API
	 * @param userFacebookId facebook user platform ID
	 * @param userDir root path to facebook user where all user info will be saved
	 */
	public String writeUserInfo(FacebookClient facebookClient, String userFacebookId, String userDir) {
		User user = facebookClient.fetchObject(userFacebookId, User.class);
		String infoPath ="";

		ObjectMapper jacksonMapper = new ObjectMapper();
		jacksonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonFormat = null;
		try {
			jsonFormat = jacksonMapper.writeValueAsString(user);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		try {
			infoPath = userDir + File.separator + user.getName() + ".txt";
			if(jsonFormat != null) {
				DiskUtils diskUtils = new DiskUtils();
				diskUtils.writeFileToDisk(infoPath, jsonFormat.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return infoPath;
	}

	/**
	 * Method that will store all the data (info and pictures) for all facebook friends
	 * @param facebookClient client for facebook graph API
	 * @param outputDir root folder where all facebook friends will be saved
	 * @return true if the method returns (used in case the user token expires and user input is needed)
	 */
	public boolean storeDataForAllUsers(FacebookClient facebookClient, String outputDir) {
		Connection<User> allFriends = facebookClient.fetchConnection("me/friends", User.class);
		System.out.println("Nr of facebook friends: " + allFriends.getData().size());
		for (User friend : allFriends.getData()) {
			System.out.println("Name: " + friend.getName() + " id: " + friend.getId());

			int tries=0;
			boolean finished = false;
			do {
				try {
					finished = storeDataForUser(facebookClient, friend, outputDir);
				} catch (FacebookOAuthException authException) {
					System.out.println("Invalid or expired user token, please provide a new one");
					facebookClient = FacebookBasicClient.refreshConnection();
					tries++;
				}
			} while (tries < 3 && !finished);
		}

		return  true;
	}

	/**
	 * This method will refresh the facebook client with a new user token thus preventing the 2 hours expiration
	 * @return a new facebook client
	 */
	public static FacebookClient refreshConnection() {
		DiskUtils diskUtils = new DiskUtils();
		String newUserToken = diskUtils.getNewTokenFromConsole();
		return  new DefaultFacebookClient(newUserToken, Version.VERSION_1_0);
	}

}
