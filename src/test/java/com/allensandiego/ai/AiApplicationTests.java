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
    void shouldShowLogin(Page page) throws Exception {
        page.navigate("http://localhost:" + port + "/authentication/login");
        assert(page.title().equals("Login"));
        page.screenshot(new Page.ScreenshotOptions().setPath(Path.of(ts, "login.png")));
    }

    @Test
    void shouldRegister(Page page) throws Exception {
        page.navigate("http://localhost:" + port + "/authentication/register");
        assert(page.title().equals("Register"));

        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "register.png")));

        page.fill("#username", "test");
        page.fill("#email", "test@demo.com");
        page.fill("#password", "qwert123");

        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "register-filled.png")));

        page.click("button[type='submit']");
    
        assert(page.url().equals("http://localhost:" + port + "/authentication/login"));

        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "register-after.png")));

        page.fill("#username", "test");
        page.fill("#password", "qwert123");

        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "login-filled.png")));

        page.click("button[type='submit']");

        assert(page.url().equals("http://localhost:" + port + "/dashboard"));
        assert(page.title().equals("Dashboard"));

        page.screenshot(new ScreenshotOptions().setPath(Path.of(ts, "dashboard.png")));
    }

}