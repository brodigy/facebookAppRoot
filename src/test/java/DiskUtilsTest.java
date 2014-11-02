import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.io.FileUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import storage.DiskUtils;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


public class DiskUtilsTest {

	private static final String imageContent = "random characters that will be written as an image";

	@ClassRule
	@Rule
	public static WireMockClassRule wireMockRule = new WireMockClassRule(18089);

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Test
	public void shouldWriteNewImageFileAndPersistContent() throws IOException {
		String path = testFolder.newFile("image.jpg").getAbsolutePath();
		String imageContent = "random characters that will be written as an image";
		byte[] fileContent = imageContent.getBytes();

		DiskUtils diskUtils = new DiskUtils();
		diskUtils.writeFileToDisk(path, fileContent);
		File testFile = new File(path);

		assertThat("File should exist in the temporary folder", testFile.isFile(), equalTo(true));
		assertEquals("File doesn't have the expected bytes persisted", imageContent, FileUtils.fileRead(path));
	}

	@Test
	public void shouldCreateNewDirectory() {
		String path = testFolder.getRoot().getAbsolutePath();
		String userName = "Test User";

		DiskUtils diskUtils = new DiskUtils();
		diskUtils.createNewUserDir(path, userName);
		File testDirectory = new File(path + File.separator + userName);

		assertThat("Directory doesn't exist", testDirectory.exists(), equalTo(true));
	}

	@Test
	public void shouldDownloadImageFormUrlAndPersistToDisk() throws IOException {
		String pathToDiskForImage = testFolder.getRoot().getAbsolutePath() + File.separator + "newImage.jpg";

		stubFor(get(urlEqualTo("/test.jpg")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "image/jpeg").withBody(imageContent)));
		String urlPathForImage = "http://localhost:18089/test.jpg";

		DiskUtils diskUtils = new DiskUtils();
		diskUtils.writeImageToDisk(pathToDiskForImage, urlPathForImage);
		assertEquals("File doesn't have the expected bytes persisted", imageContent, FileUtils.fileRead(pathToDiskForImage));
	}

}
