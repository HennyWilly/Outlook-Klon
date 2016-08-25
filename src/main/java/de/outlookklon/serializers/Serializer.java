package de.outlookklon.serializers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.outlookklon.serializers.mixins.AddressMixIn;
import de.outlookklon.serializers.mixins.InternetAddressMixIn;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * This class manages serialization/deserialization of Objects to JSON and vise
 * versa.
 *
 * @author Hendrik Karwanni
 */
@Service
@Scope(value = "singleton")
public class Serializer {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final ObjectMapper mapper;

    public Serializer() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        mapper.addMixIn(Address.class, AddressMixIn.class);
        mapper.addMixIn(InternetAddress.class, InternetAddressMixIn.class);

        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    }

    /**
     * Deserializes a given {@link java.io.File} into an object of type
     * {@code T}.
     *
     * @param <T> type of the deserialized instance
     * @param target target file for deserialization
     * @param clazz target type of resulting object
     * @return a deserialized object
     * @throws IOException if deserialization fails
     */
    public <T> T deserializeJson(File target, Class<T> clazz) throws IOException {
        return mapper.readValue(target, clazz);
    }

    /**
     * Deserializes a given {@link String} into an object of type {@code T}.
     *
     * @param <T> type of the deserialized instance
     * @param jsonString string to be deserialized
     * @param clazz target type of resulting object
     * @return a deserialized object
     * @throws IOException if deserialization fails
     */
    public <T> T deserializeJson(String jsonString, Class<T> clazz) throws IOException {
        return mapper.readValue(jsonString, clazz);
    }

    /**
     * Deserializes a given {@link java.io.File} into a {@link String}.
     *
     * @param target target file for deserialization
     * @return a deserialized string
     * @throws IOException if deserialization fails
     */
    public String deserializePlainText(File target) throws IOException {
        return FileUtils.readFileToString(target, CHARSET);
    }

    /**
     * Serializes an object of type {@code T} into a {@link java.io.File}.
     *
     * @param <T> type of the serialized instance
     * @param target target file for serialization
     * @param value object to be serialized
     * @throws IOException if serialization fails
     */
    public <T> void serializeObjectToJson(File target, T value) throws IOException {
        mapper.writeValue(target, value);
    }

    /**
     * Serializes an object of type {@code T} into a {@link String}.
     *
     * @param <T> type of the serialized instance
     * @param value object to be serialized
     * @return JSON as String
     * @throws IOException if serialization fails
     */
    public <T> String serializeObjectToJson(T value) throws IOException {
        return mapper.writeValueAsString(value);
    }

    /**
     * Serializes a given {@link String} into a {@link java.io.File}.
     *
     * @param target target file for serialization
     * @param value string to be serialized
     * @throws IOException if serialization fails
     */
    public void serializeStringToPlainText(File target, String value) throws IOException {
        FileUtils.writeStringToFile(target, value, CHARSET);
    }
}
