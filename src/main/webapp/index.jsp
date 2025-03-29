<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>课堂辅助教学平台服务端</title>
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
            display: flex;
            justify-content: center;
            align-items: center;
            color: #333;
            padding: 2rem 1rem;
        }
        .container {
            background-color: white;
            border-radius: 16px;
            box-shadow: 0 15px 35px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
            padding: 3rem;
            text-align: center;
            max-width: 95%;
            width: 700px;
            animation: slideUp 0.8s ease-out;
            overflow: hidden;
            position: relative;
        }
        .success-icon {
            color: #38b2ac;
            font-size: 3.5rem;
            margin-bottom: 1.5rem;
            animation: pulse 2s infinite;
            text-shadow: 0 0 10px rgba(56, 178, 172, 0.3);
        }
        h1 {
            color: #2d3748;
            margin-bottom: 2rem;
            font-size: 2rem;
            line-height: 1.4;
            font-weight: 600;
        }
        .divider {
            height: 2px;
            background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
            margin: 2rem 0;
        }
        .footer {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-top: 1.5rem;
        }
        .footer h2 {
            color: #4a5568;
            font-size: 1.1rem;
            font-weight: 500;
            margin-bottom: 1.5rem;
        }
        .logo-container {
            position: relative;
            width: 140px;
            height: 140px;
            margin: 0.5rem auto;
            border-radius: 50%;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        .logo-container:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
        }
        .logo-container img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.5s ease;
        }
        .logo-container:hover img {
            transform: scale(1.05);
        }
        .attribution {
            margin-top: 1.5rem;
            color: #718096;
            font-size: 0.9rem;
        }
        @keyframes slideUp {
            from { opacity: 0; transform: translateY(30px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.1); }
            100% { transform: scale(1); }
        }
        .school-link {
            text-decoration: none;
            color: #4299e1;
            font-weight: 500;
            transition: color 0.3s;
        }
        .school-link:hover {
            color: #2b6cb0;
            text-decoration: underline;
        }
        @media (max-width: 600px) {
            .container {
                padding: 2rem 1.5rem;
            }
            h1 {
                font-size: 1.6rem;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="success-icon">✓</div>
    <h1>Congratulations！<br>看到此页面说明部署成功了</h1>

    <div class="divider"></div>

    <div class="footer">
        <h2>课堂辅助教学平台服务端</h2>
        <a href="https://www.gsupl.edu.cn/index.htm" class="school-link" target="_blank">
            <div class="logo-container">
                <img src="xh.jpg" alt="甘肃政法大学校徽">
            </div>
        </a>
        <div class="attribution">
            Design & Develop By FlyyZJL from GSUPL
        </div>
    </div>
</div>
</body>
</html>