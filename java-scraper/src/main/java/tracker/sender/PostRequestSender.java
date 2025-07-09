package tracker.sender;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

// public class PostRequestSender {

//     private static final String BASE_URL = "http://localhost:5000/api";

//     // ✅ NEW: Inserts user and returns generated user_id
//     public static int userAdditionInBackend(String user, String usernameLC, String usernameGFG) {
//         JSONObject userAddition = new JSONObject();
//         userAddition.put("username", user);
//         userAddition.put("leetcode", usernameLC);
//         userAddition.put("gfg", usernameGFG);

//         sendPostRequest(BASE_URL+"/users", userAddition);

//         try {
//             URI uri = new URI(BASE_URL + "/users");
//             URL url = uri.toURL();
//             HttpURLConnection con = (HttpURLConnection) url.openConnection();

//             con.setRequestMethod("POST");
//             con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//             con.setRequestProperty("Accept", "application/json");
//             con.setDoOutput(true);

//             try (OutputStream os = con.getOutputStream()) {
//                 byte[] input = userAddition.toString().getBytes(StandardCharsets.UTF_8);
//                 os.write(input, 0, input.length);
//             }

//             int responseCode = con.getResponseCode();
//             System.out.println("POST /users → Status: " + responseCode);

//             if (responseCode == 201 || responseCode == 200) {
//                 try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
//                     StringBuilder response = new StringBuilder();
//                     String line;
//                     while ((line = in.readLine()) != null) {
//                         response.append(line);
//                     }
//                     JSONObject responseJson = new JSONObject(response.toString());
//                     int userId = responseJson.getInt("userId"); // 👈 extract returned user_id
//                     System.out.println("✅ User created with user_id: " + userId);
//                     return userId;
//                 }
//             } else {
//                 System.err.println("⚠️ Failed to add user. Response Code: " + responseCode);
//             }

//             con.disconnect();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         return -1; // Return -1 on error
//     }

//     public static void sendDataToBackend(Map<String, Object> data, int userId) {
//         try {
//             // 1. Send platform_stats
//             JSONObject platformStats = new JSONObject();
//             platformStats.put("user_id", userId);
//             platformStats.put("platform", data.get("platform"));
//             platformStats.put("total_attempted", data.get("total_attempted"));
//             platformStats.put("last_updated", java.time.LocalDate.now().toString());

//             sendPostRequest(BASE_URL + "/platform-stats", platformStats);

//             // 2. Send topic_stats
//             Map<String, Integer> topics = (Map<String, Integer>) data.get("topics");
//             for (String topic : topics.keySet()) {
//                 JSONObject topicObj = new JSONObject();
//                 topicObj.put("user_id", userId);
//                 topicObj.put("platform", data.get("platform"));
//                 topicObj.put("topic", topic);
//                 topicObj.put("total_attempted", topics.get(topic));
//                 topicObj.put("last_updated", java.time.LocalDate.now().toString());

//                 sendPostRequest(BASE_URL + "/topic-stats", topicObj);
//             }

//             // 3. Send language_stats
//             List<String> languages = (List<String>) data.get("languages");
//             for (String language : languages) {
//                 JSONObject langObj = new JSONObject();
//                 langObj.put("user_id", userId);
//                 langObj.put("platform", data.get("platform"));
//                 langObj.put("language", language);
//                 langObj.put("total_attempted", 1);  // Or use real value if available

//                 sendPostRequest(BASE_URL + "/language-stats", langObj);
//             }

//             System.out.println("✅ Data sent to backend successfully.");
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     private static void sendPostRequest(String urlString, JSONObject jsonBody) {
//         try {
//             URI uri = new URI(urlString);
//             URL url = uri.toURL();
//             HttpURLConnection con = (HttpURLConnection) url.openConnection();

//             con.setRequestMethod("POST");
//             con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//             con.setRequestProperty("Accept", "application/json");
//             con.setDoOutput(true);

//             System.out.println("Sending JSON to " + urlString + ": " + jsonBody.toString());

//             try (OutputStream os = con.getOutputStream()) {
//                 byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
//                 os.write(input, 0, input.length);
//             }

//             int code = con.getResponseCode();
//             System.out.println("POST to " + urlString + " → Status: " + code);

//             con.disconnect();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }


public class PostRequestSender {

    private static final String BASE_URL = "http://localhost:5000/api";

    public static int userAdditionInBackend(String user, String usernameLC, String usernameGFG, String codeforces, String hackerrank){
        JSONObject userAddition=new JSONObject();
        
        userAddition.put("username", user);
        userAddition.put("leetcode", usernameLC);
        userAddition.put("gfg", usernameGFG);
        userAddition.put("codeforces", codeforces);
        userAddition.put("hackerrank", hackerrank);
        userAddition.put("email", JSONObject.NULL); 

        return sendPostRequest(BASE_URL+"/users", userAddition);
    }

    public static void sendDataToBackend(Map<String, Object> data, int userId) {
        try {
            JSONObject platformStats = new JSONObject();
            platformStats.put("user_id", userId);
            platformStats.put("platform", data.get("platform"));
            platformStats.put("total_attempted", data.get("total_attempted"));
            platformStats.put("last_updated", java.time.LocalDate.now().toString());

            sendPostRequest(BASE_URL + "/platform-stats", platformStats);


            Map<String, Integer> topics = (Map<String, Integer>) data.get("topics");
            for (String topic : topics.keySet()) {
                JSONObject topicObj = new JSONObject();
                topicObj.put("user_id", userId);
                topicObj.put("platform", data.get("platform"));
                topicObj.put("topic", topic);
                topicObj.put("total_attempted", topics.get(topic));
                topicObj.put("last_updated", java.time.LocalDate.now().toString());
                
                sendPostRequest(BASE_URL + "/topic-stats", topicObj);
            }


            List<String> languages = (List<String>) data.get("languages");
            for (String language : languages) {
                JSONObject langObj = new JSONObject();
                langObj.put("user_id", userId);
                langObj.put("platform", data.get("platform"));
                langObj.put("language", language);
                langObj.put("total_attempted", 1);  // or real value if available

                sendPostRequest(BASE_URL + "/language-stats", langObj);
            }

            System.out.println("✅ Data sent to backend successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int sendPostRequest(String urlString, JSONObject jsonBody) {
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setRequestProperty("Accept-Charset", "UTF-8");

            System.out.println("Sending JSON to " + urlString + ": " + jsonBody.toString());

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode=con.getResponseCode();

            if((responseCode==200 || responseCode==201) && urlString.equals(BASE_URL+"/users")){
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println(response);
                    
                    JSONObject responseJson = new JSONObject(response.toString());
                    int userId = responseJson.getInt("userId"); // 👈 extract returned user_id
                    System.out.println("✅ User created with userId: " + userId);
                    return userId;
                }
            }

            System.out.println("POST to " + urlString + " → Status: " + responseCode);

            con.disconnect();
            return -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
            
        }
    }
}
