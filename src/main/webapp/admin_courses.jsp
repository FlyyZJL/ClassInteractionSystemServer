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
    <title>课程管理 - 课堂辅助教学平台</title>
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
            background-color: #48bb78;
        }
        .menu-item:hover {
            background-color: rgba(72, 187, 120, 0.5);
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
            color: #48bb78;
        }
        .date {
            color: #718096;
            font-size: 0.9rem;
        }

        /* 课程管理特定样式 */
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
            color: #48bb78;
        }
        .tab.active {
            color: #48bb78;
            border-bottom: 2px solid #48bb78;
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

        .add-btn {
            background-color: #48bb78;
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
            background-color: #38a169;
        }

        .add-btn i {
            margin-right: 0.5rem;
        }

        /* 课程列表样式 */
        .courses-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
            padding: 1.5rem;
        }

        .course-card {
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            overflow: hidden;
            transition: all 0.3s;
            position: relative;
        }

        .course-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.12);
        }

        .course-header {
            background-color: #48bb78;
            padding: 1.5rem;
            color: white;
        }

        .course-header h3 {
            margin: 0;
            font-size: 1.2rem;
        }

        .course-body {
            padding: 1.5rem;
        }

        .course-description {
            color: #718096;
            font-size: 0.9rem;
            margin-bottom: 1rem;
            height: 60px;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .course-teacher {
            display: flex;
            align-items: center;
            margin-bottom: 1rem;
        }

        .teacher-avatar {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 0.75rem;
            font-weight: bold;
            color: #48bb78;
        }

        .teacher-name {
            font-size: 0.9rem;
            color: #4a5568;
        }

        .course-footer {
            padding: 1rem 1.5rem;
            border-top: 1px solid #e9ecef;
            display: flex;
            justify-content: space-between;
        }

        .course-actions {
            display: flex;
            gap: 0.5rem;
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
            color: #3182ce;
            background-color: #ebf8ff;
        }

        .edit-btn:hover {
            color: #48bb78;
            background-color: #e6fffa;
        }

        .delete-btn:hover {
            color: #e53e3e;
            background-color: #fff5f5;
        }

        .course-stats {
            display: flex;
            align-items: center;
            font-size: 0.85rem;
            color: #718096;
        }

        .course-stats i {
            margin-right: 0.3rem;
        }

        .course-stats .dot {
            margin: 0 0.5rem;
            color: #cbd5e0;
        }

        .chapter-count {
            margin-right: 0.5rem;
        }

        /* 章节列表样式 */
        .chapters-table {
            width: 100%;
            border-collapse: collapse;
        }

        .chapters-table th, .chapters-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .chapters-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #4a5568;
        }

        .chapters-table tr:hover {
            background-color: #f8f9fa;
        }

        .chapter-title {
            font-weight: 500;
            color: #2d3748;
        }

        .chapter-content {
            color: #718096;
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

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
            background-color: #48bb78;
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
            background-color: #38a169;
        }

        /* 章节详情样式 */
        .chapter-details {
            padding: 1.5rem;
        }

        .chapter-header {
            margin-bottom: 1.5rem;
        }

        .chapter-header h2 {
            font-size: 1.5rem;
            color: #2d3748;
            margin-bottom: 0.5rem;
        }

        .chapter-meta {
            color: #718096;
            font-size: 0.9rem;
        }

        .chapter-full-content {
            background-color: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
            line-height: 1.6;
            margin-bottom: 1.5rem;
        }

        .chapter-video {
            margin-bottom: 1.5rem;
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
            .courses-container {
                grid-template-columns: 1fr;
            }
            .action-bar {
                flex-direction: column;
                gap: 1rem;
            }
            .modal-content {
                width: 95%;
                margin: 5% auto;
            }
        }

        /* 课程视图和章节视图的显示控制 */
        #coursesView {
            display: block;
        }
        #chaptersView {
            display: none;
        }
        #chapterDetailsView {
            display: none;
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
            <div class="menu-item active" onclick="window.location.href='CourseServlet'">
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
                <i class="fas fa-book"></i> 课程管理
            </div>
            <div class="date" id="current-date"></div>
        </div>

        <!-- 课程视图 -->
        <div id="coursesView">
            <div class="content-container">
                <div class="action-bar">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" id="courseSearchInput" placeholder="搜索课程...">
                    </div>
                    <button class="add-btn" onclick="openAddCourseModal()">
                        <i class="fas fa-plus"></i> 添加课程
                    </button>
                </div>

                <div class="courses-container" id="coursesContainer">
                    <c:forEach var="course" items="${coursesList}">
                        <div class="course-card">
                            <div class="course-header">
                                <h3>${course.courseName}</h3>
                            </div>
                            <div class="course-body">
                                <div class="course-description">${course.description}</div>
                                <div class="course-teacher">
                                    <div class="teacher-avatar">${course.teacherName.charAt(0)}</div>
                                    <span class="teacher-name">教师: ${course.teacherName}</span>
                                </div>
                            </div>
                            <div class="course-footer">
                                <div class="course-stats">
                                    <i class="fas fa-book-open"></i>
                                    <span class="chapter-count">${course.chapterCount}章节</span>
                                    <span class="dot">•</span>
                                    <i class="fas fa-user-graduate"></i>
                                    <span>${course.studentCount}学生</span>
                                </div>
                                <div class="course-actions">
                                    <button class="action-btn view-btn" onclick="viewChapters(${course.courseId}, '${course.courseName}')">
                                        <i class="fas fa-list"></i>
                                    </button>
                                    <button class="action-btn edit-btn" onclick="openEditCourseModal(${course.courseId}, '${course.courseName}', '${course.description}', ${course.teacherId})">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="action-btn delete-btn" onclick="confirmDeleteCourse(${course.courseId}, '${course.courseName}')">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <!-- 章节视图 -->
        <div id="chaptersView">
            <button class="back-btn" onclick="backToCourses()">
                <i class="fas fa-arrow-left"></i> 返回课程列表
            </button>

            <div class="content-container">
                <div class="action-bar">
                    <h2 id="currentCourseName">课程章节</h2>
                    <button class="add-btn" onclick="openAddChapterModal()">
                        <i class="fas fa-plus"></i> 添加章节
                    </button>
                </div>

                <table class="chapters-table">
                    <thead>
                    <tr>
                        <th style="width: 40%">章节标题</th>
                        <th style="width: 40%">内容摘要</th>
                        <th style="width: 20%">操作</th>
                    </tr>
                    </thead>
                    <tbody id="chaptersTableBody">
                    <!-- 章节列表将通过AJAX加载 -->
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 章节详情视图 -->
        <div id="chapterDetailsView">
            <button class="back-btn" onclick="backToChapters()">
                <i class="fas fa-arrow-left"></i> 返回章节列表
            </button>

            <div class="content-container">
                <div class="chapter-details">
                    <div class="chapter-header">
                        <h2 id="detailChapterTitle">章节标题</h2>
                        <div class="chapter-meta">所属课程：<span id="detailCourseName"></span></div>
                    </div>

                    <div class="chapter-full-content" id="detailChapterContent">
                        <!-- 章节内容 -->
                    </div>

                    <div class="chapter-video" id="detailChapterVideo">
                        <!-- 视频将在这里显示 -->
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 添加课程模态框 -->
<div id="addCourseModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">添加新课程</h3>
            <button class="close-btn" onclick="closeAddCourseModal()">&times;</button>
        </div>
        <form id="addCourseForm" action="CourseServlet" method="post">
            <input type="hidden" name="action" value="addCourse">

            <div class="form-group">
                <label for="courseName" class="form-label">课程名称</label>
                <input type="text" id="courseName" name="courseName" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="teacherId" class="form-label">授课教师</label>
                <select id="teacherId" name="teacherId" class="form-select" required>
                    <c:forEach var="teacher" items="${teachersList}">
                        <option value="${teacher.userId}">${teacher.username}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="courseDescription" class="form-label">课程描述</label>
                <textarea id="courseDescription" name="description" class="form-textarea" rows="5"></textarea>
            </div>

            <button type="submit" class="form-submit">添加课程</button>
        </form>
    </div>
</div>

<!-- 编辑课程模态框 -->
<div id="editCourseModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">编辑课程</h3>
            <button class="close-btn" onclick="closeEditCourseModal()">&times;</button>
        </div>
        <form id="editCourseForm" action="CourseServlet" method="post">
            <input type="hidden" name="action" value="updateCourse">
            <input type="hidden" id="editCourseId" name="courseId">

            <div class="form-group">
                <label for="editCourseName" class="form-label">课程名称</label>
                <input type="text" id="editCourseName" name="courseName" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="editTeacherId" class="form-label">授课教师</label>
                <select id="editTeacherId" name="teacherId" class="form-select" required>
                    <c:forEach var="teacher" items="${teachersList}">
                        <option value="${teacher.userId}">${teacher.username}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="editCourseDescription" class="form-label">课程描述</label>
                <textarea id="editCourseDescription" name="description" class="form-textarea" rows="5"></textarea>
            </div>

            <button type="submit" class="form-submit">保存修改</button>
        </form>
    </div>
</div>

<!-- 添加章节模态框 -->
<div id="addChapterModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">添加新章节</h3>
            <button class="close-btn" onclick="closeAddChapterModal()">&times;</button>
        </div>
        <form id="addChapterForm" action="ChapterServlet" method="post">
            <input type="hidden" name="action" value="addChapter">
            <input type="hidden" id="addChapterCourseId" name="courseId">

            <div class="form-group">
                <label for="chapterTitle" class="form-label">章节标题</label>
                <input type="text" id="chapterTitle" name="title" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="chapterContent" class="form-label">章节内容</label>
                <textarea id="chapterContent" name="content" class="form-textarea" rows="10" required></textarea>
            </div>

            <div class="form-group">
                <label for="videoUrl" class="form-label">视频链接 (可选)</label>
                <input type="text" id="videoUrl" name="videoUrl" class="form-input" placeholder="例如: https://www.youtube.com/embed/...">
            </div>

            <button type="submit" class="form-submit">添加章节</button>
        </form>
    </div>
</div>

<!-- 编辑章节模态框 -->
<div id="editChapterModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">编辑章节</h3>
            <button class="close-btn" onclick="closeEditChapterModal()">&times;</button>
        </div>
        <form id="editChapterForm" action="ChapterServlet" method="post">
            <input type="hidden" name="action" value="updateChapter">
            <input type="hidden" id="editChapterId" name="chapterId">
            <input type="hidden" id="editChapterCourseId" name="courseId">

            <div class="form-group">
                <label for="editChapterTitle" class="form-label">章节标题</label>
                <input type="text" id="editChapterTitle" name="title" class="form-input" required>
            </div>

            <div class="form-group">
                <label for="editChapterContent" class="form-label">章节内容</label>
                <textarea id="editChapterContent" name="content" class="form-textarea" rows="10" required></textarea>
            </div>

            <div class="form-group">
                <label for="editVideoUrl" class="form-label">视频链接 (可选)</label>
                <input type="text" id="editVideoUrl" name="videoUrl" class="form-input">
            </div>

            <button type="submit" class="form-submit">保存修改</button>
        </form>
    </div>
</div>

<script>
    // 当前选中的课程ID
    let currentCourseId = null;
    let currentCourseName = "";

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
    function showCoursesView() {
        document.getElementById('coursesView').style.display = 'block';
        document.getElementById('chaptersView').style.display = 'none';
        document.getElementById('chapterDetailsView').style.display = 'none';
    }

    function showChaptersView() {
        document.getElementById('coursesView').style.display = 'none';
        document.getElementById('chaptersView').style.display = 'block';
        document.getElementById('chapterDetailsView').style.display = 'none';
    }

    function showChapterDetailsView() {
        document.getElementById('coursesView').style.display = 'none';
        document.getElementById('chaptersView').style.display = 'none';
        document.getElementById('chapterDetailsView').style.display = 'block';
    }

    // 返回按钮函数
    function backToCourses() {
        showCoursesView();
    }

    function backToChapters() {
        showChaptersView();
    }

    // 查看课程章节
    function viewChapters(courseId, courseName) {
        // 确保courseId有效
        if (!courseId || isNaN(courseId)) {
            alert("无效的课程ID");
            return;
        }



        currentCourseId = courseId;
        currentCourseName = courseName;
        document.getElementById('currentCourseName').textContent = courseName + " - 章节列表";
        document.getElementById('addChapterCourseId').value = courseId;

        console.log("正在加载课程ID为 " + courseId + " 的章节"); // 调试日志

        const url = "ChapterServlet?action=getChapters&courseId=" + courseId;
        console.log("请求URL:", url); // 调试日志，检查完整URL

        // 加载章节数据
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.error || "加载章节失败") });
                }
                return response.json();
            })
            .then(chapters => {
                const tbody = document.getElementById('chaptersTableBody');
                tbody.innerHTML = '';

                if (chapters.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="3" style="text-align: center;">暂无章节内容</td></tr>`;
                    return;
                }

                chapters.forEach(chapter => {
                    const contentPreview = chapter.content.length > 100 ?
                        chapter.content.substring(0, 100) + '...' : chapter.content;

                    // 创建行元素
                    const tr = document.createElement('tr');

                    // 章节标题单元格
                    const tdTitle = document.createElement('td');
                    tdTitle.className = 'chapter-title';
                    tdTitle.textContent = chapter.title;
                    tr.appendChild(tdTitle);

                    // 章节内容单元格
                    const tdContent = document.createElement('td');
                    tdContent.className = 'chapter-content';
                    tdContent.textContent = contentPreview;
                    tr.appendChild(tdContent);

                    // 操作按钮单元格
                    const tdActions = document.createElement('td');

                    // 查看按钮
                    const viewBtn = document.createElement('button');
                    viewBtn.className = 'action-btn view-btn';
                    viewBtn.innerHTML = '<i class="fas fa-eye"></i>';
                    viewBtn.onclick = function() {
                        viewChapterDetails(chapter.id, chapter.title, courseName, chapter.content, chapter.videoUrl || '');
                    };
                    tdActions.appendChild(viewBtn);

                    // 编辑按钮
                    const editBtn = document.createElement('button');
                    editBtn.className = 'action-btn edit-btn';
                    editBtn.innerHTML = '<i class="fas fa-edit"></i>';
                    editBtn.onclick = function() {
                        let safeContent = chapter.content.replace(/'/g, "\\'");
                        openEditChapterModal(chapter.id, courseId, chapter.title, safeContent, chapter.videoUrl || '');
                    };
                    tdActions.appendChild(editBtn);

                    // 删除按钮
                    const deleteBtn = document.createElement('button');
                    deleteBtn.className = 'action-btn delete-btn';
                    deleteBtn.innerHTML = '<i class="fas fa-trash"></i>';
                    deleteBtn.onclick = function() {
                        confirmDeleteChapter(chapter.id, chapter.title, courseId);
                    };
                    tdActions.appendChild(deleteBtn);

                    tr.appendChild(tdActions);

                    // 将行添加到表格
                    tbody.appendChild(tr);
                });
            })
            .catch(error => {
                console.error('Error loading chapters:', error);
                alert('加载章节数据失败');
            });

        showChaptersView();
    }

    function viewChapterDetails(chapterId, title, courseName, content, videoUrl) {
        document.getElementById('detailChapterTitle').textContent = title;
        document.getElementById('detailCourseName').textContent = courseName;
        document.getElementById('detailChapterContent').textContent = content;

        const videoContainer = document.getElementById('detailChapterVideo');
        if (videoUrl && videoUrl.trim() !== '') {
            videoContainer.innerHTML = '';
            const videoWrapper = document.createElement('div');
            videoWrapper.style.position = 'relative';
            videoWrapper.style.paddingBottom = '56.25%';
            videoWrapper.style.height = '0';
            videoWrapper.style.overflow = 'hidden';
            videoWrapper.style.maxWidth = '100%';

            const iframe = document.createElement('iframe');
            iframe.style.position = 'absolute';
            iframe.style.top = '0';
            iframe.style.left = '0';
            iframe.style.width = '100%';
            iframe.style.height = '100%';
            iframe.style.border = '0';
            iframe.setAttribute('src', videoUrl);
            iframe.setAttribute('allowfullscreen', '');

            videoWrapper.appendChild(iframe);
            videoContainer.appendChild(videoWrapper);
            videoContainer.style.display = 'block';
        } else {
            videoContainer.innerHTML = '<p class="text-muted">此章节没有配置视频</p>';
            videoContainer.style.display = 'block';
        }

        showChapterDetailsView();
    }
    // 课程模态框函数
    function openAddCourseModal() {
        document.getElementById('addCourseModal').style.display = 'block';
    }

    function closeAddCourseModal() {
        document.getElementById('addCourseModal').style.display = 'none';
    }

    function openEditCourseModal(courseId, courseName, description, teacherId) {
        document.getElementById('editCourseId').value = courseId;
        document.getElementById('editCourseName').value = courseName;
        document.getElementById('editCourseDescription').value = description;
        document.getElementById('editTeacherId').value = teacherId;
        document.getElementById('editCourseModal').style.display = 'block';
    }

    function closeEditCourseModal() {
        document.getElementById('editCourseModal').style.display = 'none';
    }

    // 章节模态框函数
    function openAddChapterModal() {
        document.getElementById('addChapterModal').style.display = 'block';
    }

    function closeAddChapterModal() {
        document.getElementById('addChapterModal').style.display = 'none';
    }

    function openEditChapterModal(chapterId, courseId, title, content, videoUrl) {
        document.getElementById('editChapterId').value = chapterId;
        document.getElementById('editChapterCourseId').value = courseId;
        document.getElementById('editChapterTitle').value = title;
        document.getElementById('editChapterContent').value = content;
        document.getElementById('editVideoUrl').value = videoUrl;
        document.getElementById('editChapterModal').style.display = 'block';
    }
    function closeEditChapterModal() {
        document.getElementById('editChapterModal').style.display = 'none';
    }

    // 删除确认函数
    function confirmDeleteCourse(courseId, courseName) {
        if (confirm(`确定要删除课程"${courseName}"吗？此操作将删除该课程的所有章节，且不可恢复！`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'CourseServlet';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'deleteCourse';

            const courseIdInput = document.createElement('input');
            courseIdInput.type = 'hidden';
            courseIdInput.name = 'courseId';
            courseIdInput.value = courseId;

            form.appendChild(actionInput);
            form.appendChild(courseIdInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    function confirmDeleteChapter(chapterId, chapterTitle, courseId) {
        if (confirm(`确定要删除章节"${chapterTitle}"吗？此操作不可恢复！`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'ChapterServlet';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'deleteChapter';

            const chapterIdInput = document.createElement('input');
            chapterIdInput.type = 'hidden';
            chapterIdInput.name = 'chapterId';
            chapterIdInput.value = chapterId;

            const courseIdInput = document.createElement('input');
            courseIdInput.type = 'hidden';
            courseIdInput.name = 'courseId';
            courseIdInput.value = courseId;

            form.appendChild(actionInput);
            form.appendChild(chapterIdInput);
            form.appendChild(courseIdInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    // 搜索课程
    document.getElementById('courseSearchInput').addEventListener('input', function() {
        const searchQuery = this.value.toLowerCase();
        const courseCards = document.querySelectorAll('.course-card');

        courseCards.forEach(card => {
            const courseName = card.querySelector('.course-header h3').textContent.toLowerCase();
            const description = card.querySelector('.course-description').textContent.toLowerCase();
            const teacher = card.querySelector('.teacher-name').textContent.toLowerCase();

            if (courseName.includes(searchQuery) ||
                description.includes(searchQuery) ||
                teacher.includes(searchQuery)) {
                card.style.display = '';
            } else {
                card.style.display = 'none';
            }
        });
    });

    // 点击模态框外部关闭模态框
    window.onclick = function(event) {
        if (event.target.className === 'modal') {
            event.target.style.display = 'none';
        }
    }
</script>
</body>
</html>