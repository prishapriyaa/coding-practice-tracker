package tracker.scraper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class GFGScraper {
    private WebDriver driver;
    
    public GFGScraper() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(40));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    public Map<String, Object> scrapedDataGFG(String username){
        Map<String, Object> scrapedData=new HashMap<>();
        
        try {
            driver.navigate().to("https://www.geeksforgeeks.org/user/"+username);

            int totalSolved=totalQuestionsScraper();
            List<String> languages=languageScraper();
            Map<String, Integer> topicsMap=new HashMap<>();

            scrapedData.put("username", username);
            scrapedData.put("platform", "gfg");
            scrapedData.put("total_attempted", totalSolved);
            scrapedData.put("languages", languages);
            scrapedData.put("topics", topicsMap);

        } catch (Exception e) {
            // TODO: handle exception
        }finally{
            driver.quit();
        }

        return scrapedData;
    }

    private List<String> languageScraper() {
        List<String> languages=new ArrayList<>();

        By languagesBy=By.xpath("//div[@class='educationDetails_head_right--text__lLOHI']");
        String languageLine=driver.findElement(languagesBy).getText();

        String[] s=languageLine.split(", ");

        for(String str: s) languages.add(str);

        return languages;
    }

    private int totalQuestionsScraper(){
        By totalSolvedBy=By.xpath("//div[@class='scoreCard_head_left--text__KZ2S1' and text()='Problem Solved']/following-sibling::div");
        int totalSolved=Integer.parseInt(driver.findElement(totalSolvedBy).getText());

        return totalSolved;
    }
}
