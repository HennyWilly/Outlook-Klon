package de.outlookklon.application.mailclient;

import com.sun.mail.imap.IMAPFolder;
import de.outlookklon.model.mails.AuthentificationType;
import de.outlookklon.model.mails.ConnectionSecurity;
import de.outlookklon.model.mails.FolderInfo;
import de.outlookklon.model.mails.SendMailInfo;
import de.outlookklon.model.mails.ServerSettings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MailAccountTest {

    private InboxServer workingInbox;
    private OutboxServer workingOutbox;
    private MailAccount workingAccount;

    @Before
    public void init() throws Exception {
        workingInbox = spy(new ImapServer(new ServerSettings("testHost.com", 1234, ConnectionSecurity.NONE, AuthentificationType.NORMAL)));
        workingOutbox = spy(new SmtpServer(new ServerSettings("testHost.org", 5678, ConnectionSecurity.NONE, AuthentificationType.NORMAL)));

        workingAccount = spy(new MailAccount(workingInbox, workingOutbox, new InternetAddress("test@test.com"), "MyUser", "Abcd1234"));
    }

    @Test
    public void shouldCreateMailAccount() throws Exception {
        InboxServer inServer = new ImapServer(new ServerSettings("testHost.com", 1234, ConnectionSecurity.NONE, AuthentificationType.NORMAL));
        OutboxServer outServer = new SmtpServer(new ServerSettings("testHost.org", 5678, ConnectionSecurity.NONE, AuthentificationType.NORMAL));

        MailAccount account = new MailAccount(inServer, outServer, new InternetAddress("test@test.com"), "MyUser", "Abcd1234");
        assertThat(account.getUser(), is("MyUser"));
        assertThat(account.getAddress(), is(equalTo(new InternetAddress("test@test.com"))));
        assertThat(account.getInboxMailServer(), is(equalTo(inServer)));
        assertThat(account.getOutboxMailServer(), is(equalTo(outServer)));
        assertThat(account, hasToString("test@test.com"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateMailAccount_InboxServerNull() throws Exception {
        new MailAccount(null, mock(OutboxServer.class), new InternetAddress("test@test.com"), "", "").toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateMailAccount_OutboxServerNull() throws Exception {
        new MailAccount(mock(InboxServer.class), null, new InternetAddress("test@test.com"), "", "").toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateMailAccount_AddressNull() throws Exception {
        new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), null, "", "").toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateMailAccount_UserNull() throws Exception {
        new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), null, "").toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateMailAccount_PasswordNull() throws Exception {
        new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), "", null).toString();
    }

    @Test
    public void shouldBeEqual() throws Exception {
        MailAccount mailAccount1 = new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), "", "");
        MailAccount mailAccount2 = new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), "", "");

        assertThat(mailAccount1, is(equalTo(mailAccount2)));
    }

    @Test
    public void shouldBeEqual_SameInstance() throws Exception {
        MailAccount mailAccount = new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), "", "");

        assertThat(mailAccount, is(equalTo(mailAccount)));
    }

    @Test
    public void shouldNotBeEqual_Null() throws Exception {
        MailAccount mailAccount = new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), "", "");

        assertThat(mailAccount, is(not(equalTo(null))));
    }

    @Test
    public void shouldNotBeEqual_OtherClass() throws Exception {
        MailAccount mailAccount = new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), "", "");

        assertThat(mailAccount, is(not(equalTo(new Object()))));
    }

    @Test
    public void shouldTestHashCodeContract() throws Exception {
        Map<MailAccount, String> map = new HashMap<>();

        map.put(new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), "", ""), "aaaa");
        map.put(new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.net"), "", ""), "bbbb");

        assertThat(map, hasEntry(new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.com"), "", ""), "aaaa"));
        assertThat(map, hasEntry(new MailAccount(mock(InboxServer.class), mock(OutboxServer.class), new InternetAddress("test@test.net"), "", ""), "bbbb"));
    }

    @Test
    public void shouldValidateLogin() throws Exception {
        String user = "MyUser";
        String pw = "Abcd1234";

        InboxServer inServer = mock(InboxServer.class);
        OutboxServer outServer = mock(OutboxServer.class);

        when(inServer.checkLogin(user, pw)).thenReturn(Boolean.TRUE);
        when(outServer.checkLogin(user, pw)).thenReturn(Boolean.TRUE);

        MailAccount account = new MailAccount(inServer, outServer, new InternetAddress("test@test.com"), user, pw);
        assertThat(account.validate(), is(true));
    }

    @Test
    public void shouldNotValidateLogin_InboxFailed() throws Exception {
        String user = "MyUser";
        String pw = "Abcd1234";

        InboxServer inServer = mock(InboxServer.class);
        OutboxServer outServer = mock(OutboxServer.class);

        when(inServer.checkLogin(user, pw)).thenReturn(Boolean.FALSE);
        when(outServer.checkLogin(user, pw)).thenReturn(Boolean.TRUE);

        MailAccount account = new MailAccount(inServer, outServer, new InternetAddress("test@test.com"), user, pw);
        assertThat(account.validate(), is(false));
    }

    @Test
    public void shouldNotValidateLogin_OutboxFailed() throws Exception {
        String user = "MyUser";
        String pw = "Abcd1234";

        InboxServer inServer = mock(InboxServer.class);
        OutboxServer outServer = mock(OutboxServer.class);

        when(inServer.checkLogin(user, pw)).thenReturn(Boolean.TRUE);
        when(outServer.checkLogin(user, pw)).thenReturn(Boolean.FALSE);

        MailAccount account = new MailAccount(inServer, outServer, new InternetAddress("test@test.com"), user, pw);
        assertThat(account.validate(), is(false));
    }

    @Test
    public void shouldSendMail_NoMultipleFolders() throws Exception {
        SendMailInfo mailToSend = new SendMailInfo("TestMail", "This is a test mail", "text/plain",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")), null, null);

        doReturn(mock(Message.class)).when(workingOutbox).sendMail(any(String.class), any(String.class), any(SendMailInfo.class));
        doReturn(false).when(workingInbox).supportsMultipleFolders();

        workingAccount.sendMail(mailToSend);
    }

    @Test
    public void shouldSendMail_MultipleFolders_NoSendFolder() throws Exception {
        SendMailInfo mailToSend = new SendMailInfo("TestMail", "This is a test mail", "text/plain",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")), null, null);

        Folder rootFolder = mock(Folder.class);
        when(rootFolder.list(any(String.class))).thenReturn(new Folder[]{});

        Store inboxMailStore = mock(Store.class);
        when(inboxMailStore.getDefaultFolder()).thenReturn(rootFolder);
        when(inboxMailStore.isConnected()).thenReturn(true);

        doReturn(mock(Message.class)).when(workingOutbox).sendMail(any(String.class), any(String.class), any(SendMailInfo.class));
        doReturn(true).when(workingInbox).supportsMultipleFolders();
        doReturn(inboxMailStore).when(workingInbox).getMailStore(any(String.class), any(String.class));

        workingAccount.sendMail(mailToSend);
    }

    @Test
    public void shouldSendMail_MultipleFolders_DefaultSendFolder() throws Exception {
        SendMailInfo mailToSend = new SendMailInfo("TestMail", "This is a test mail", "text/plain",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")), null, null);

        Folder sendFolder = mock(Folder.class);
        when(sendFolder.getName()).thenReturn("Sent");

        Folder rootFolder = mock(Folder.class);
        when(rootFolder.list(any(String.class))).thenReturn(new Folder[]{sendFolder});

        Store inboxMailStore = mock(Store.class);
        when(inboxMailStore.getDefaultFolder()).thenReturn(rootFolder);
        when(inboxMailStore.isConnected()).thenReturn(true);

        doReturn(mock(Message.class)).when(workingOutbox).sendMail(any(String.class), any(String.class), any(SendMailInfo.class));
        doReturn(true).when(workingInbox).supportsMultipleFolders();
        doReturn(inboxMailStore).when(workingInbox).getMailStore(any(String.class), any(String.class));

        workingAccount.sendMail(mailToSend);
        verify(sendFolder).appendMessages(any(Message[].class));
    }

    @Test
    public void shouldSendMail_MultipleFolders_IMAPSendFolder() throws Exception {
        SendMailInfo mailToSend = new SendMailInfo("TestMail", "This is a test mail", "text/plain",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")), null, null);

        IMAPFolder sendFolder = mock(IMAPFolder.class);
        when(sendFolder.getAttributes()).thenReturn(new String[]{"\\Sent"});

        Folder rootFolder = mock(Folder.class);
        when(rootFolder.list(any(String.class))).thenReturn(new Folder[]{sendFolder});

        Store inboxMailStore = mock(Store.class);
        when(inboxMailStore.getDefaultFolder()).thenReturn(rootFolder);
        when(inboxMailStore.isConnected()).thenReturn(true);

        doReturn(mock(Message.class)).when(workingOutbox).sendMail(any(String.class), any(String.class), any(SendMailInfo.class));
        doReturn(true).when(workingInbox).supportsMultipleFolders();
        doReturn(inboxMailStore).when(workingInbox).getMailStore(any(String.class), any(String.class));

        workingAccount.sendMail(mailToSend);
        verify(sendFolder).appendMessages(any(Message[].class));
    }

    @Test
    public void shouldSendMail_MultipleFolders_IMAPSendFolder_FallbackToName() throws Exception {
        SendMailInfo mailToSend = new SendMailInfo("TestMail", "This is a test mail", "text/plain",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")), null, null);

        IMAPFolder sendFolder = mock(IMAPFolder.class);
        when(sendFolder.getAttributes()).thenReturn(new String[]{"\\Invalid"});
        when(sendFolder.getName()).thenReturn("Sent");

        Folder rootFolder = mock(Folder.class);
        when(rootFolder.list(any(String.class))).thenReturn(new Folder[]{sendFolder});

        Store inboxMailStore = mock(Store.class);
        when(inboxMailStore.getDefaultFolder()).thenReturn(rootFolder);
        when(inboxMailStore.isConnected()).thenReturn(true);

        doReturn(mock(Message.class)).when(workingOutbox).sendMail(any(String.class), any(String.class), any(SendMailInfo.class));
        doReturn(true).when(workingInbox).supportsMultipleFolders();
        doReturn(inboxMailStore).when(workingInbox).getMailStore(any(String.class), any(String.class));

        workingAccount.sendMail(mailToSend);
        verify(sendFolder).appendMessages(any(Message[].class));
    }

    @Test(expected = MessagingException.class)
    public void shouldNotSendMail() throws Exception {
        SendMailInfo mailToSend = new SendMailInfo("TestMail", "This is a test mail", "text/plain",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")), null, null);

        doThrow(new MessagingException()).when(workingOutbox).sendMail(any(String.class), any(String.class), any(SendMailInfo.class));

        workingAccount.sendMail(mailToSend);
    }

    @Test(expected = MessagingException.class)
    public void shouldSendMail_ButFailsToSaveMailInSendFolder() throws Exception {
        SendMailInfo mailToSend = new SendMailInfo("TestMail", "This is a test mail", "text/plain",
                Arrays.<Address>asList(new InternetAddress("tester@test.com")), null, null);

        Folder sendFolder = mock(Folder.class);
        when(sendFolder.getName()).thenReturn("Sent");
        doThrow(new MessagingException()).when(sendFolder).appendMessages(any(Message[].class));

        Folder rootFolder = mock(Folder.class);
        when(rootFolder.list(any(String.class))).thenReturn(new Folder[]{sendFolder});

        Store inboxMailStore = mock(Store.class);
        when(inboxMailStore.getDefaultFolder()).thenReturn(rootFolder);
        when(inboxMailStore.isConnected()).thenReturn(true);

        doReturn(mock(Message.class)).when(workingOutbox).sendMail(any(String.class), any(String.class), any(SendMailInfo.class));
        doReturn(true).when(workingInbox).supportsMultipleFolders();
        doReturn(inboxMailStore).when(workingInbox).getMailStore(any(String.class), any(String.class));

        workingAccount.sendMail(mailToSend);
    }

    @Test
    public void shouldGetFolderStructure() throws Exception {
        Folder f1 = mock(Folder.class);
        when(f1.getName()).thenReturn("TestFolder1");
        when(f1.getFullName()).thenReturn("TestFolder1");

        Folder f2 = mock(Folder.class);
        when(f2.getName()).thenReturn("TestFolder2");
        when(f2.getFullName()).thenReturn("TestFolder1/TestFolder2");

        Folder f3 = mock(Folder.class);
        when(f3.getName()).thenReturn("TestFolder3");
        when(f3.getFullName()).thenReturn("TestFolder3");

        Folder rootFolder = mock(Folder.class);
        when(rootFolder.list(any(String.class))).thenReturn(new Folder[]{f1, f2, f3});

        Store inboxMailStore = mock(Store.class);
        when(inboxMailStore.getDefaultFolder()).thenReturn(rootFolder);
        when(inboxMailStore.isConnected()).thenReturn(true);

        doReturn(inboxMailStore).when(workingInbox).getMailStore(any(String.class), any(String.class));

        FolderInfo[] folders = workingAccount.getFolderStructure();
        assertThat(Arrays.asList(folders), containsInAnyOrder(
                new FolderInfo("TestFolder1", "TestFolder1", 0),
                new FolderInfo("TestFolder2", "TestFolder1/TestFolder2", 0),
                new FolderInfo("TestFolder3", "TestFolder3", 0)
        ));
    }
}
