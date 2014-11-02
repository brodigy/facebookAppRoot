package utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class DiskUtils {

	/**
	 * Method that persists a image resource found at a specific url to local file system path
	 * @param path provide the full path with the name and format of the image that will be stored
	 * @param imageUrl url location from where the file will be downloaded
	 */
	public void writeImageToDisk(String path, String imageUrl) {
		URL url = null;
		try {
			url = new URL(imageUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		InputStream in = null;
		try {
			in = new BufferedInputStream(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n;

		try {
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}

			out.close();
			in.close();
			byte[] response = out.toByteArray();
			writeFileToDisk(path, response);
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	/**
	 * Method that creates a new folder with a given name
	 * @param rootPath path where new folder will be created
	 * @param userName facebook name for friend
	 * @return full path of the new folder
	 */
	public String createNewUserDir(String rootPath, String userName) {
		String userFolderPath = rootPath + File.separator + userName;
		File dir = new File(userFolderPath);
		dir.mkdir();

		return userFolderPath;
	}

	/**
	 * Method that save content to disk with a given file name
	 * @param path full path where new file will be saved, complete with filename and extension
	 * @param fileContent actual content that will be stored to disk
	 * @throws IOException
	 */
	public void writeFileToDisk(String path, byte[] fileContent) throws IOException {

		FileOutputStream fos = new FileOutputStream(path);
		fos.write(fileContent);
		fos.close();
	}

	/**
	 * Method that will ask a user to give a refreshed access token via cli
	 * @return new user token given as console input
	 */
	public String getNewTokenFromConsole() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String newUserToken = null;

		try {
			System.out.println("Enter new user token bellow:");
			newUserToken = bufferedReader.readLine();
		} catch (IOException e) {
			System.out.println("IO error trying to get the new user token!");
			System.exit(1);
		}

		return newUserToken;
	}

}
