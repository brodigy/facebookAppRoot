package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.FacebookUser;
import utils.DiskUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PersistServiceImpl implements PersistService {

	@Override
	public boolean persistsUserToDisk(FacebookUser facebookUser, String rootPath) {
		String userDir = persistUserDirectory(facebookUser.getFullName(), rootPath);
		persistUserInfoToDisk(facebookUser, userDir);
		persistsUserImagesToDisk(facebookUser, userDir);

		return true;

	}

	@Override
	public String persistUserDirectory(String name, String rootPath) {
		DiskUtils diskUtils = new DiskUtils();
		return diskUtils.createNewUserDir(rootPath, name);

	}

	@Override
	public boolean persistUserInfoToDisk(FacebookUser facebookUser, String userDir) {
		String infoPath;
		ObjectMapper jacksonMapper = new ObjectMapper();
		jacksonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonFormat = null;

		try {
			jsonFormat = jacksonMapper.writeValueAsString(FacebookUser.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		try {
			infoPath = userDir + File.separator + facebookUser.getFullName() + ".txt";
			if (jsonFormat != null) {
				DiskUtils diskUtils = new DiskUtils();
				diskUtils.writeFileToDisk(infoPath, jsonFormat.getBytes());
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean persistsUserImagesToDisk(FacebookUser facebookUser, String userDir) {

		Map<String, String> userImages = facebookUser.getImages();
		if (userImages == null) {
			return false;
		}
		int count = 0;

		for (Map.Entry<String, String> entry : userImages.entrySet()) {
			DiskUtils diskUtils = new DiskUtils();
			diskUtils.writeImageToDisk(userDir + File.separator + entry.getKey() + ".jpg", entry.getValue());
			count++;

		}

		if (userImages.size() == count) {
			return true;
		} else {
			return false;
		}
	}
}
