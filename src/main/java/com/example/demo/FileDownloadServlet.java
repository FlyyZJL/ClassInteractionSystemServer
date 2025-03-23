package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/download")
public class FileDownloadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "submissions";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        String fileName = request.getParameter("file");
        if (fileName == null) {
            response.sendError(400, "缺少文件名参数");
            return;
        }

        // 防止路径遍历攻击
        if (fileName.contains("..")) {
            response.sendError(403, "非法文件名");
            return;
        }

        // 设置响应头
        response.setContentType(getServletContext().getMimeType(fileName));
        response.setHeader("Content-Disposition",
                "inline; filename=\"" + fileName + "\"");

        // 读取文件
        Path filePath = Paths.get(getServletContext().getRealPath("/"), UPLOAD_DIR, fileName);
        if (!Files.exists(filePath)) {
            response.sendError(404, "文件不存在");
            return;
        }

        // 流式传输
        Files.copy(filePath, response.getOutputStream());
    }
}