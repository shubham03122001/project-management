# 🧩 Project Management API

A Spring Boot-based RESTful API for managing users, projects, and tasks with JWT authentication.

---

## 🚀 Features

### 🧑‍💼 User Management
- Register new users
- Login and obtain JWT token
- Secure endpoints using JWT

### 🧰 Project Management
- Create, update, view, and delete projects
- Each user can manage their own projects

### ✅ Task Management
- Add, update, list, and delete tasks under projects
- Filter tasks by `status` or `priority`
- Search tasks by title/description
- Sort by `dueDate` or `priority`

---

## 🗄️ Database Schema (MySQL)

### 🧍 **users**
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary Key |
| user_name | VARCHAR | Username |
| email | VARCHAR | User email |
| password | VARCHAR | Encrypted password |

### 🧱 **projects**
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary Key |
| name | VARCHAR | Project name |
| description | TEXT | Project description |
| user_id | UUID | Foreign key (User) |
| created_at | DATETIME | Created timestamp |
| updated_at | DATETIME | Updated timestamp |

### 📋 **tasks**
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary Key |
| title | VARCHAR | Task title |
| description | TEXT | Task details |
| status | ENUM | (PENDING, IN_PROGRESS, COMPLETED) |
| priority | ENUM | (LOW, MEDIUM, HIGH) |
| due_date | DATE | Task due date |
| project_id | UUID | Foreign key (Project) |
| created_at | DATETIME | Created timestamp |
| updated_at | DATETIME | Updated timestamp |

---

## ⚙️ Setup Instructions

### 1️⃣ Prerequisites
- Java 17 or 21
- Maven
- MySQL running locally (port 3306)

### 2️⃣ Database Setup
Create a database in MySQL:
```sql
CREATE DATABASE projectmanagement;
```

# 📡 Project Management API — Endpoints Overview

This document provides a clear summary of all available API endpoints in the **Project Management API**, including authentication, project, and task management operations.

---

## 🧑 User APIs

| Method | Endpoint | Description |
|---------|-----------|-------------|
| **POST** | `/v1/auth/registeruser` | Register a new user |
| **POST** | `/v1/auth/login` | Login and get JWT token |
| **GET** | `/v1/auth/myprofile` | Get user profile *(JWT required)* |
| **GET** | `/v1/auth/getallusers` | Get list of all registered users  |

---

## 🧱 Project APIs

| Method | Endpoint                              | Description |
|---------|---------------------------------------|-------------|
| **POST** | `/v1/projects`                        | Create a new project |
| **GET** | `/v1/projects/getAllProjects`         | Get all projects |
| **PUT** | `/v1/projects/updateProjectByID/{id}` | Update an existing project |
| **DELETE** | `/v1/projects/deleteProjectByID/{id}` | Delete a specific project |

---

## 📋 Task APIs

| Method | Endpoint | Description |
|---------|-----------|-------------|
| **POST** | `/v1/tasks/project/{projectId}` | Add new task under a specific project |
| **PUT** | `/v1/tasks/{taskId}` | Update an existing task |
| **GET** | `/v1/tasks` | List tasks (supports filters & sorting) |
| **GET** | `/v1/tasks/search?keyword=` | Search tasks by title or description |
| **DELETE** | `/v1/tasks/{taskId}` | Delete a specific task |

---

## 🔍 Query Parameters (Filtering & Sorting)

| Parameter | Example | Description |
|------------|----------|-------------|
| `status` | `/v1/tasks?status=COMPLETED` | Filter tasks by status *(PENDING, IN_PROGRESS, COMPLETED)* |
| `priority` | `/v1/tasks?priority=HIGH` | Filter tasks by priority *(LOW, MEDIUM, HIGH)* |
| `sortBy` | `/v1/tasks?sortBy=dueDate` | Sort tasks by due date or priority |
| `keyword` | `/v1/tasks/search?keyword=API` | Search tasks by title or description |

---

## 🧠 Notes
- All **Project** and **Task** APIs require a valid JWT token.
- Include the token in headers:


