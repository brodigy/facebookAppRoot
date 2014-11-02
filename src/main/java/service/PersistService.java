package service;

import dao.FacebookUser;

public interface PersistService {
	public boolean persistsUserToDisk(FacebookUser facebookUser, String rootPath);
	public String persistUserDirectory(String name, String rootPath);
	public boolean persistUserInfoToDisk(FacebookUser facebookUser, String rootPath);
	public boolean persistsUserImagesToDisk(FacebookUser facebookUser, String rootPath);
}
