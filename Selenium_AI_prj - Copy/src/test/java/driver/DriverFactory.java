package driver;

import com.epam.healenium.SelfHealingDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.epam.healenium.SelfHealingDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverFactory {

    public static SelfHealingDriver getDriver() {

        WebDriver driver = new ChromeDriver();

        // wrap
        SelfHealingDriver healingDriver = SelfHealingDriver.create(driver);

        return healingDriver;
    }
}