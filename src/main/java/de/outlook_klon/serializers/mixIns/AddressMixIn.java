package de.outlook_klon.serializers.mixIns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * MixIn for class {@link javax.mail.Address}. Adds a class id to the serialized
 * data in order to store type information.
 *
 * @author Hendrik Karwanni
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class AddressMixIn {

    /**
     * Ignore method {@link javax.mail.Address#getType()} during serialization.
     *
     * @return Some interesting stuff....
     */
    @JsonIgnore
    public abstract String getType();
}
