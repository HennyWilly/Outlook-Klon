package de.outlookklon.logik.mailclient;

import java.util.Arrays;
import java.util.List;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import org.junit.Test;

public class MailInfoTest {

    @Test
    public void shouldCreateMailInfo_ContentTypeNull() throws Exception {
        MailInfo mailInfo = new MailInfoImpl("TestSubject", "TestText", null,
                Arrays.<Address>asList(new InternetAddress("tester@test.com")),
                Arrays.<Address>asList(new InternetAddress("management@test.com")),
                Arrays.asList("/a/test/path.txt"));

        Address[] expectedTo = new Address[]{new InternetAddress("tester@test.com")};
        Address[] expectedCc = new Address[]{new InternetAddress("management@test.com")};
        String[] expectedAttachments = new String[]{"/a/test/path.txt"};

        assertThat(mailInfo.getSubject(), is("TestSubject"));
        assertThat(mailInfo.getText(), is("TestText"));
        assertThat(mailInfo.getContentType(), is(nullValue()));
        assertThat(mailInfo.getSender(), is(nullValue()));
        assertThat(mailInfo.getTo(), contains(expectedTo));
        assertThat(mailInfo.getCc(), contains(expectedCc));
        assertThat(mailInfo.getAttachment(), contains(expectedAttachments));
    }

    private class MailInfoImpl extends MailInfo {

        public MailInfoImpl(String subject, String text, String contentType, List<Address> to, List<Address> cc, List<String> attachment) {
            super(subject, text, contentType, to, cc, attachment);
        }

    }
}
