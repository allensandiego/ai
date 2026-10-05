package com.allensandiego.ai;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AiApplicationTests {

	@LocalServerPort
	private int port;

	@Test
	void contextLoads() {
	}

	@Test
	void opensLoginPageAndSavesScreenshot() throws Exception {
		Path screenshot = Path.of("target", "screenshots", "login-page.png");
		Files.createDirectories(screenshot.getParent());

		try (Playwright playwright = Playwright.create();
			 Browser browser = playwright.chromium().launch(
				 new BrowserType.LaunchOptions().setHeadless(true))) {
			Page page = browser.newPage();
			var response = page.navigate(
				"http://localhost:" + port + "/login",
				new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE)
			);

			assertNotNull(response, "The login page should return an HTTP response");
			assertEquals(200, response.status());
			assertTrue(page.getByRole(
				AriaRole.HEADING,
				new Page.GetByRoleOptions().setName("Login to your account")
			).isVisible());

			page.screenshot(new Page.ScreenshotOptions()
				.setPath(screenshot)
				.setFullPage(true));
		}

		assertTrue(Files.size(screenshot) > 0, "The screenshot should not be empty");
	}
}
