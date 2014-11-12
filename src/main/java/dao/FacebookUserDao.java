package dao;


import java.util.Map;

public interface FacebookUserDao {

	public Map<String, FacebookUser> getAllFacebookUsers();

	public FacebookUser getFacebookUserWithBasicInfo(String id);

	public FacebookUser getFacebookUserWithCompleteInfo(String id);

}
