package com.eventsphere.config;

import com.eventsphere.entity.*;
import com.eventsphere.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@Transactional
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private VolunteerAssignmentRepository volunteerAssignmentRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (!seedEnabled) {
            return;
        }

        // Run only if database is empty of standard seeded users (e.g. only contains <= 1 admin)
        if (userRepository.count() > 1) {
            return;
        }

        // 1. Seed/Ensure Admin User
        User adminUser = userRepository.findByEmail("admin@eventsphere.com").orElse(null);
        if (adminUser == null) {
            adminUser = new User();
            adminUser.setFullName("Admin User");
            adminUser.setEmail("admin@eventsphere.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(User.UserRole.ADMIN);
            adminUser.setEnabled(true);
            adminUser = userRepository.save(adminUser);
        }

        // 2. Seed Organizers
        User organizerCSE = new User();
        organizerCSE.setFullName("Dr. Sarah Jenkins");
        organizerCSE.setEmail("organizer@eventsphere.com");
        organizerCSE.setPassword(passwordEncoder.encode("organizer123"));
        organizerCSE.setRole(User.UserRole.ORGANIZER);
        organizerCSE.setEnabled(true);
        organizerCSE = userRepository.save(organizerCSE);

        User organizerMBA = new User();
        organizerMBA.setFullName("Prof. Alan Vance");
        organizerMBA.setEmail("biz_organizer@eventsphere.com");
        organizerMBA.setPassword(passwordEncoder.encode("organizer123"));
        organizerMBA.setRole(User.UserRole.ORGANIZER);
        organizerMBA.setEnabled(true);
        organizerMBA = userRepository.save(organizerMBA);

        // 3. Seed Volunteers
        User volunteer1 = new User();
        volunteer1.setFullName("Alex Carter");
        volunteer1.setEmail("volunteer@eventsphere.com");
        volunteer1.setPassword(passwordEncoder.encode("volunteer123"));
        volunteer1.setRole(User.UserRole.VOLUNTEER);
        volunteer1.setEnabled(true);
        volunteer1 = userRepository.save(volunteer1);

        User volunteer2 = new User();
        volunteer2.setFullName("Elena Rostova");
        volunteer2.setEmail("volunteer2@eventsphere.com");
        volunteer2.setPassword(passwordEncoder.encode("volunteer123"));
        volunteer2.setRole(User.UserRole.VOLUNTEER);
        volunteer2.setEnabled(true);
        volunteer2 = userRepository.save(volunteer2);

        // 4. Seed Students
        User student1 = new User();
        student1.setFullName("John Doe");
        student1.setEmail("student1@eventsphere.com");
        student1.setPassword(passwordEncoder.encode("student123"));
        student1.setRole(User.UserRole.STUDENT);
        student1.setEnabled(true);
        student1 = userRepository.save(student1);

        User student2 = new User();
        student2.setFullName("Jane Smith");
        student2.setEmail("student2@eventsphere.com");
        student2.setPassword(passwordEncoder.encode("student123"));
        student2.setRole(User.UserRole.STUDENT);
        student2.setEnabled(true);
        student2 = userRepository.save(student2);

        User student3 = new User();
        student3.setFullName("Bob Johnson");
        student3.setEmail("student3@eventsphere.com");
        student3.setPassword(passwordEncoder.encode("student123"));
        student3.setRole(User.UserRole.STUDENT);
        student3.setEnabled(true);
        student3 = userRepository.save(student3);

        User student4 = new User();
        student4.setFullName("Alice Williams");
        student4.setEmail("student4@eventsphere.com");
        student4.setPassword(passwordEncoder.encode("student123"));
        student4.setRole(User.UserRole.STUDENT);
        student4.setEnabled(true);
        student4 = userRepository.save(student4);

        User student5 = new User();
        student5.setFullName("Charlie Brown");
        student5.setEmail("student5@eventsphere.com");
        student5.setPassword(passwordEncoder.encode("student123"));
        student5.setRole(User.UserRole.STUDENT);
        student5.setEnabled(true);
        student5 = userRepository.save(student5);

        // 5. Seed Departments
        Department cseDept = new Department();
        cseDept.setName("Computer Science & Engineering");
        cseDept.setDescription("CSE Department and software development related events");
        cseDept = departmentRepository.save(cseDept);

        Department eceDept = new Department();
        eceDept.setName("Electronics & Communication");
        eceDept.setDescription("ECE Department and hardware/robotics related events");
        eceDept = departmentRepository.save(eceDept);

        Department mbaDept = new Department();
        mbaDept.setName("Business Administration");
        mbaDept.setDescription("MBA Department and management/finance events");
        mbaDept = departmentRepository.save(mbaDept);

        Department itDept = new Department();
        itDept.setName("Information Technology");
        itDept.setDescription("IT Department, networking, and cloud computing events");
        itDept = departmentRepository.save(itDept);

        // 6. Seed Clubs
        Club gdscClub = new Club();
        gdscClub.setName("Google Developer Student Club (GDSC)");
        gdscClub.setDescription("GDSC focuses on Google developer technologies and community development.");
        gdscClub.setDepartment(cseDept);
        gdscClub = clubRepository.save(gdscClub);

        Club ricClub = new Club();
        ricClub.setName("Robotics & IoT Circle (RIC)");
        ricClub.setDescription("A hub for students interested in microcontrollers, robotics, and smart systems.");
        ricClub.setDepartment(eceDept);
        ricClub = clubRepository.save(ricClub);

        Club fecClub = new Club();
        fecClub.setName("Finance & Entrepreneurship Cell (FEC)");
        fecClub.setDescription("Connecting aspiring startup founders, venture capitalists, and business students.");
        fecClub.setDepartment(mbaDept);
        fecClub = clubRepository.save(fecClub);

        Club wdsClub = new Club();
        wdsClub.setName("Web Development Society (WDS)");
        wdsClub.setDescription("Promoting web development technologies, hosting sprints and web hackathons.");
        wdsClub.setDepartment(itDept);
        wdsClub = clubRepository.save(wdsClub);

        // 7. Seed Events
        // Event 1 (COMPLETED): National Hackathon 2026
        Event eventHackathon = new Event();
        eventHackathon.setTitle("National Hackathon 2026");
        eventHackathon.setDescription("EventSphere's premiere coding event. Build software solutions in 24 hours.");
        eventHackathon.setVenue("Main Auditorium & CSE Lab");
        eventHackathon.setStartDateTime(LocalDateTime.now().minusDays(7));
        eventHackathon.setEndDateTime(LocalDateTime.now().minusDays(6));
        eventHackathon.setRegistrationDeadline(LocalDateTime.now().minusDays(10));
        eventHackathon.setCapacity(150);
        eventHackathon.setCategory("Technology");
        eventHackathon.setStatus(Event.EventStatus.COMPLETED);
        eventHackathon.setOrganizer(organizerCSE);
        eventHackathon.setDepartment(cseDept);
        eventHackathon.setClub(gdscClub);
        eventHackathon = eventRepository.save(eventHackathon);

        // Event 2 (PUBLISHED - Seats Available): AI & Machine Learning Masterclass
        Event eventAI = new Event();
        eventAI.setTitle("AI & Machine Learning Masterclass");
        eventAI.setDescription("A comprehensive hands-on bootcamp detailing regression, deep learning, and transformer networks.");
        eventAI.setVenue("Seminar Hall 3");
        eventAI.setStartDateTime(LocalDateTime.now().plusDays(4));
        eventAI.setEndDateTime(LocalDateTime.now().plusDays(5));
        eventAI.setRegistrationDeadline(LocalDateTime.now().plusDays(3));
        eventAI.setCapacity(40);
        eventAI.setCategory("Technology");
        eventAI.setStatus(Event.EventStatus.PUBLISHED);
        eventAI.setOrganizer(organizerCSE);
        eventAI.setDepartment(cseDept);
        eventAI.setClub(wdsClub);
        eventAI = eventRepository.save(eventAI);

        // Event 3 (PUBLISHED - Full & Waitlisted): Robotics & IoT Expo
        Event eventRobotics = new Event();
        eventRobotics.setTitle("Robotics & IoT Expo");
        eventRobotics.setDescription("Hands-on hardware lab for building Arduino systems and sensor integration. Extremely limited seats!");
        eventRobotics.setVenue("Lab 402");
        eventRobotics.setStartDateTime(LocalDateTime.now().plusDays(2));
        eventRobotics.setEndDateTime(LocalDateTime.now().plusDays(2).plusHours(4));
        eventRobotics.setRegistrationDeadline(LocalDateTime.now().plusDays(1));
        eventRobotics.setCapacity(2); // Low capacity to easily trigger waitlist mechanics
        eventRobotics.setCategory("Technology");
        eventRobotics.setStatus(Event.EventStatus.PUBLISHED);
        eventRobotics.setOrganizer(organizerCSE);
        eventRobotics.setDepartment(eceDept);
        eventRobotics.setClub(ricClub);
        eventRobotics = eventRepository.save(eventRobotics);

        // Event 4 (DRAFT): UI/UX Design Bootcamp
        Event eventDesign = new Event();
        eventDesign.setTitle("UI/UX Design Bootcamp");
        eventDesign.setDescription("Learn the principles of typography, wireframing, color theory and visual communication in Figma.");
        eventDesign.setVenue("Studio Room B");
        eventDesign.setStartDateTime(LocalDateTime.now().plusDays(12));
        eventDesign.setEndDateTime(LocalDateTime.now().plusDays(12).plusHours(3));
        eventDesign.setRegistrationDeadline(LocalDateTime.now().plusDays(10));
        eventDesign.setCapacity(30);
        eventDesign.setCategory("Design");
        eventDesign.setStatus(Event.EventStatus.DRAFT);
        eventDesign.setOrganizer(organizerCSE);
        eventDesign.setDepartment(cseDept);
        eventDesign.setClub(wdsClub);
        eventDesign = eventRepository.save(eventDesign);

        // Event 5 (PUBLISHED - Seats Available): Annual Start-Up Pitch Challenge
        Event eventPitch = new Event();
        eventPitch.setTitle("Annual Start-Up Pitch Challenge");
        eventPitch.setDescription("Pitch your groundbreaking business ideas to real angel investors and win cash prizes of up to $10,000.");
        eventPitch.setVenue("Business Block Auditorium");
        eventPitch.setStartDateTime(LocalDateTime.now().plusDays(9));
        eventPitch.setEndDateTime(LocalDateTime.now().plusDays(9).plusHours(6));
        eventPitch.setRegistrationDeadline(LocalDateTime.now().plusDays(8));
        eventPitch.setCapacity(50);
        eventPitch.setCategory("Business");
        eventPitch.setStatus(Event.EventStatus.PUBLISHED);
        eventPitch.setOrganizer(organizerMBA);
        eventPitch.setDepartment(mbaDept);
        eventPitch.setClub(fecClub);
        eventPitch = eventRepository.save(eventPitch);

        // Event 6 (CANCELLED): Traditional Sports Tournament
        Event eventSports = new Event();
        eventSports.setTitle("Traditional Sports Tournament");
        eventSports.setDescription("Intra-college tracks, fields, and traditional events. Postponed/cancelled due to weather constraints.");
        eventSports.setVenue("College Ground");
        eventSports.setStartDateTime(LocalDateTime.now().plusDays(6));
        eventSports.setEndDateTime(LocalDateTime.now().plusDays(6).plusHours(8));
        eventSports.setRegistrationDeadline(LocalDateTime.now().plusDays(5));
        eventSports.setCapacity(100);
        eventSports.setCategory("Sports");
        eventSports.setStatus(Event.EventStatus.CANCELLED);
        eventSports.setOrganizer(organizerMBA);
        eventSports.setDepartment(mbaDept);
        eventSports.setClub(fecClub);
        eventSports = eventRepository.save(eventSports);

        // 8. Seed Event Registrations, Attendance, Feedbacks & Certificates
        // Event 1 (Completed GDSC Hackathon) Registrations
        User[] students = {student1, student2, student3, student4, student5};
        boolean[] attendedStatus = {true, true, true, false, true};
        int[] ratings = {5, 4, 5, 0, 5};
        String[] comments = {
            "Best campus hackathon! Exceptionally well-coordinated.",
            "Great coding prompts and mentors, but the food could have been better!",
            "Loved the coding challenges, sandbox testing, and cloud credits.",
            null,
            "Amazing experience! Looking forward to GDSC events next semester."
        };

        for (int i = 0; i < students.length; i++) {
            User student = students[i];
            EventRegistration reg = new EventRegistration();
            reg.setRegistrationNumber(generateRegNumber(student.getId(), eventHackathon.getId()));
            reg.setRegistrationDate(LocalDateTime.now().minusDays(12));
            reg.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
            reg.setStudent(student);
            reg.setEvent(eventHackathon);
            reg = registrationRepository.save(reg);

            // Attendance Record
            Attendance att = new Attendance();
            att.setRegistration(reg);
            att.setAttended(attendedStatus[i]);
            if (attendedStatus[i]) {
                att.setCheckInTime(eventHackathon.getStartDateTime().plusMinutes(15));
            }
            attendanceRepository.save(att);

            // Feedback (if attended or simply submitted as mock)
            if (ratings[i] > 0) {
                Feedback feedback = new Feedback();
                feedback.setEvent(eventHackathon);
                feedback.setStudent(student);
                feedback.setRating(ratings[i]);
                feedback.setComment(comments[i]);
                feedback.setSubmittedAt(eventHackathon.getEndDateTime().plusHours(2));
                feedbackRepository.save(feedback);
            }

            // Issue Certificate if attended
            if (attendedStatus[i]) {
                Certificate cert = new Certificate();
                cert.setCertificateNumber("CERT-GDSC-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
                cert.setIssueDate(eventHackathon.getEndDateTime().plusDays(1));
                cert.setCertificateType(Certificate.CertificateType.PARTICIPATION);
                cert.setFilePath("uploads/certificates/gdsc_hackathon_" + student.getId() + ".pdf");
                cert.setRegistration(reg);
                certificateRepository.save(cert);
            }
        }

        // Event 2 (AI Masterclass) - registrations
        EventRegistration regAI1 = new EventRegistration();
        regAI1.setRegistrationNumber(generateRegNumber(student1.getId(), eventAI.getId()));
        regAI1.setRegistrationDate(LocalDateTime.now().minusDays(1));
        regAI1.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
        regAI1.setStudent(student1);
        regAI1.setEvent(eventAI);
        registrationRepository.save(regAI1);

        Attendance attAI1 = new Attendance();
        attAI1.setRegistration(regAI1);
        attAI1.setAttended(false);
        attendanceRepository.save(attAI1);

        EventRegistration regAI2 = new EventRegistration();
        regAI2.setRegistrationNumber(generateRegNumber(student2.getId(), eventAI.getId()));
        regAI2.setRegistrationDate(LocalDateTime.now().minusDays(1));
        regAI2.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
        regAI2.setStudent(student2);
        regAI2.setEvent(eventAI);
        registrationRepository.save(regAI2);

        Attendance attAI2 = new Attendance();
        attAI2.setRegistration(regAI2);
        attAI2.setAttended(false);
        attendanceRepository.save(attAI2);

        // Event 3 (Robotics Expo - low capacity: 2) - full and waitlisted registrations
        // Student 3: REGISTERED
        EventRegistration regRob1 = new EventRegistration();
        regRob1.setRegistrationNumber(generateRegNumber(student3.getId(), eventRobotics.getId()));
        regRob1.setRegistrationDate(LocalDateTime.now().minusDays(2));
        regRob1.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
        regRob1.setStudent(student3);
        regRob1.setEvent(eventRobotics);
        registrationRepository.save(regRob1);

        Attendance attRob1 = new Attendance();
        attRob1.setRegistration(regRob1);
        attRob1.setAttended(false);
        attendanceRepository.save(attRob1);

        // Student 4: REGISTERED (Full!)
        EventRegistration regRob2 = new EventRegistration();
        regRob2.setRegistrationNumber(generateRegNumber(student4.getId(), eventRobotics.getId()));
        regRob2.setRegistrationDate(LocalDateTime.now().minusDays(2));
        regRob2.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
        regRob2.setStudent(student4);
        regRob2.setEvent(eventRobotics);
        registrationRepository.save(regRob2);

        Attendance attRob2 = new Attendance();
        attRob2.setRegistration(regRob2);
        attRob2.setAttended(false);
        attendanceRepository.save(attRob2);

        // Student 5: WAITLISTED (Pos #1)
        EventRegistration regRob3 = new EventRegistration();
        regRob3.setRegistrationNumber(generateRegNumber(student5.getId(), eventRobotics.getId()));
        regRob3.setRegistrationDate(LocalDateTime.now().minusDays(1));
        regRob3.setStatus(EventRegistration.RegistrationStatus.WAITLISTED);
        regRob3.setStudent(student5);
        regRob3.setEvent(eventRobotics);
        registrationRepository.save(regRob3);

        Waitlist w1 = new Waitlist();
        w1.setEvent(eventRobotics);
        w1.setStudent(student5);
        w1.setPosition(1);
        w1.setAddedDate(LocalDateTime.now().minusDays(1));
        waitlistRepository.save(w1);

        // Student 1: WAITLISTED (Pos #2)
        EventRegistration regRob4 = new EventRegistration();
        regRob4.setRegistrationNumber(generateRegNumber(student1.getId(), eventRobotics.getId()));
        regRob4.setRegistrationDate(LocalDateTime.now());
        regRob4.setStatus(EventRegistration.RegistrationStatus.WAITLISTED);
        regRob4.setStudent(student1);
        regRob4.setEvent(eventRobotics);
        registrationRepository.save(regRob4);

        Waitlist w2 = new Waitlist();
        w2.setEvent(eventRobotics);
        w2.setStudent(student1);
        w2.setPosition(2);
        w2.setAddedDate(LocalDateTime.now());
        waitlistRepository.save(w2);

        // Event 5 (Start-Up Pitch Challenge) registrations
        EventRegistration regPitch1 = new EventRegistration();
        regPitch1.setRegistrationNumber(generateRegNumber(student2.getId(), eventPitch.getId()));
        regPitch1.setRegistrationDate(LocalDateTime.now());
        regPitch1.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
        regPitch1.setStudent(student2);
        regPitch1.setEvent(eventPitch);
        registrationRepository.save(regPitch1);

        Attendance attPitch1 = new Attendance();
        attPitch1.setRegistration(regPitch1);
        attPitch1.setAttended(false);
        attendanceRepository.save(attPitch1);

        EventRegistration regPitch2 = new EventRegistration();
        regPitch2.setRegistrationNumber(generateRegNumber(student3.getId(), eventPitch.getId()));
        regPitch2.setRegistrationDate(LocalDateTime.now());
        regPitch2.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
        regPitch2.setStudent(student3);
        regPitch2.setEvent(eventPitch);
        registrationRepository.save(regPitch2);

        Attendance attPitch2 = new Attendance();
        attPitch2.setRegistration(regPitch2);
        attPitch2.setAttended(false);
        attendanceRepository.save(attPitch2);

        // Event 6 (Cancelled traditional sports) registrations
        EventRegistration regSports = new EventRegistration();
        regSports.setRegistrationNumber(generateRegNumber(student1.getId(), eventSports.getId()));
        regSports.setRegistrationDate(LocalDateTime.now().minusDays(3));
        regSports.setStatus(EventRegistration.RegistrationStatus.CANCELLED);
        regSports.setStudent(student1);
        regSports.setEvent(eventSports);
        registrationRepository.save(regSports);

        // 9. Seed Volunteer Assignments
        VolunteerAssignment volAss1 = new VolunteerAssignment();
        volAss1.setEvent(eventHackathon);
        volAss1.setVolunteer(volunteer1);
        volAss1.setAssignedDate(LocalDateTime.now().minusDays(10));
        volunteerAssignmentRepository.save(volAss1);

        VolunteerAssignment volAss2 = new VolunteerAssignment();
        volAss2.setEvent(eventRobotics);
        volAss2.setVolunteer(volunteer1);
        volAss2.setAssignedDate(LocalDateTime.now().minusDays(1));
        volunteerAssignmentRepository.save(volAss2);

        // 10. Seed Student Notifications for Student 1 (John Doe)
        Notification notif1 = new Notification();
        notif1.setUser(student1);
        notif1.setTitle("Certificate Issued");
        notif1.setMessage("Congratulations! Your participation certificate for National Hackathon 2026 is generated. Download it from your certificates section.");
        notif1.setReadStatus(true);
        notificationRepository.save(notif1);

        Notification notif2 = new Notification();
        notif2.setUser(student1);
        notif2.setTitle("Waitlist Alert: Robotics & IoT Expo");
        notif2.setMessage("The Robotics & IoT Expo is currently full. You have been placed on the waitlist at Position #2. We will notify you if a spot opens up.");
        notif2.setReadStatus(false);
        notificationRepository.save(notif2);

        Notification notif3 = new Notification();
        notif3.setUser(student1);
        notif3.setTitle("Event Cancelled");
        notif3.setMessage("Please note that Traditional Sports Tournament has been cancelled due to heavy rain warnings. Any pending registrations are cancelled.");
        notif3.setReadStatus(false);
        notificationRepository.save(notif3);
    }

    private String generateRegNumber(Long studentId, Long eventId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String shortId = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "REG-" + timestamp + "-" + studentId + eventId + "-" + shortId;
    }
}
