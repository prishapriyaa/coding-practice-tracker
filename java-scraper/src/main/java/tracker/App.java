package tracker;

import tracker.scraper.GFGScraper;
import tracker.scraper.LeetCodeScraper;
import tracker.sender.PostRequestSender;

import java.util.Map;

public class App {
    public static void main(String[] args) {    
        System.out.println("🕒 Run at: " + java.time.LocalDateTime.now());
        System.out.println("Received " + args.length + " arguments:");
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "]: '" + args[i] + "'");
        }


        if (args.length < 5) {
            System.out.println("Insufficient args!");
            return;
        }

        String username = args[0].equals("__EMPTY__") ? "" : args[0];
        String leetcodeUsername = args[1].equals("__EMPTY__") ? "" : args[1];
        String gfgUsername = args[2].equals("__EMPTY__") ? "" : args[2];
        String codeforcesUsername = args[3].equals("__EMPTY__") ? "" : args[3];
        String hackerrankUsername = args[4].equals("__EMPTY__") ? "" : args[4];


        int userId=PostRequestSender.userAdditionInBackend(username, leetcodeUsername, gfgUsername, codeforcesUsername, hackerrankUsername);

        System.out.println("Received in scraper:");
        System.out.println("username: " + username);
        System.out.println("LeetCode: " + leetcodeUsername);

        LeetCodeScraper scraperLC= new LeetCodeScraper();
        Map<String, Object> scrapedDataLC= scraperLC.scrapeUserDataLC(leetcodeUsername, userId);

        GFGScraper scraperGFG= new GFGScraper();
        Map<String, Object> scrapedDataGFG= scraperGFG.scrapedDataGFG(gfgUsername);

        System.out.println("userId: "+userId+" username: "+username);

        PostRequestSender.sendDataToBackend(scrapedDataLC, userId);
        PostRequestSender.sendDataToBackend(scrapedDataGFG, userId);
    }
}

