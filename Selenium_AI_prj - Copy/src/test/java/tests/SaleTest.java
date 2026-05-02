package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.*;
import org.testng.Assert;
import org.testng.annotations.*;
import page.*;
import java.util.HashMap;
import java.util.Map;

public class SaleTest {
    WebDriver driver;
    SalePage salePage;

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--kiosk-printing", "--disable-print-preview");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("plugins.always_open_pdf_externally", true);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://hvtester.pos365.vn/Signin");

        new LoginPage(driver).loginQuanLy("admin", "999999");
        salePage = new SalePage(driver);
        salePage.goToCashier();
    }

    // BH_01. Bán hàng thành công
    @Test(priority = 1)
    public void BH_01_Success() {
        salePage.searchAndSelectProduct("test bán hàng");
        salePage.clickPayment();
        Assert.assertTrue(salePage.isOrderSavedSuccessfully(), "Lỗi: Không lưu được đơn hàng!");
    }

    // BH_02. Bán hàng khi giỏ hàng trống
    @Test(priority = 2)
    public void BH_02_EmptyCart() {
        // Đợi page ổn định hoàn toàn
        salePage.waitForPageReady();

        // Thực hiện click (hàm clickPayment đã có xử lý đợi loading ở trên)
        salePage.clickPayment();

        // Kiểm tra toast message
        String msg = salePage.getToastMessage();
        Assert.assertTrue(msg.contains("Chưa có mặt hàng"),
                "Lỗi: Mong đợi thông báo giỏ hàng trống nhưng lại nhận được: " + msg);
    }

    // BH_03. tìm kiếm sản phẩm không tồn tại
    @Test(priority = 3)
    public void BH_03_ProductNotFound() {
        salePage.searchAndSelectProduct("SP_Loi_123");
        Assert.assertTrue(salePage.getToastMessage().contains("không tồn tại"));
    }

    // BH_04. Bán nhiều sản phẩm
    @Test(priority = 4)
    public void BH_04_MultipleItems() {
        salePage.searchAndSelectProduct("test bán hàng");
        salePage.searchAndSelectProduct("trà sữa 3ae");
        salePage.clickPayment();
        Assert.assertTrue(salePage.isOrderSavedSuccessfully());
    }

    // BH_05. Xóa sản phẩm khỏi giỏ
    @Test(priority = 5)
    public void BH_05_RemoveItem() {
        salePage.searchAndSelectProduct("test bán hàng");
        salePage.removeItem("test bán hàng");
        salePage.clickPayment();
        Assert.assertTrue(salePage.getToastMessage().contains("Chưa có mặt hàng"));
    }

    // BH_06. tăng số lượng
    @Test(priority = 6)
    public void BH_06_IncreaseQty() {
        salePage.searchAndSelectProduct("test bán hàng");
        salePage.clickPlus("test bán hàng");
        salePage.clickPayment();
        Assert.assertTrue(salePage.isOrderSavedSuccessfully());
    }

    // BH_07. giảm số lượng
    @Test(priority = 7)
    public void BH_07_DecreaseQty() {
        salePage.searchAndSelectProduct("test bán hàng");
        salePage.clickPlus("test bán hàng");
        salePage.clickMinus("test bán hàng");
        salePage.clickPayment();
        Assert.assertTrue(salePage.isOrderSavedSuccessfully());
    }

    // BH_08. trả đúng giá
    @Test(priority = 8)
    public void BH_08_ExactPayment() {
        salePage.searchAndSelectProduct("trà sữa 3ae");
        //salePage.inputCustomerPayment("30000");
        Assert.assertEquals(salePage.getChangeAmount(), "0");
        salePage.clickPayment();
        Assert.assertTrue(salePage.isOrderSavedSuccessfully());
    }

    // BH_09. trả ít hơn
    @Test(priority = 9)
    public void BH_09_LessPayment() {
        salePage.searchAndSelectProduct("trà sữa 3ae");
        salePage.inputCustomerPayment("1000");
        salePage.clickPayment();
        // Kiểm tra thông báo lỗi tiền thiếu
        Assert.assertTrue(salePage.getToastMessage().length() > 0);
    }

    // BH_10. trả thừa
    @Test(priority = 10)
    public void BH_10_OverPayment() {
        salePage.searchAndSelectProduct("trà sữa 3ae");
        salePage.inputCustomerPayment("500000");
        Assert.assertNotEquals(salePage.getChangeAmount(), "470000");
        salePage.clickPayment();
        Assert.assertTrue(salePage.isOrderSavedSuccessfully());
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}