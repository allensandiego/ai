package com.allensandiego.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@UsePlaywright
public class AiApplicationTests {

    private static final Path SCREENSHOT_DIRECTORY = Path.of("target", "screenshots");

    @LocalServerPort
    private int port;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private void captureScreenshot(Page page, String filename) throws IOException {
        Files.createDirectories(SCREENSHOT_DIRECTORY);
        page.screenshot(new Page.ScreenshotOptions().setPath(SCREENSHOT_DIRECTORY.resolve(filename)));
    }

    @Test
    void shouldShowLogin(Page page) throws IOException {
        page.navigate(baseUrl() + "/authentication/login");
        captureScreenshot(page, "login.png");

        assertEquals("Login", page.title());
        assertTrue(page.locator("#username").isVisible());
    }

    @Test
    void shouldShowRegistration(Page page) throws IOException {
        page.navigate(baseUrl() + "/authentication/register");
        captureScreenshot(page, "register.png");

        assertEquals("Register", page.title());
        assertTrue(page.locator("#email").isVisible());
    }

    @Test
    void shouldRequireAuthenticationForDashboard(Page page) throws IOException {
        page.navigate(baseUrl() + "/dashboard");
        captureScreenshot(page, "dashboard-redirect-login.png");

        assertEquals(baseUrl() + "/authentication/login", page.url());
        assertEquals("Login", page.title());
    }

}