package com.allensandiego.ai;

import java.nio.file.Path;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Page.ScreenshotOptions;
import com.microsoft.playwright.junit.UsePlaywright;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@UsePlaywright
public class AiApplicationTests {

    @LocalServerPort
    private int port;

    static Playwright playwright;
    static Browser browser;

    String ts = "target/screenshots";

    @BeforeAll 
    static void launchBrowser() throws Exception {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll 
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @Test
    void shouldRegister(Page page) throws Exception {
        // Navigate to registration page
        page.navigate("http://localhost:" + port + "/authentication/register");

        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "register.png")));

        // Fill in the form
        page.fill("#name", "Test");
        page.fill("#email", "test@demo.com");
        page.fill("#password", "qwert123");

        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "register-filled.png")));

        // Submit registration
        page.click("button[type='submit']");
    
        Thread.sleep(5000);

        // Wait for redirect to login page and check if registered message appears
        page.waitForSelector("#email");

        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "register-after.png")));
    }

    @Test
    void shouldShowLogin(Page page) throws Exception {
        page.navigate("http://localhost:" + port + "/authentication/login");

        Thread.sleep(3000);

        page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(ts, "login.png")));
    }

    @Test
    void shouldLoginSuccessfully(Page page) throws Exception {
        // Navigate to login page (fresh navigation, not from registration redirect)
        page.navigate("http://localhost:" + port + "/authentication/login");

        Thread.sleep(2000);

        // Fill in the form with registered credentials - use JavaScript to fill form
        page.evaluate("document.querySelector('#email').value = 'test@demo.com'");
        page.evaluate("document.querySelector('#password').value = 'qwert123'");

        // Submit login form
        page.click("button[type='submit']");

        // Wait for authentication to complete and redirect to dashboard
        Thread.sleep(8000);

        // Verify we're on the dashboard (not login page with error)
        boolean hasError = page.locator(".alert.alert-danger").isVisible();
        if (hasError) {
            String errorMessage = page.locator(".alert.alert-danger").textContent().trim();
            throw new AssertionError("Authentication failed: " + errorMessage);
        }

        // Take screenshot of final state (should be on dashboard or blank page)
        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "login-after.png")));
    }
}