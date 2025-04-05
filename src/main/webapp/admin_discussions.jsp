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
    <title>讨论区管理 - 课堂辅助教学平台</title>
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
            background-color: #ed8936;
        }
        .menu-item:hover {
            background-color: rgba(237, 137, 54, 0.5);
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
            color: #ed8936;
        }
        .date {
            color: #718096;
            font-size: 0.9rem;
        }

        /* 讨论区管理特定样式 */
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
            color: #ed8936;
        }
        .tab.active {
            color: #ed8936;
            border-bottom: 2px solid #ed8936;
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
            background-color: #ed8936;
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
            background-color: #dd6b20;
        }

        .add-btn i {
            margin-right: 0.5rem;
        }

        /* 讨论列表样式 */
        .discussions-table {
            width: 100%;
            border-collapse: collapse;
        }

        .discussions-table th, .discussions-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .discussions-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #4a5568;
        }

        .discussions-table tr:hover {
            background-color: #f8f9fa;
        }

        .discussion-title {
            font-weight: 500;
            color: #2d3748;
        }

        .discussion-title a {
            color: #4a5568;
            text-decoration: none;
        }

        .discussion-title a:hover {
            color: #ed8936;
        }

        .pinned-tag {
            display: inline-block;
            background-color: #fed7aa;
            color: #9c4221;
            padding: 0.2rem 0.5rem;
            border-radius: 4px;
            font-size: 0.8rem;
            margin-right: 0.5rem;
            font-weight: normal;
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

        .pin-btn:hover {
            color: #ed8936;
            background-color: #fffaf0;
        }

        .unpin-btn {
            color: #ed8936;
        }

        .unpin-btn:hover {
            color: #dd6b20;
            background-color: #fffaf0;
        }

        .edit-btn:hover {
            color: #3182ce;
            background-color: #ebf8ff;
        }

        .delete-btn:hover {
            color: #e53e3e;
            background-color: #fff5f5;
        }

        .view-btn:hover {
            color: #2f855a;
            background-color: #f0fff4;
        }

        .status-indicator {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            display: inline-block;
            margin-right: 6px;
        }

        .status-active {
            background-color: #48bb78;
        }

        .status-deleted {
            background-color: #e53e3e;
        }

        .user-info {
            display: flex;
            align-items: center;
        }

        .user-avatar {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 0.5rem;
            font-weight: bold;
            color: #4a5568;
        }

        /* 回复列表样式 */
        .replies-container {
            margin-top: 20px;
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 1rem;
        }

        .reply-item {
            padding: 1rem;
            border-bottom: 1px solid #e2e8f0;
        }

        .reply-item:last-child {
            border-bottom: none;
        }

        .reply-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }

        .reply-user {
            font-weight: 500;
            color: #4a5568;
        }

        .reply-time {
            color: #718096;
            font-size: 0.85rem;
        }

        .reply-content {
            color: #4a5568;
            line-height: 1.5;
        }

        .reply-actions {
            margin-top: 0.5rem;
            display: flex;
            justify-content: flex-end;
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
            background-color: #ed8936;
            color: white;
            border-color: #ed8936;
        }

        /* 详情视图样式 */
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

        .discussion-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid #e2e8f0;
        }

        .discussion-info h2 {
            font-size: 1.5rem;
            margin-bottom: 0.5rem;
            color: #2d3748;
        }

        .discussion-meta {
            color: #718096;
            font-size: 0.9rem;
            display: flex;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .discussion-meta span {
            display: flex;
            align-items: center;
        }

        .discussion-meta i {
            margin-right: 0.3rem;
        }

        .discussion-actions {
            display: flex;
            gap: 0.5rem;
        }

        .discussion-content {
            line-height: 1.6;
            color: #4a5568;
            margin-bottom: 2rem;
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

        .form-group {
            margin-bottom: 1rem;
        }

        .form-label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: #4a5568;
        }

        .form-input, .form-textarea, .form-select {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            font-size: 0.9rem;
            font-family: inherit;
        }

        .form-textarea {
            min-height: 150px;
            resize: vertical;
        }

        .form-submit {
            background-color: #ed8936;
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
            background-color: #dd6b20;
        }

        /* 讨论视图和回复视图的显示控制 */
        #discussionsView {
            display: block;
        }
        #discussionDetailsView {
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
            .discussions-table th:nth-child(2),
            .discussions-table td:nth-child(2),
            .discussions-table th:nth-child(4),
            .discussions-table td:nth-child(4) {
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
            <div class="menu-item active" onclick="window.location.href='DiscussionServlet'">
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
                <i class="fas fa-comments"></i> 讨论区管理
            </div>
            <div class="date" id="current-date"></div>
        </div>

        <!-- 讨论列表视图 -->
        <div id="discussionsView">
            <div class="tabs">
                <div class="tab active" onclick="filterDiscussions('all')">全部讨论</div>
                <div class="tab" onclick="filterDiscussions('pinned')">置顶讨论</div>
                <div class="tab" onclick="filterDiscussions('active')">活跃讨论</div>
                <div class="tab" onclick="filterDiscussions('deleted')">已删除讨论</div>
            </div>

            <div class="content-container">
                <div class="action-bar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" id="discussionSearchInput" placeholder="搜索讨论主题...">
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
                        <button class="add-btn" onclick="openAddDiscussionModal()">
                            <i class="fas fa-plus"></i> 添加讨论
                        </button>
                    </div>
                </div>

                <table class="discussions-table" id="discussionsTable">
                    <thead>
                    <tr>
                        <th style="width: 45%">讨论主题</th>
                        <th style="width: 15%">所属课程</th>
                        <th style="width: 15%">发布者</th>
                        <th style="width: 15%">发布时间</th>
                        <th style="width: 10%">操作</th>
                    </tr>
                    </thead>
                    <tbody id="discussionsTableBody">
                    <c:forEach var="discussion" items="${discussionsList}">
                        <tr class="${discussion.isDeleted ? 'deleted' : ''} ${discussion.isPinned ? 'pinned' : ''}" data-course-id="${discussion.courseId}">
                            <td class="discussion-title">
                                <c:if test="${discussion.isPinned}">
                                    <span class="pinned-tag">置顶</span>
                                </c:if>
                                <a href="#" onclick="viewDiscussionDetails(${discussion.discussionId})">${discussion.title}</a>
                            </td>
                            <td>
                                <span class="course-tag">${discussion.courseName}</span>
                            </td>
                            <td>
                                <div class="user-info">
                                    <div class="user-avatar">${discussion.username.charAt(0)}</div>
                                        ${discussion.username}
                                </div>
                            </td>
                            <td>
                                <fmt:formatDate value="${discussion.createdAt}" pattern="yyyy-MM-dd HH:mm" />
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <c:choose>
                                        <c:when test="${discussion.isPinned}">
                                            <button class="action-btn unpin-btn" onclick="unpinDiscussion(${discussion.discussionId})">
                                                <i class="fas fa-thumbtack"></i>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="action-btn pin-btn" onclick="pinDiscussion(${discussion.discussionId})">
                                                <i class="fas fa-thumbtack"></i>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    <button class="action-btn view-btn" onclick="viewDiscussionDetails(${discussion.discussionId})">
                                        <i class="fas fa-eye"></i>
                                    </button>
                                    <c:if test="${!discussion.isDeleted}">
                                        <button class="action-btn edit-btn" onclick="openEditDiscussionModal(${discussion.discussionId})">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="action-btn delete-btn" onclick="confirmDeleteDiscussion(${discussion.discussionId})">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </c:if>
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

        <!-- 讨论详情视图 -->
        <div id="discussionDetailsView">
            <button class="back-btn" onclick="backToDiscussions()">
                <i class="fas fa-arrow-left"></i> 返回讨论列表
            </button>

            <div class="content-container">
                <div class="discussion-header">
                    <div class="discussion-info">
                        <h2 id="detailDiscussionTitle">讨论主题</h2>
                        <div class="discussion-meta">
                            <span id="detailDiscussionCourse"><i class="fas fa-book"></i> 课程</span>
                            <span id="detailDiscussionAuthor"><i class="fas fa-user"></i> 作者</span>
                            <span id="detailDiscussionTime"><i class="fas fa-clock"></i> 发布时间</span>
                        </div>
                    </div>
                    <div class="discussion-actions">
                        <button id="detailPinBtn" class="action-btn pin-btn" onclick="pinFromDetails()">
                            <i class="fas fa-thumbtack"></i>
                        </button>
                        <button id="detailEditBtn" class="action-btn edit-btn" onclick="editFromDetails()">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button id="detailDeleteBtn" class="action-btn delete-btn" onclick="deleteFromDetails()">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>

                <div class="discussion-content" id="detailDiscussionContent">
                    <!-- 讨论内容将在这里显示 -->
                </div>

                <h3 style="margin-bottom: 1rem; color: #4a5568;">回复列表</h3>

                <div class="replies-container" id="repliesContainer">
                    <!-- 回复将在这里显示 -->
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 添加讨论模态框 -->
<div id="addDiscussionModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">添加新讨论</h3>
            <button class="close-btn" onclick="closeAddDiscussionModal()">&times;</button>
        </div>
        <form id="addDiscussionForm" action="DiscussionServlet" method="post">
            <input type="hidden" name="action" value="addDiscussion">

            <div class="form-group">
                <label for="discussionTitle" class="form-label">讨论主题</label>
                <input type="text" id="discussionTitle" name="title" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="discussionCourseId" class="form-label">所属课程</label>
                <select id="discussionCourseId" name="courseId" class="form-select" required>
                    <c:forEach var="course" items="${coursesList}">
                        <option value="${course.courseId}">${course.courseName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="discussionContent" class="form-label">讨论内容</label>
                <textarea id="discussionContent" name="content" class="form-textarea" rows="10" required></textarea>
            </div>

            <div class="form-group" style="display: flex; align-items: center;">
                <input type="checkbox" id="isPinned" name="isPinned" style="margin-right: 10px;">
                <label for="isPinned">设为置顶</label>
            </div>

            <button type="submit" class="form-submit">添加讨论</button>
        </form>
    </div>
</div>

<!-- 编辑讨论模态框 -->
<div id="editDiscussionModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">编辑讨论</h3>
            <button class="close-btn" onclick="closeEditDiscussionModal()">&times;</button>
        </div>
        <form id="editDiscussionForm" action="DiscussionServlet" method="post">
            <input type="hidden" name="action" value="updateDiscussion">
            <input type="hidden" id="editDiscussionId" name="discussionId">

            <div class="form-group">
                <label for="editDiscussionTitle" class="form-label">讨论主题</label>
                <input type="text" id="editDiscussionTitle" name="title" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="editDiscussionCourseId" class="form-label">所属课程</label>
                <select id="editDiscussionCourseId" name="courseId" class="form-select" required>
                    <c:forEach var="course" items="${coursesList}">
                        <option value="${course.courseId}">${course.courseName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="editDiscussionContent" class="form-label">讨论内容</label>
                <textarea id="editDiscussionContent" name="content" class="form-textarea" rows="10" required></textarea>
            </div>

            <div class="form-group" style="display: flex; align-items: center;">
                <input type="checkbox" id="editIsPinned" name="isPinned" style="margin-right: 10px;">
                <label for="editIsPinned">设为置顶</label>
            </div>

            <button type="submit" class="form-submit">保存修改</button>
        </form>
    </div>
</div>

<script>
    // 当前选中的讨论ID
    let currentDiscussionId = null;
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

    // 切换视图函数
    function showDiscussionsView() {
        document.getElementById('discussionsView').style.display = 'block';
        document.getElementById('discussionDetailsView').style.display = 'none';
    }

    function showDiscussionDetailsView() {
        document.getElementById('discussionsView').style.display = 'none';
        document.getElementById('discussionDetailsView').style.display = 'block';
    }

    // 返回按钮函数
    function backToDiscussions() {
        showDiscussionsView();
    }

    // 筛选讨论函数
    function filterDiscussions(filter) {
        currentFilter = filter;

        // 更新标签激活状态
        document.querySelectorAll('.tab').forEach(tab => {
            tab.classList.remove('active');
        });
        event.target.classList.add('active');

        // 应用筛选
        const rows = document.querySelectorAll('#discussionsTableBody tr');

        rows.forEach(row => {
            // 优先应用课程筛选
            const courseId = row.getAttribute('data-course-id');
            const courseVisible = currentCourseFilter === 'all' || courseId === currentCourseFilter;

            // 然后应用状态筛选
            let statusVisible = true;
            if (filter === 'pinned') {
                statusVisible = row.classList.contains('pinned');
            } else if (filter === 'active') {
                statusVisible = !row.classList.contains('deleted');
            } else if (filter === 'deleted') {
                statusVisible = row.classList.contains('deleted');
            }

            // 最终显示状态
            row.style.display = (courseVisible && statusVisible) ? '' : 'none';
        });
    }

    // 课程筛选函数
    function filterByCourse(courseId) {
        currentCourseFilter = courseId;

        // 应用筛选
        const rows = document.querySelectorAll('#discussionsTableBody tr');

        rows.forEach(row => {
            const rowCourseId = row.getAttribute('data-course-id');

            // 先检查课程筛选
            const courseVisible = courseId === 'all' || rowCourseId == courseId;

            // 然后应用状态筛选
            let statusVisible = true;
            if (currentFilter === 'pinned') {
                statusVisible = row.classList.contains('pinned');
            } else if (currentFilter === 'active') {
                statusVisible = !row.classList.contains('deleted');
            } else if (currentFilter === 'deleted') {
                statusVisible = row.classList.contains('deleted');
            }

            // 最终显示状态
            row.style.display = (courseVisible && statusVisible) ? '' : 'none';
        });
    }

    // 搜索讨论函数
    document.getElementById('discussionSearchInput').addEventListener('input', function() {
        const searchQuery = this.value.toLowerCase();
        const rows = document.querySelectorAll('#discussionsTableBody tr');

        rows.forEach(row => {
            const title = row.querySelector('.discussion-title').textContent.toLowerCase();
            const courseId = row.getAttribute('data-course-id');

            // 应用搜索过滤
            const searchMatch = title.includes(searchQuery);

            // 应用课程筛选
            const courseVisible = currentCourseFilter === 'all' || courseId == currentCourseFilter;

            // 应用状态筛选
            let statusVisible = true;
            if (currentFilter === 'pinned') {
                statusVisible = row.classList.contains('pinned');
            } else if (currentFilter === 'active') {
                statusVisible = !row.classList.contains('deleted');
            } else if (currentFilter === 'deleted') {
                statusVisible = row.classList.contains('deleted');
            }

            // 最终显示状态
            row.style.display = (searchMatch && courseVisible && statusVisible) ? '' : 'none';
        });
    });

    // 查看讨论详情
    function viewDiscussionDetails(discussionId) {
        currentDiscussionId = discussionId;

        const url = "DiscussionServlet?action=getDiscussion&discussionId=" + discussionId;
        console.log("请求URL:", url); // 调试日志，检查完整URL
        // 加载讨论详情
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.error || "加载讨论详情失败") });
                }
                return response.json();
            })
            .then(discussion => {
                // 填充讨论详情
                document.getElementById('detailDiscussionTitle').textContent = discussion.title;
                document.getElementById('detailDiscussionCourse').innerHTML = `<i class="fas fa-book"></i> ${discussion.courseName}`;
                document.getElementById('detailDiscussionAuthor').innerHTML = `<i class="fas fa-user"></i> ${discussion.username}`;

                const createdAt = new Date(discussion.createdAt);
                const formattedDate = createdAt.toLocaleString('zh-CN');
                document.getElementById('detailDiscussionTime').innerHTML = `<i class="fas fa-clock"></i> ${formattedDate}`;

                document.getElementById('detailDiscussionContent').textContent = discussion.content;

                // 设置置顶按钮状态
                const pinBtn = document.getElementById('detailPinBtn');
                if (discussion.isPinned) {
                    pinBtn.className = 'action-btn unpin-btn';
                    pinBtn.onclick = function() { unpinDiscussion(discussionId, true); };
                } else {
                    pinBtn.className = 'action-btn pin-btn';
                    pinBtn.onclick = function() { pinDiscussion(discussionId, true); };
                }

                // 设置是否显示编辑和删除按钮
                const editBtn = document.getElementById('detailEditBtn');
                const deleteBtn = document.getElementById('detailDeleteBtn');

                if (discussion.isDeleted) {
                    editBtn.style.display = 'none';
                    deleteBtn.style.display = 'none';
                } else {
                    editBtn.style.display = '';
                    deleteBtn.style.display = '';
                }
                const url2 = "DiscussionServlet?action=getReplies&discussionId=" + discussionId;
                console.log("请求URL:", url2); // 调试日志，检查完整URL
                // 加载讨论回复
                fetch(url2)
                    .then(response => response.json())
                    .then(replies => {
                        const repliesContainer = document.getElementById('repliesContainer');
                        repliesContainer.innerHTML = '';

                        if (replies.length === 0) {
                            repliesContainer.innerHTML = '<p style="text-align: center; padding: 1rem;">暂无回复</p>';
                            return;
                        }

                        replies.forEach(reply => {
                            const replyDiv = document.createElement('div');
                            replyDiv.className = 'reply-item' + (reply.isDeleted ? ' deleted' : '');

                            const replyHeader = document.createElement('div');
                            replyHeader.className = 'reply-header';

                            const replyUser = document.createElement('span');
                            replyUser.className = 'reply-user';
                            replyUser.textContent = reply.username;

                            const replyTime = document.createElement('span');
                            replyTime.className = 'reply-time';
                            const replyDate = new Date(reply.createdAt);
                            replyTime.textContent = replyDate.toLocaleString('zh-CN');

                            replyHeader.appendChild(replyUser);
                            replyHeader.appendChild(replyTime);

                            const replyContent = document.createElement('div');
                            replyContent.className = 'reply-content';
                            replyContent.textContent = reply.content;

                            const replyActions = document.createElement('div');
                            replyActions.className = 'reply-actions';

                            if (!reply.isDeleted) {
                                const deleteButton = document.createElement('button');
                                deleteButton.className = 'action-btn delete-btn';
                                deleteButton.innerHTML = '<i class="fas fa-trash"></i>';
                                deleteButton.onclick = function() {
                                    confirmDeleteReply(reply.replyId, discussionId);
                                };
                                replyActions.appendChild(deleteButton);
                            } else {
                                replyContent.textContent = "[此回复已删除]";
                                replyContent.style.fontStyle = "italic";
                                replyContent.style.color = "#a0aec0";
                            }

                            replyDiv.appendChild(replyHeader);
                            replyDiv.appendChild(replyContent);
                            replyDiv.appendChild(replyActions);

                            repliesContainer.appendChild(replyDiv);
                        });
                    })
                    .catch(error => {
                        console.error('Error loading replies:', error);
                        document.getElementById('repliesContainer').innerHTML = '<p style="text-align: center; color: red; padding: 1rem;">加载回复失败</p>';
                    });
            })
            .catch(error => {
                console.error('Error loading discussion details:', error);
                alert('加载讨论详情失败: ' + error.message);
            });

        showDiscussionDetailsView();
    }

    // 置顶讨论
    function pinDiscussion(discussionId, fromDetails = false) {
        fetch('DiscussionServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `action=pinDiscussion&discussionId=`+discussionId
        })
            .then(response => {
                if (!response.ok) throw new Error('操作失败');
                return response.text();
            })
            .then(() => {
                if (fromDetails) {
                    // 如果是从详情页操作，刷新详情
                    viewDiscussionDetails(discussionId);
                } else {
                    // 否则刷新列表
                    location.reload();
                }
            })
            .catch(error => {
                alert('置顶失败: ' + error.message);
            });
    }

    // 取消置顶讨论
    function unpinDiscussion(discussionId, fromDetails = false) {
        fetch('DiscussionServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `action=unpinDiscussion&discussionId=`+discussionId
        })
            .then(response => {
                if (!response.ok) throw new Error('操作失败');
                return response.text();
            })
            .then(() => {
                if (fromDetails) {
                    // 如果是从详情页操作，刷新详情
                    viewDiscussionDetails(discussionId);
                } else {
                    // 否则刷新列表
                    location.reload();
                }
            })
            .catch(error => {
                alert('取消置顶失败: ' + error.message);
            });
    }

    // 从详情页操作函数
    function pinFromDetails() {
        pinDiscussion(currentDiscussionId, true);
    }

    function editFromDetails() {
        openEditDiscussionModal(currentDiscussionId);
    }

    function deleteFromDetails() {
        confirmDeleteDiscussion(currentDiscussionId, true);
    }

    // 删除确认函数
    function confirmDeleteDiscussion(discussionId, fromDetails = false) {
        if (confirm("确定要删除这个讨论吗？此操作不可恢复！")) {
            fetch('DiscussionServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=deleteDiscussion&discussionId=`+discussionId
            })
                .then(response => {
                    if (!response.ok) throw new Error('删除失败');
                    return response.text();
                })
                .then(() => {
                    if (fromDetails) {
                        // 如果是从详情页删除，返回列表
                        backToDiscussions();
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

    // 删除回复确认函数
    function confirmDeleteReply(replyId, discussionId) {
        if (confirm("确定要删除这条回复吗？此操作不可恢复！")) {
            fetch('DiscussionServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=deleteReply&replyId=`+replyId
            })
                .then(response => {
                    if (!response.ok) throw new Error('删除失败');
                    return response.text();
                })
                .then(() => {
                    // 刷新回复列表
                    viewDiscussionDetails(discussionId);
                })
                .catch(error => {
                    alert('删除回复失败: ' + error.message);
                });
        }
    }

    // 添加讨论模态框函数
    function openAddDiscussionModal() {
        document.getElementById('addDiscussionModal').style.display = 'block';
    }

    function closeAddDiscussionModal() {
        document.getElementById('addDiscussionModal').style.display = 'none';
    }

    // 编辑讨论模态框函数
    function openEditDiscussionModal(discussionId) {

        const url3 = "DiscussionServlet?action=getDiscussion&discussionId=" + discussionId;
        console.log("请求URL:", url3); // 调试日志，检查完整URL
        // 加载讨论详情数据
        fetch(url3)
            .then(response => response.json())
            .then(discussion => {
                document.getElementById('editDiscussionId').value = discussionId;
                document.getElementById('editDiscussionTitle').value = discussion.title;
                document.getElementById('editDiscussionCourseId').value = discussion.courseId;
                document.getElementById('editDiscussionContent').value = discussion.content;
                document.getElementById('editIsPinned').checked = discussion.isPinned;

                document.getElementById('editDiscussionModal').style.display = 'block';
            })
            .catch(error => {
                console.error('Error loading discussion for edit:', error);
                alert('加载讨论信息失败');
            });
    }

    function closeEditDiscussionModal() {
        document.getElementById('editDiscussionModal').style.display = 'none';
    }

    // 点击模态框外部关闭模态框
    window.onclick = function(event) {
        if (event.target.className === 'modal') {
            event.target.style.display = 'none';
        }
    }

    // 页面加载完成后初始化
    document.addEventListener('DOMContentLoaded', function() {
        // 默认筛选显示活跃讨论
        filterDiscussions('all');
    });
</script>
</body>
</html>