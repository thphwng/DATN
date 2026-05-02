package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import page.LoginPage;
import org.testng.annotations.Listeners;

import java.time.Duration;
public class LoginTest extends BaseTest {

    // TC01: Login thành công
    @Test
    public void TC01_Login_Success() {

        LoginPage page = new LoginPage(driver);

        // Điền đúng tên đăng nhập, mật khẩu
        page.loginQuanLy("admin", "999999");
        // đợi 15s để lòa trang
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        // chuyển đến trang dashboard
        wait.until(ExpectedConditions.urlContains("Dashboard"));
        // bắt element dashboard hiện ra
        By dashboardElement = By.xpath("//header[contains(@class, 'dashboard')]");

        boolean isDashboardVisible = wait.until(
                ExpectedConditions.visibilityOfElementLocated(dashboardElement)
        ).isDisplayed();

        // Bắt dashboard hiện ra sau khi login
        Assert.assertTrue(isDashboardVisible,
                "Không hiển thị Dashboard sau khi login!");

    }
    // TC02: Login sai Password
    @Test
    public void TC02_WrongPassword() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy("admin", "123");

        // 1. Verify error message (đúng 100%)
        String actualMsg = page.getErrorMessage().trim();

        Assert.assertEquals(actualMsg,
                "Tên đăng nhập hoặc mật khẩu không hợp lệ.",
                "Error message not correct. Actual: " + actualMsg);

        // 2. Verify password bị reset (tuỳ hệ thống)
        Assert.assertEquals(page.getPasswordValue(), "",
                "Password is not cleared after failed login");

        // 3. Verify username vẫn giữ (nếu hệ thống giữ)
        Assert.assertEquals(page.getUsernameValue(), "admin",
                "Username should remain after failed login");

    }
    // TC03: Sai username
    @Test
    public void TC03_WrongUsername() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy("abc", "123456");

        Assert.assertTrue(page.getErrorMessage().length() > 0);
    }

    // TC04: Username rỗng
    @Test
    public void TC04_EmptyUsername() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy("", "123456");

        Assert.assertTrue(page.getErrorMessage().length() > 0);
    }

    // TC05: Password rỗng
    @Test
    public void TC05_EmptyPassword() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy("admin", "");

        Assert.assertTrue(page.getErrorMessage().length() > 0);
    }

    // TC06: Cả 2 rỗng
    @Test
    public void TC06_EmptyAll() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy("", "");

        Assert.assertTrue(page.getErrorMessage().length() > 0);
    }

    // TC07: Username có khoảng trắng
    @Test
    public void TC07_UsernameWithSpace() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy(" admin ", "123456");

        Assert.assertTrue(page.getErrorMessage().length() > 0);
    }

    // TC08: Password có khoảng trắng
    @Test
    public void TC08_PasswordWithSpace() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy("admin", " 123456 ");

        Assert.assertTrue(page.getErrorMessage().length() > 0);
    }

    // TC09: Username dài bất thường
    @Test
    public void TC09_LongUsername() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "123456");

        Assert.assertTrue(page.getErrorMessage().length() > 0);
    }

    // TC10: SQL Injection
    @Test
    public void TC10_SQLInjection() {
        LoginPage page = new LoginPage(driver);
        page.loginQuanLy("' OR '1'='1", "' OR '1'='1");

        Assert.assertTrue(page.getErrorMessage().length() > 0);
    }
}