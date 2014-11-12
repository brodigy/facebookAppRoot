import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import dao.FacebookUser;
import dao.FacebookUserDao;
import dao.FacebookUserDaoImpl;
import service.PersistService;
import service.PersistServiceImpl;

import java.util.Map;

public class Run {

	public static void main(String[] args) {

		FacebookClient facebookClient = new DefaultFacebookClient(args[0], Version.VERSION_1_0);
		FacebookUserDao facebookUserDao = new FacebookUserDaoImpl(facebookClient);
		Map<String, FacebookUser> map = facebookUserDao.getAllFacebookUsers();

		if (args.length == 1) {
			showAllAvailableFriends(map);
		} else if (args.length == 2) {
			RunParallel runParallel = new RunParallel();
			runParallel.runMultipleThreads(map, facebookUserDao, args[0]);
		} else if (args.length == 3) {
			getFacebookUserById(map, args[2], facebookUserDao, args[1]);
		}

		System.out.println("The task has finished!");
	}

	public static void showAllAvailableFriends(Map<String, FacebookUser> map) {
		for (Map.Entry<String, FacebookUser> entry : map.entrySet()) {
			System.out.println(entry.getValue().getFullName() + " - " + entry.getKey());
		}
	}

	public static void getFacebookUserById(Map<String, FacebookUser> map, String id, FacebookUserDao facebookUserDao, String outputDir) {

		if (map.containsKey(id)) {
			PersistService persistService = new PersistServiceImpl();
			FacebookUser facebookUser = facebookUserDao.getFacebookUserWithCompleteInfo(id);
			persistService.persistsUserToDisk(facebookUser, outputDir);
		} else {
			System.out.println("Could not find user with specified id: " + id);
		}
	}
}
