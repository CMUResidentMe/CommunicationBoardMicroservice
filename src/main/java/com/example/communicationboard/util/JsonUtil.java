package com.example.communicationboard.util;

import com.example.communicationboard.dto.RmNotification;

import com.google.gson.Gson;

// This class is used to convert a notification object to a JSON string
public class JsonUtil {
    // Convert a notification object to a JSON string
    public static String convert2Str(RmNotification obj) {
        Gson gson = new Gson();
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
