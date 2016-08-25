package de.outlookklon.serializers;

import java.io.File;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SerializerTest {

    @Autowired
    private Serializer serializer;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldSerializeAndDeserializeJodaDateTime_AsString() throws Exception {
        DateTime dateTime = new DateTime();
        String json = serializer.serializeObjectToJson(dateTime);

        try {
            Long.parseLong(json);
            assertThat("json is a timestamp", false);
        } catch (NumberFormatException ex) {
            // OK ;-)
        }

        assertThat(serializer.deserializeJson(json, DateTime.class), is(equalTo(dateTime)));
    }

    @Test
    public void shouldSerializeAndDeserializeJodaDateTime_AsFile() throws Exception {
        DateTime dateTime = new DateTime();
        File jsonFile = folder.newFile();
        serializer.serializeObjectToJson(jsonFile, dateTime);
        assertThat(serializer.deserializeJson(jsonFile, DateTime.class), is(equalTo(dateTime)));
    }

    @Test
    public void shouldSerializeAndDeserializePlainText() throws Exception {
        String testString = "This is an awesome message";
        File jsonFile = folder.newFile();
        serializer.serializeStringToPlainText(jsonFile, testString);
        assertThat(serializer.deserializePlainText(jsonFile), is(equalTo(testString)));
    }

    @Configuration
    public static class SerializerTestConfiguration {

        @Bean
        public Serializer getSerializer() {
            return spy(new Serializer());
        }
    }
}
