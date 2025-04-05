package com.example.demo;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 用于导出数据到CSV格式的工具类
 */
public class CSVExporter {

    /**
     * 导出成绩数据为CSV格式
     *
     * @param writer 输出流
     * @param grades 成绩列表
     */
    public static void exportGradesToCSV(PrintWriter writer, List<Grade> grades) {
        // 写入CSV头
        writer.println("\"课程\",\"学生\",\"成绩类型\",\"分数\",\"评语\",\"评分日期\",\"评分人\"");

        // 日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 写入数据
        for (Grade grade : grades) {
            StringBuilder sb = new StringBuilder();

            // 在CSV中包含双引号的字段需要用双引号转义
            sb.append("\"").append(escapeCSV(grade.getCourseName())).append("\",");
            sb.append("\"").append(escapeCSV(grade.getStudentName())).append("\",");
            sb.append("\"").append(escapeCSV(grade.getGradeType())).append("\",");
            sb.append(grade.getScore()).append(",");

            String feedback = grade.getFeedback();
            if (feedback != null && !feedback.isEmpty()) {
                sb.append("\"").append(escapeCSV(feedback)).append("\",");
            } else {
                sb.append("\"\",");
            }

            sb.append("\"").append(dateFormat.format(grade.getGradeDate())).append("\",");

            String gradedByName = grade.getGradedByName();
            if (gradedByName != null) {
                sb.append("\"").append(escapeCSV(gradedByName)).append("\"");
            } else {
                sb.append("\"\"");
            }

            writer.println(sb.toString());
        }

        writer.flush();
    }

    /**
     * 为CSV转义字符串中的双引号
     *
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private static String escapeCSV(String str) {
        return str != null ? str.replace("\"", "\"\"") : "";
    }

    /**
     * 生成CSV文件名
     *
     * @param prefix 文件名前缀
     * @param courseId 课程ID，可为null
     * @return 适合的文件名
     */
    public static String generateCSVFileName(String prefix, Integer courseId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String timestamp = dateFormat.format(new java.util.Date());

        if (courseId != null) {
            return prefix + "_course" + courseId + "_" + timestamp + ".csv";
        } else {
            return prefix + "_all_" + timestamp + ".csv";
        }
    }
}