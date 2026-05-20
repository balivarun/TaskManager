# Team Task Manager

Spring Boot full-stack task manager with JWT authentication, role-based access, project/team management, task assignment, and progress tracking.

## Stack

- Java 21
- Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA
- H2 for local development
- PostgreSQL-ready configuration for Render or Railway
- Static HTML/CSS/JS frontend served by Spring Boot

## Features

- Signup and login
- Admin and Member roles
- Project creation and team assignment
- Task creation, assignment, and status tracking
- Dashboard with totals, status breakdown, and overdue tasks
- REST APIs with validation and relational data model

## Local Run

```bash
./gradlew bootRun
```

Open `http://localhost:8080`

Demo users:

- `admin@taskmanager.com` / `admin123`
- `member@taskmanager.com` / `member123`

## Tests

```bash
./gradlew test
```

## Render Deployment

This repo includes a Render-ready `Dockerfile`.

1. Create a new **Web Service** on Render from this repo.
2. Choose **Docker** as the runtime.
3. Add a PostgreSQL database on Render or use any external Postgres instance.
4. Set these environment variables on the service:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<port>/<database>
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
APP_JWT_SECRET=<long-random-secret>
```

5. Deploy.

Render injects `PORT` automatically. The app listens on that port and falls back to H2 locally when datasource variables are not set.

## Main API Endpoints

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/dashboard`
- `GET /api/projects`
- `POST /api/projects`
- `POST /api/projects/{projectId}/members/{userId}`
- `GET /api/tasks`
- `POST /api/tasks`
- `PATCH /api/tasks/{taskId}/status`
