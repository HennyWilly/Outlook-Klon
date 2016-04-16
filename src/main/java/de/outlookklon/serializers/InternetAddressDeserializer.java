package de.outlookklon.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

/**
 * This is a custom {@link com.fasterxml.jackson.databind.JsonDeserializer} that
 * helps deserializing non-public sub classes of
 * {@link javax.mail.internet.InternetAddress}.
 *
 * @author Hendrik Karwanni
 */
public class InternetAddressDeserializer extends JsonDeserializer<Address> {

    @Override
    public Address deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        if (p.getCurrentToken() == JsonToken.START_OBJECT) {
            p.nextToken();
        }

        JsonNode node = p.getCodec().readTree(p);

        String address = null;
        String personal = null;

        Iterator<Entry<String, JsonNode>> outerIterator = node.fields();
        while (outerIterator.hasNext()) {
            Entry<String, JsonNode> entry = outerIterator.next();
            String name = entry.getKey();
            JsonNode jsonNode = entry.getValue();

            if ("address".equals(name)) {
                address = jsonNode.asText(null);
            } else if ("personal".equals(name)) {
                personal = jsonNode.asText(null);
            } else if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                throw ctxt.mappingException(InternetAddress.class, jsonNode.asToken());
            }
        }

        return new InternetAddress(address, personal);
    }
}
