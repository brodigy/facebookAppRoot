import client.FacebookBasicClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

import java.util.concurrent.Callable;


public class UserTask implements Callable<Boolean>{
	private User user;
	private FacebookClient facebookClient;
	private String outputDir;


	public UserTask(User user, FacebookClient facebookClient, String outputDir) {
		this.user = user;
		this.facebookClient = facebookClient;
		this.outputDir = outputDir;
	}


	@Override
	public Boolean call() throws Exception {
		System.out.println("Called UserTask.call() for user:" + user.getName());
		FacebookBasicClient facebookBasicClient = new FacebookBasicClient();
		return facebookBasicClient.storeDataForUser(facebookClient, user, outputDir);
	}
}
