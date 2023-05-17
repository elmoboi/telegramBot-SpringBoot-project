package com.example.telegrambot.bot;


import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class ChatGPTClient {
    private static final String API_URL = "https://api.openai.com/v1/completions";
    private static final String API_URL2 = "https://api.openai.com/v1/engines/davinci-codex/completions";
    @Value("${openai.api.key}")
    private static String API_SECRET_KEY;

    public static String generateResponse(String prompt) throws Exception {
        String url = "https://api.openai.com/v1/completions";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer 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");

        JSONObject data = new JSONObject();
        data.put("model", "text-davinci-003");
        data.put("prompt", prompt);
        data.put("max_tokens", 4000);
        data.put("temperature", 1.0);

        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes());

        String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
                .reduce((a, b) -> a + b).get();

        return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text");


//        OkHttpClient client = new OkHttpClient();
//        String responseBody;
//        RequestBody requestBody = RequestBody.create(
//                MediaType.parse("application/json; charset=utf-8"),
//                "{\"prompt\": \"" + prompt + "\",\"max_tokens\": 75}"
//        );
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .addHeader("Content-Type", "application/json")
//                .post(requestBody)
//                .build();
//        try(Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new Exception("Unexpected response: " + response);
//            }
//            responseBody = response.body().string();
//            return responseBody.substring(19, responseBody.length()-3);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return responseBody = "Не удалось сгенерировать запрос. Попробуйте еще раз";
//        }
//        OkHttpClient client = new OkHttpClient();
//        String responseBody;
//        RequestBody requestBody = RequestBody.create(
//                MediaType.parse("application/json; charset=utf-8"),
//                "{\"prompt\": \"" + prompt + "\",\"max_tokens\": 75}"
//        );
//        Request request = new Request.Builder()
//                .url(API_URL2)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer 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")
//                .post(requestBody)
//                .build();
//        try(Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new Exception("Unexpected response: " + response);
//            }
//            responseBody = response.body().string();
//            return responseBody.substring(19, responseBody.length()-3);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return responseBody = "Не удалось сгенерировать запрос. Попробуйте еще раз";
//        }
    }

    public static void checkResponse() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/engines/davinci-codex/completions")
                .header("Authorization", "Bearer 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")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                System.out.println("API key has the necessary permissions.");
            } else {
                System.out.println("API key does not have the necessary permissions.");
            }
        } catch (IOException e) {
            System.err.println("Request failed: " + e.getMessage());
        }
    }
}
