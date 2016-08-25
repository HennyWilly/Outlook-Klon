package de.outlookklon.serializers.mixins;

import de.outlookklon.serializers.Serializer;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class InternetAddressMixInTest {

    private static final String TEST_IMAP_ADDRESS
            = "{"
            + "  \"@class\" : \"com.sun.mail.imap.protocol.IMAPAddress\","
            + "  \"address\" : \"test@test.net\","
            + "  \"personal\" : \"Test Person\""
            + "}";

    @Autowired
    private Serializer serializer;

    @Test
    public void shouldDeserializeInternetAddress() throws Exception {
        Address deserialized = serializer.deserializeJson(TEST_IMAP_ADDRESS, Address.class);
        Address expected = new InternetAddress("test@test.net", "Test Person");

        assertThat(deserialized, is(instanceOf(InternetAddress.class)));
        assertThat(deserialized, is(equalTo(expected)));
    }

    @Configuration
    public static class InternetAddressMixInTestConfiguration {

        @Bean
        public Serializer getSerializer() {
            return spy(new Serializer());
        }
    }
}
