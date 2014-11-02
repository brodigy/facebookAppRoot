import client.FacebookBasicClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;

public class TestMain {

	private static final String outputDir = "D:\\output\\";

	public static void main(String[] args) {

		FacebookClient facebookClient;
		int tries = 0;
		boolean finished = false;

		do {
			try {

				facebookClient = FacebookBasicClient.refreshConnection();
				FacebookBasicClient facebookBasicClient = new FacebookBasicClient();
				finished = facebookBasicClient.storeDataForAllUsers(facebookClient, outputDir);

			} catch (FacebookOAuthException authException) {
				System.out.println("Invalid or expired user token, please provide a new one");
				tries++;
			}
		} while (tries < 3 && !finished);

	}
}

