# EventSphere - College Event Management Platform

A comprehensive, production-quality web application for managing college events, registrations, attendance, and certificate generation.

## 🎯 Features

### Core Features
- **Event Management**: Create, publish, and manage events with full details
- **User Registration**: Students can register for events with capacity management
- **Attendance Tracking**: Mark attendance using QR codes or manual entry
- **Feedback System**: Collect event feedback with rating system
- **Certificate Generation**: Auto-generate participation and achievement certificates (PDF)
- **Notifications**: Real-time notifications for registrations and updates
- **Analytics Dashboard**: Event analytics and attendance reports

### Role-Based Access
- **Admin**: System administration, user management, reports
- **Organizer**: Create and manage events, track registrations and attendance
- **Volunteer**: Mark attendance, support event execution
- **Student**: Browse events, register, provide feedback, download certificates

## 🏗️ Architecture

### Technology Stack
- **Backend**: Java 17, Spring Boot 3.2, Spring Security 6
- **Database**: MySQL 8.0
- **ORM**: Hibernate/JPA
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Additional Libraries**:
  - ZXing: QR code generation
  - OpenPDF: PDF certificate generation
  - Lombok: Code generation
  - Spring Data JPA: Database access

### Project Structure
```
EventSphere/
├── src/main/java/com/eventsphere/
│   ├── controller/               # REST & MVC Controllers
│   ├── service/                  # Business Logic Layer
│   │   ├── impl/                 # Service Implementations
│   │   └── [Interfaces]
│   ├── repository/               # Data Access Layer
│   ├── entity/                   # JPA Entities
│   ├── dto/                      # Data Transfer Objects
│   ├── security/                 # Security Configuration
│   ├── config/                   # Application Configuration
│   ├── exception/                # Custom Exceptions
│   ├── util/                     # Utility Classes
│   ├── validation/               # Validation Classes
│   └── EventSphereApplication.java
├── src/main/resources/
│   ├── templates/                # Thymeleaf Templates
│   │   ├── auth/                 # Authentication pages
│   │   ├── events/               # Event pages
│   │   ├── student/              # Student dashboard
│   │   ├── organizer/            # Organizer dashboard
│   │   ├── admin/                # Admin dashboard
│   │   └── volunteer/            # Volunteer pages
│   ├── static/
│   │   ├── css/                  # Stylesheets
│   │   ├── js/                   # JavaScript files
│   │   └── images/               # Images
│   ├── application.properties    # Configuration
│   └── schema.sql                # Database schema
└── pom.xml                       # Maven dependencies
```

## 📊 Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐
│     User        │
├─────────────────┤
│ id (PK)         │
│ fullName        │
│ email (UNIQUE)  │
│ password        │
│ role            │
│ enabled         │
│ createdAt       │
└─────────────────┘
        │
        ├─────┐
        │     │
        │  ┌─────────────────────┐
        │  │  Event              │
        │  ├─────────────────────┤
        │  │ id (PK)             │
        │  │ title               │
        │  │ description         │
        │  │ venue               │
        │  │ startDateTime       │
        │  │ endDateTime         │
        │  │ registrationDeadline│
        │  │ capacity            │
        │  │ category            │
        │  │ status              │
        │  │ organizer_id (FK)   │
        │  │ department_id (FK)  │
        │  │ club_id (FK)        │
        │  └─────────────────────┘
        │          │
        │          ├──────────────┬────────────────┐
        │          │              │                │
        │    ┌──────────────────┐ │     ┌──────────────────┐
        │    │ EventRegistration│ │     │   Attendance     │
        │    ├──────────────────┤ │     ├──────────────────┤
        │    │ id (PK)          │ │     │ id (PK)          │
        │    │ registration_id  │ │     │ checkInTime      │
        │    │ student_id (FK)  │─┼────│ attended         │
        │    │ event_id (FK)    │ │     │ registration_id  │
        │    │ status           │ │     │ (FK, UNIQUE)     │
        │    │ registrationDate │ │     └──────────────────┘
        │    └──────────────────┘ │
        │                          │
        │              ┌──────────────────┐
        │              │   Certificate    │
        │              ├──────────────────┤
        │              │ id (PK)          │
        │              │ certificateNum   │
        │              │ issueDate        │
        │              │ type             │
        │              │ registration_id  │
        │              │ (FK)             │
        │              └──────────────────┘
        │
        ├──────────────────────────────┐
        │                              │
    ┌──────────────┐          ┌──────────────┐
    │   Feedback   │          │ Notification │
    ├──────────────┤          ├──────────────┤
    │ id (PK)      │          │ id (PK)      │
    │ rating       │          │ title        │
    │ comment      │          │ message      │
    │ event_id (FK)│          │ readStatus   │
    │ student_id   │          │ user_id (FK) │
    │ (FK)         │          └──────────────┘
    └──────────────┘

┌──────────────────┐       ┌──────────────────┐
│   Department     │       │      Club        │
├──────────────────┤       ├──────────────────┤
│ id (PK)          │──────│ id (PK)          │
│ name             │      │ name             │
│ description      │      │ description      │
└──────────────────┘      │ department_id(FK)│
                          └──────────────────┘

┌──────────────────────┐
│ VolunteerAssignment  │
├──────────────────────┤
│ id (PK)              │
│ volunteer_id (FK)    │
│ event_id (FK)        │
│ assignedDate         │
└──────────────────────┘

┌──────────────────┐
│    Waitlist      │
├──────────────────┤
│ id (PK)          │
│ student_id (FK)  │
│ event_id (FK)    │
│ position         │
│ addedDate        │
└──────────────────┘
```

## 🔐 Security Features

- **Spring Security**: Role-based access control (RBAC)
- **Password Encryption**: BCrypt password encoding
- **CSRF Protection**: Token-based CSRF protection
- **Session Management**: Secure session handling
- **Input Validation**: Server-side validation with annotations
- **Authorization**: Method-level security with @PreAuthorize

## 🚀 Installation & Setup

### Prerequisites
- Java 17 or higher
- MySQL 8.0
- Maven 3.8.0+

### Step 1: Clone and Setup

```bash
git clone <repository-url>
cd EventSphere
```

### Step 2: Database Setup

```bash
mysql -u root -p
CREATE DATABASE eventsphere_db;
USE eventsphere_db;
source src/main/resources/schema.sql;
```

### Step 3: Configure Database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/eventsphere_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Step 4: Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application will start at `http://localhost:8080/eventsphere`

### Docker Setup

Build and start the application with MySQL:

```bash
docker compose up -d --build
```

Open the application at `http://localhost:28080/`.

Useful Docker commands:

```bash
docker compose ps
docker compose logs -f app
docker compose down
```

The Compose stack uses the `docker` Spring profile, persists MySQL data in a named Docker volume, and mounts `src/main/resources/schema.sql` for first-time database initialization.

## 📱 Default Credentials

- **Admin Account**:
  - Email: `admin@eventsphere.com`
  - Password: `admin123`

## 🎓 User Workflows

### Student Workflow
1. Register/Login
2. Browse upcoming events
3. Register for desired events
4. Receive confirmation notification
5. Attend event and mark attendance
6. Submit feedback and rating
7. Download certificate after event

### Organizer Workflow
1. Login to organizer dashboard
2. Create new event with details
3. Publish event for registrations
4. Track registrations and attendance
5. View event feedback and analytics
6. Generate and distribute certificates
7. Download reports

### Admin Workflow
1. Access admin dashboard
2. Manage all users and roles
3. Monitor system statistics
4. Manage departments and clubs
5. View system reports
6. Configure system settings

## 🔌 API Endpoints (Sample)

### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /logout` - User logout

### Events
- `GET /events` - List all events
- `GET /events/{id}` - Get event details
- `POST /student/register-event/{eventId}` - Register for event
- `POST /organizer/events` - Create event

### Registrations
- `GET /student/my-registrations` - Get my registrations
- `GET /organizer/events/{eventId}/registrations` - Get event registrations
- `POST /organizer/events/{eventId}/attendance/mark/{registrationId}` - Mark attendance

### Certificates
- `GET /certificates/{id}` - Download certificate
- `POST /organizer/events/{eventId}/generate-certificates/{type}` - Generate certificates

## 📈 Key Metrics & Analytics

- Total events created
- Registration trends
- Attendance rates
- Feedback scores
- User engagement statistics
- Certificate issuance reports

## 🛠️ Technologies Deep Dive

### Spring Boot 3.2
- Latest Spring Framework 6.x
- Improved performance and security
- Enhanced autoconfiguration

### Spring Security 6
- Servlet-based authentication
- Role-based access control
- CSRF protection with tokens
- Method-level security

### Thymeleaf
- Server-side template rendering
- Spring integration
- Responsive design with Bootstrap 5

### MySQL/Hibernate
- Automatic schema generation
- Transaction management
- Query optimization with custom repositories

## 📝 Code Examples

### Creating an Event
```java
@PostMapping("/events")
public String createEvent(@ModelAttribute EventDTO eventDTO) {
    EventDTO createdEvent = eventService.createEvent(eventDTO, getCurrentUserId());
    return "redirect:/organizer/dashboard";
}
```

### Registering for Event
```java
@PostMapping("/register-event/{eventId}")
public String registerForEvent(@PathVariable Long eventId) {
    User user = getCurrentUser();
    registrationService.registerForEvent(eventId, user.getId());
    return "redirect:/events/" + eventId;
}
```

### Generating Certificate
```java
@PostMapping("/events/{eventId}/generate-certificates/{type}")
public String generateCertificates(@PathVariable Long eventId, @PathVariable String type) {
    certificateService.generateCertificatesForEvent(eventId, type);
    return "redirect:/organizer/dashboard";
}
```

## 🐛 Troubleshooting

### Database Connection Error
- Verify MySQL is running
- Check database credentials in application.properties
- Ensure database exists: `SHOW DATABASES;`

### Port Already in Use
- Change port in application.properties: `server.port=8081`

### Template Not Found
- Verify template files are in `src/main/resources/templates/`
- Check template names match controller method returns

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see LICENSE file for details.

## 👥 Authors

- EventSphere Development Team

## 📞 Support

For support, email support@eventsphere.com or open an issue in the repository.

## 🎉 Future Enhancements

- [ ] Mobile application (React Native/Flutter)
- [ ] Advanced analytics and reporting
- [ ] Email notifications integration
- [ ] Payment gateway integration
- [ ] Event sponsorship management
- [ ] Networking features
- [ ] Real-time chat during events
- [ ] Video recording and streaming
- [ ] Social media integration

---

**Version**: 1.0.0  
**Last Updated**: 2024  
**Status**: Production Ready
