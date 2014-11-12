import dao.FacebookUser;
import dao.FacebookUserDao;
import service.PersistService;
import service.PersistServiceImpl;

import java.util.concurrent.Callable;


public class UserTask implements Callable<Boolean> {
	String id;
	private FacebookUserDao facebookUserDao;
	private String outputDir;


	public UserTask(String id, FacebookUserDao facebookUserDao, String outputDir) {
		this.id = id;
		this.facebookUserDao = facebookUserDao;
		this.outputDir = outputDir;
	}


	@Override
	public Boolean call() throws Exception {
		PersistService persistService = new PersistServiceImpl();
		FacebookUser facebookUser = facebookUserDao.getFacebookUserWithCompleteInfo(id);
		System.out.println("New thread was spawned for user :" + facebookUser.getFullName());
		return persistService.persistsUserToDisk(facebookUser, outputDir);
	}
}
