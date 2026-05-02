package tests;

import com.epam.healenium.SelfHealingDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {
    protected SelfHealingDriver driver;

    @BeforeMethod
    public void setUp() {

        // cấu hình Healenium
        System.setProperty("hlm.server.url", "http://localhost:7878");
        System.setProperty("hlm.imitator.url", "http://localhost:8000");

        // driver gốc
        WebDriver delegate = new ChromeDriver();

        // bọc bằng AI (quan trọng nhất)
        driver = SelfHealingDriver.create(delegate);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        driver.get("https://hvtester.pos365.vn/");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Lệnh này sẽ kích hoạt việc gửi report về cổng 7878
        }
    }
}