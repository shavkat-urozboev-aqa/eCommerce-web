package ShavkatUrozboev.TestComponents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class BaseTest {
    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected static ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();
    protected String baseUrl;
    protected String browser;
    protected static String OS;

    @BeforeSuite
    @Parameters({"os"})
    public void setupOS(@Optional("Mac") String os) {
        OS = System.getProperty("os.name").toLowerCase();
        System.out.println("🖥 OS Selected: " + OS);
    }

    @BeforeMethod(alwaysRun=true)
    @Parameters({"browser", "env"})
    public void setupSuite(@Optional("chrome") String browser, @Optional("test") String env) throws IOException {
        this.browser = System.getProperty("browser") != null ? System.getProperty("browser") : browser.toLowerCase();
        this.baseUrl = env.equalsIgnoreCase("prod") ?
                "https://rahulshettyacademy.com/client" :
                "https://rahulshettyacademy.com/client";

        initializeDriver();
        driver.get().navigate().to(baseUrl);
    }

    private void initializeDriver() throws IOException {
        Properties prop = new Properties();
        FileInputStream path = new FileInputStream(System.getProperty("user.dir")
                + "//src//main//java//ShavkatUrozboev//Resources//GlobalData.properties");
        prop.load(path);

        String browserName = this.browser != null ? this.browser : prop.getProperty("browser");

        WebDriver webDriver;

        switch (browserName.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                WebDriverManager.chromedriver().setup();
                if (browserName.contains("headless")) {
                    options.addArguments("headless");
                }
                webDriver = new ChromeDriver(options);
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                webDriver = new EdgeDriver();
            }
            case "safari" -> {
                if (!OS.contains("mac")) {
                    throw new IllegalArgumentException("Safari only works MacOS!");
                }
                webDriver = new SafariDriver();
            }
            default -> throw new IllegalArgumentException("Invalid browser: " + browserName);
        }

        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.set(webDriver);
        wait.set(new WebDriverWait(webDriver, Duration.ofSeconds(10)));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver webDriver = driver.get();
        try {
            if (webDriver != null) {
                webDriver.quit();
            }
        } finally {
            driver.remove();
            wait.remove();
        }
    }

    @AfterSuite
    public void cleanupSuite() {
        System.out.println("✅ All tests completed. Cleaning up resources...");
    }

    public List<HashMap<String, String>> getJsonDataToMap(String filePath) throws IOException {
        //read json to string
        String jsonContent = FileUtils.readFileToString(new File(filePath),
                StandardCharsets.UTF_8);

//*	    System.getProperty("user.dir")+"//src//test//java//ShavkatUrozboev//Data//DataComponents.json
//?	    String to HashMap- Jackson Data Bind Dependency

        ObjectMapper mapper = new ObjectMapper();
        List<HashMap<String, String>> data = mapper.readValue(jsonContent, new TypeReference<List<HashMap<String, String>>>() {});
        return data;
    }

    public String getScreenshot(String testCaseName,WebDriver driver) throws IOException {
        TakesScreenshot ts = (TakesScreenshot)driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        File file = new File(System.getProperty("user.dir") + "//reports//" + testCaseName + ".png");
        FileUtils.copyFile(source, file);
        return System.getProperty("user.dir") + "//reports//" + testCaseName + ".png";
    }
}

