package sample.webdriver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver;
import org.springframework.web.context.WebApplicationContext;
import sample.config.MockDataConfig;
import sample.config.WebMvcConfig;
import sample.data.Message;
import sample.webdriver.pages.CreateMessagePage;
import sample.webdriver.pages.ViewMessagePage;

import java.text.ParseException;

import static sample.fest.Assertions.assertThat;

/**
 * An end to end test that validates the {@link CreateMessagePage}. A few things to notice:
 *
 * <ul>
 * <li>Notice that we are able to reuse the same page objects as the {@link MockMvcHtmlUnitDriverCreateMessageTest} and the
 * {@link MockitoMockMvcHtmlUnitDriverCreateMessageTest}. This saves time testing since we do not need to reinvent the wheel for our
 * integration and end to end testing.</li>
 * <li>You will see that all the tests are the same as {@link WebDriverCreateMessageITest}. This shows how little difference
 * there is in how you would write the tests.</li>
 * <li>The only difference is how we initialize our {@link WebDriver}</li>
 * <li>We do not need to run the web application on a server for this test since we are using
 * {@link org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver}</li>
 * </ul>
 *
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebMvcConfig.class, MockDataConfig.class})
@WebAppConfiguration
public class MockMvcHtmlUnitDriverCreateMessageTest extends AbstractWebDriverTest {
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private Message expectedMessage;

	@Test
	public void missingFieldWithJavascriptValidationDisplaysError() {
		CreateMessagePage messagePage = CreateMessagePage.to(getDriver());
		messagePage = messagePage.createMessage(CreateMessagePage.class, "", "");
		assertThat(messagePage.getErrors()).isEqualTo("This field is required.");
	}

	@Test
	public void missingFieldServerSideValidationDisplaysError() {
		CreateMessagePage messagePage = CreateMessagePage.to(getDriver());
		messagePage = messagePage.createMessage(CreateMessagePage.class, "Summary", "");
		assertThat(messagePage.getErrors()).isEqualTo("Message is required.");
	}

	@Test
	public void successfullyCreateMessage() throws ParseException {
		String expectedSummary = expectedMessage.getSummary();
		String expectedText = expectedMessage.getText();

		CreateMessagePage page = CreateMessagePage.to(getDriver());

		ViewMessagePage viewMessagePage = page.createMessage(ViewMessagePage.class, expectedSummary, expectedText);
		assertThat(viewMessagePage.getMessage()).isEqualTo(expectedMessage);
		assertThat(viewMessagePage.getSuccess()).isEqualTo("Successfully created a new message");
	}

	@Override
	protected WebDriver createDriver() {
		return new MockMvcHtmlUnitDriver(context, true);
	}
}
