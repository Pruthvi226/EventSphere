package com.eventsphere;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:eventsphere_test;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "spring.jpa.show-sql=false",
        "logging.level.root=WARN",
        "logging.level.com.eventsphere=WARN",
        "logging.level.org.springframework.security=WARN",
        "logging.level.org.springframework.web=WARN",
        "logging.level.org.hibernate.SQL=WARN"
})
class EventSphereApplicationTest {
    @Test
    void contextLoads() {
    }
}
