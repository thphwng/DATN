package page;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OrderPage {
    WebDriver driver;
    WebDriverWait wait;

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // danh sách locator
    private By menuGiaoDich = By.xpath("//a[contains(@class,'dropdown-toggle') and contains(normalize-space(),'Giao dịch')]");
    private By menuDonHang = By.xpath("//a[contains(normalize-space(),'Danh sách đơn hàng')]");
    private By kendoGrid = By.cssSelector("div[kendo-grid='mainGrid']");
    private By orderContainer = By.xpath("(//div[contains(@class,'col-lg-12')])[last()]");
    private By orderRows = By.xpath("//tbody/tr");
    private By firstOrder = By.xpath("(//tbody/tr)[1]");
    private By orderDetail = By.xpath("//div[contains(@class,'order-detail')]");
    private By searchByCodeBox = By.xpath("//input[@ng-model='filter.keyword']");
    private By searchProductBox = By.xpath("//input[@ng-model='findproduct']");
    private By productSuggestionList = By.xpath("//ul[contains(@class,'dropdown-menu') and @role='listbox']");
    private By firstProductSuggestion = By.xpath("//ul[contains(@class,'dropdown-menu')]//li[1]");
    private By toastMessage = By.xpath("//div[contains(@class,'k-notification-content')]");


    // mở trang danh sách đơn hàng
    public void openOrderPage() {
        // Sử dụng Actions để hover nếu cần, hoặc click trực tiếp
        wait.until(ExpectedConditions.elementToBeClickable(menuGiaoDich)).click();

        WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(menuDonHang));
        try {
            subMenu.click();
        } catch (Exception e) {
            // Nếu menu bị ẩn do hiệu ứng hover, dùng JS click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(menuDonHang));
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(kendoGrid));
    }


     // Tìm kiếm theo Mã chứng từ

    public void searchByOrderCode(String keyword) {
        int initialCount = getOrderCount(); // Lưu lại số lượng dòng trước khi search

        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchByCodeBox));
        search.clear();
        search.sendKeys(keyword);
        search.sendKeys(Keys.ENTER);

        // Đợi danh sách thay đổi để tránh lấy nhầm data cũ
        waitForListToLoad(initialCount);
    }

    // tìm kiếm theo hàng hóa trong đơn hàng
    public void searchAndSelectProduct(String productName) {
        int initialCount = getOrderCount();

        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchProductBox));
        search.clear();
        search.sendKeys(productName);

        try {
            // Chờ dropdown gợi ý hiện ra trong 3 giây
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(productSuggestionList));
            driver.findElement(firstProductSuggestion).click();
        } catch (Exception e) {
            // Nếu không có gợi ý (DH_04) hoặc lỗi, nhấn Enter
            search.sendKeys(Keys.ENTER);
        }

        // Đợi danh sách thay đổi
        waitForListToLoad(initialCount);
    }

    // chỉ tìm mỗi sản phẩm
    public void searchProductOnly(String productName) {
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchProductBox));
        search.clear();
        search.sendKeys(productName);
        search.sendKeys(Keys.ENTER);
    }

    /**
     * Hàm dùng chung để đợi bảng load xong dữ liệu mới
     */
    private void waitForListToLoad(int oldCount) {
        try {
            // Đợi tối đa 5s cho đến khi số dòng khác số dòng cũ HOẶC bảng trống rỗng (size = 0)
            wait.until(d -> {
                int currentCount = driver.findElements(orderRows).size();
                return currentCount != oldCount || currentCount == 0;
            });
            // Nghỉ thêm 500ms để ổn định giao diện
            Thread.sleep(500);
        } catch (Exception e) {
            // Timeout nếu kết quả tìm kiếm trùng khớp đúng số lượng dòng cũ
        }
    }

    public boolean isOrderListDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(orderContainer));
            return getOrderCount() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public int getOrderCount() {
        return driver.findElements(
                By.xpath("//tbody/tr[td[contains(text(),'-')]]")
        ).size();
    }

    // không có d liệu
    public boolean isNoDataDisplayed() {
        try {
            return driver.findElement(By.xpath("//*[contains(text(),'No items to display')]"))
                    .isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void selectFirstOrder() {
        By firstDataRow = By.xpath("(//tbody/tr[td[contains(text(),'-')]])[1]");

        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(firstDataRow));

        // Scroll tới element (tránh bị che)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", row);

        row.click();
    }

    public boolean isOrderDetailDisplayed() {
        return driver.findElements(
                By.xpath("//*[contains(text(),'Chi tiết')]")
        ).size() > 0;
    }

    public String getToast() {
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastMessage));

            String text = toast.getText();
            System.out.println("Thông báo thực tế: " + text);

            // đợi nó biến mất để tránh ảnh hưởng test sau
            wait.until(ExpectedConditions.invisibilityOf(toast));

            return text;
        } catch (TimeoutException e) {
            return "Không tìm thấy thông báo toast!";
        }
    }
}