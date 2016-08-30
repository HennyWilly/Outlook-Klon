package de.outlookklon.logik.mailclient.javamail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.mail.MessagingException;
import javax.mail.Service;
import javax.mail.Session;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceWrapper<T extends Service> implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceWrapper.class);

    private final T service;

    public ServiceWrapper(@NonNull T service) {
        this.service = service;
    }

    public T getService() {
        return service;
    }

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
                LOGGER.error("Could not close " + getServiceClassName() + " object", ex);
            }
        }
    }

    public String getServiceClassName() {
        return getService().getClass().getSimpleName();
    }
}
