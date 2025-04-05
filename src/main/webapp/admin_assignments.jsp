<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
    <title>作业管理 - 课堂辅助教学平台</title>
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
            background-color: #9f7aea;
        }
        .menu-item:hover {
            background-color: rgba(159, 122, 234, 0.5);
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
            color: #9f7aea;
        }
        .date {
            color: #718096;
            font-size: 0.9rem;
        }

        /* 作业管理特定样式 */
        .tabs {
            display: flex;
            margin-bottom: 1.5rem;
            border-bottom: 1px solid #e2e8f0;
        }
        .tab {
            padding: 0.75rem 1.5rem;
            cursor: pointer;
            border-bottom: 2px solid transparent;
            font-weight: 500;
            color: #718096;
            transition: all 0.3s;
        }
        .tab:hover {
            color: #9f7aea;
        }
        .tab.active {
            color: #9f7aea;
            border-bottom: 2px solid #9f7aea;
        }

        .content-container {
            background-color: white;
            border-radius: 12px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            margin-bottom: 2rem;
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

        .action-right {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .filter-dropdown {
            position: relative;
            display: inline-block;
        }

        .filter-btn {
            background-color: #e2e8f0;
            color: #4a5568;
            border: none;
            border-radius: 6px;
            padding: 0.6rem 1rem;
            font-size: 0.9rem;
            cursor: pointer;
            display: flex;
            align-items: center;
        }

        .filter-btn i {
            margin-right: 5px;
        }

        .filter-dropdown-content {
            display: none;
            position: absolute;
            right: 0;
            background-color: white;
            min-width: 160px;
            box-shadow: 0 8px 16px rgba(0,0,0,0.1);
            z-index: 1;
            border-radius: 6px;
            overflow: hidden;
        }

        .filter-item {
            padding: 12px 16px;
            text-decoration: none;
            display: block;
            color: #4a5568;
            cursor: pointer;
        }

        .filter-item:hover {
            background-color: #f8f9fa;
        }

        .filter-dropdown:hover .filter-dropdown-content {
            display: block;
        }

        .add-btn {
            background-color: #9f7aea;
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
            background-color: #805ad5;
        }

        .add-btn i {
            margin-right: 0.5rem;
        }

        /* 作业列表样式 */
        .assignments-table {
            width: 100%;
            border-collapse: collapse;
        }

        .assignments-table th, .assignments-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .assignments-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #4a5568;
        }

        .assignments-table tr:hover {
            background-color: #f8f9fa;
        }

        .assignment-title {
            font-weight: 500;
            color: #2d3748;
        }

        .assignment-title a {
            color: #4a5568;
            text-decoration: none;
        }

        .assignment-title a:hover {
            color: #9f7aea;
        }

        .course-tag {
            display: inline-block;
            background-color: #e9ecef;
            color: #4a5568;
            padding: 0.2rem 0.5rem;
            border-radius: 4px;
            font-size: 0.8rem;
            margin-right: 0.5rem;
        }

        .due-date {
            color: #718096;
        }

        .due-date.overdue {
            color: #e53e3e;
        }

        .submission-count {
            color: #4a5568;
            font-weight: 500;
        }

        .action-btn {
            background-color: transparent;
            border: none;
            cursor: pointer;
            font-size: 1.1rem;
            color: #718096;
            transition: all 0.2s;
            padding: 0.3rem;
            border-radius: 4px;
        }

        .view-btn:hover {
            color: #4299e1;
            background-color: #ebf8ff;
        }

        .edit-btn:hover {
            color: #48bb78;
            background-color: #f0fff4;
        }

        .delete-btn:hover {
            color: #e53e3e;
            background-color: #fff5f5;
        }

        /* 作业提交列表样式 */
        .submissions-table {
            width: 100%;
            border-collapse: collapse;
        }

        .submissions-table th, .submissions-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .submissions-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #4a5568;
        }

        .submissions-table tr:hover {
            background-color: #f8f9fa;
        }

        .student-info {
            display: flex;
            align-items: center;
        }

        .student-avatar {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 0.75rem;
            font-weight: bold;
            color: #4a5568;
        }

        .score {
            font-weight: bold;
        }

        .score.high {
            color: #48bb78;
        }

        .score.medium {
            color: #ecc94b;
        }

        .score.low {
            color: #e53e3e;
        }

        .score.ungraded {
            color: #718096;
            font-weight: normal;
            font-style: italic;
        }

        .submission-details {
            padding: 1.5rem;
        }

        .submission-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid #e2e8f0;
        }

        .submission-info h2 {
            font-size: 1.5rem;
            margin-bottom: 0.5rem;
            color: #2d3748;
        }

        .submission-meta {
            color: #718096;
            font-size: 0.9rem;
            display: flex;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .submission-meta span {
            display: flex;
            align-items: center;
        }

        .submission-meta i {
            margin-right: 0.3rem;
        }

        .submission-actions {
            display: flex;
            gap: 0.5rem;
        }

        .submission-content {
            line-height: 1.6;
            color: #4a5568;
            margin-bottom: 2rem;
            padding: 1rem;
            background-color: #f8f9fa;
            border-radius: 8px;
            border: 1px solid #e9ecef;
        }

        .file-attachment {
            display: flex;
            align-items: center;
            padding: 1rem;
            background-color: #ebf8ff;
            border-radius: 8px;
            margin-bottom: 2rem;
        }

        .file-attachment i {
            font-size: 2rem;
            color: #4299e1;
            margin-right: 1rem;
        }

        .file-info {
            flex-grow: 1;
        }

        .file-name {
            font-weight: 500;
            color: #2d3748;
            margin-bottom: 0.25rem;
        }

        .file-size {
            font-size: 0.85rem;
            color: #718096;
        }

        .download-btn {
            background-color: #4299e1;
            color: white;
            border: none;
            border-radius: 6px;
            padding: 0.5rem 1rem;
            font-size: 0.9rem;
            cursor: pointer;
            transition: all 0.3s;
            display: flex;
            align-items: center;
        }

        .download-btn:hover {
            background-color: #3182ce;
        }

        .download-btn i {
            margin-right: 0.5rem;
        }

        .grading-section {
            margin-top: 2rem;
            padding-top: 1.5rem;
            border-top: 1px solid #e2e8f0;
        }

        .grading-section h3 {
            color: #4a5568;
            margin-bottom: 1rem;
        }

        .grade-form {
            margin-top: 1rem;
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

        .form-input, .form-textarea {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            font-size: 0.9rem;
            font-family: inherit;
        }

        .form-textarea {
            height: 150px;
            resize: vertical;
        }

        .submit-grade-btn {
            background-color: #9f7aea;
            color: white;
            border: none;
            border-radius: 6px;
            padding: 0.75rem 1.5rem;
            font-size: 0.9rem;
            cursor: pointer;
            transition: all 0.3s;
            margin-top: 1rem;
        }

        .submit-grade-btn:hover {
            background-color: #805ad5;
        }

        .grade-display {
            display: flex;
            align-items: center;
            margin-bottom: 1rem;
        }

        .grade-circle {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            font-weight: bold;
            margin-right: 1.5rem;
            color: white;
        }

        .grade-circle.high {
            background-color: #48bb78;
        }

        .grade-circle.medium {
            background-color: #ecc94b;
        }

        .grade-circle.low {
            background-color: #e53e3e;
        }

        .grade-circle.ungraded {
            background-color: #a0aec0;
            font-style: italic;
        }

        .grade-info {
            line-height: 1.6;
        }

        .grade-info p {
            margin-bottom: 0.5rem;
            color: #4a5568;
        }

        .grade-info .graded-by {
            font-size: 0.9rem;
            color: #718096;
        }

        .feedback-box {
            padding: 1rem;
            background-color: #ebf8ff;
            border-left: 4px solid #4299e1;
            border-radius: 4px;
            margin-top: 1rem;
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
            background-color: #9f7aea;
            color: white;
            border-color: #9f7aea;
        }

        /* 详情视图样式 */
        .assignment-details {
            padding: 1.5rem;
        }

        .assignment-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid #e2e8f0;
        }

        .assignment-info h2 {
            font-size: 1.5rem;
            margin-bottom: 0.5rem;
            color: #2d3748;
        }

        .assignment-meta {
            color: #718096;
            font-size: 0.9rem;
            display: flex;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .assignment-meta span {
            display: flex;
            align-items: center;
        }

        .assignment-meta i {
            margin-right: 0.3rem;
        }

        .assignment-actions {
            display: flex;
            gap: 0.5rem;
        }

        .assignment-content {
            line-height: 1.6;
            color: #4a5568;
            margin-bottom: 2rem;
        }

        /* 返回按钮 */
        .back-btn {
            background-color: #e2e8f0;
            color: #4a5568;
            border: none;
            border-radius: 6px;
            padding: 0.6rem 1.2rem;
            font-size: 0.9rem;
            cursor: pointer;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            margin-bottom: 1rem;
        }

        .back-btn:hover {
            background-color: #cbd5e0;
        }

        .back-btn i {
            margin-right: 0.5rem;
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
            width: 600px;
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

        /* 视图显示控制 */
        #assignmentsView {
            display: block;
        }
        #assignmentDetailsView {
            display: none;
        }
        #submissionsView {
            display: none;
        }
        #submissionDetailsView {
            display: none;
        }

        /* 响应式样式 */
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
                align-items: stretch;
            }
            .action-right {
                flex-direction: column;
                width: 100%;
            }
            .assignments-table th:nth-child(3),
            .assignments-table td:nth-child(3),
            .assignments-table th:nth-child(4),
            .assignments-table td:nth-child(4) {
                display: none;
            }
            .submissions-table th:nth-child(3),
            .submissions-table td:nth-child(3) {
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
            <div class="menu-item" onclick="window.location.href='UserServlet'">
                <i class="fas fa-users"></i> 用户管理
            </div>
            <div class="menu-item" onclick="window.location.href='CourseServlet'">
                <i class="fas fa-book"></i> 课程管理
            </div>
            <div class="menu-item" onclick="window.location.href='DiscussionServlet'">
                <i class="fas fa-comments"></i> 讨论区管理
            </div>
            <div class="menu-item active" onclick="window.location.href='AssignmentServlet'">
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
                <i class="fas fa-tasks"></i> 作业管理
            </div>
            <div class="date" id="current-date"></div>
        </div>

        <!-- 作业列表视图 -->
        <div id="assignmentsView">
            <div class="tabs">
                <div class="tab active" onclick="filterAssignments('all')">全部作业</div>
                <div class="tab" onclick="filterAssignments('active')">进行中</div>
                <div class="tab" onclick="filterAssignments('overdue')">已截止</div>
            </div>

            <div class="content-container">
                <div class="action-bar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" id="assignmentSearchInput" placeholder="搜索作业标题...">
                    </div>
                    <div class="action-right">
                        <div class="filter-dropdown">
                            <button class="filter-btn">
                                <i class="fas fa-filter"></i> 课程筛选
                            </button>
                            <div class="filter-dropdown-content" id="courseFilterDropdown">
                                <div class="filter-item" onclick="filterByCourse('all')">全部课程</div>
                                <c:forEach var="course" items="${coursesList}">
                                    <div class="filter-item" onclick="filterByCourse(${course.courseId})">${course.courseName}</div>
                                </c:forEach>
                            </div>
                        </div>
                        <button class="add-btn" onclick="openAddAssignmentModal()">
                            <i class="fas fa-plus"></i> 添加作业
                        </button>
                    </div>
                </div>

                <table class="assignments-table" id="assignmentsTable">
                    <thead>
                    <tr>
                        <th style="width: 35%">作业标题</th>
                        <th style="width: 15%">所属课程</th>
                        <th style="width: 15%">截止日期</th>
                        <th style="width: 15%">提交数量</th>
                        <th style="width: 20%">操作</th>
                    </tr>
                    </thead>
                    <tbody id="assignmentsTableBody">
                    <c:forEach var="assignment" items="${assignmentsList}">
                        <tr data-course-id="${assignment.courseId}" class="${assignment.isOverdue ? 'overdue' : 'active'}">
                            <td class="assignment-title">
                                <a href="#" onclick="viewAssignmentDetails(${assignment.assignmentId})">${assignment.title}</a>
                            </td>
                            <td>
                                <span class="course-tag">${assignment.courseName}</span>
                            </td>
                            <td>
                  <span class="due-date ${assignment.isOverdue ? 'overdue' : ''}">
                    <fmt:formatDate value="${assignment.dueDate}" pattern="yyyy-MM-dd HH:mm" />
                  </span>
                            </td>
                            <td>
                                <span class="submission-count">${assignment.submissionCount}/${assignment.totalStudents}</span>
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <button class="action-btn view-btn" title="查看详情" onclick="viewAssignmentDetails(${assignment.assignmentId})">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <button class="action-btn view-btn" title="查看提交" onclick="viewSubmissions(${assignment.assignmentId}, '${assignment.title}')">
                                        <i class="fas fa-list-alt"></i>
                                    </button>
                                    <button class="action-btn edit-btn" title="编辑" onclick="openEditAssignmentModal(${assignment.assignmentId})">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="action-btn delete-btn" title="删除" onclick="confirmDeleteAssignment(${assignment.assignmentId})">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
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

        <!-- 作业详情视图 -->
        <div id="assignmentDetailsView">
            <button class="back-btn" onclick="backToAssignments()">
                <i class="fas fa-arrow-left"></i> 返回作业列表
            </button>

            <div class="content-container">
                <div class="assignment-details">
                    <div class="assignment-header">
                        <div class="assignment-info">
                            <h2 id="detailAssignmentTitle">作业标题</h2>
                            <div class="assignment-meta">
                                <span id="detailCourseName"><i class="fas fa-book"></i> 所属课程</span>
                                <span id="detailDueDate"><i class="fas fa-calendar"></i> 截止日期</span>
                                <span id="detailSubmissionCount"><i class="fas fa-file-alt"></i> 提交情况</span>
                            </div>
                        </div>
                        <div class="assignment-actions">
                            <button class="action-btn edit-btn" id="editAssignmentBtn" onclick="editFromDetails()">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn view-btn" id="viewSubmissionsBtn" onclick="viewSubmissionsFromDetails()">
                                <i class="fas fa-list-alt"></i>
                            </button>
                            <button class="action-btn delete-btn" id="deleteAssignmentBtn" onclick="deleteFromDetails()">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>

                    <div class="assignment-content" id="detailAssignmentDescription">
                        <!-- 作业内容/描述将在这里显示 -->
                    </div>
                </div>
            </div>
        </div>

        <!-- 提交列表视图 -->
        <div id="submissionsView">
            <button class="back-btn" onclick="backToAssignments()">
                <i class="fas fa-arrow-left"></i> 返回作业列表
            </button>

            <div class="content-container">
                <div class="action-bar">
                    <h2 id="submissionsTitle">作业提交列表</h2>
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" id="submissionSearchInput" placeholder="搜索学生...">
                    </div>
                </div>

                <table class="submissions-table" id="submissionsTable">
                    <thead>
                    <tr>
                        <th style="width: 25%">学生</th>
                        <th style="width: 25%">提交时间</th>
                        <th style="width: 25%">成绩</th>
                        <th style="width: 25%">操作</th>
                    </tr>
                    </thead>
                    <tbody id="submissionsTableBody">
                    <!-- 提交列表将通过AJAX加载 -->
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 提交详情视图 -->
        <div id="submissionDetailsView">
            <button class="back-btn" onclick="backToSubmissions()">
                <i class="fas fa-arrow-left"></i> 返回提交列表
            </button>

            <div class="content-container">
                <div class="submission-details">
                    <div class="submission-header">
                        <div class="submission-info">
                            <h2 id="detailSubmissionAssignmentTitle">作业标题</h2>
                            <div class="submission-meta">
                                <span id="detailSubmissionStudentName"><i class="fas fa-user"></i> 学生姓名</span>
                                <span id="detailSubmissionTime"><i class="fas fa-clock"></i> 提交时间</span>
                            </div>
                        </div>
                    </div>

                    <div id="submissionContentSection">
                        <h3>提交内容</h3>
                        <div class="submission-content" id="detailSubmissionContent">
                            <!-- 提交内容将在这里显示 -->
                        </div>

                        <div id="fileAttachmentSection" style="display: none;">
                            <div class="file-attachment">
                                <i class="fas fa-file-alt"></i>
                                <div class="file-info">
                                    <div class="file-name" id="detailFileName">文件名称</div>
                                    <div class="file-size" id="detailFileSize">文件大小</div>
                                </div>
                                <button class="download-btn" id="downloadFileBtn">
                                    <i class="fas fa-download"></i> 下载
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="grading-section">
                        <h3>评分信息</h3>

                        <div id="gradeDisplaySection" style="display: none;">
                            <div class="grade-display">
                                <div class="grade-circle" id="gradeCircle">--</div>
                                <div class="grade-info">
                                    <p id="gradeScore">成绩: --</p>
                                    <p id="gradeFeedback">反馈: --</p>
                                    <p class="graded-by" id="gradedBy">评分人: --</p>
                                    <p class="graded-by" id="gradedAt">评分时间: --</p>
                                </div>
                            </div>
                        </div>

                        <div id="gradeFormSection">
                            <form id="gradeForm" action="AssignmentSubmissionServlet" method="post">
                                <input type="hidden" id="gradeSubmissionId" name="submissionId">
                                <input type="hidden" name="action" value="gradeSubmission">

                                <div class="form-group">
                                    <label for="score" class="form-label">成绩</label>
                                    <input type="number" id="score" name="score" min="0" max="100" step="0.01" class="form-input" required>
                                </div>

                                <div class="form-group">
                                    <label for="feedback" class="form-label">评语反馈</label>
                                    <textarea id="feedback" name="feedback" class="form-textarea"></textarea>
                                </div>

                                <button type="submit" class="submit-grade-btn">提交评分</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 添加作业模态框 -->
<div id="addAssignmentModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">添加新作业</h3>
            <button class="close-btn" onclick="closeAddAssignmentModal()">&times;</button>
        </div>
        <form id="addAssignmentForm" action="AssignmentServlet" method="post">
            <input type="hidden" name="action" value="addAssignment">

            <div class="form-group">
                <label for="assignmentTitle" class="form-label">作业标题</label>
                <input type="text" id="assignmentTitle" name="title" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="assignmentCourseId" class="form-label">所属课程</label>
                <select id="assignmentCourseId" name="courseId" class="form-input" required>
                    <c:forEach var="course" items="${coursesList}">
                        <option value="${course.courseId}">${course.courseName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="assignmentDescription" class="form-label">作业描述</label>
                <textarea id="assignmentDescription" name="description" class="form-textarea" rows="10"></textarea>
            </div>

            <div class="form-group">
                <label for="assignmentDueDate" class="form-label">截止日期</label>
                <input type="datetime-local" id="assignmentDueDate" name="dueDate" class="form-input" required>
            </div>

            <button type="submit" class="submit-grade-btn">添加作业</button>
        </form>
    </div>
</div>

<!-- 编辑作业模态框 -->
<div id="editAssignmentModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">编辑作业</h3>
            <button class="close-btn" onclick="closeEditAssignmentModal()">&times;</button>
        </div>
        <form id="editAssignmentForm" action="AssignmentServlet" method="post">
            <input type="hidden" name="action" value="updateAssignment">
            <input type="hidden" id="editAssignmentId" name="assignmentId">

            <div class="form-group">
                <label for="editAssignmentTitle" class="form-label">作业标题</label>
                <input type="text" id="editAssignmentTitle" name="title" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="editAssignmentCourseId" class="form-label">所属课程</label>
                <select id="editAssignmentCourseId" name="courseId" class="form-input" required>
                    <c:forEach var="course" items="${coursesList}">
                        <option value="${course.courseId}">${course.courseName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="editAssignmentDescription" class="form-label">作业描述</label>
                <textarea id="editAssignmentDescription" name="description" class="form-textarea" rows="10"></textarea>
            </div>

            <div class="form-group">
                <label for="editAssignmentDueDate" class="form-label">截止日期</label>
                <input type="datetime-local" id="editAssignmentDueDate" name="dueDate" class="form-input" required>
            </div>

            <button type="submit" class="submit-grade-btn">保存修改</button>
        </form>
    </div>
</div>

<script>
    // 当前选中的作业ID和提交ID
    let currentAssignmentId = null;
    let currentSubmissionId = null;
    let currentAssignmentTitle = "";
    let currentFilter = 'all';
    let currentCourseFilter = 'all';

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

    // 视图切换函数
    function showAssignmentsView() {
        document.getElementById('assignmentsView').style.display = 'block';
        document.getElementById('assignmentDetailsView').style.display = 'none';
        document.getElementById('submissionsView').style.display = 'none';
        document.getElementById('submissionDetailsView').style.display = 'none';
    }

    function showAssignmentDetailsView() {
        document.getElementById('assignmentsView').style.display = 'none';
        document.getElementById('assignmentDetailsView').style.display = 'block';
        document.getElementById('submissionsView').style.display = 'none';
        document.getElementById('submissionDetailsView').style.display = 'none';
    }

    function showSubmissionsView() {
        document.getElementById('assignmentsView').style.display = 'none';
        document.getElementById('assignmentDetailsView').style.display = 'none';
        document.getElementById('submissionsView').style.display = 'block';
        document.getElementById('submissionDetailsView').style.display = 'none';
    }

    function showSubmissionDetailsView() {
        document.getElementById('assignmentsView').style.display = 'none';
        document.getElementById('assignmentDetailsView').style.display = 'none';
        document.getElementById('submissionsView').style.display = 'none';
        document.getElementById('submissionDetailsView').style.display = 'block';
    }

    // 返回按钮函数
    function backToAssignments() {
        showAssignmentsView();
    }

    function backToSubmissions() {
        showSubmissionsView();
    }

    // 筛选作业函数
    function filterAssignments(filter) {
        currentFilter = filter;

        // 更新标签激活状态
        document.querySelectorAll('.tab').forEach(tab => {
            tab.classList.remove('active');
        });
        event.target.classList.add('active');

        // 应用筛选
        const rows = document.querySelectorAll('#assignmentsTableBody tr');

        rows.forEach(row => {
            // 优先应用课程筛选
            const courseId = row.getAttribute('data-course-id');
            const courseVisible = currentCourseFilter === 'all' || courseId === currentCourseFilter;

            // 然后应用状态筛选
            let statusVisible = true;
            if (filter === 'active') {
                statusVisible = row.classList.contains('active');
            } else if (filter === 'overdue') {
                statusVisible = row.classList.contains('overdue');
            }

            // 最终显示状态
            row.style.display = (courseVisible && statusVisible) ? '' : 'none';
        });
    }

    // 课程筛选函数
    function filterByCourse(courseId) {
        currentCourseFilter = courseId;

        // 应用筛选
        const rows = document.querySelectorAll('#assignmentsTableBody tr');

        rows.forEach(row => {
            const rowCourseId = row.getAttribute('data-course-id');

            // 先检查课程筛选
            const courseVisible = courseId === 'all' || rowCourseId == courseId;

            // 然后应用状态筛选
            let statusVisible = true;
            if (currentFilter === 'active') {
                statusVisible = row.classList.contains('active');
            } else if (currentFilter === 'overdue') {
                statusVisible = row.classList.contains('overdue');
            }

            // 最终显示状态
            row.style.display = (courseVisible && statusVisible) ? '' : 'none';
        });
    }

    // 搜索作业函数
    document.getElementById('assignmentSearchInput').addEventListener('input', function() {
        const searchQuery = this.value.toLowerCase();
        const rows = document.querySelectorAll('#assignmentsTableBody tr');

        rows.forEach(row => {
            const title = row.querySelector('.assignment-title').textContent.toLowerCase();
            const courseId = row.getAttribute('data-course-id');

            // 应用搜索过滤
            const searchMatch = title.includes(searchQuery);

            // 应用课程筛选
            const courseVisible = currentCourseFilter === 'all' || courseId == currentCourseFilter;

            // 应用状态筛选
            let statusVisible = true;
            if (currentFilter === 'active') {
                statusVisible = row.classList.contains('active');
            } else if (currentFilter === 'overdue') {
                statusVisible = row.classList.contains('overdue');
            }

            // 最终显示状态
            row.style.display = (searchMatch && courseVisible && statusVisible) ? '' : 'none';
        });
    });

    function viewAssignmentDetails(assignmentId) {
        currentAssignmentId = assignmentId;

        // 加载作业详情
        fetch("AssignmentServlet?action=getAssignment&assignmentId=" + assignmentId)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.error || "加载作业详情失败") });
                }
                return response.json();
            })
            .then(assignment => {
                // 填充作业详情
                document.getElementById('detailAssignmentTitle').textContent = assignment.title;

                var courseNameHtml = '<i class="fas fa-book"></i> ' + assignment.courseName;
                document.getElementById('detailCourseName').innerHTML = courseNameHtml;

                // 格式化日期
                const dueDate = new Date(assignment.dueDate);
                const formattedDueDate = dueDate.toLocaleString('zh-CN');
                const dueDateClass = assignment.isOverdue ? 'due-date overdue' : 'due-date';
                var dueDateHtml = '<i class="fas fa-calendar"></i> <span class="' + dueDateClass + '">' + formattedDueDate + '</span>';
                document.getElementById('detailDueDate').innerHTML = dueDateHtml;

                // 提交情况
                var submissionHtml = '<i class="fas fa-file-alt"></i> 提交情况: ' + assignment.submissionCount + '/' + assignment.totalStudents;
                document.getElementById('detailSubmissionCount').innerHTML = submissionHtml;

                // 作业描述
                document.getElementById('detailAssignmentDescription').textContent = assignment.description || '无描述内容';

                // 存储当前作业标题，用于显示提交列表
                currentAssignmentTitle = assignment.title;
            })
            .catch(error => {
                console.error('Error loading assignment details:', error);
                alert('加载作业详情失败: ' + error.message);
            });

        showAssignmentDetailsView();
    }

    function viewSubmissions(assignmentId, assignmentTitle) {
        currentAssignmentId = assignmentId;
        currentAssignmentTitle = assignmentTitle || currentAssignmentTitle;

        // 更新提交列表标题
        document.getElementById('submissionsTitle').textContent = '"' + currentAssignmentTitle + '" - 提交列表';

        // 加载提交列表
        fetch("AssignmentSubmissionServlet?action=getSubmissions&assignmentId=" + assignmentId)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.error || "加载提交列表失败") });
                }
                return response.json();
            })
            .then(submissions => {
                const tbody = document.getElementById('submissionsTableBody');
                tbody.innerHTML = '';

                if (submissions.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">暂无提交记录</td></tr>';
                    return;
                }

                submissions.forEach(function(submission) {
                    const tr = document.createElement('tr');

                    // 学生信息
                    const tdStudent = document.createElement('td');
                    const studentInfo = document.createElement('div');
                    studentInfo.className = 'student-info';

                    const studentAvatar = document.createElement('div');
                    studentAvatar.className = 'student-avatar';
                    studentAvatar.textContent = submission.studentName.charAt(0).toUpperCase();

                    const studentName = document.createTextNode(submission.studentName);

                    studentInfo.appendChild(studentAvatar);
                    studentInfo.appendChild(studentName);
                    tdStudent.appendChild(studentInfo);
                    tr.appendChild(tdStudent);

                    // 提交时间
                    const tdSubmitTime = document.createElement('td');
                    const submitDate = new Date(submission.submitTime);
                    tdSubmitTime.textContent = submitDate.toLocaleString('zh-CN');
                    tr.appendChild(tdSubmitTime);

                    // 成绩
                    const tdScore = document.createElement('td');
                    const scoreSpan = document.createElement('span');

                    if (submission.score !== null) {
                        let scoreClass = '';
                        if (submission.score >= 90) {
                            scoreClass = 'high';
                        } else if (submission.score >= 60) {
                            scoreClass = 'medium';
                        } else {
                            scoreClass = 'low';
                        }

                        scoreSpan.className = 'score ' + scoreClass;
                        scoreSpan.textContent = submission.score;
                    } else {
                        scoreSpan.className = 'score ungraded';
                        scoreSpan.textContent = '未评分';
                    }

                    tdScore.appendChild(scoreSpan);
                    tr.appendChild(tdScore);

                    // 操作按钮
                    const tdActions = document.createElement('td');
                    const viewBtn = document.createElement('button');
                    viewBtn.className = 'action-btn view-btn';
                    viewBtn.innerHTML = '<i class="fas fa-eye"></i>';
                    viewBtn.title = '查看详情';
                    viewBtn.onclick = function() {
                        viewSubmissionDetails(submission.submissionId);
                    };

                    const gradeBtn = document.createElement('button');
                    gradeBtn.className = 'action-btn edit-btn';
                    gradeBtn.innerHTML = '<i class="fas fa-check-circle"></i>';
                    gradeBtn.title = submission.score !== null ? '修改评分' : '评分';
                    gradeBtn.onclick = function() {
                        viewSubmissionDetails(submission.submissionId, true);
                    };

                    tdActions.appendChild(viewBtn);
                    tdActions.appendChild(gradeBtn);
                    tr.appendChild(tdActions);

                    tbody.appendChild(tr);
                });
            })
            .catch(error => {
                console.error('Error loading submissions:', error);
                alert('加载提交列表失败: ' + error.message);
            });

        showSubmissionsView();
    }

    // 搜索提交记录
    document.getElementById('submissionSearchInput').addEventListener('input', function() {
        const searchQuery = this.value.toLowerCase();
        const rows = document.querySelectorAll('#submissionsTableBody tr');

        rows.forEach(row => {
            const studentInfo = row.querySelector('.student-info');
            if (studentInfo) {
                const studentName = studentInfo.textContent.toLowerCase();
                row.style.display = studentName.includes(searchQuery) ? '' : 'none';
            }
        });
    });

    function viewSubmissionDetails(submissionId, focusOnGrading = false) {
        currentSubmissionId = submissionId;

        // 加载提交详情
        fetch("AssignmentSubmissionServlet?action=getSubmission&submissionId=" + submissionId)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.error || "加载提交详情失败") });
                }
                return response.json();
            })
            .then(submission => {
                // 填充提交详情
                document.getElementById('detailSubmissionAssignmentTitle').textContent = submission.assignmentTitle;

                var studentHtml = '<i class="fas fa-user"></i> ' + submission.studentName;
                document.getElementById('detailSubmissionStudentName').innerHTML = studentHtml;

                // 提交时间
                const submitDate = new Date(submission.submitTime);
                var timeHtml = '<i class="fas fa-clock"></i> ' + submitDate.toLocaleString('zh-CN');
                document.getElementById('detailSubmissionTime').innerHTML = timeHtml;

                // 提交内容
                if (submission.content) {
                    document.getElementById('detailSubmissionContent').textContent = submission.content;
                    document.getElementById('submissionContentSection').style.display = 'block';
                } else {
                    document.getElementById('submissionContentSection').style.display = 'none';
                }

                // 文件附件
                if (submission.filePath) {
                    document.getElementById('detailFileName').textContent = submission.filePath.split('/').pop();
                    document.getElementById('detailFileSize').textContent = '点击下载查看';
                    document.getElementById('downloadFileBtn').onclick = function() {
                        window.open("AssignmentSubmissionServlet?action=downloadSubmission&submissionId=" + submissionId, '_blank');
                    };
                    document.getElementById('fileAttachmentSection').style.display = 'block';
                } else {
                    document.getElementById('fileAttachmentSection').style.display = 'none';
                }

                // 成绩信息
                document.getElementById('gradeSubmissionId').value = submissionId;

                if (submission.score !== null) {
                    // 显示已有的成绩信息
                    let scoreClass = '';
                    if (submission.score >= 90) {
                        scoreClass = 'high';
                    } else if (submission.score >= 60) {
                        scoreClass = 'medium';
                    } else {
                        scoreClass = 'low';
                    }

                    const gradeCircle = document.getElementById('gradeCircle');
                    gradeCircle.textContent = submission.score;
                    gradeCircle.className = 'grade-circle ' + scoreClass;

                    document.getElementById('gradeScore').textContent = '成绩: ' + submission.score;
                    document.getElementById('gradeFeedback').textContent = '反馈: ' + (submission.feedback || '无反馈');

                    if (submission.gradedBy) {
                        document.getElementById('gradedBy').textContent = '评分人: ' + submission.gradedByName;
                        document.getElementById('gradedBy').style.display = 'block';
                    } else {
                        document.getElementById('gradedBy').style.display = 'none';
                    }

                    if (submission.gradedAt) {
                        const gradedDate = new Date(submission.gradedAt);
                        document.getElementById('gradedAt').textContent = '评分时间: ' + gradedDate.toLocaleString('zh-CN');
                        document.getElementById('gradedAt').style.display = 'block';
                    } else {
                        document.getElementById('gradedAt').style.display = 'none';
                    }

                    document.getElementById('gradeDisplaySection').style.display = 'block';

                    // 填充评分表单以便修改
                    document.getElementById('score').value = submission.score;
                    document.getElementById('feedback').value = submission.feedback || '';

                    // 如果不是要聚焦评分，就隐藏评分表单
                    document.getElementById('gradeFormSection').style.display = focusOnGrading ? 'block' : 'none';
                } else {
                    // 显示评分表单
                    document.getElementById('gradeDisplaySection').style.display = 'none';
                    document.getElementById('gradeFormSection').style.display = 'block';

                    // 清空表单
                    document.getElementById('score').value = '';
                    document.getElementById('feedback').value = '';

                    // 如果要聚焦评分，滚动到评分表单
                    if (focusOnGrading) {
                        setTimeout(function() {
                            document.getElementById('score').focus();
                        }, 100);
                    }
                }
            })
            .catch(error => {
                console.error('Error loading submission details:', error);
                alert('加载提交详情失败: ' + error.message);
            });

        showSubmissionDetailsView();
    }

    // 从详情页操作函数
    function viewSubmissionsFromDetails() {
        viewSubmissions(currentAssignmentId, currentAssignmentTitle);
    }

    function editFromDetails() {
        openEditAssignmentModal(currentAssignmentId);
    }

    function deleteFromDetails() {
        confirmDeleteAssignment(currentAssignmentId, true);
    }

    // 删除确认函数
    function confirmDeleteAssignment(assignmentId, fromDetails = false) {
        if (confirm("确定要删除这个作业吗？此操作将同时删除所有相关的提交记录，且不可恢复！")) {
            fetch('AssignmentServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=deleteAssignment&assignmentId=`+assignmentId
            })
                .then(response => {
                    if (!response.ok) throw new Error('删除失败');
                    return response.text();
                })
                .then(() => {
                    if (fromDetails) {
                        // 如果是从详情页删除，返回列表
                        backToAssignments();
                        setTimeout(() => { location.reload(); }, 500);
                    } else {
                        location.reload();
                    }
                })
                .catch(error => {
                    alert('删除失败: ' + error.message);
                });
        }
    }

    // 添加作业模态框函数
    function openAddAssignmentModal() {
        // 设置默认截止日期为一周后
        const oneWeekLater = new Date();
        oneWeekLater.setDate(oneWeekLater.getDate() + 7);

        // 格式化为本地日期时间字符串，适用于datetime-local输入
        const year = oneWeekLater.getFullYear();
        const month = String(oneWeekLater.getMonth() + 1).padStart(2, '0');
        const day = String(oneWeekLater.getDate()).padStart(2, '0');
        const hours = String(oneWeekLater.getHours()).padStart(2, '0');
        const minutes = String(oneWeekLater.getMinutes()).padStart(2, '0');

        document.getElementById('assignmentDueDate').value = `${year}-${month}-${day}T${hours}:${minutes}`;
        document.getElementById('addAssignmentModal').style.display = 'block';
    }

    function closeAddAssignmentModal() {
        document.getElementById('addAssignmentModal').style.display = 'none';
    }

    // 编辑作业模态框函数
    function openEditAssignmentModal(assignmentId) {
        // 加载作业详情数据
        fetch(`AssignmentServlet?action=getAssignment&assignmentId=`+assignmentId)
            .then(response => response.json())
            .then(assignment => {
                document.getElementById('editAssignmentId').value = assignmentId;
                document.getElementById('editAssignmentTitle').value = assignment.title;
                document.getElementById('editAssignmentCourseId').value = assignment.courseId;
                document.getElementById('editAssignmentDescription').value = assignment.description;

                // 格式化日期时间为input[type="datetime-local"]可接受的格式
                const dueDate = new Date(assignment.dueDate);
                const year = dueDate.getFullYear();
                const month = String(dueDate.getMonth() + 1).padStart(2, '0');
                const day = String(dueDate.getDate()).padStart(2, '0');
                const hours = String(dueDate.getHours()).padStart(2, '0');
                const minutes = String(dueDate.getMinutes()).padStart(2, '0');

                document.getElementById('editAssignmentDueDate').value = `${year}-${month}-${day}T${hours}:${minutes}`;

                document.getElementById('editAssignmentModal').style.display = 'block';
            })
            .catch(error => {
                console.error('Error loading assignment for edit:', error);
                alert('加载作业信息失败');
            });
    }

    function closeEditAssignmentModal() {
        document.getElementById('editAssignmentModal').style.display = 'none';
    }

    // 点击模态框外部关闭模态框
    window.onclick = function(event) {
        if (event.target.className === 'modal') {
            event.target.style.display = 'none';
        }
    }

    // 页面加载完成后初始化
    document.addEventListener('DOMContentLoaded', function() {
        // 默认筛选显示全部作业
        filterAssignments('all');

        // 为评分表单添加提交事件
        document.getElementById('gradeForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const submissionId = document.getElementById('gradeSubmissionId').value;
            const score = document.getElementById('score').value;
            const feedback = document.getElementById('feedback').value;

            fetch('AssignmentSubmissionServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body:  'action=gradeSubmission'
                    + '&submissionId=' + encodeURIComponent(submissionId)
                    + '&score=' + encodeURIComponent(score)
                    + '&feedback=' + encodeURIComponent(feedback)
            })
                .then(response => {
                    if (!response.ok) throw new Error('评分失败');
                    return response.text();
                })
                .then(() => {
                    alert('评分成功！');
                    // 重新加载提交详情
                    viewSubmissionDetails(submissionId);
                })
                .catch(error => {
                    alert('评分失败: ' + error.message);
                });
        });
    });
</script>
</body>
</html>