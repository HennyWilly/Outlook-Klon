package de.outlookklon.config;

import de.outlookklon.logik.calendar.AppointmentCalendar;
import de.outlookklon.logik.contacts.ContactManagement;
import de.outlookklon.serializers.Serializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicSingletons {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogicSingletons.class);

    @Autowired
    private Paths paths;

    @Autowired
    private Serializer serializer;

    @Bean
    public ContactManagement getContactManagement() {
        ContactManagement contacts;

        try {
            contacts = serializer.deserializeJson(paths.contactFile(), ContactManagement.class);
        } catch (IOException ex) {
            LOGGER.warn("Could not load contacts", ex);
            contacts = new ContactManagement();
        }

        return contacts;
    }

    @Bean
    public AppointmentCalendar getAppointmentCalendar() {
        AppointmentCalendar appointments;

        try {
            appointments = serializer.deserializeJson(paths.appointmentFile(), AppointmentCalendar.class);
        } catch (IOException ex) {
            LOGGER.warn("Could not load appointments", ex);
            appointments = new AppointmentCalendar();
        }

        return appointments;
    }
}
