package de.outlookklon.dao.impl;

import de.outlookklon.dao.DAOException;
import de.outlookklon.dao.StoredMailInfoDAO;
import de.outlookklon.logik.mailclient.StoredMailInfo;
import de.outlookklon.serializers.Serializer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration
public class StoredMailInfoDAOFilePersistenceTest {

    private static final String ID = "abcd1234";
    private static final StoredMailInfo TEST_MAIL_INFO = new StoredMailInfo(ID);
    private static final String PATH = "aPath/bPath";

    private static final String MAILADDRESS = "tester@test.com";

    @ClassRule
    public static final TemporaryFolder FOLDER = new TemporaryFolder();

    @Autowired
    private Serializer serializer;

    @Autowired
    private StoredMailInfoDAO dao;

    @Autowired
    private String accountFolderPattern;

    @Before
    public void init() throws Exception {
        dao.saveStoredMailInfo(MAILADDRESS, TEST_MAIL_INFO, PATH);
    }

    @After
    public void clear() throws Exception {
        if (dao.loadStoredMailInfo(MAILADDRESS, ID, PATH) != null) {
            dao.deleteStoredMailInfo(MAILADDRESS, ID, PATH);
        }
    }

    @Test
    public void shouldNotLoadStoredMailInfo_FileDoesNotExist() throws Exception {
        String nonExistentID = "ANonExistantID";
        String path = "aPath";

        StoredMailInfo mailInfo = dao.loadStoredMailInfo(MAILADDRESS, nonExistentID, path);
        assertThat(mailInfo, is(nullValue()));
    }

    @Test(expected = DAOException.class)
    public void shouldNotLoadStoredMailInfo_InvalidFile() throws Exception {
        String id = "AInvalidFile";
        String fileName = id + ".json";
        String path = "aPath";

        File folderPath = new File(String.format(accountFolderPattern, MAILADDRESS), path);
        File file = new File(folderPath, fileName);
        FileUtils.writeStringToFile(file, "Invalid data for a StoredMailInfo", Charset.defaultCharset());

        dao.loadStoredMailInfo(MAILADDRESS, id, path);
    }

    @Test
    public void shouldLoadStoredMailInfo() throws Exception {
        StoredMailInfo expResult = TEST_MAIL_INFO;
        StoredMailInfo result = dao.loadStoredMailInfo(MAILADDRESS, ID, PATH);
        assertThat(result, is(equalTo(expResult)));
    }

    @Test(expected = DAOException.class)
    public void shouldNotSaveStoredMailInfo_FileInUse() throws Exception {
        doThrow(new IOException()).when(serializer).serializeObjectToJson(any(File.class), any(Object.class));

        String id = "anID";
        String path = "aPath";

        StoredMailInfo info = new StoredMailInfo(id);
        dao.saveStoredMailInfo(MAILADDRESS, info, path);
    }

    @Test
    public void shouldSaveStoredMailInfo() throws Exception {
        dao.deleteStoredMailInfo(MAILADDRESS, ID, PATH);
        StoredMailInfo beforeInfo = dao.loadStoredMailInfo(MAILADDRESS, ID, PATH);
        assertThat(beforeInfo, is(nullValue()));

        dao.saveStoredMailInfo(MAILADDRESS, TEST_MAIL_INFO, PATH);

        StoredMailInfo afterInfo = dao.loadStoredMailInfo(MAILADDRESS, ID, PATH);
        assertThat(afterInfo, is(not(nullValue())));
    }

    @Test(expected = DAOException.class)
    public void shouldNotDeleteStoredMailInfo_InfoDoesNotExist() throws Exception {
        String nonExistentID = "ANonExistantID";
        String path = "aPath";

        dao.deleteStoredMailInfo(MAILADDRESS, nonExistentID, path);
    }

    @Test
    public void shouldDeleteStoredMailInfo() throws Exception {
        StoredMailInfo beforeInfo = dao.loadStoredMailInfo(MAILADDRESS, ID, PATH);
        assertThat(beforeInfo, is(not(nullValue())));

        dao.deleteStoredMailInfo(MAILADDRESS, ID, PATH);

        StoredMailInfo afterInfo = dao.loadStoredMailInfo(MAILADDRESS, ID, PATH);
        assertThat(afterInfo, is(nullValue()));
    }

    @Configuration
    public static class StoredMailInfoDAOFilePersistenceTestConfiguration {

        @Bean(name = "dataFolder")
        public File dataFolder() {
            return FOLDER.getRoot();
        }

        @Bean(name = "accountFolderPattern")
        public String accountFolderPattern() {
            return FilenameUtils.concat(dataFolder().getAbsolutePath(), "%s");
        }

        @Bean
        public StoredMailInfoDAO getStoredMailInfoDAO() {
            return new StoredMailInfoDAOFilePersistence();
        }

        @Bean
        public Serializer getSerializer() {
            return spy(new Serializer());
        }
    }
}
