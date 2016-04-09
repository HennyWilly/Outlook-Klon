package de.outlookklon.serializers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.outlookklon.serializers.mixins.AddressMixIn;
import de.outlookklon.serializers.mixins.InternetAddressMixIn;
import java.io.File;
import java.io.IOException;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import org.apache.commons.io.FileUtils;

/**
 * This class manages serialization/deserialization of Objects to JSON and vise
 * versa.
 *
 * @author Hendrik Karwanni
 */
public final class Serializer {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        MAPPER.addMixIn(Address.class, AddressMixIn.class);
        MAPPER.addMixIn(InternetAddress.class, InternetAddressMixIn.class);
    }

    private Serializer() {
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
    public static <T> T deserializeJson(File target, Class<T> clazz) throws IOException {
        return MAPPER.readValue(target, clazz);
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
    public static <T> T deserializeJson(String jsonString, Class<T> clazz) throws IOException {
        return MAPPER.readValue(jsonString, clazz);
    }

    /**
     * Deserializes a given {@link java.io.File} into a {@link String}.
     *
     * @param target target file for deserialization
     * @return a deserialized string
     * @throws IOException if deserialization fails
     */
    public static String deserializePlainText(File target) throws IOException {
        return FileUtils.readFileToString(target);
    }

    /**
     * Serializes an object of type {@code T} into a {@link java.io.File}.
     *
     * @param <T> type of the serialized instance
     * @param target target file for serialization
     * @param value object to be serialized
     * @throws IOException if serialization fails
     */
    public static <T> void serializeObjectToJson(File target, T value) throws IOException {
        MAPPER.writeValue(target, value);
    }

    /**
     * Serializes an object of type {@code T} into a {@link String}.
     *
     * @param <T> type of the serialized instance
     * @param value object to be serialized
     * @return JSON as String
     * @throws IOException if serialization fails
     */
    public static <T> String serializeObjectToJson(T value) throws IOException {
        return MAPPER.writeValueAsString(value);
    }

    /**
     * Serializes a given {@link String} into a {@link java.io.File}.
     *
     * @param target target file for serialization
     * @param value string to be serialized
     * @throws IOException if serialization fails
     */
    public static void serializeStringToPlainText(File target, String value) throws IOException {
        FileUtils.writeStringToFile(target, value);
    }
}
