<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  // 检查用户是否已登录且是管理员
  String userType = (String) session.getAttribute("user_type");
  if (userType == null || !userType.equals("admin")) {
    response.sendRedirect("login.jsp");
    return;
  }
  String username = (String) session.getAttribute("username");
%>
<!DOCTYPE html>
<html>
<head>
  <title>管理员控制台 - 课堂辅助教学平台</title>
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
    }
    .dashboard {
      display: flex;
      min-height: 100vh;
    }
    .sidebar {
      width: 250px;
      background-color: #2d3748;
      color: #fff;
      padding: 2rem 1rem;
      display: flex;
      flex-direction: column;
    }
    .logo-container {
      width: 70px;
      height: 70px;
      margin: 0 auto 1.5rem;
      border-radius: 50%;
      overflow: hidden;
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
    }
    .logo-container img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .sidebar-header {
      text-align: center;
      margin-bottom: 2rem;
    }
    .sidebar-header h2 {
      font-size: 1.2rem;
      margin-bottom: 0.5rem;
    }
    .sidebar-header p {
      opacity: 0.7;
      font-size: 0.9rem;
    }
    .sidebar-menu {
      flex-grow: 1;
    }
    .menu-item {
      padding: 0.75rem 1rem;
      border-radius: 8px;
      margin-bottom: 0.5rem;
      cursor: pointer;
      transition: all 0.3s;
      display: flex;
      align-items: center;
    }
    .menu-item.active {
      background-color: #4299e1;
    }
    .menu-item:hover {
      background-color: rgba(66, 153, 225, 0.5);
    }
    .menu-item i {
      margin-right: 10px;
      font-size: 1.1rem;
    }
    .sidebar-footer {
      text-align: center;
      font-size: 0.8rem;
      opacity: 0.7;
      margin-top: 2rem;
    }
    .main-content {
      flex-grow: 1;
      padding: 2rem;
    }
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }
    .welcome {
      font-size: 1.5rem;
      color: #2d3748;
    }
    .date {
      color: #718096;
      font-size: 0.9rem;
    }
    .card-container {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 1.5rem;
    }
    .card {
      background-color: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 10px 25px rgba(50, 50, 93, 0.1), 0 5px 15px rgba(0, 0, 0, 0.07);
      cursor: pointer;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
    }
    .card:hover {
      transform: translateY(-5px);
      box-shadow: 0 15px 35px rgba(50, 50, 93, 0.15), 0 5px 15px rgba(0, 0, 0, 0.1);
    }
    .card::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      height: 5px;
      width: 100%;
    }
    .card.users::before { background-color: #4299e1; }
    .card.courses::before { background-color: #48bb78; }
    .card.discussions::before { background-color: #ed8936; }
    .card.assignments::before { background-color: #9f7aea; }
    .card.grades::before { background-color: #f56565; }

    .card h2 {
      font-size: 1.2rem;
      margin-bottom: 0.5rem;
      color: #2d3748;
    }
    .card p {
      color: #718096;
      margin-bottom: 1rem;
    }
    .card-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .card-button {
      background-color: #e2e8f0;
      color: #4a5568;
      border: none;
      border-radius: 6px;
      padding: 0.5rem 1rem;
      font-size: 0.9rem;
      cursor: pointer;
      transition: all 0.3s;
    }
    .card-button:hover {
      background-color: #cbd5e0;
    }
    .card-icon {
      font-size: 2.5rem;
      opacity: 0.15;
      position: absolute;
      bottom: 10px;
      right: 15px;
    }

    @media (max-width: 768px) {
      .dashboard {
        flex-direction: column;
      }
      .sidebar {
        width: 100%;
        padding: 1.5rem 1rem;
      }
      .logo-container {
        width: 60px;
        height: 60px;
      }
      .card-container {
        grid-template-columns: 1fr;
      }
    }
  </style>
  <!-- Font Awesome for icons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="dashboard">
  <!-- Sidebar -->
  <div class="sidebar">
    <div class="sidebar-header">
      <div class="logo-container">
        <img src="xh.jpg" alt="甘肃政法大学校徽">
      </div>
      <h2>课堂辅助教学平台</h2>
      <p>管理员控制台</p>
    </div>
    <div class="sidebar-menu">
      <div class="menu-item active">
        <i class="fas fa-home"></i> 控制面板
      </div>
      <div class="menu-item" onclick="window.location.href='UserServlet'">
        <i class="fas fa-users"></i> 用户管理
      </div>
      <div class="menu-item" onclick="window.location.href='CourseServlet'">
        <i class="fas fa-book"></i> 课程管理
      </div>
      <div class="menu-item" onclick="window.location.href='DiscussionServlet'">
        <i class="fas fa-comments"></i> 讨论区管理
      </div>
      <div class="menu-item" onclick="window.location.href='AssignmentServlet'">
        <i class="fas fa-tasks"></i> 作业管理
      </div>
      <div class="menu-item" onclick="window.location.href='GradeServlet'">
        <i class="fas fa-chart-line"></i> 成绩管理
      </div>
      <div class="menu-item" onclick="logout()">
        <i class="fas fa-sign-out-alt"></i> 退出登录
      </div>
    </div>
    <div class="sidebar-footer">
      Design & Develop By FlyyZJL from GSUPL
    </div>
  </div>

  <!-- Main Content -->
  <div class="main-content">
    <div class="header">
      <div class="welcome">欢迎，<%= username %></div>
      <div class="date" id="current-date"></div>
    </div>

    <div class="card-container">
      <!-- User Management Card -->
      <div class="card users" onclick="window.location.href='UserServlet'">
        <h2>用户管理</h2>
        <p>管理学生、教师和管理员账户</p>
        <div class="card-footer">
          <button class="card-button">进入管理</button>
        </div>
        <i class="fas fa-users card-icon"></i>
      </div>

      <!-- Course Management Card -->
      <div class="card courses" onclick="window.location.href='CourseServlet'">
        <h2>课程管理</h2>
        <p>管理课程和章节内容</p>
        <div class="card-footer">
          <button class="card-button">进入管理</button>
        </div>
        <i class="fas fa-book card-icon"></i>
      </div>

      <!-- Discussion Management Card -->
      <div class="card discussions" onclick="window.location.href='DiscussionServlet'">
        <h2>讨论区管理</h2>
        <p>管理课程讨论区和回复</p>
        <div class="card-footer">
          <button class="card-button">进入管理</button>
        </div>
        <i class="fas fa-comments card-icon"></i>
      </div>

      <!-- Assignment Management Card -->
      <div class="card assignments" onclick="window.location.href='AssignmentServlet'">
        <h2>作业管理</h2>
        <p>管理作业布置和提交</p>
        <div class="card-footer">
          <button class="card-button">进入管理</button>
        </div>
        <i class="fas fa-tasks card-icon"></i>
      </div>

      <!-- Grade Management Card -->
      <div class="card grades" onclick="window.location.href='GradeServlet'">
        <h2>成绩管理</h2>
        <p>管理学生成绩和反馈</p>
        <div class="card-footer">
          <button class="card-button">进入管理</button>
        </div>
        <i class="fas fa-chart-line card-icon"></i>
      </div>
    </div>
  </div>
</div>

<script>
  // Set current date
  function updateDate() {
    const now = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('current-date').textContent = now.toLocaleDateString('zh-CN', options);
  }

  updateDate();

  // Logout function
  function logout() {
    // Clear session
    fetch('logout', {
      method: 'POST'
    }).then(() => {
      // Clear local storage
      localStorage.removeItem('user_id');
      localStorage.removeItem('user_type');
      // Redirect to login page
      window.location.href = 'login.jsp';
    });
  }
</script>
</body>
</html>