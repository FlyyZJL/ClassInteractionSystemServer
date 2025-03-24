package com.example.demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/upload/video")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part filePart = request.getPart("video");
        String fileName = "video_" + System.currentTimeMillis() + "_" +
                getFileName(filePart);

        // 保存路径（物理路径不需要包含项目名）
        String savePath = getServletContext().getRealPath("/uploads/videos");
        Files.createDirectories(Paths.get(savePath));
        filePart.write(savePath + File.separator + fileName);

        // 生成访问URL时添加项目路径
        String contextPath = request.getContextPath();
        String fileUrl = request.getScheme() + "://" + request.getServerName() + ":" +
                request.getServerPort() + contextPath + "/uploads/videos/" + fileName;

        response.setContentType("application/json");
        response.getWriter().print("{\"url\":\"" + fileUrl + "\"}");
    }

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        return header.split("filename=")[1].split("\"")[1];
    }
}