package page;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CustomerPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Định nghĩa các Locator ở đầu trang để dễ quản lý và bảo trì
    private By menuDoiTac = By.xpath("//a[contains(.,'Đối tác')]");
    private By menuKhachHang = By.xpath("//a[contains(.,'Khách hàng')]");
    private By kendoGrid = By.cssSelector("div[kendo-grid='mainGrid']");
    private By searchBox = By.xpath("input[ng-model='filter.keyword']");
    private By btnAdd = By.xpath("//button[@ng-click='addNewButton()']");
    private By inputName = By.xpath("//input[@ng-model='partner.Name']");
    private By inputPhone = By.xpath("//input[@ng-model='partner.Phone']");
    private By inputCCCD = By.xpath("//input[@ng-model='partner.IdentifyNo']");
    private By btnSave = By.xpath("//button[@ng-click='save()']");
    private By toastMessage = By.xpath("//div[contains(@class,'k-notification-content')]");

    public CustomerPage(WebDriver driver) {
        this.driver = driver;
        // Khởi tạo bộ đợi Explicit Wait 10 giây
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openCustomer() {
        // Chờ và click menu Đối tác
        wait.until(ExpectedConditions.elementToBeClickable(menuDoiTac)).click();
        // Chờ và click menu Khách hàng
        wait.until(ExpectedConditions.elementToBeClickable(menuKhachHang)).click();

        // SỬA LỖI TIMEOUT: Chờ bảng Kendo Grid hiển thị thay vì tìm customerTable không tồn tại
        wait.until(ExpectedConditions.visibilityOfElementLocated(kendoGrid));
    }

    public void search(String value) {
        WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));

        //  xóa sạch ô input
        box.sendKeys(Keys.CONTROL + "a");
        box.sendKeys(Keys.BACK_SPACE);

        box.sendKeys(value);
        box.sendKeys(Keys.ENTER);

        // Chờ cho đến khi bảng xuất hiện ít nhất một dòng dữ liệu chứa kết quả

        try {
            Thread.sleep(1000); // Tạm thời dùng để test xem có phải do load chậm không
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clickAdd() {
        wait.until(ExpectedConditions.elementToBeClickable(btnAdd)).click();
    }

    public void addCustomer(String name, String phone, String cccd) {
        // Đợi các ô input hiển thị rồi mới nhập
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputName)).sendKeys(name);
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputPhone)).sendKeys(phone);
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputCCCD)).sendKeys(cccd);

        // Click Lưu
        wait.until(ExpectedConditions.elementToBeClickable(btnSave)).click();
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