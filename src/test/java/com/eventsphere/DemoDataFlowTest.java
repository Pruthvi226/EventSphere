package com.eventsphere;

import com.eventsphere.dto.EventRegistrationDTO;
import com.eventsphere.entity.Certificate;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.EventRegistration;
import com.eventsphere.entity.User;
import com.eventsphere.entity.Waitlist;
import com.eventsphere.repository.AttendanceRepository;
import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.repository.EventRegistrationRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.FeedbackRepository;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.repository.VolunteerAssignmentRepository;
import com.eventsphere.repository.WaitlistRepository;
import com.eventsphere.service.CertificateService;
import com.eventsphere.service.EventRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:eventsphere_demo_flow_test;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "spring.jpa.show-sql=false",
        "logging.level.root=WARN",
        "logging.level.com.eventsphere=WARN",
        "logging.level.org.springframework.security=WARN",
        "logging.level.org.springframework.web=WARN",
        "logging.level.org.hibernate.SQL=WARN"
})
@org.springframework.transaction.annotation.Transactional
class DemoDataFlowTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private VolunteerAssignmentRepository volunteerAssignmentRepository;

    @Autowired
    private EventRegistrationService registrationService;

    @Autowired
    private CertificateService certificateService;

    @Test
    void seededDemoDataProvesCampusEventWorkflows() {
        assertThat(userRepository.findByEmail("admin@eventsphere.com"))
                .get()
                .extracting(User::getRole)
                .isEqualTo(User.UserRole.ADMIN);
        assertThat(userRepository.findByEmail("organizer@eventsphere.com"))
                .get()
                .extracting(User::getRole)
                .isEqualTo(User.UserRole.ORGANIZER);
        assertThat(userRepository.findByEmail("volunteer@eventsphere.com"))
                .get()
                .extracting(User::getRole)
                .isEqualTo(User.UserRole.VOLUNTEER);
        assertThat(userRepository.findByEmail("student1@eventsphere.com"))
                .get()
                .extracting(User::getRole)
                .isEqualTo(User.UserRole.STUDENT);

        List<Event> publishedUpcomingEvents = eventRepository.findPublishedUpcomingEvents();
        assertThat(publishedUpcomingEvents)
                .extracting(Event::getTitle)
                .contains("AI & Machine Learning Masterclass", "Robotics & IoT Expo", "Annual Start-Up Pitch Challenge")
                .doesNotContain("UI/UX Design Bootcamp", "Traditional Sports Tournament");

        Event aiMasterclass = eventByTitle("AI & Machine Learning Masterclass");
        User student3 = userByEmail("student3@eventsphere.com");
        EventRegistrationDTO newRegistration = registrationService.registerForEvent(aiMasterclass.getId(), student3.getId());
        EventRegistration savedRegistration = registrationRepository.findById(newRegistration.getId()).orElseThrow();
        assertThat(savedRegistration.getStatus()).isEqualTo(EventRegistration.RegistrationStatus.REGISTERED);
        assertThat(attendanceRepository.findByRegistration(savedRegistration)).isPresent();
        assertThat(registrationRepository.countRegisteredByEvent(aiMasterclass)).isEqualTo(3);

        Event roboticsExpo = eventByTitle("Robotics & IoT Expo");
        assertThat(registrationRepository.countRegisteredByEvent(roboticsExpo)).isEqualTo(roboticsExpo.getCapacity().longValue());
        assertThat(waitlistRepository.findByEventOrderByPosition(roboticsExpo))
                .extracting(Waitlist::getPosition)
                .containsExactly(1, 2);

        EventRegistration firstRoboticsRegistration = registrationRepository
                .findByEventAndStatus(roboticsExpo, EventRegistration.RegistrationStatus.REGISTERED)
                .get(0);
        registrationService.cancelRegistration(firstRoboticsRegistration.getId());
        assertThat(registrationRepository.countRegisteredByEvent(roboticsExpo)).isEqualTo(roboticsExpo.getCapacity().longValue());
        assertThat(registrationRepository.findByEventAndStudent(roboticsExpo, userByEmail("student5@eventsphere.com")))
                .get()
                .extracting(EventRegistration::getStatus)
                .isEqualTo(EventRegistration.RegistrationStatus.REGISTERED);
        assertThat(waitlistRepository.findByEventOrderByPosition(roboticsExpo))
                .extracting(Waitlist::getPosition)
                .containsExactly(1);

        Event hackathon = eventByTitle("National Hackathon 2026");
        assertThat(attendanceRepository.countAttendedByEvent(hackathon.getId())).isEqualTo(4);
        assertThat(feedbackRepository.countByEvent(hackathon)).isEqualTo(4);

        List<Certificate> hackathonCertificates = certificateRepository
                .findByTypeAndEvent(Certificate.CertificateType.PARTICIPATION, hackathon.getId());
        assertThat(hackathonCertificates).hasSize(4);
        assertThat(certificateService.verifyCertificate(hackathonCertificates.get(0).getCertificateNumber())).isTrue();

        User volunteer = userByEmail("volunteer@eventsphere.com");
        assertThat(volunteerAssignmentRepository.findByVolunteer(volunteer))
                .extracting(assignment -> assignment.getEvent().getTitle())
                .contains("National Hackathon 2026", "Robotics & IoT Expo");
    }

    private Event eventByTitle(String title) {
        return eventRepository.findAll().stream()
                .filter(event -> event.getTitle().equals(title))
                .findFirst()
                .orElseThrow();
    }

    private User userByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }
}
