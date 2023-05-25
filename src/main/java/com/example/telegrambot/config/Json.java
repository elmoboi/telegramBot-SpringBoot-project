package com.example.telegrambot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class Json {
    static final ObjectMapper mapper = new ObjectMapper();

    public static String encode(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    public static JsonArray generateJsonArray(List<String> questionsAndAnswersList, String userMessage) {
        JsonArray promptArray = new JsonArray();
        if(!questionsAndAnswersList.isEmpty()) {
            for (String qa : questionsAndAnswersList) {
                String[] buff = qa.split(":");
                String userOldQuestion = buff[0];
                String gptOldAnswer = buff[1];

                JsonObject userObject = new JsonObject();
                userObject.addProperty("role", "user");
                userObject.addProperty("content", userOldQuestion);
                promptArray.add(userObject);

                JsonObject assistantObject = new JsonObject();
                assistantObject.addProperty("role", "assistant");
                assistantObject.addProperty("content", gptOldAnswer);
                promptArray.add(assistantObject);
            }
        }

        JsonObject userObject = new JsonObject();
        userObject.addProperty("role", "user");
        userObject.addProperty("content", userMessage);
        promptArray.add(userObject);

        return promptArray;
    }
}
