package com.example.demo;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;

public class JsonUtils {
    private static final Gson gson = new Gson();

    /**
     * 从请求中读取JSON并转换为指定类型的对象
     */
    public static <T> T fromJson(HttpServletRequest request, Class<T> classOfT) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        return gson.fromJson(sb.toString(), classOfT);
    }

    /**
     * 从请求中读取JSON并转换为指定类型的对象
     */
    public static <T> T fromJson(HttpServletRequest request, Type typeOfT) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        return gson.fromJson(sb.toString(), typeOfT);
    }

    /**
     * 将对象转换为JSON字符串
     */
    public static String toJson(Object src) {
        return gson.toJson(src);
    }
}