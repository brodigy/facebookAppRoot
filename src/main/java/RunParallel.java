import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.exception.FacebookResponseContentException;
import com.restfb.types.User;
import dao.FacebookUser;
import dao.FacebookUserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class RunParallel {

	private static final Integer NUM_THREADS = 8;

	public boolean runMultipleThreads(Map<String, FacebookUser> map, FacebookUserDao facebookUserDao, String outputDir) {

		List<String> usersIds = new ArrayList<>();
		for (Map.Entry<String, FacebookUser> entry : map.entrySet()) {
			usersIds.add(entry.getKey());
		}

		Integer batches = usersIds.size() / NUM_THREADS;
		Integer modulo = usersIds.size() % NUM_THREADS;

		Integer downloadedUsers = 0;
		for (int i = 0; i <= batches; i++) {

			if(modulo !=0 && i != batches) {
				downloadedUsers += runBatch(i * NUM_THREADS, (i + 1) * NUM_THREADS, usersIds, facebookUserDao, outputDir);
			} else {
				downloadedUsers += runBatch(i * NUM_THREADS, i * NUM_THREADS + modulo, usersIds, facebookUserDao, outputDir);
			}

			System.out.println("Successfully downloaded: " + downloadedUsers + " users");
		}

		return true;
	}

	public Integer runBatch(Integer from, Integer to, List<String> usersIds, FacebookUserDao facebookUserDao, String outputDir) {
		ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
		List<Future<Boolean>> userTasks = new ArrayList<>();

		for (int i = from; i < to; i++) {
			Callable<Boolean> userTask = new UserTask(usersIds.get(i), facebookUserDao, outputDir);
			Future<Boolean> futureUser = executorService.submit(userTask);
			userTasks.add(futureUser);
		}

		executorService.shutdown();

		Integer downloadedUsers = 0;
		for (Future<Boolean> futureUser : userTasks) {

			try {
				if (futureUser.get()) {
					downloadedUsers++;
				}
			} catch (CancellationException | ExecutionException | InterruptedException | FacebookResponseContentException e) {
				e.printStackTrace();
			}
		}

		return downloadedUsers;
	}
}
