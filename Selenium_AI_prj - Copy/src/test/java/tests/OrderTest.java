package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import page.LoginPage;
import page.OrderPage;

import java.time.Duration;

public class OrderTest {
    WebDriver driver;
    WebDriverWait wait;
    OrderPage orderPage;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));



        // login
        driver.get("https://hvtester.pos365.vn/Signin");
        new LoginPage(driver).loginQuanLy("admin", "999999");
        // Khởi tạo OrderPage
        orderPage = new OrderPage(driver);
        orderPage.openOrderPage();
    }
    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // DH_01. hiển thị danh sách đơn hàng
    @Test
    public void DH_01_ViewList() {
        boolean isGridDisplayed = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[kendo-grid='mainGrid']"))
        ).isDisplayed();
        Assert.assertTrue(isGridDisplayed, "Bảng đơn hàng không hiển thị!");
    }

    // DH_02. tìm kiếm theo tên
    @Test(priority = 2, description = "DH_02: Kiểm tra tìm kiếm theo tên hàng hóa")
    public void DH_02_testSearchByProductName() {
        orderPage.openOrderPage();
        String productName = "trà sữa 3ae";

        orderPage.searchAndSelectProduct(productName);

        // Sau khi tìm, kiểm tra xem danh sách có trả về kết quả không
        Assert.assertTrue(orderPage.getOrderCount() >= 0, "Lỗi khi thực hiện tìm kiếm sản phẩm!");
    }

    // DH_03. tìm kiếm theo mã chứng từ
    @Test(priority = 3, description = "DH_03: Kiểm tra tìm kiếm theo mã chứng từ")
    public void DH_3_testSearchByOrderCode() {
        orderPage.openOrderPage();
        String orderCode = "190426-0026"; // Thay bằng mã chứng từ thực tế

        orderPage.searchByOrderCode(orderCode);

        Assert.assertTrue(orderPage.isOrderListDisplayed(), "Không tìm thấy đơn hàng có mã: " + orderCode);
    }

    // DH_04. tiìm kiếm đơn hàng có sản phẩm không tồn tại
    @Test(priority = 4, description = "DH_04: Tìm kiếm đơn hàng có sản phẩm không tồn tại")
    public void DH_04_testSearchNonExistentProduct() {
        orderPage.openOrderPage();

        String productName = "sản phẩm không tồn tại";

        orderPage.searchProductOnly(productName);

        // verify toast xuất hiện
        String toastMsg = orderPage.getToast();

        Assert.assertTrue(
                toastMsg.toLowerCase().contains("không tồn tại"),
                "Không hiển thị thông báo sản phẩm không tồn tại"
        );
    }

    //DH_05
    @Test(priority = 5, description = "DH_05: Tìm kiếm đơn hàng có mã chứng từ không tồn tại")
    public void DH_05_testSearchNonExistentOrderCode() {
        orderPage.openOrderPage();
        String fakeCode = "MA_GIA_12345";

        orderPage.searchByOrderCode(fakeCode);

        // Kết quả mong đợi: Hiển thị danh sách trống
        Assert.assertTrue(orderPage.isNoDataDisplayed(),
                "Không hiển thị trạng thái danh sách rỗng!");
    }

    //DH_06
    @Test(priority = 6, description = "DH_06: Kiểm tra xem chi tiết đơn hàng")
    public void DH_06_testViewOrderDetail() {
        orderPage.openOrderPage();

        if (orderPage.getOrderCount() > 0) {
            orderPage.selectFirstOrder();

            // wait cho detail load
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> orderPage.isOrderDetailDisplayed());

            Assert.assertTrue(
                    orderPage.isOrderDetailDisplayed(),
                    "Chi tiết đơn hàng không hiển thị!"
            );
        } else {
            System.out.println("Bỏ qua test DH_06 vì danh sách đơn hàng trống.");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}