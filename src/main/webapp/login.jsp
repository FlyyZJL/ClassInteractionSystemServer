<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>课堂辅助教学平台 - 登录</title>
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
            width: 450px;
            animation: slideUp 0.8s ease-out;
            overflow: hidden;
        }
        h1 {
            color: #2d3748;
            margin-bottom: 1.5rem;
            font-size: 1.8rem;
            font-weight: 600;
        }
        .logo-container {
            width: 90px;
            height: 90px;
            margin: 0 auto 1.5rem;
            border-radius: 50%;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        .logo-container img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .form-group {
            margin-bottom: 1.5rem;
            text-align: left;
        }
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: #4a5568;
            font-weight: 500;
        }
        .form-control {
            width: 100%;
            padding: 0.75rem 1rem;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            font-size: 1rem;
            transition: all 0.3s;
        }
        .form-control:focus {
            outline: none;
            border-color: #4299e1;
            box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.2);
        }
        .btn {
            background-color: #4299e1;
            color: white;
            border: none;
            border-radius: 8px;
            padding: 0.75rem 1.5rem;
            font-size: 1rem;
            font-weight: 500;
            cursor: pointer;
            width: 100%;
            margin-top: 1rem;
            transition: background-color 0.3s;
        }
        .btn:hover {
            background-color: #3182ce;
        }
        .error-message {
            color: #e53e3e;
            margin-top: 1rem;
            font-size: 0.9rem;
            display: none;
        }
        .footer {
            margin-top: 2rem;
            color: #718096;
            font-size: 0.9rem;
        }
        @keyframes slideUp {
            from { opacity: 0; transform: translateY(30px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @media (max-width: 500px) {
            .container {
                padding: 2rem 1.5rem;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="logo-container">
        <img src="xh.jpg" alt="甘肃政法大学校徽">
    </div>
    <h1>课堂辅助教学平台</h1>

    <div id="loginForm">
        <div class="form-group">
            <label for="username">用户名</label>
            <input type="text" id="username" class="form-control" placeholder="请输入用户名">
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" class="form-control" placeholder="请输入密码">
        </div>
        <button type="button" class="btn" id="loginButton">登录</button>
        <div class="error-message" id="errorMessage"></div>
    </div>

    <div class="footer">
        Design & Develop By FlyyZJL from GSUPL
    </div>
</div>

<script>
    document.getElementById('loginButton').addEventListener('click', function() {
        login();
    });

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            login();
        }
    });

    function login() {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const errorMessage = document.getElementById('errorMessage');

        if (!username || !password) {
            errorMessage.textContent = '用户名和密码不能为空';
            errorMessage.style.display = 'block';
            return;
        }

        const data = {
            username: username,
            password: password
        };

        fetch('login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('登录失败，状态码: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                if (data.status === 'success') {
                    // 保存用户信息到本地存储
                    localStorage.setItem('user_id', data.user_id);
                    localStorage.setItem('user_type', data.user_type);

                    // 跳转到对应页面
                    if (data.user_type === 'teacher') {
                        window.location.href = 'teacher_dashboard.jsp';
                    } else if (data.user_type === 'student') {
                        window.location.href = 'student_dashboard.jsp';
                    } else {
                        window.location.href = 'admin_dashboard.jsp';
                    }
                } else {
                    errorMessage.textContent = data.message || '登录失败，请检查用户名和密码';
                    errorMessage.style.display = 'block';
                }
            })
            .catch(error => {
                errorMessage.textContent = error.message;
                errorMessage.style.display = 'block';
            });
    }
</script>
</body>
</html>