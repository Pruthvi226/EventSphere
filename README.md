# EventSphere

![Java 17](https://img.shields.io/badge/Java-17-blue)
![Spring Boot 3.2](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
[![CI](https://github.com/Pruthvi226/EventSphere/actions/workflows/ci.yml/badge.svg)](https://github.com/Pruthvi226/EventSphere/actions/workflows/ci.yml)
![License](https://img.shields.io/badge/License-Not%20specified-lightgrey)

EventSphere is a college event management web app that helps students, organizers, volunteers, and admins manage events, registrations, attendance, feedback, and certificates in one place.

## 1. Project Title & Tagline

**EventSphere - College Event Management Platform**

A role-based event operations system for colleges, built with Spring Boot (a Java framework for building production-ready web apps) and Thymeleaf (a server-side template engine that renders HTML pages).

## 2. Live Demo / Video / Screenshots

**Local Docker Demo**

```text
http://localhost:28080
```

**Local Maven Demo**

```text
http://localhost:8080/eventsphere
```

**Demo Preview**

[![EventSphere Demo Walkthrough](docs/screenshots/demo/eventsphere-demo.gif)](https://media.githubusercontent.com/media/Pruthvi226/EventSphere/main/EventSphere.mp4)

*The animated preview plays automatically inline. **Click the preview animation above** to watch or download the full high-resolution demo video.*

Screenshots below are captured from the demo flow and stored under `docs/screenshots/demo/`.

**Home Page**
![Home Page](docs/screenshots/demo/home.png)
> Shows the public landing page, featured events, and the main navigation into the platform.

**Event Catalog Page**
![Event Catalog](docs/screenshots/demo/events.png)
> Lets visitors browse available campus events before logging in.

**Event Details Page**
![Event Details](docs/screenshots/demo/event-details.png)
> Shows event information, schedule details, capacity, and registration actions.

**Login Page**
![Login](docs/screenshots/demo/login.png)
> Allows users to sign in with demo or registered accounts.

**Register Page**
![Register](docs/screenshots/demo/register.png)
> Lets a student create an account and start registering for events.

**Admin Dashboard Page**
![Admin Dashboard](docs/screenshots/demo/admin-dashboard.png)
> Gives admins a high-level view of users, events, and platform activity.

**Admin Users Page**
![Admin Users](docs/screenshots/demo/admin-users.png)
> Lets admins enable or disable users and manage role assignments.

**Organizer Dashboard Page**
![Organizer Dashboard](docs/screenshots/demo/organizer-dashboard.png)
> Helps organizers manage their events and track registrations.

**Create Event Page**
![Create Event](docs/screenshots/demo/organizer-new-event.png)
> Lets organizers create and publish new campus events.

**Organizer Registrations Page**
![Organizer Registrations](docs/screenshots/demo/organizer-registrations.png)
> Shows who registered for an event and supports event operations.

**Organizer Attendance Page**
![Organizer Attendance](docs/screenshots/demo/organizer-attendance.png)
> Lets organizers mark attendance and verify participation.

**Organizer Feedback Page**
![Organizer Feedback](docs/screenshots/demo/organizer-feedback.png)
> Displays student feedback so organizers can review event quality.

**Student Dashboard Page**
![Student Dashboard](docs/screenshots/demo/student-dashboard.png)
> Shows a student's registered events, upcoming opportunities, and quick actions.

**Student Registrations Page**
![Student Registrations](docs/screenshots/demo/student-registrations.png)
> Lets students review registrations and cancel when needed.

**Student Certificates Page**
![Student Certificates](docs/screenshots/demo/student-certificates.png)
> Shows earned certificates and download options.

**Volunteer Dashboard Page**
![Volunteer Dashboard](docs/screenshots/demo/volunteer-dashboard.png)
> Shows assigned events and attendance responsibilities for volunteers.

**Volunteer Attendance Page**
![Volunteer Attendance](docs/screenshots/demo/volunteer-attendance.png)
> Lets volunteers mark student attendance by registration details.

**Certificate Verification Page**
![Certificate Verification](docs/screenshots/demo/certificate-verify.png)
> Lets anyone verify whether a certificate is valid.

**Contact Page**
![Contact](docs/screenshots/demo/contact.png)
> Provides a simple way for visitors to find contact information.

**Screenshot Checklist**

- [ ] Home / Landing page - show the hero section, navigation, and featured event cards.
- [ ] Event Catalog page - show multiple events with dates, category, and capacity details.
- [ ] Event Details page - show one event with the registration call-to-action visible.
- [ ] Login page - show demo account cards and the sign-in form.
- [ ] Register page - show the student account form and validation-friendly fields.
- [ ] Admin Dashboard - show the main admin metrics and management overview.
- [ ] Admin Users panel - show user rows, status controls, and role management.
- [ ] Organizer Dashboard - show organizer-owned events and event actions.
- [ ] Create Event form - show event title, date, capacity, venue, and publish flow.
- [ ] Organizer Registrations - show registered students for one event.
- [ ] Organizer Attendance - show attendance marking controls.
- [ ] Organizer Feedback - show feedback entries and ratings.
- [ ] Student Dashboard - show a logged-in student view with upcoming events.
- [ ] Student Registrations - show current registrations and cancellation action.
- [ ] Student Certificates - show certificate list and download action.
- [ ] Volunteer Dashboard - show assigned event responsibilities.
- [ ] Volunteer Attendance - show attendance entry or lookup workflow.
- [ ] Certificate Verification - show certificate lookup and result message.
- [ ] Validation feedback - submit one form with missing data and capture friendly errors.
- [ ] Error or empty state - show how the app behaves when no records are available.

## 3. Key Features (Bullet List)

- Securely log in with role-based access for Admin, Organizer, Volunteer, and Student users.
- Browse campus events without an account and view event details before registering.
- Register for events, join waitlists, cancel registrations, and track participation.
- Create, publish, archive, and manage events from an organizer workspace.
- Mark attendance through organizer and volunteer workflows.
- Collect student feedback after events to improve future planning.
- Generate and download certificates for eligible participants.
- Verify certificates publicly so achievements can be trusted outside the app.

## 4. Tech Stack Table

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2 (framework for building Java web applications), Spring MVC (pattern for routing browser requests to Java controllers) |
| Frontend/Templates | Thymeleaf (HTML templates rendered by the server), HTML, CSS, JavaScript |
| Database | MySQL 8, Spring Data JPA (library that maps Java objects to database tables), Hibernate (database engine used by JPA) |
| Security | Spring Security (handles login, sessions, and role-based page access), BCrypt password hashing |
| Build Tool | Maven (downloads dependencies, runs tests, and packages the app) |
| Deployment | Docker Compose (runs the app and MySQL together), GitHub Actions (automated build checks), Render with TiDB support |

## 5. Architecture Overview (Optional - include if relevant)

EventSphere follows a layered MVC structure. Browser requests go to controllers, controllers call services for business rules, services use repositories for database access, and Thymeleaf templates render the final HTML pages.

```text
Browser
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
MySQL Database
```

## 6. Getting Started (Step-by-Step)

**Prerequisites**

- Java 17+
- Maven 3.8+
- MySQL 8+ for local development, or Docker Desktop for the easiest setup

**1. Clone the repository**

```powershell
git clone https://github.com/Pruthvi226/EventSphere.git
cd EventSphere
```

**2. Run with Docker Compose**

Create a local environment file from the sample:

```powershell
Copy-Item .env.example .env
```

Adjust passwords or ports in `.env` if needed, then start the stack:

```powershell
docker compose up -d --build
```

Open the app:

```text
http://localhost:28080
```

Stop the app:

```powershell
docker compose down
```

Watch app logs:

```powershell
docker compose logs -f app
```

Open phpMyAdmin when you need to inspect the database:

```powershell
docker compose --profile tools up -d phpmyadmin
```

```text
http://localhost:28081
```

Reset local Docker data if credentials or seeded data get out of sync:

```powershell
docker compose down -v
docker compose up -d --build
```

**3. Run locally with Maven and MySQL**

Create the database:

```sql
CREATE DATABASE eventsphere_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Set database environment variables:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/eventsphere_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="your-password"
```

Start the app:

```powershell
mvn spring-boot:run
```

Open the app:

```text
http://localhost:8080/eventsphere
```

**Default demo accounts**

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@eventsphere.com` | `admin123` |
| Organizer | `organizer@eventsphere.com` | `organizer123` |
| Volunteer | `volunteer@eventsphere.com` | `volunteer123` |
| Student | `student1@eventsphere.com` | `student123` |

**Run tests**

```powershell
mvn test
```

**Build the deployable jar**

```powershell
mvn clean package
```

## 7. Project Structure

```text
src/main/java/com/eventsphere
  config/          App configuration, security setup, and demo data seeding
  controller/      Web controllers that handle page routes and form actions
  dto/             Data transfer objects used for forms and validation
  entity/          Database models such as User, Event, Registration, and Certificate
  exception/       Custom exceptions for missing resources and access errors
  repository/      Spring Data JPA database access interfaces
  security/        Custom user lookup for login and role checks
  service/         Business logic interfaces
  service/impl/    Business logic implementations
  validation/      Custom form validation annotations and rules

src/main/resources
  static/          CSS and JavaScript assets
  templates/       Thymeleaf HTML pages
  application.properties
  application-docker.properties
  schema.sql       Database schema reference

docs/screenshots   Existing generated screenshot assets for demo review
scripts/           Utility scripts, including screenshot capture
```

## 8. API / Pages Reference (if applicable)

| Method | URL | Description | Auth Required |
| --- | --- | --- | --- |
| GET | `/` | Home page | No |
| GET | `/about` | About page | No |
| GET | `/contact` | Contact page | No |
| GET | `/events` | Event catalog | No |
| GET | `/events/{id}` | Event details | No |
| GET | `/events/search` | Search events | No |
| GET | `/certificate-verify` | Certificate verification page | No |
| GET | `/auth/login` | Login page | No |
| POST | `/auth/login` | Submit login form | No |
| GET | `/auth/register` | Student registration page | No |
| POST | `/auth/register` | Submit student registration | No |
| GET | `/dashboard` | Redirects users to their role dashboard | Yes |
| GET | `/admin/dashboard` | Admin overview | Admin |
| GET | `/admin/users` | Manage users and roles | Admin |
| POST | `/admin/users/{userId}/enable` | Enable a user | Admin |
| POST | `/admin/users/{userId}/disable` | Disable a user | Admin |
| POST | `/admin/users/{userId}/role` | Update a user role | Admin |
| DELETE | `/admin/users/{userId}` | Delete a user | Admin |
| GET | `/organizer/dashboard` | Organizer event workspace | Organizer |
| GET | `/organizer/events/new` | Create event form | Organizer |
| POST | `/organizer/events` | Create event | Organizer |
| POST | `/organizer/events/{eventId}/publish` | Publish event | Organizer |
| POST | `/organizer/events/{eventId}/archive` | Archive event | Organizer |
| GET | `/organizer/events/{eventId}/registrations` | View event registrations | Organizer |
| GET | `/organizer/events/{eventId}/attendance` | Manage attendance | Organizer |
| POST | `/organizer/events/{eventId}/attendance/mark/{registrationId}` | Mark attendance | Organizer |
| GET | `/organizer/events/{eventId}/feedback` | View feedback | Organizer |
| POST | `/organizer/events/{eventId}/generate-certificates/{type}` | Generate certificates | Organizer |
| GET | `/student/dashboard` | Student dashboard | Student |
| POST | `/student/register-event/{eventId}` | Register for an event | Student |
| POST | `/student/join-waitlist/{eventId}` | Join an event waitlist | Student |
| GET | `/student/my-registrations` | View student registrations | Student |
| POST | `/student/registrations/{registrationId}/cancel` | Cancel a registration | Student |
| GET | `/student/certificates` | View certificates | Student |
| POST | `/student/submit-feedback/{eventId}` | Submit feedback | Student |
| GET | `/volunteer/dashboard` | Volunteer dashboard | Volunteer |
| GET | `/volunteer/attendance` | Volunteer attendance page | Volunteer |
| POST | `/volunteer/mark-attendance` | Mark attendance | Volunteer |
| POST | `/volunteer/mark-attendance/number` | Mark attendance by number | Volunteer |
| POST | `/volunteer/mark-attendance/{registrationId}` | Mark attendance by registration | Volunteer |
| GET | `/certificates/{id}/download` | Download a certificate PDF | Logged-in certificate owner or authorized role |

## 9. What I Learned / Highlights (Recruiter Section)

- Designed a real multi-role workflow where each user type sees only the pages and actions they need.
- Built a layered Spring Boot application that separates page routing, business rules, and database access.
- Used validation and clear form flows to reduce bad event, registration, and feedback data.
- Containerized the app with Docker Compose so reviewers can run the full web app and database with one command.
- Added demo seed data and screenshot tooling to make the project easy to review quickly.

## 10. Future Improvements

- Add email notifications for registration updates, waitlist movement, and certificate availability.
- Add richer analytics for organizers, such as attendance trends and feedback summaries.
- Add file uploads for event posters and student proof documents.
- Add automated end-to-end browser tests for the main user journeys.

## 11. Contact

| Platform | Link |
| --- | --- |
| GitHub | [github.com/Pruthvi226](https://github.com/Pruthvi226) |
| LinkedIn | Add your LinkedIn profile URL |
| Email | Add your professional email address |
