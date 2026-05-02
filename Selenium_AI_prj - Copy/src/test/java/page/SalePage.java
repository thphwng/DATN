package page;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class SalePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators cố định
    private By loadingIcon = By.cssSelector(".k-loading-mask, .loading-container");
    private By searchInput = By.id("inputtypehead");
    private By toastLocator = By.xpath("//div[contains(@class,'k-notification-content')]");

    public SalePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }


    // Xử lý tìm kiếm thông minh từ code của bạn để tăng độ tin cậy
    public WebElement findSmart(By locator) {
        waitLoading();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Mở màn thu ngân
    public void goToCashier() {
        // Sử dụng URL trực tiếp để tiết kiệm thời gian, hoặc dùng click menu như bạn đã bắt
        driver.get("https://hvtester.pos365.vn/Sell");

        // Đóng mọi popup tồn đọng
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        } catch (Exception ignored) {}

        waitLoading();
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
    }

    // đợi load hết sản phẩm
    public void waitLoading() {
        // Sử dụng locator từ log lỗi ng-show="$root.showLoading"
        By rootLoading = By.xpath("//div[@ng-show='$root.showLoading']");
        try {
            // Chờ tối đa 10s cho loading ẩn đi
            wait.until(ExpectedConditions.invisibilityOfElementLocated(rootLoading));
        } catch (Exception ignored) {
            // Nếu không có loading thì chạy tiếp luôn
        }
    }

    public void waitForPageReady() {
        waitLoading();
        // Đảm bảo ô search hiển thị mới coi là trang đã load xong
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputtypehead")));
    }
    // ================= THAO TÁC NGHIỆP VỤ =================

    public void searchAndSelectProduct(String productName) {
        waitLoading();
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchInput));

        // Clear triệt để bằng phím tắt
        input.clear();
        input.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);

        if (productName != null && !productName.isEmpty()) {
            input.sendKeys(productName);

            // Đợi dropdown gợi ý xuất hiện và click kết quả đầu tiên
            try {
                By suggestion = By.xpath("//ul[contains(@class,'drop-typeahead')]//li//a");
                wait.until(ExpectedConditions.elementToBeClickable(suggestion)).click();
            } catch (TimeoutException e) {
                input.sendKeys(Keys.ENTER);
            }
            waitLoading();
        }
    }

    public void clickPlus(String productName) {
        By plusBtn = By.xpath("//span[contains(@class,'input-group-addon') and contains(@ng-click,'increasingQItem')]");
        wait.until(ExpectedConditions.elementToBeClickable(plusBtn)).click();
    }

    public void clickMinus(String productName) {
        By minusBtn = By.xpath("//span[contains(@class,'input-group-addon') and contains(@ng-click,'reduceQItem')]");
        wait.until(ExpectedConditions.elementToBeClickable(minusBtn)).click();
    }

    public void removeItem(String productName) {
        By removeBtn = By.xpath("//span[@ng-click='removeItem(dataItem)']");
        wait.until(ExpectedConditions.elementToBeClickable(removeBtn)).click();
        waitLoading();
    }

    public void inputCustomerPayment(String amount) {
        // 1. Chờ icon loading biến mất
        waitLoading();

        // 2. Tìm ô nhập tiền khách đưa (Khách thanh toán)
        By amountInput = By.id("inputAmountReceived");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput));

        // 3. Clear triệt để bằng phím tắt (quan trọng cho các input có format tiền tệ)
        input.clear();
        input.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);

        // 4. Nhập số tiền mới
        input.sendKeys(amount);

        // 5. Nhấn TAB hoặc ENTER để hệ thống tính toán Tiền thừa (refreshExcessCash)
        input.sendKeys(Keys.TAB);

        // 6. Đợi hệ thống xử lý tính toán xong
        waitLoading();
    }

    public void clickPayment() {
        waitLoading();
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'Thanh toán')]")));
        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            // Nếu vẫn bị cái loading đè lên, dùng JS để click xuyên qua nó
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public String getChangeAmount() {
        // Dùng XPath Tiền thừa của bạn
        return findSmart(By.xpath("//span[@class='txtSum']/strong[contains(@class, 'ng-binding')]")).getText().trim();
    }

    // ================= KIỂM TRA THÔNG BÁO =================

    public String getToastMessage() {
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastLocator));
            String text = toast.getText();
            // Đợi nó biến mất để không làm nhiễu test case sau
            wait.until(ExpectedConditions.invisibilityOf(toast));
            return text;
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isOrderSavedSuccessfully() {
        try {
            // Kiểm tra nội dung chính xác "Cập nhật dữ liệu thành công"
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    toastLocator, "Cập nhật dữ liệu thành công"
            ));
        } catch (Exception e) {
            return false;
        }
    }
}