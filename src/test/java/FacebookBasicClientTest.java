import client.FacebookBasicClient;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.types.Photo;
import com.restfb.types.User;
import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.io.FileUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class FacebookBasicClientTest {

	private static final String imageContent = "random characters that will be written as an image";

	@ClassRule
	@Rule
	public static WireMockClassRule wireMockRule = new WireMockClassRule(18089);

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Test
	public void shouldSaveUserInfoInJsonFormat() throws IOException {
		String dirPath = testFolder.getRoot().getAbsolutePath();

		ObjectMapper mapper = new ObjectMapper();
		User testUser = mapper.readValue(new File("src/test/resources/TestUser.json"), User.class);

		FacebookClient mockFacebookClient = mock(FacebookClient.class);
		when(mockFacebookClient.fetchObject("userNr1", User.class)).thenReturn(testUser);

		FacebookBasicClient facebookBasicClient = new FacebookBasicClient();
		String infoPath = facebookBasicClient.writeUserInfo(mockFacebookClient, "userNr1", dirPath);

		assertThat("Facebook info not the same as expected", FileUtils.fileRead(infoPath),
				sameJSONAs(FileUtils.fileRead("src/test/resources/TestUser.json")).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());
	}

	@Test
	public void shouldSavePictureForUser() throws Exception {
		String dirPath = testFolder.getRoot().getAbsolutePath();

		List<Photo> listOfMockedPhotos = new ArrayList<>();
		List<Photo.Image> listOfMockedImages = new ArrayList<>();

		Connection mockConnection = mock(Connection.class);
		FacebookClient mockFacebookClient = mock(FacebookClient.class);
		Photo mockPhoto = mock(Photo.class);
		Photo.Image mockImage = mock(Photo.Image.class);
		listOfMockedPhotos.add(mockPhoto);
		listOfMockedImages.add(mockImage);

		stubFor(get(urlEqualTo("/test.jpg")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "image/jpeg").withBody(imageContent)));
		String urlPathForImage = "http://localhost:18089/test.jpg";

		when(mockFacebookClient.fetchConnection(anyString(), eq(Photo.class))).thenReturn(mockConnection);
		when(mockFacebookClient.fetchConnectionPage(anyString(), eq(Photo.class))).thenReturn(mockConnection);
		when(mockConnection.getData()).thenReturn(listOfMockedPhotos);
		when(mockConnection.hasNext()).thenReturn(false);
		when(mockPhoto.getId()).thenReturn("1234");
		when(mockPhoto.getImages()).thenReturn(listOfMockedImages);
		when(mockImage.getSource()).thenReturn(urlPathForImage);

		FacebookBasicClient facebookBasicClient = new FacebookBasicClient();
		int nrPictures = facebookBasicClient.writeAllPictures(mockFacebookClient, "userId", dirPath);

		assertThat("Only one picture should be persisted", nrPictures, equalTo(1));
		assertEquals("File doesn't have the expected bytes persisted", imageContent, FileUtils.fileRead(dirPath + File.separator + "1234.jpg"));

	}
}
