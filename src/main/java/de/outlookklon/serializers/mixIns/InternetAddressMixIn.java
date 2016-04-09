package de.outlookklon.serializers.mixIns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.outlookklon.serializers.InternetAddressDeserializer;

/**
 * MixIn for class {@link javax.mail.internet.InternetAddress}. In order to
 * deserialize non-public sub classes of
 * {@link javax.mail.internet.InternetAddress}, a custom
 * {@link com.fasterxml.jackson.databind.JsonDeserializer} is used.
 *
 * @author Hendrik Karwanni
 */
@JsonDeserialize(using = InternetAddressDeserializer.class)
public abstract class InternetAddressMixIn {

    /**
     * Ignore method {@link javax.mail.internet.InternetAddress#isGroup()}
     * during serialization.
     *
     * @return Some interesting stuff....
     */
    @JsonIgnore
    public abstract boolean isGroup();
}
