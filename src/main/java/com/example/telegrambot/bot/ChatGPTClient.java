package com.example.telegrambot.bot;


import com.example.telegrambot.entity.User;
import com.example.telegrambot.service.conversation.ConversationHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Component
@Slf4j
public class ChatGPTClient {

    private static ConversationHistoryService conversationHistoryService;

    public ChatGPTClient(ConversationHistoryService conversationHistoryService) {
        ChatGPTClient.conversationHistoryService = conversationHistoryService;
    }

    public static String generateResponse(String userMessage, User user, List<String> questionsAndAnswersList) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        int maxContextCount = 3;
        int maxTokens = 2000;
        int currentContextQuestions = conversationHistoryService.getMaxContextQuestions(user.getId());
        StringBuffer prompt = new StringBuffer();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer API");

        String model = "gpt-3.5-turbo";

        //saving context
        if(currentContextQuestions != maxContextCount && currentContextQuestions > 0) {
            String userOldQuestion;
            String gptOldAnswer;
            prompt.append("[");
            for(int i=0; i<questionsAndAnswersList.size();i++) {
                String[] buff = questionsAndAnswersList.get(i).split(":");
                userOldQuestion = buff[0];
                gptOldAnswer = buff[1];
                prompt
                        .append("{\"role\": \"user\", \"content\": \"" + userOldQuestion + "\"}, ")
                        .append("{\"role\": \"assistant\", \"content\": \"" + gptOldAnswer + "\"}, ");
            }
            prompt.append("{\"role\": \"user\", \"content\": \"" + userMessage + "\"}");
            prompt.append("]");
            log.info("request currentContextQuestions > 0 " + prompt);
        } else if(currentContextQuestions == 0) {
            prompt = prompt.append("[{\"role\": \"user\", \"content\": \"" + userMessage + "\"}]");
            log.info("request currentContextQuestions = 0 " + prompt);
        } else {
            conversationHistoryService.resetMaxContextQuestions(user.getId());
            conversationHistoryService.setConversationText("", user.getId());
            log.info("request currentContextQuestions > max " + prompt);
            prompt = prompt.append("[{\"role\": \"user\", \"content\": \"" + userMessage + "\"}]");
        }

        con.setDoOutput(true);
        String body = "{\"model\": \"" + model + "\", \"messages\": " + prompt + ", \"max_tokens\": " + maxTokens + "}";

        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(body);
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
