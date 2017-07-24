package de.outlookklon.application.mailclient.javamail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.mail.MessagingException;
import javax.mail.Service;
import javax.mail.Session;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse kapselt einen Service, damit dieser mittels try-with-ressources
 * geschlossen werden kann.
 *
 * @author Hendrik Karwanni
 * @param <T> Service-Typ
 */
public class ServiceWrapper<T extends Service> implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceWrapper.class);

    @Getter
    private final T service;

    /**
     * Erstellt eine neue Instanz der Klasse mit dem übergebenen zu kapselnden
     * Objekt.
     *
     * @param service Der zu kapselnde Service
     */
    public ServiceWrapper(@NonNull T service) {
        this.service = service;
    }

    /**
     * Gibt das Session-Objekt des Services zurück.
     *
     * @return Session-Objekt des Services
     */
    @SneakyThrows({NoSuchMethodException.class, IllegalAccessException.class, InvocationTargetException.class})
    public Session getSession() {
        Method getSessionMethod = Service.class.getDeclaredMethod("getSession");
        getSessionMethod.setAccessible(true);

        try {
            return (Session) getSessionMethod.invoke(service);
        } finally {
            getSessionMethod.setAccessible(false);
        }
    }

    @Override
    public void close() {
        T serviceToClose = getService();

        if (serviceToClose.isConnected()) {
            try {
                serviceToClose.close();
            } catch (MessagingException ex) {
                LOGGER.warn("Could not close " + getServiceClassName() + " object", ex);
            }
        }
    }

    /**
     * Gibt den Klassennamen des gekapselten Services zurück.
     *
     * @return Klassenname des gekapselten Services
     */
    public String getServiceClassName() {
        return getService().getClass().getSimpleName();
    }
}
