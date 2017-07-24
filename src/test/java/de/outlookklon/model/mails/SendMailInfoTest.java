package de.outlookklon.model.mails;

import de.outlookklon.serializers.Serializer;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Session.class)
public class SendMailInfoTest {

    private static final String EXAMPLE_JSON = "{"
            + "  \"subject\" : \"TestSubject\","
            + "  \"sender\" : null,"
            + "  \"text\" : \"TestText\","
            + "  \"contentType\" : \"TEXT/plain; charset=utf-8\","
            + "  \"to\" : [ {"
            + "    \"@class\" : \"javax.mail.internet.InternetAddress\","
            + "    \"address\" : \"tester@test.com\","
            + "    \"personal\" : null"
            + "  } ],"
            + "  \"cc\" : [ {"
            + "    \"@class\" : \"javax.mail.internet.InternetAddress\","
            + "    \"address\" : \"management@test.com\","
            + "    \"personal\" : null"
            + "  } ],"
            + "  \"attachment\" : [ "
            + "     \"/a/test/path.txt\""
            + " ]"
            + "}";

    @Test
    public void shouldSerializeStoredMailInfo() throws Exception {
        SendMailInfo mailInfo = new SendMailInfo("TestSubject", "TestText", "TEXT/plain; charset=utf-8",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")),
                Arrays.<Address>asList(new InternetAddress("management@test.com")),
                Arrays.asList("/a/test/path.txt"));

        String json = Serializer.serializeObjectToJson(mailInfo);
        assertThat(json, jsonEquals(EXAMPLE_JSON));
    }

    @Test
    public void shouldDeserializeStoredMailInfo() throws Exception {
        SendMailInfo expected = new SendMailInfo("TestSubject", "TestText", "TEXT/plain; charset=utf-8",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")),
                Arrays.<Address>asList(new InternetAddress("management@test.com")),
                Arrays.asList("/a/test/path.txt"));

        SendMailInfo actual = Serializer.deserializeJson(EXAMPLE_JSON, SendMailInfo.class);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldCreateMessageToSend() throws Exception {
        SendMailInfo info = new SendMailInfo("TestSubject", "TestText", "TEXT/plain; charset=utf-8",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")),
                Arrays.<Address>asList(new InternetAddress("management@test.com")),
                Arrays.asList("/a/test/path.txt"));
        Message message = info.createMessage(getSession());

        MimeMultipart content = (MimeMultipart) message.getContent();
        MimeBodyPart body = (MimeBodyPart) content.getBodyPart(0);
        String text = (String) body.getContent();

        MimeBodyPart attachmentPart = (MimeBodyPart) content.getBodyPart(1);

        assertThat(message.getSubject(), is("TestSubject"));
        assertThat(text, is("TestText"));
        assertThat(message.getContentType(), equalToIgnoringCase("TEXT/plain"));
        assertThat(Arrays.asList(message.getRecipients(Message.RecipientType.TO)), contains(new Address[]{new InternetAddress("tester@test.com")}));
        assertThat(Arrays.asList(message.getRecipients(Message.RecipientType.CC)), contains(new Address[]{new InternetAddress("management@test.com")}));
        assertThat(attachmentPart.getFileName(), is("path.txt"));
        assertThat(attachmentPart.getDisposition(), is(Part.ATTACHMENT));
    }

    private Session getSession() {
        Session session = mock(Session.class);
        when(session.getProperties()).thenReturn(new Properties());
        return session;
    }

    @Test
    public void shouldCreateMessageToSend_AttachmentIsNull() throws Exception {
        SendMailInfo info = new SendMailInfo("TestSubject", "TestText", "TEXT/plain; charset=utf-8",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")),
                Arrays.<Address>asList(new InternetAddress("management@test.com")),
                null);
        Message message = info.createMessage(getSession());

        MimeMultipart content = (MimeMultipart) message.getContent();
        MimeBodyPart body = (MimeBodyPart) content.getBodyPart(0);
        String text = (String) body.getContent();

        assertThat(message.getSubject(), is("TestSubject"));
        assertThat(text, is("TestText"));
        assertThat(message.getContentType(), equalToIgnoringCase("TEXT/plain"));
        assertThat(Arrays.asList(message.getRecipients(Message.RecipientType.TO)), contains(new Address[]{new InternetAddress("tester@test.com")}));
        assertThat(Arrays.asList(message.getRecipients(Message.RecipientType.CC)), contains(new Address[]{new InternetAddress("management@test.com")}));
        assertThat(content.getCount(), is(1));
    }
}
