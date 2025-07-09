package tracker.scraper;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;

public class LeetCodeScraper {
    private WebDriver driver;

    public LeetCodeScraper() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(40));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    public Map<String, Object> scrapeUserDataLC(String username, int userId) {
        Map<String, Object> scrapedData = new HashMap<>();

        try {
            driver.get("https://leetcode.com/u/" + username);
                
            int totalSolved = totalQuestionsScraper();           
            List<String> languages = languageScraper();
            Map<String, Integer> topicsMap=topicScraper();

            scrapedData.put("username", username);
            scrapedData.put("platform", "leetcode");
            scrapedData.put("total_attempted", totalSolved);
            scrapedData.put("languages", languages);  
            scrapedData.put("topics", topicsMap); 

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return scrapedData;
    }

    private int totalQuestionsScraper(){
        WebDriverWait wait=new WebDriverWait(driver, Duration.ofSeconds(10));

        By totalAttempted=By.xpath("//div[contains(text(),'Solved')]/preceding-sibling::div//span[contains(text(), '/')]/preceding-sibling::span");

        wait.until(new Function<WebDriver,Boolean>() {
            public Boolean apply(WebDriver driver){
                return !driver.findElement(totalAttempted).getText().equals("-");
            }
        });
            
        WebElement solvedElement = driver.findElement(totalAttempted);      
        int totalSolved = Integer.parseInt(solvedElement.getText().trim());

        return totalSolved;
    }

    private List<String> languageScraper(){
        List<WebElement> languageElements =driver.findElements(By.xpath("//div[@class='text-xs']//span"));

        List<String> languages = new ArrayList<>();
        for (WebElement langElem : languageElements) {
            String lang = langElem.getText().trim();
            if (!lang.isEmpty() && !languages.contains(lang)) {
                languages.add(lang); 
            }
        }

        return languages;
    }

    private Map<String, Integer> topicScraper(){
        By topicsBy=By.xpath("//div[@class='mt-3 flex flex-wrap']//a//span");
        List<WebElement> topicNames=driver.findElements(topicsBy);

        By qBy=By.xpath("//div[@class='mt-3 flex flex-wrap']//span[@class='pl-1 text-xs text-label-3 dark:text-dark-label-3'][1]");
        List<WebElement> questionsPerTopic=driver.findElements(qBy);

        Map<String, Integer> topicMap=new HashMap<>();

        int total=topicNames.size();
        for(int i=0; i<total; i++){
            topicMap.put(topicNames.get(i).getText(), Integer.parseInt(questionsPerTopic.get(i).getText().substring(1)));
        }

        return topicMap;
    }
}
