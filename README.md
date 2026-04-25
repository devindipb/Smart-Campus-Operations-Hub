# Smart Campus Operations Hub - Module A

This repository contains a complete implementation of **Module A - Facilities & Assets Catalogue** for the IT3030 PAF Assignment 2026.

## Tech Stack

- Backend: Spring Boot 3, Spring Data JPA, Spring Validation, Spring Security
- Database: H2 file database
- Frontend: React 18 + Vite
- CI: GitHub Actions

## Project Structure

```text
.
|-- backend
|   |-- src/main/java/com/sliit/facilitiescatalogue
|   |-- src/main/resources
|   |-- src/test/java/com/sliit/facilitiescatalogue
|   `-- pom.xml
|-- frontend
|   |-- src
|   `-- package.json
`-- .github/workflows
```

## Module A Scope

This module supports:

- Create, read, update, and delete bookable resources
- Search and filtering by type, capacity, location, status, and active flag
- Resource metadata management
- Availability window management
- Validation and meaningful error responses
- Role-based endpoint protection for `USER` and `ADMIN`

## Default Login Accounts

Backend uses HTTP Basic authentication for local development:

- Admin: `admin@sliit.lk` / `Admin@123`
- User: `user@sliit.lk` / `User@123`

These are for local module development only. In the full group project, you can replace this with the required OAuth 2.0 login flow.

## How To Run

### Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`

H2 console: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:file:~/facilities-catalogue-db/facilitiesdb`
- Username: `sa`
- Password: `password`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`

## Main Endpoints

- `GET /api/resources`
- `GET /api/resources/{id}`
- `POST /api/resources`
- `PUT /api/resources/{id}`
- `PATCH /api/resources/{id}/status`
- `DELETE /api/resources/{id}`
- `GET /api/auth/me`

## Filter Example

```http
GET /api/resources?type=LAB&minCapacity=40&location=Malabe&status=ACTIVE
```

## Notes For Your Report

- This repository is focused on **your individual contribution**: Module A
- The security here is intentionally lightweight for local execution
- For final team submission, integrate this module with the group OAuth 2.0 implementation and shared user model
- The frontend now hides admin-only actions for non-admin users based on the authenticated role
