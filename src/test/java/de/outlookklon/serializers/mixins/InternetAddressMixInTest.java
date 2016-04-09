package de.outlookklon.serializers.mixins;

import de.outlookklon.serializers.Serializer;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * @author Hendrik Karwanni
 */
public class InternetAddressMixInTest {

    private static final String TEST_IMAP_ADDRESS
            = "{"
            + "  \"@class\" : \"com.sun.mail.imap.protocol.IMAPAddress\","
            + "  \"address\" : \"test@test.net\","
            + "  \"personal\" : \"Test Person\""
            + "}";

    @Test
    public void shouldDeserializeInternetAddress() throws Exception {
        Address deserialized = Serializer.deserializeJson(TEST_IMAP_ADDRESS, Address.class);
        Address expected = new InternetAddress("test@test.net", "Test Person");

        assertThat(deserialized, is(instanceOf(InternetAddress.class)));
        assertThat(deserialized, is(equalTo(expected)));
    }
}
