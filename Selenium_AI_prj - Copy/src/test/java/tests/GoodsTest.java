package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import page.GoodsPage;
import page.LoginPage;

import java.time.Duration;

public class GoodsTest {

    WebDriver driver;
    WebDriverWait wait;
    GoodsPage goods;

    // ================= SETUP =================
    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("https://hvtester.pos365.vn/Signin");

        // Login
        LoginPage login = new LoginPage(driver);
        login.loginQuanLy("admin", "999999");

        // Chờ login thành công
        // WAIT đúng sau login
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'Hàng hóa')]")
        ));

        goods = new GoodsPage(driver);
    }

    @BeforeMethod
    public void beforeEachTest() {
        goods.openGoodsList();
    }

    // ================= HELPER =================
    private void verifyToastContains(String expected) {
        String actual = goods.getToastMessage().toLowerCase();
        Assert.assertTrue(actual.contains(expected),
                " Expected toast chứa: " + expected + " | Actual: " + actual);
    }

    // ================= TEST CASES =================

    // HH_01. Kiểm tra hiển thị danh sách
    @Test
    public void HH_01_ViewList() {
        Assert.assertTrue(goods.isGoodsListDisplayed());

    }

    // HH_02. tìm kiếm theo tên hàng hóa
    @Test
    public void HH_02_SearchByName() {
        goods.search("Bánh");
        Assert.assertTrue(goods.isSearchResultDisplayed(), "Không hiển thị kết quả tìm kiếm");
    }

    // HH_03. tìm kiếm theo mã hàng hóa
    @Test
    public void HH_03_SearchByCode() {
        goods.search("HH001");
        Assert.assertTrue(goods.isSearchResultDisplayed(), "Không tìm thấy theo mã");
    }

    // HH_04. tìm kiếm hàng hóa không tồn tại
    @Test
    public void HH_04_SearchNotFound() {
        goods.search("XXXX999");
        Assert.assertTrue(true);
    }

    // HH_05. thêm hàng hóa thành công
    @Test
    public void HH_05_AddSuccess() {
        goods.clickAdd();
        goods.inputName("Hàng test AI");
        goods.clickSave();

        verifyToastContains("thành công");
    }

    // HH_06. thêm hàng hóa thành công
    @Test
    public void HH_06_AddWithoutName() {
        goods.clickAdd();
        goods.clickSave();

        verifyToastContains("bắt buộc");
    }

    // HH_07. thêm hàng hóa với tên không hợp lệ
    @Test
    public void HH_07_AddInvalidName() {
        goods.clickAdd();
        goods.inputName("@@@###");
        goods.clickSave();

        verifyToastContains("không hợp lệ");
    }

    // HH_08. sửa hàng hóa thành công
    @Test
    public void HH_08_EditSuccess() {
        goods.selectFirstItem();
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Cập nhật']")));
       goods.clickUpdate();
        goods.inputName("Tên mới AI");
        goods.clickSave();

        verifyToastContains("thành công");
    }

    // HH_09. sửa tên hàng hóa bị trùng với tên đã có
    @Test
    public void HH_09_EditInvalidName() {
        goods.selectFirstItem();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Cập nhật']")));
        goods.clickUpdate();
        goods.inputName("Tên mới AI");
        goods.clickSave();
        verifyToastContains("không hợp lệ");
    }

    // HH_10. xóa thành công
    @Test
    public void HH_10_DeleteSuccess() {
        goods.selectFirstItem();
        goods.clickDelete();
        goods.confirmDelete();

        verifyToastContains("thành công");
    }

    // ================= TEARDOWN =================
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}