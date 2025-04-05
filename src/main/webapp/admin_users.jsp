<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    <title>用户管理 - 课堂辅助教学平台</title>
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
            overflow-x: hidden;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }
        .page-title {
            font-size: 1.5rem;
            color: #2d3748;
            display: flex;
            align-items: center;
        }
        .page-title i {
            margin-right: 10px;
            color: #4299e1;
        }
        .date {
            color: #718096;
            font-size: 0.9rem;
        }

        /* 用户管理特定样式 */
        .content-container {
            background-color: white;
            border-radius: 12px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .action-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
            background-color: #f8f9fa;
            border-bottom: 1px solid #e9ecef;
        }

        .search-box {
            display: flex;
            align-items: center;
            background-color: white;
            border-radius: 6px;
            padding: 0.5rem 1rem;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
        }

        .search-box input {
            border: none;
            outline: none;
            padding: 0.3rem;
            width: 200px;
            font-size: 0.9rem;
        }

        .search-box i {
            color: #718096;
            margin-right: 0.5rem;
        }

        .add-btn {
            background-color: #4299e1;
            color: white;
            border: none;
            border-radius: 6px;
            padding: 0.6rem 1.2rem;
            font-size: 0.9rem;
            cursor: pointer;
            transition: all 0.3s;
            display: flex;
            align-items: center;
        }

        .add-btn:hover {
            background-color: #3182ce;
        }

        .add-btn i {
            margin-right: 0.5rem;
        }

        .users-table {
            width: 100%;
            border-collapse: collapse;
        }

        .users-table th, .users-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .users-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #4a5568;
        }

        .users-table tr:hover {
            background-color: #f8f9fa;
        }

        .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background-color: #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            color: #4a5568;
        }

        .user-type {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .user-type.student {
            background-color: #ebf4ff;
            color: #3182ce;
        }

        .user-type.teacher {
            background-color: #e6fffa;
            color: #319795;
        }

        .user-type.admin {
            background-color: #feebef;
            color: #e53e3e;
        }

        .action-btn {
            background-color: transparent;
            border: none;
            color: #718096;
            cursor: pointer;
            font-size: 1rem;
            margin-right: 0.5rem;
            transition: all 0.2s;
        }

        .edit-btn:hover {
            color: #4299e1;
        }

        .delete-btn:hover {
            color: #e53e3e;
        }

        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 1rem;
            background-color: #f8f9fa;
        }

        .page-btn {
            background-color: white;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            padding: 0.5rem 0.75rem;
            margin: 0 0.25rem;
            cursor: pointer;
            transition: all 0.2s;
        }

        .page-btn:hover {
            background-color: #e9ecef;
        }

        .page-btn.active {
            background-color: #4299e1;
            color: white;
            border-color: #4299e1;
        }

        /* 模态框样式 */
        .modal {
            display: none;
            position: fixed;
            z-index: 100;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgba(0, 0, 0, 0.4);
        }

        .modal-content {
            background-color: white;
            margin: 10% auto;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 5px 25px rgba(0, 0, 0, 0.2);
            width: 500px;
            max-width: 90%;
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }

        .modal-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #2d3748;
        }

        .close-btn {
            background: none;
            border: none;
            font-size: 1.5rem;
            cursor: pointer;
            color: #a0aec0;
        }

        .form-group {
            margin-bottom: 1rem;
        }

        .form-label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: #4a5568;
        }

        .form-input {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            font-size: 0.9rem;
        }

        .form-select {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            font-size: 0.9rem;
            background-color: white;
        }

        .form-submit {
            background-color: #4299e1;
            color: white;
            border: none;
            border-radius: 6px;
            padding: 0.75rem 1.5rem;
            font-size: 0.9rem;
            cursor: pointer;
            transition: all 0.3s;
            margin-top: 1rem;
            width: 100%;
        }

        .form-submit:hover {
            background-color: #3182ce;
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
            .action-bar {
                flex-direction: column;
                gap: 1rem;
            }
            .users-table th:nth-child(3),
            .users-table td:nth-child(3) {
                display: none;
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
            <div class="menu-item" onclick="window.location.href='admin_dashboard.jsp'">
                <i class="fas fa-home"></i> 控制面板
            </div>
            <div class="menu-item active" onclick="window.location.href='UserServlet'">
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
            <div class="page-title">
                <i class="fas fa-users"></i> 用户管理
            </div>
            <div class="date" id="current-date"></div>
        </div>

        <div class="content-container">
            <div class="action-bar">
                <div class="search-box">
                    <i class="fas fa-search"></i>
                    <input type="text" id="searchInput" placeholder="搜索用户...">
                </div>
                <button class="add-btn" onclick="openAddModal()">
                    <i class="fas fa-plus"></i> 添加用户
                </button>
            </div>

            <table class="users-table">
                <thead>
                <tr>
                    <th>用户</th>
                    <th>用户名</th>
                    <th>邮箱</th>
                    <th>用户类型</th>
                    <th>创建时间</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody id="usersTableBody">
                <c:forEach var="user" items="${usersList}">
                    <tr>
                        <td>
                            <div class="user-avatar">${user.username.charAt(0)}</div>
                        </td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>
                <span class="user-type ${user.userType}">
                  <c:choose>
                      <c:when test="${user.userType == 'student'}">学生</c:when>
                      <c:when test="${user.userType == 'teacher'}">教师</c:when>
                      <c:when test="${user.userType == 'admin'}">管理员</c:when>
                  </c:choose>
                </span>
                        </td>
                        <td>${user.createdAt}</td>
                        <td>
                            <button class="action-btn edit-btn" onclick="openEditModal(${user.userId}, '${user.username}', '${user.email}', '${user.userType}')">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete-btn" onclick="confirmDelete(${user.userId}, '${user.username}')">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="pagination">
                <button class="page-btn"><i class="fas fa-angle-left"></i></button>
                <button class="page-btn active">1</button>
                <button class="page-btn">2</button>
                <button class="page-btn">3</button>
                <button class="page-btn"><i class="fas fa-angle-right"></i></button>
            </div>
        </div>
    </div>
</div>

<!-- 添加用户模态框 -->
<div id="addUserModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">添加新用户</h3>
            <button class="close-btn" onclick="closeAddModal()">&times;</button>
        </div>
        <form id="addUserForm" action="UserServlet" method="post">
            <input type="hidden" name="action" value="add">

            <div class="form-group">
                <label for="username" class="form-label">用户名</label>
                <input type="text" id="username" name="username" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="password" class="form-label">密码</label>
                <input type="password" id="password" name="password" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="email" class="form-label">邮箱</label>
                <input type="email" id="email" name="email" class="form-input">
            </div>

            <div class="form-group">
                <label for="userType" class="form-label">用户类型</label>
                <select id="userType" name="userType" class="form-select" required>
                    <option value="student">学生</option>
                    <option value="teacher">教师</option>
                    <option value="admin">管理员</option>
                </select>
            </div>

            <button type="submit" class="form-submit">添加用户</button>
        </form>
    </div>
</div>

<!-- 编辑用户模态框 -->
<div id="editUserModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">编辑用户</h3>
            <button class="close-btn" onclick="closeEditModal()">&times;</button>
        </div>
        <form id="editUserForm" action="UserServlet" method="post">
            <input type="hidden" name="action" value="update">
            <input type="hidden" id="editUserId" name="userId">

            <div class="form-group">
                <label for="editUsername" class="form-label">用户名</label>
                <input type="text" id="editUsername" name="username" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="editPassword" class="form-label">密码（留空则保持不变）</label>
                <input type="password" id="editPassword" name="password" class="form-input">
            </div>

            <div class="form-group">
                <label for="editEmail" class="form-label">邮箱</label>
                <input type="email" id="editEmail" name="email" class="form-input">
            </div>

            <div class="form-group">
                <label for="editUserType" class="form-label">用户类型</label>
                <select id="editUserType" name="userType" class="form-select" required>
                    <option value="student">学生</option>
                    <option value="teacher">教师</option>
                    <option value="admin">管理员</option>
                </select>
            </div>

            <button type="submit" class="form-submit">保存修改</button>
        </form>
    </div>
</div>

<script>
    // 设置当前日期
    function updateDate() {
        const now = new Date();
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('current-date').textContent = now.toLocaleDateString('zh-CN', options);
    }

    updateDate();

    // 退出登录函数
    function logout() {
        fetch('logout', {
            method: 'POST'
        }).then(() => {
            localStorage.removeItem('user_id');
            localStorage.removeItem('user_type');
            window.location.href = 'login.jsp';
        });
    }

    // 添加用户模态框操作
    function openAddModal() {
        document.getElementById('addUserModal').style.display = 'block';
    }

    function closeAddModal() {
        document.getElementById('addUserModal').style.display = 'none';
    }

    // 编辑用户模态框操作
    function openEditModal(userId, username, email, userType) {
        document.getElementById('editUserId').value = userId;
        document.getElementById('editUsername').value = username;
        document.getElementById('editEmail').value = email;
        document.getElementById('editUserType').value = userType;
        document.getElementById('editUserModal').style.display = 'block';
    }

    function closeEditModal() {
        document.getElementById('editUserModal').style.display = 'none';
    }

    // 确认删除用户
    function confirmDelete(userId, username) {
        if (confirm(`确定要删除用户吗？此操作不可恢复！`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'UserServlet';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';

            const userIdInput = document.createElement('input');
            userIdInput.type = 'hidden';
            userIdInput.name = 'userId';
            userIdInput.value = userId;

            form.appendChild(actionInput);
            form.appendChild(userIdInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    // 搜索用户功能
    document.getElementById('searchInput').addEventListener('input', function() {
        const searchQuery = this.value.toLowerCase();
        const rows = document.querySelectorAll('#usersTableBody tr');

        rows.forEach(row => {
            const username = row.cells[1].textContent.toLowerCase();
            const email = row.cells[2].textContent.toLowerCase();
            const userType = row.cells[3].textContent.toLowerCase();

            if (username.includes(searchQuery) ||
                email.includes(searchQuery) ||
                userType.includes(searchQuery)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });

    // 点击模态框外部关闭模态框
    window.onclick = function(event) {
        if (event.target == document.getElementById('addUserModal')) {
            closeAddModal();
        }
        if (event.target == document.getElementById('editUserModal')) {
            closeEditModal();
        }
    }
</script>
</body>
</html>