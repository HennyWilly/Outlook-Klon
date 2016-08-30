package de.outlookklon.logik.mailclient;

import de.outlookklon.serializers.Serializer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.internal.util.collections.Sets;

public class StoredMailInfoTest {

    private static final String EXAMPLE_JSON = "{"
            + "  \"id\" : \"<12345.JavaMail.app@wtfLol>\","
            + "  \"read\" : true,"
            + "  \"subject\" : \"TestSubject\","
            + "  \"sender\" : {"
            + "    \"@class\" : \"javax.mail.internet.InternetAddress\","
            + "    \"address\" : \"tester@test.com\","
            + "    \"personal\" : \"Tester\""
            + "  },"
            + "  \"date\" : \"2016-08-29T00:00:00.000+0000\","
            + "  \"text\" : \"TestText\","
            + "  \"contentType\" : \"TEXT/plain; charset=utf-8\","
            + "  \"to\" : [ {"
            + "    \"@class\" : \"javax.mail.internet.InternetAddress\","
            + "    \"address\" : \"management@test.com\","
            + "    \"personal\" : null"
            + "  } ],"
            + "  \"cc\" : [ ],"
            + "  \"attachment\" : [ ]"
            + "}";

    @Test
    public void shouldCreateStoredMailInfo_WithStringID() throws Exception {
        StoredMailInfo mailInfo = new StoredMailInfo("ABCD1234");
        assertThat(mailInfo.getID(), is("ABCD1234"));
    }

    @Test
    public void shouldCreateStoredMailInfo_WithMessageAndIDHeader() throws Exception {
        Message message = mock(Message.class);
        when(message.getHeader("Message-Id")).thenReturn(new String[]{"ABCD1234"});

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getID(), is("ABCD1234"));
    }

    @Test
    public void shouldCreateStoredMailInfo_WithMimeMessageID() throws Exception {
        MimeMessage message = mock(MimeMessage.class);
        when(message.getHeader("Message-Id")).thenReturn(new String[]{});
        when(message.getMessageID()).thenReturn("ABCD1234");

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getID(), is("ABCD1234"));
    }

    @Test
    public void shouldCreateStoredMailInfo_WithMimeMessageContentID() throws Exception {
        MimeMessage message = mock(MimeMessage.class);
        when(message.getHeader("Message-Id")).thenReturn(null);
        when(message.getMessageID()).thenReturn(null);
        when(message.getContentID()).thenReturn("ABCD1234");

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getID(), is("ABCD1234"));
    }

    @Test
    public void shouldSerializeStoredMailInfo() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        mailInfo.loadData(message, EnumSet.allOf(MailContent.class));

        String json = Serializer.serializeObjectToJson(mailInfo);
        assertThat(json, jsonEquals(EXAMPLE_JSON));
    }

    @Test
    public void shouldDeserializeStoredMailInfo() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo expected = new StoredMailInfo(message);
        expected.loadData(message, EnumSet.allOf(MailContent.class));

        StoredMailInfo actual = Serializer.deserializeJson(EXAMPLE_JSON, StoredMailInfo.class);
        assertThat(actual, is(equalTo(expected)));
    }

    private Message getTestMessage() throws Exception {
        Message message = mock(Message.class);
        when(message.getHeader("Message-Id")).thenReturn(new String[]{"<12345.JavaMail.app@wtfLol>"});
        when(message.isSet(Flags.Flag.SEEN)).thenReturn(Boolean.TRUE);
        when(message.getSubject()).thenReturn("TestSubject");
        when(message.getFrom()).thenReturn(new Address[]{new InternetAddress("tester@test.com", "Tester")});
        when(message.getSentDate()).thenReturn(new DateTime(2016, 8, 29, 2, 0).toDate());
        when(message.getContent()).thenReturn("TestText");
        when(message.getContentType()).thenReturn("TEXT/plain; charset=utf-8");
        when(message.isMimeType("text/*")).thenReturn(Boolean.TRUE);
        when(message.getRecipients(Message.RecipientType.TO)).thenReturn(new Address[]{new InternetAddress("management@test.com")});
        return message;
    }

    @Test
    public void shouldBeEqual() throws Exception {
        Message message = mock(Message.class);
        when(message.getHeader("Message-Id")).thenReturn(new String[]{"ABCD1234"});

        MailInfo mailInfo1 = new StoredMailInfo(message);
        MailInfo mailInfo2 = new StoredMailInfo("ABCD1234");

        assertThat(mailInfo1, is(equalTo(mailInfo2)));
    }

    @Test
    public void shouldBeEqual_SameInstance() throws Exception {
        MailInfo mailInfo = new StoredMailInfo("ABCD1234");

        assertThat(mailInfo, is(equalTo(mailInfo)));
    }

    @Test
    public void shouldNotBeEqual_Null() throws Exception {
        MailInfo mailInfo = new StoredMailInfo("ABCD1234");

        assertThat(mailInfo, is(not(equalTo(null))));
    }

    @Test
    public void shouldNotBeEqual_OtherClass() throws Exception {
        MailInfo mailInfo = new StoredMailInfo("ABCD1234");

        assertThat(mailInfo, is(not(equalTo(new Object()))));
    }

    @Test
    public void shouldTestHashCodeContract() throws Exception {
        Map<StoredMailInfo, String> map = new HashMap<>();

        map.put(new StoredMailInfo("ABCD1234"), "aaaa");
        map.put(new StoredMailInfo("EFGH5678"), "bbbb");

        assertThat(map, hasEntry(new StoredMailInfo("ABCD1234"), "aaaa"));
        assertThat(map, hasEntry(new StoredMailInfo("EFGH5678"), "bbbb"));
    }

    @Test
    public void shouldLoadSubject() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getSubject(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.SUBJECT)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.SUBJECT));
        assertThat(mailInfo.getSubject(), is("TestSubject"));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.SUBJECT)), is(true));
    }

    @Test
    public void shouldLoadSender() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getSender(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.SENDER)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.SENDER));
        assertThat(mailInfo.getSender(), is(equalTo((Address) new InternetAddress("tester@test.com", "Tester"))));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.SENDER)), is(true));
    }

    @Test
    public void shouldLoadDate() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getDate(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.DATE)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.DATE));
        assertThat(mailInfo.getDate(), is(equalTo(new DateTime(2016, 8, 29, 2, 0).toDate())));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.DATE)), is(true));
    }

    @Test
    public void shouldLoadText() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getText(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.TEXT)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.TEXT));
        assertThat(mailInfo.getText(), is("TestText"));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.TEXT)), is(true));
    }

    @Test
    public void shouldLoadContentType() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getContentType(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.CONTENTTYPE)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.CONTENTTYPE));
        assertThat(mailInfo.getContentType(), is("TEXT/plain; charset=utf-8"));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.CONTENTTYPE)), is(true));
    }

    @Test
    public void shouldLoadTo() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getTo(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.TO)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.TO));
        assertThat(mailInfo.getTo(), contains((Address) new InternetAddress("management@test.com")));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.TO)), is(true));
    }

    @Test
    public void shouldLoadCc() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getCc(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.CC)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.CC));
        assertThat(mailInfo.getCc(), is(empty()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.CC)), is(true));
    }

    @Test
    public void shouldLoadAttachments() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getAttachment(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.ATTACHMENT)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.ATTACHMENT));
        assertThat(mailInfo.getAttachment(), is(empty()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.ATTACHMENT)), is(true));
    }

    @Test
    public void shouldLoadRead() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.isRead(), is(nullValue()));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.READ)), is(false));

        mailInfo.loadData(message, Sets.newSet(MailContent.READ));
        assertThat(mailInfo.isRead(), is(true));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.READ)), is(true));
    }

    @Test
    public void shouldLoadId() throws Exception {
        Message message = getTestMessage();

        StoredMailInfo mailInfo = new StoredMailInfo(message);
        assertThat(mailInfo.getID(), is("<12345.JavaMail.app@wtfLol>"));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.ID)), is(true));

        mailInfo.loadData(message, Sets.newSet(MailContent.READ));
        assertThat(mailInfo.getID(), is("<12345.JavaMail.app@wtfLol>"));
        assertThat(mailInfo.hasAlreadyLoadedData(Sets.newSet(MailContent.ID)), is(true));
    }
}
