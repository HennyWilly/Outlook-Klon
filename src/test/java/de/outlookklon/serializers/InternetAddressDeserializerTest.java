package de.outlookklon.serializers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
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
public class InternetAddressDeserializerTest {

    private static final String EXAMPLE_JSON
            = "{"
            + "  \"@class\" : \"javax.mail.internet.InternetAddress\","
            + "  \"address\" : \"henny-willy@test.de\","
            + "  \"personal\" : \"Henny The Willy\""
            + "}";

    private JsonFactory factory;
    private DeserializationContext context;
    private InternetAddressDeserializer deserializer;

    @Autowired
    private Serializer serializer;

    @Before
    public void init() throws Exception {
        ObjectMapper mapper = getSerializerMapper();
        factory = spy(mapper.getFactory());
        context = mapper.getDeserializationContext();

        deserializer = new InternetAddressDeserializer();
    }

    private ObjectMapper getSerializerMapper() throws Exception {
        Field f = Serializer.class.getDeclaredField("mapper");
        f.setAccessible(true);

        try {
            return spy((ObjectMapper) f.get(serializer));
        } finally {
            f.setAccessible(false);
        }
    }

    @Test
    public void shouldDeserializeAddress() throws Exception {
        Address expected = new InternetAddress("henny-willy@test.de", "Henny The Willy");

        JsonParser parser = factory.createParser(EXAMPLE_JSON);
        Address actual = deserializer.deserialize(parser, context);

        assertThat(actual, is(expected));
    }

    @Configuration
    public static class InternetAddressDeserializerTestConfiguration {

        @Bean
        public Serializer getSerializer() {
            return spy(new Serializer());
        }
    }
}
