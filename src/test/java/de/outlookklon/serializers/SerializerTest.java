package de.outlookklon.serializers;

import static de.outlookklon.matchers.UtilityMatchers.isWellDefinedUtilityClass;
import java.io.File;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SerializerTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldCheckIfUtilityClassIsWellCoded() throws Exception {
        assertThat(Serializer.class, isWellDefinedUtilityClass());
    }

    @Test
    public void shouldSerializeAndDeserializeJodaDateTime_AsString() throws Exception {
        DateTime dateTime = new DateTime();
        String json = Serializer.serializeObjectToJson(dateTime);

        try {
            Long.parseLong(json);
            assertThat("json is a timestamp", false);
        } catch (NumberFormatException ex) {
            // OK ;-)
        }

        assertThat(Serializer.deserializeJson(json, DateTime.class), is(equalTo(dateTime)));
    }

    @Test
    public void shouldSerializeAndDeserializeJodaDateTime_AsFile() throws Exception {
        DateTime dateTime = new DateTime();
        File jsonFile = folder.newFile();
        Serializer.serializeObjectToJson(jsonFile, dateTime);
        assertThat(Serializer.deserializeJson(jsonFile, DateTime.class), is(equalTo(dateTime)));
    }

    @Test
    public void shouldSerializeAndDeserializePlainText() throws Exception {
        String testString = "This is an awesome message";
        File jsonFile = folder.newFile();
        Serializer.serializeStringToPlainText(jsonFile, testString);
        assertThat(Serializer.deserializePlainText(jsonFile), is(equalTo(testString)));
    }
}
