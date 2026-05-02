package page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ===== Constructor =====
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    //  khai báo các locator
    private By txtUsername = By.name("Username");
    private By txtPassword = By.name("Password");
    private By btnQuanLy = By.cssSelector("button[value='Dashboard']");
    private By btnBanHang = By.cssSelector("button[value='Sell']");

    // Message lỗi
    private By lblError = By.id("p_errmsg");



    // Nhập username
    public void enterUsername(String username) {
        WebElement user = wait.until(ExpectedConditions.visibilityOfElementLocated(txtUsername));
        user.clear();
        user.sendKeys(username);
    }

    // Nhập password
    public void enterPassword(String password) {
        WebElement pass = wait.until(ExpectedConditions.visibilityOfElementLocated(txtPassword));
        pass.clear();
        pass.sendKeys(password);
    }

    // Click nút Quản lý (Dashboard)
    public void clickQuanLy() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnQuanLy));
        btn.click();
    }

    // Click nút Bán hàng
    public void clickBanHang() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnBanHang));
        btn.click();
    }

    // Login vào Quản lý
    public void loginQuanLy(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickQuanLy();
    }

    // Login vào Bán hàng
    public void loginBanHang(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickBanHang();
    }

    // Lấy message lỗi
    public String getErrorMessage() {
        try {
            WebElement error = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(lblError)
            );
            return error.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // Check login thành công (URL chứa dashboard)
    public boolean isLoginSuccess() {
        return wait.until(driver -> driver.getCurrentUrl().contains("Dashboard"));
    }

    // Lấy value của password
    public String getPasswordValue() {
        return driver.findElement(txtPassword).getAttribute("value");
    }

    // Lấy value của username
    public String getUsernameValue() {
        return driver.findElement(txtUsername).getAttribute("value");
    }
}