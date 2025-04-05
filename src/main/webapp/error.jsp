<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>错误 - 课堂辅助教学平台</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            min-height: 100vh;
            color: #333;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .error-container {
            background-color: white;
            border-radius: 12px;
            box-shadow: 0 5px 25px rgba(0, 0, 0, 0.1);
            width: 90%;
            max-width: 600px;
            padding: 2rem;
            text-align: center;
        }
        .error-icon {
            font-size: 4rem;
            color: #e53e3e;
            margin-bottom: 1rem;
        }
        .error-title {
            font-size: 1.8rem;
            color: #2d3748;
            margin-bottom: 1rem;
        }
        .error-message {
            color: #718096;
            margin-bottom: 2rem;
            line-height: 1.6;
        }
        .back-btn {
            background-color: #ed8936;
            color: white;
            border: none;
            border-radius: 6px;
            padding: 0.75rem 1.5rem;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            text-decoration: none;
        }
        .back-btn:hover {
            background-color: #dd6b20;
        }
        .back-btn i {
            margin-right: 0.5rem;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="error-container">
    <div class="error-icon">
        <i class="fas fa-exclamation-circle"></i>
    </div>
    <h1 class="error-title">出错了！</h1>
    <p class="error-message">
        ${param.message != null ? param.message : '系统出现未知错误，请联系管理员。'}
    </p>
    <a href="admin_dashboard.jsp" class="back-btn">
        <i class="fas fa-home"></i> 返回控制面板
    </a>
</div>
</body>
</html>