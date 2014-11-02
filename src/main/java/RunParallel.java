import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.exception.FacebookResponseContentException;
import com.restfb.types.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RunParallel {

    private static final String accessToken = "CAACEdEose0cBAOHaaXAdKwXyfo7s3Vv7ZCiPhQH457bXcIl9seVizWoTCetDq60ZA5WC1WP5S51ieoIU44d9oN4zbGAZCMLJSvyWV7KphilH7ZALW8hdnOIomxRu2yPZCu01UjPCZB0qkNieLFFvhMiDLxHM7EzOlK8fkoTkFRB2j4T3AckFE8zhvHW4uFbatXsOso3npG4W1U7sG7BzM4";
    private static final String outputDir = "//Users//petrealexandru//output";
    private static final Integer NUM_THREADS = 8;

    public static void main(String[] args) {


        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_1_0);
        Connection<User> allFriends = facebookClient.fetchConnection("me/friends", User.class);
        List<User> usersList = allFriends.getData();

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        List<Future<Boolean>> userTasks = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            Callable<Boolean> userTask = new UserTask(usersList.get(i), facebookClient, outputDir);
            Future<Boolean> futureUser = executorService.submit(userTask);
            userTasks.add(futureUser);
        }

        executorService.shutdown();

        int downloadedUsers = 0;
        for (Future<Boolean> futureUser : userTasks) {

            try {
                if (futureUser.get()) {
                    downloadedUsers++;
                }
            } catch (CancellationException | ExecutionException | InterruptedException | FacebookResponseContentException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Number of successfully downloaded users: " + downloadedUsers);

    }
}
