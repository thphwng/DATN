package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import page.*;

public class CustomerTest extends BaseTest {

    private CustomerPage customerPage;

    // Sử dụng BeforeMethod để tự động login và mở trang khách hàng trước mỗi test case
    @BeforeMethod
    public void setupTest() {
        //Đăng nhập
        new LoginPage(driver).loginQuanLy("admin", "999999");
        customerPage = new CustomerPage(driver);
        customerPage.openCustomer();
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // KH_01: Kiểm tra xem danh sách khách hàng
    @Test
    public void KH_01_ViewList() {
        boolean isGridDisplayed = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[kendo-grid='mainGrid']"))
        ).isDisplayed();
        Assert.assertTrue(isGridDisplayed, "Bảng khách hàng không hiển thị!");
    }

    // KH_02 + KH_03 + KH_04: Tìm kiếm theo Mã, Tên, SĐT
    @Test
    public void KH_02_03_04_Search() {
        // Tìm theo mã
        customerPage.search("KH-1065");
        Assert.assertTrue(driver.findElement(By.cssSelector("div[kendo-grid='mainGrid']")).getText().contains("KH-1065"));

        // Tìm theo tên
        customerPage.search("Trang Vũ");
        Assert.assertTrue(driver.findElement(By.cssSelector("div[kendo-grid='mainGrid']")).getText().contains("Trang Vũ"));

        // Tìm theo SĐT
        customerPage.search("0998767676");
        Assert.assertTrue(driver.findElement(By.cssSelector("div[kendo-grid='mainGrid']")).getText().contains("0998767676"));
    }

    // KH_05: Tìm kiếm khách hàng không tồn tại
    @Test
    public void KH_05_Search_NotFound() {
        customerPage.search("xxx123");

        // Đợi một chút để Grid cập nhật trạng thái trống
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[kendo-grid='mainGrid']")));

        // Lấy danh sách các dòng trong bảng
        int rowCount = driver.findElements(By.cssSelector("div[kendo-grid='mainGrid'] tbody tr")).size();

        // Nếu bảng trống hoặc chỉ hiện 1 dòng thông báo "No records found" của Kendo
        String gridText = driver.findElement(By.cssSelector("div[kendo-grid='mainGrid']")).getText();  //Kiểm tra text đặc trưng của Kendo Grid khi trống

        Assert.assertTrue(gridText.contains("Không có dữ liệu") || rowCount == 0, "Vẫn có dữ liệu hiển thị trong bảng!");
    }

    // KH_06: Thêm thành công khách hàng
    @Test
    public void KH_06_Add_Success() {
        customerPage.clickAdd();
        customerPage.addCustomer("Test Automation 1", "0987654000", "123456789");

        String toastContent = customerPage.getToast();

        // Kiểm tra thông báo có chứa chữ "thành công"
        Assert.assertTrue(toastContent.contains("thành công"),
                "Lỗi: Hệ thống hiện thông báo khác: " + toastContent);
    }

    // KH_07: Thêm khách hàng bỏ trống tất cả các trường
    @Test
    public void KH_07_Add_Empty() {
        customerPage.clickAdd();
        // Click lưu luôn không nhập liệu
        getWait().until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@ng-click='save()']"))).click();
        Assert.assertTrue(customerPage.getToast().contains("Vui lòng nhập đủ"));
    }

    // KH_08: Kiểm tra sửa thông tin khách hàng thành công
    @Test
    public void KH_08_Edit_Success() {
        // Chọn dòng đầu tiên trong grid để sửa
        getWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[kendo-grid='mainGrid'] tbody tr:first-child"))).click();

        // Nhấn nút cập nhật (Giả sử locator dựa trên ng-click edit)
        getWait().until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@ng-click, 'updateButton(dataItem.Id)')]"))).click();

        // Chỉnh sửa tên và lưu
        customerPage.addCustomer("Sửa thông tin", "01234567890", "9876543210");
        Assert.assertTrue(customerPage.getToast().contains("thành công"));
    }

    // KH_09: Kiểm tra sửa thông tin trùng nhau
    @Test
    public void KH_09_Edit_Duplicate() {
        getWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[kendo-grid='mainGrid'] tbody tr:first-child"))).click();
        getWait().until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@ng-click, 'updateButton(dataItem.Id)')]"))).click();

        // Nhập thông tin trùng khách hàng khác
        customerPage.addCustomer("Khách Hàng Đã Có", "0999999999", "111111");
        Assert.assertTrue(customerPage.getToast().contains("đã tồn tại"));
    }

    // KH_10: Kiểm tra xóa khách hàng thành công
    @Test
    public void KH_10_Delete_Success() {
        // Chọn 1 khách hàng
        getWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[kendo-grid='mainGrid'] tbody tr:first-child"))).click();

        // Nhấn nút xóa
        getWait().until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@ng-click, 'delete')]"))).click();

        // Xác nhận đồng ý trên popup (Xpath cho nút Đồng ý/OK trên alert/popup)
        getWait().until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'Đồng ý')]"))).click();

        Assert.assertTrue(customerPage.getToast().contains("thành công"));
    }
}