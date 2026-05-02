package page;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class GoodsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public GoodsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // tìm kiếm thông minh

    public WebElement findSmart(By... locators) {
        for (By by : locators) {
            try {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            } catch (Exception ignored) {}
        }
        throw new NoSuchElementException("Không tìm thấy element với locator đã cho");
    }

    public void click(By... locators) {
        WebElement el = findSmart(locators);
        wait.until(ExpectedConditions.elementToBeClickable(el)).click();
    }

    public void sendKeys(String text, By... locators) {
        WebElement el = findSmart(locators);
        el.clear();
        el.sendKeys(text);
    }

    // chuyển sang màn danh sách hàng hóa

    public void openGoodsList() { findSmart(By.xpath("//a[contains(@class,'dropdown-toggle') and contains(.,'Hàng hóa')]")).click(); findSmart(By.xpath("//a[contains(.,'Danh sách hàng hóa')]")).click(); }

    // Hiển thị bảng danh sách hàng hóa
    public boolean isGoodsListDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@role='treegrid']")
            )).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Hiển thị kết quả tìm kiếm
    public boolean isSearchResultDisplayed() {
        List<WebElement> rows = driver.findElements(By.xpath("//table//tr"));
        return rows.size() > 1;
    }
    // xử lý nếu không có dữ liệu
    public boolean isNoDataDisplayed() {
        return driver.findElements(
                By.xpath("//*[contains(text(),'Không có dữ liệu')]")
        ).size() > 0;
    }

    // input tìm kiếm

    public void search(String keyword) {
        WebElement input = findSmart(
                By.xpath("//input[contains(@placeholder,'Tìm')]"),
                By.xpath("//input")
        );

        input.clear();
        input.sendKeys(keyword);
        input.sendKeys(Keys.ENTER);

        // wait kết quả load
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//table")
        ));
    }

    // ================= CRUD =================

    public void clickAdd() {
        click(
                By.xpath("//button[normalize-space()='Thêm mới Hàng hóa']")
        );
    }

    public void clickUpdate() {
        click(
                By.xpath("//button[normalize-space()='Cập nhật']")
        );
    }

    public void inputName(String name) {
        sendKeys(name,
                By.xpath("//input[@ng-model='product.Name']"),
                By.xpath("//input[contains(@placeholder,'Tên hàng hóa')]"),
                By.xpath("//input")
        );
    }

    public void clickSave() {
        click(
                By.xpath("//button[normalize-space()='Lưu' and @ng-click='save()']")
        );
    }

    public boolean selectFirstItem() {
        try {
            WebElement firstRow = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//table[@role='treegrid']//tbody/tr[1]")
            ));

            firstRow.click();
            return true;
        } catch (Exception e) {
            System.out.println("Không click được dòng đầu: " + e.getMessage());
            return false;
        }
    }

    public void clickDelete() {
        click(
                By.xpath("//button[contains(.,'Xóa')]")
        );
    }

    public void confirmDelete() {
        click(
                By.xpath("//button[contains(.,'Đồng ý')]"),
                By.xpath("//button[contains(.,'OK')]")
        );
    }

    // ================= TOAST =================

    public String getToastMessage() {
        By toast = By.xpath("//div[contains(@class,'k-notification-content')]");

        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(toast));
        String text = el.getText().trim();

        // đợi toast biến mất (tránh lỗi test sau)
        wait.until(ExpectedConditions.invisibilityOf(el));

        return text;
    }

    public boolean verifyToastContains(String expected) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//div[contains(@class,'k-notification-content')]"),
                expected
        ));
    }
}