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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
				"http://localhost:" + port + "/authentication/login",
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

    @Test
    void seededAdminCanLogInWithPassword123() throws Exception {
        Path loginScreenshot = Path.of("target", "screenshots", "login-before.png");
        Path homeScreenshot = Path.of("target", "screenshots", "home-after.png");
        
        try (Playwright playwright = Playwright.create();
                Browser browser = playwright.chromium().launch(
                        new BrowserType.LaunchOptions().setHeadless(true))) {
            Page page = browser.newPage();
            page.navigate("http://localhost:" + port + "/authentication/login");
            
            // Save login page screenshot
            Files.createDirectories(loginScreenshot.getParent());
            page.screenshot(new Page.ScreenshotOptions().setPath(loginScreenshot).setFullPage(true));
            
            // Fill form and submit
            page.getByLabel("Username or email").fill("admin");
            page.getByLabel("Password").fill("password123");
            page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Sign in")
            ).click();
            
            // Wait for navigation to complete
            page.waitForTimeout(2000);
            
            // Debug: Check what elements are visible
            String currentUrl = page.url();
            System.out.println("URL after submit: " + currentUrl);
            System.out.println("Page title: " + page.title());
            System.out.println("Login heading visible: " + page.getByText("Login to your account").isVisible());
            System.out.println("Welcome heading visible: " + page.getByText("Welcome").isVisible());
            System.out.println("Logout visible: " + page.getByText("Logout").isVisible());
            
            // Verify we're on the home page (not login page)
            assertFalse(currentUrl.contains("/authentication/login"), "Should redirect to home page, not remain on login page");
            
            // Verify home page elements are visible (logout button, welcome message)
            boolean h2Visible = page.locator("h2").first().isVisible();
            System.out.println("h2 visible: " + h2Visible);
            boolean logoutVisible = page.getByText("Logout").isVisible();
            System.out.println("Logout visible: " + logoutVisible);
            assertTrue(h2Visible, "Welcome heading should be visible on home page");
            assertTrue(logoutVisible, "Logout button should be visible on home page");
            
            Files.createDirectories(homeScreenshot.getParent());
            page.screenshot(new Page.ScreenshotOptions().setPath(homeScreenshot).setFullPage(true));
            
            // Verify screenshot was created
            assertTrue(Files.size(homeScreenshot) > 0, "Home screenshot should not be empty");
        }
    }
}
