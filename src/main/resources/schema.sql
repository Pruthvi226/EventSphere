-- EventSphere Database Schema
-- College Event Management Platform

-- Users Table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'STUDENT',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Department Table
CREATE TABLE departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Clubs Table
CREATE TABLE clubs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    department_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    INDEX idx_name (name),
    INDEX idx_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Events Table
CREATE TABLE events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description LONGTEXT NOT NULL,
    venue VARCHAR(255) NOT NULL,
    start_date_time DATETIME NOT NULL,
    end_date_time DATETIME NOT NULL,
    registration_deadline DATETIME NOT NULL,
    capacity INT NOT NULL,
    banner_image_path VARCHAR(500),
    category VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    organizer_id BIGINT NOT NULL,
    department_id BIGINT,
    club_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE SET NULL,
    INDEX idx_title (title),
    INDEX idx_organizer (organizer_id),
    INDEX idx_status (status),
    INDEX idx_start_date (start_date_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event Registrations Table
CREATE TABLE event_registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    registration_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'REGISTERED',
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_event (student_id, event_id),
    INDEX idx_registration_number (registration_number),
    INDEX idx_student (student_id),
    INDEX idx_event (event_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Attendance Table
CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_in_time DATETIME,
    attended BOOLEAN DEFAULT FALSE,
    registration_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES event_registrations(id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (registration_id),
    INDEX idx_attended (attended)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volunteer Assignments Table
CREATE TABLE volunteer_assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    volunteer_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    assigned_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (volunteer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE KEY unique_volunteer_event (volunteer_id, event_id),
    INDEX idx_volunteer (volunteer_id),
    INDEX idx_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Certificates Table
CREATE TABLE certificates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    certificate_number VARCHAR(100) UNIQUE NOT NULL,
    issue_date DATETIME NOT NULL,
    certificate_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500),
    registration_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES event_registrations(id) ON DELETE CASCADE,
    INDEX idx_certificate_number (certificate_number),
    INDEX idx_certificate_type (certificate_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Feedback Table
CREATE TABLE feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment LONGTEXT,
    submitted_at DATETIME NOT NULL,
    event_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_event_student_feedback (event_id, student_id),
    INDEX idx_event (event_id),
    INDEX idx_student (student_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Notifications Table
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    message LONGTEXT NOT NULL,
    read_status BOOLEAN DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_read_status (read_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Waitlist Table
CREATE TABLE waitlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    position INT,
    added_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_event_waitlist (student_id, event_id),
    INDEX idx_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert initial admin user (password: admin123 - BCrypt encoded)
INSERT INTO users (full_name, email, password, role, enabled) VALUES
('Admin User', 'admin@eventsphere.com', '$2a$10$slYQmyNdGzin7olVN3p5be4NzSKVK.vw0z5XBuPLN4GEh.r/QcsYS', 'ADMIN', TRUE);
