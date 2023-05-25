package com.example.telegrambot.bot;


import com.example.telegrambot.config.Json;
import com.example.telegrambot.entity.User;
import com.example.telegrambot.service.conversation.ConversationHistoryService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Component
public class ChatGPTClient {

    private static ConversationHistoryService conversationHistoryService;
    private static final JsonObject assistantObject = new JsonObject();
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.url}")
    private String gptUrl;
    @Value("${openai.model}")
    private String gptModel;

    public ChatGPTClient(ConversationHistoryService conversationHistoryService) {
        ChatGPTClient.conversationHistoryService = conversationHistoryService;
    }

    public String generateResponse(String userMessage, User user, List<String> questionsAndAnswersList) throws Exception {
        int maxContextCount = 3;
        int maxTokens = 2000;
        int currentContextQuestions = conversationHistoryService.getMaxContextQuestions(user.getId());
        JsonArray promptArray;
        HttpURLConnection con = (HttpURLConnection) new URL(gptUrl).openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer "+apiKey);

        //saving context
        if(currentContextQuestions != maxContextCount && currentContextQuestions > 0) {
            promptArray = Json.generateJsonArray(questionsAndAnswersList,userMessage);
        } else if(currentContextQuestions == 0) {
            promptArray = Json.generateJsonArray(questionsAndAnswersList,userMessage);
        } else {
            conversationHistoryService.resetMaxContextQuestions(user.getId());
            conversationHistoryService.setConversationText("", user.getId());
            promptArray = Json.generateJsonArray(questionsAndAnswersList,userMessage);
        }

        con.setDoOutput(true);
        assistantObject.addProperty("model", gptModel);
        assistantObject.add("messages", promptArray);
        assistantObject.addProperty("max_tokens", maxTokens);
        String request = Json.encode(assistantObject);

        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(request);
        writer.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String json = String.valueOf(response);

        JSONObject obj = new JSONObject(json);
        JSONArray choices = obj.getJSONArray("choices");
        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }
}
